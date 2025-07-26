import React, { useState } from 'react';
import config from './config';

function MyPurchases() {
  const [email, setEmail] = useState('');
  const [purchases, setPurchases] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchPurchases = async () => {
    if (!email) {
      alert('Please enter your email');
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await fetch(`${config.API_BASE_URL}/api/photo-purchases/user/${encodeURIComponent(email)}`);
      
      if (response.ok) {
        const data = await response.json();
        setPurchases(data);
      } else {
        setError('Failed to fetch purchases');
      }
    } catch (e) {
      setError('Error: ' + e.message);
    } finally {
      setLoading(false);
    }
  };

  const handleDownload = async (purchase) => {
    try {
      const response = await fetch(`${config.API_BASE_URL}/api/photo-purchases/download?downloadToken=${purchase.downloadToken}`);
      
      if (response.ok) {
        const downloadUrl = await response.text();
        
        // Створюємо тимчасове посилання для скачування
        const link = document.createElement('a');
        link.href = downloadUrl;
        link.download = purchase.photoTitle || 'photo';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      } else {
        const errorText = await response.text();
        alert(`Download failed: ${errorText}`);
      }
    } catch (e) {
      alert('Failed to download: ' + e.message);
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString();
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'COMPLETED': return '#27ae60';
      case 'PENDING': return '#f39c12';
      case 'FAILED': return '#e74c3c';
      case 'EXPIRED': return '#95a5a6';
      default: return '#000';
    }
  };

  return (
    <div style={{ maxWidth: 1000, margin: '2rem auto', padding: '0 1rem' }}>
      <h2>My Purchases</h2>
      
      <div style={{ marginBottom: '2rem' }}>
        <div style={{ display: 'flex', gap: '1rem', alignItems: 'center', marginBottom: '1rem' }}>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="Enter your email"
            style={{ padding: '0.5rem', flex: 1, maxWidth: '300px' }}
          />
          <button 
            onClick={fetchPurchases}
            disabled={loading}
            style={{ 
              padding: '0.5rem 1rem', 
              backgroundColor: '#3498db', 
              color: 'white', 
              border: 'none',
              borderRadius: '4px',
              cursor: loading ? 'not-allowed' : 'pointer'
            }}
          >
            {loading ? 'Loading...' : 'View Purchases'}
          </button>
        </div>
      </div>

      {error && (
        <div style={{ color: '#e74c3c', marginBottom: '1rem' }}>
          {error}
        </div>
      )}

      {purchases.length > 0 ? (
        <div style={{ display: 'grid', gap: '1rem' }}>
          {purchases.map(purchase => (
            <div 
              key={purchase.id} 
              style={{ 
                border: '1px solid #ddd', 
                borderRadius: '8px', 
                padding: '1rem',
                backgroundColor: '#f9f9f9'
              }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
                <h3 style={{ margin: 0 }}>{purchase.photoTitle}</h3>
                <span style={{ 
                  color: getStatusColor(purchase.status),
                  fontWeight: 'bold',
                  fontSize: '0.9rem'
                }}>
                  {purchase.status}
                </span>
              </div>
              
              <div style={{ marginBottom: '0.5rem' }}>
                <strong>Amount:</strong> ${purchase.amountPaid}
              </div>
              
              <div style={{ marginBottom: '0.5rem' }}>
                <strong>Purchase Date:</strong> {formatDate(purchase.purchaseDate)}
              </div>
              
              <div style={{ marginBottom: '0.5rem' }}>
                <strong>Transaction ID:</strong> {purchase.transactionId}
              </div>
              
              {purchase.tokenExpiry && (
                <div style={{ marginBottom: '0.5rem' }}>
                  <strong>Download Expires:</strong> {formatDate(purchase.tokenExpiry)}
                </div>
              )}
              
              {purchase.canDownload && (
                <button 
                  onClick={() => handleDownload(purchase)}
                  style={{ 
                    padding: '0.5rem 1rem', 
                    backgroundColor: '#27ae60', 
                    color: 'white', 
                    border: 'none',
                    borderRadius: '4px',
                    cursor: 'pointer'
                  }}
                >
                  <i className="fa-solid fa-download" style={{ marginRight: '0.5rem' }}></i>
                  Download
                </button>
              )}
              
              {purchase.isExpired && (
                <div style={{ color: '#e74c3c', fontWeight: 'bold' }}>
                  Download link has expired
                </div>
              )}
            </div>
          ))}
        </div>
      ) : purchases.length === 0 && !loading && email && (
        <div style={{ textAlign: 'center', color: '#7f8c8d' }}>
          No purchases found for this email address.
        </div>
      )}
    </div>
  );
}

export default MyPurchases; 