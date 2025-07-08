import React, { useState, useEffect } from 'react';
import './AdminPanel.css';

const AdminPanel = () => {
    const [activeTab, setActiveTab] = useState('dashboard');
    const [dashboardData, setDashboardData] = useState(null);
    const [purchases, setPurchases] = useState([]);
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [unauthorized, setUnauthorized] = useState(false);

    useEffect(() => {
        loadDashboardData();
        loadPurchases();
        loadOrders();
    }, []);

    const handle401 = (response) => {
        if (response.status === 401) {
            setUnauthorized(true);
            setTimeout(() => {
                window.location.href = '/';
            }, 1500);
            return true;
        }
        return false;
    };

    const loadDashboardData = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/admin/dashboard', {
                credentials: 'include'
            });
            if (handle401(response)) return;
            const data = await response.json();
            setDashboardData(data);
        } catch (error) {
            console.error('Error loading dashboard data:', error);
        }
    };

    const loadPurchases = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/admin/purchases', {
                credentials: 'include'
            });
            if (handle401(response)) return;
            const data = await response.json();
            setPurchases(data);
        } catch (error) {
            console.error('Error loading purchases:', error);
        }
    };

    const loadOrders = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/admin/orders', {
                credentials: 'include'
            });
            if (handle401(response)) return;
            const data = await response.json();
            setOrders(data);
        } catch (error) {
            console.error('Error loading orders:', error);
        } finally {
            setLoading(false);
        }
    };

    const confirmPayment = async (purchaseId) => {
        try {
            const response = await fetch(`http://localhost:8080/api/admin/purchases/${purchaseId}/confirm`, {
                method: 'POST',
                credentials: 'include'
            });
            if (handle401(response)) return;
            if (response.ok) {
                alert('Payment confirmed successfully!');
                loadPurchases();
                loadDashboardData();
            }
        } catch (error) {
            console.error('Error confirming payment:', error);
            alert('Error confirming payment');
        }
    };

    const updateOrderStatus = async (orderId, status) => {
        try {
            const response = await fetch(`http://localhost:8080/api/admin/orders/${orderId}/status?status=${status}`, {
                method: 'PUT',
                credentials: 'include'
            });
            if (handle401(response)) return;
            if (response.ok) {
                alert('Order status updated successfully!');
                loadOrders();
            }
        } catch (error) {
            console.error('Error updating order status:', error);
            alert('Error updating order status');
        }
    };

    const handleLogout = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/admin/logout', {
                method: 'POST',
                credentials: 'include'
            });
            if (handle401(response)) return;
            if (response.ok) {
                alert('Logged out successfully!');
                window.location.reload();
            }
        } catch (error) {
            console.error('Error logging out:', error);
            alert('Error logging out');
        }
    };

    const renderDashboard = () => {
        if (!dashboardData) return <div>Loading...</div>;

        return (
            <div className="dashboard">
                <h2>Dashboard</h2>
                
                {/* Статистика */}
                <div className="stats-grid">
                    <div className="stat-card">
                        <h3>Total Photos</h3>
                        <p className="stat-number">{dashboardData.totalPhotos}</p>
                    </div>
                    <div className="stat-card">
                        <h3>Total Purchases</h3>
                        <p className="stat-number">{dashboardData.totalPurchases}</p>
                    </div>
                    <div className="stat-card">
                        <h3>Total Orders</h3>
                        <p className="stat-number">{dashboardData.totalOrders}</p>
                    </div>
                    <div className="stat-card">
                        <h3>Total Revenue</h3>
                        <p className="stat-number">${dashboardData.totalRevenue || 0}</p>
                    </div>
                    <div className="stat-card">
                        <h3>Monthly Revenue</h3>
                        <p className="stat-number">${dashboardData.monthlyRevenue || 0}</p>
                    </div>
                </div>

                {/* Останні покупки */}
                <div className="recent-section">
                    <h3>Recent Purchases</h3>
                    <div className="table-container">
                        <table>
                            <thead>
                                <tr>
                                    <th>Photo</th>
                                    <th>Customer</th>
                                    <th>Amount</th>
                                    <th>Date</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                {dashboardData.recentPurchases?.map((purchase, index) => (
                                    <tr key={index}>
                                        <td>{purchase.photoTitle}</td>
                                        <td>{purchase.customerEmail}</td>
                                        <td>${purchase.amount}</td>
                                        <td>{new Date(purchase.date).toLocaleDateString()}</td>
                                        <td>
                                            <span className={`status ${purchase.status.toLowerCase()}`}>
                                                {purchase.status}
                                            </span>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>

                {/* Популярні фотографії */}
                <div className="popular-section">
                    <h3>Popular Photos</h3>
                    <div className="popular-photos">
                        {dashboardData.popularPhotos?.map((photo, index) => (
                            <div key={index} className="popular-photo">
                                <h4>{photo.title}</h4>
                                <p>Photographer: {photo.photographer}</p>
                                <p>Price: ${photo.price}</p>
                                <p>Purchases: {photo.purchaseCount}</p>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        );
    };

    const renderPurchases = () => {
        return (
            <div className="purchases">
                <h2>Photo Purchases</h2>
                <div className="table-container">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Photo</th>
                                <th>Customer</th>
                                <th>Amount</th>
                                <th>Date</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {purchases.map((purchase) => (
                                <tr key={purchase.id}>
                                    <td>{purchase.id}</td>
                                    <td>{purchase.photoTitle}</td>
                                    <td>{purchase.customerEmail}</td>
                                    <td>${purchase.amountPaid}</td>
                                    <td>{new Date(purchase.purchaseDate).toLocaleDateString()}</td>
                                    <td>
                                        <span className={`status ${purchase.status.toLowerCase()}`}>
                                            {purchase.status}
                                        </span>
                                    </td>
                                    <td>
                                        {purchase.status === 'PENDING' && (
                                            <button 
                                                onClick={() => confirmPayment(purchase.id)}
                                                className="btn-confirm"
                                            >
                                                Confirm Payment
                                            </button>
                                        )}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
        );
    };

    const renderOrders = () => {
        return (
            <div className="orders">
                <h2>Artwork Orders</h2>
                <div className="table-container">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Customer</th>
                                <th>Phone</th>
                                <th>Total Price</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {orders.map((order) => (
                                <tr key={order.id}>
                                    <td>{order.id}</td>
                                    <td>{order.customerName}</td>
                                    <td>{order.phoneNumber}</td>
                                    <td>${order.totalPrice}</td>
                                    <td>
                                        <span className={`status ${order.status.toLowerCase()}`}>
                                            {order.status}
                                        </span>
                                    </td>
                                    <td>
                                        <select 
                                            value={order.status}
                                            onChange={(e) => updateOrderStatus(order.id, e.target.value)}
                                            className="status-select"
                                        >
                                            <option value="NEW">New</option>
                                            <option value="PROCESSING">Processing</option>
                                            <option value="COMPLETED">Completed</option>
                                            <option value="CANCELLED">Cancelled</option>
                                        </select>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
        );
    };

    const renderContent = () => {
        switch (activeTab) {
            case 'dashboard':
                return renderDashboard();
            case 'purchases':
                return renderPurchases();
            case 'orders':
                return renderOrders();
            default:
                return renderDashboard();
        }
    };

    if (unauthorized) {
        return <div className="unauthorized">Unauthorized. Redirecting...</div>;
    }
    if (loading || !dashboardData) {
        return <div className="loading">Loading admin panel...</div>;
    }

    return (
        <div className="admin-panel">
            <div className="admin-header">
                <h1>Art Gallery Admin Panel</h1>
                <button onClick={handleLogout} className="logout-btn">
                    Logout
                </button>
            </div>
            
            <div className="admin-nav">
                <button 
                    className={`nav-btn ${activeTab === 'dashboard' ? 'active' : ''}`}
                    onClick={() => setActiveTab('dashboard')}
                >
                    Dashboard
                </button>
                <button 
                    className={`nav-btn ${activeTab === 'purchases' ? 'active' : ''}`}
                    onClick={() => setActiveTab('purchases')}
                >
                    Photo Purchases
                </button>
                <button 
                    className={`nav-btn ${activeTab === 'orders' ? 'active' : ''}`}
                    onClick={() => setActiveTab('orders')}
                >
                    Artwork Orders
                </button>
            </div>
            
            <div className="admin-content">
                {renderContent()}
            </div>
        </div>
    );
};

export default AdminPanel; 