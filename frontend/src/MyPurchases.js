import React, { useState } from 'react';
import config from './config';
import './Gallery.css';

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
      const [photosRes, videosRes] = await Promise.all([
        fetch(`${config.API_BASE_URL}/api/photo-purchases/user/${encodeURIComponent(email)}`),
        fetch(`${config.API_BASE_URL}/api/video-purchases/user/${encodeURIComponent(email)}`)
      ]);

      if (!photosRes.ok || !videosRes.ok) {
        setError('Failed to fetch purchases');
        return;
      }

      const photos = await photosRes.json();
      const videos = await videosRes.json();

      const combined = [
        ...photos.map(p => ({
          ...p,
          type: 'photo',
          title: p.photoTitle,
          downloadEndpoint: 'photo-purchases'
        })),
        ...videos.map(v => ({
          ...v,
          type: 'video',
          title: v.videoTitle,
          downloadEndpoint: 'video-purchases'
        }))
      ].sort((a, b) => new Date(b.purchaseDate) - new Date(a.purchaseDate));

      setPurchases(combined);
    } catch (e) {
      setError('Error: ' + e.message);
    } finally {
      setLoading(false);
    }
  };

  const handleDownload = async (purchase) => {
    try {
      const response = await fetch(
        `${config.API_BASE_URL}/api/${purchase.downloadEndpoint}/download-file?downloadToken=${purchase.downloadToken}`
      );

      if (response.ok) {
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = purchase.title || (purchase.type === 'video' ? 'video.mp4' : 'photo.jpg');
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
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
    <div className="purchases-container">
      <h2 className="purchases-title">My Purchases</h2>

      <div className="purchases-search">
        <div className="purchases-search-form">
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="Enter your email"
            className="purchases-email-input"
          />
          <button
            onClick={fetchPurchases}
            disabled={loading}
            className="purchases-search-btn"
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
        <div className="purchases-grid">
          {purchases.map(purchase => (
            <div key={`${purchase.type}-${purchase.id}`} className="purchase-card">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
                <h3 className="purchase-photo-title">
                  {purchase.type === 'video' ? '🎬 ' : '📷 '}
                  {purchase.title}
                </h3>
                <span style={{
                  color: getStatusColor(purchase.status),
                  fontWeight: 'bold',
                  fontSize: '0.9rem'
                }}>
                  {purchase.status}
                </span>
              </div>

              <div className="purchase-info">
                <strong>Type:</strong> {purchase.type === 'video' ? 'Video' : 'Photo'}
              </div>

              <div className="purchase-info">
                <strong>Amount:</strong> ${purchase.amountPaid}
              </div>

              <div className="purchase-info">
                <strong>Purchase Date:</strong> {formatDate(purchase.purchaseDate)}
              </div>

              <div className="purchase-info">
                <strong>Transaction ID:</strong> {purchase.transactionId}
              </div>

              {purchase.tokenExpiry && (
                <div className="purchase-info">
                  <strong>Download Expires:</strong> {formatDate(purchase.tokenExpiry)}
                </div>
              )}

              {purchase.canDownload && (
                <button
                  onClick={() => handleDownload(purchase)}
                  className="purchase-download-btn"
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
