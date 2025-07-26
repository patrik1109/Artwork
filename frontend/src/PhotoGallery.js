import React, { useEffect, useState } from 'react';
import StripePayment from './StripePayment';
import config from './config';

function PhotoGallery() {
  const [photos, setPhotos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [purchaseModal, setPurchaseModal] = useState({ show: false, photo: null });
  const [userEmail, setUserEmail] = useState(''); // Додаємо стан для email користувача

  useEffect(() => {
    fetch(`${config.API_BASE_URL}/api/photos`)
      .then(res => {
        if (!res.ok) throw new Error('Failed to load photos');
        return res.json();
      })
      .then(data => {
        setPhotos(data);
        setLoading(false);
      })
      .catch(e => {
        setError(e.message);
        setLoading(false);
      });
  }, []);

  const handleDownload = async (photo) => {
    console.log('🔍 handleDownload called for photo:', photo.title);
    console.log('💰 Price:', photo.price, 'Type:', typeof photo.price);
    console.log('📧 Current userEmail:', userEmail);
    
    if (photo.price && photo.price > 0) {
      console.log('✅ Price > 0, proceeding to purchase flow');
      
      // Запитати email користувача, якщо він ще не введений
      if (!userEmail) {
        console.log('📨 No email set, showing prompt');
        const email = prompt('Please enter your email to purchase this photo:');
        if (!email) {
          console.log('❌ User cancelled email prompt');
          return;
        }
        console.log('📨 Email entered:', email);
        setUserEmail(email);
      }
      
      console.log('🛒 Opening purchase modal');
      setPurchaseModal({ show: true, photo: photo });
    } else {
      console.log('🆓 Free photo, downloading directly');
      await downloadPhoto(photo);
    }
  };

  const downloadPhoto = async (photo) => {
    console.log('⬇️ downloadPhoto called for:', photo.title);
    try {
      const response = await fetch(`${config.API_BASE_URL}${photo.imageUrl}`);
      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = photo.title || 'photo';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (e) {
      alert('Failed to download photo');
    }
  };

  const handlePurchaseSuccess = (purchase) => {
    console.log('🎉 Purchase successful!', purchase);
    alert(`Purchase successful! Transaction ID: ${purchase.transactionId}\nCheck your email for download link.`);
    setPurchaseModal({ show: false, photo: null });
  };

  const closeModal = () => {
    setPurchaseModal({ show: false, photo: null });
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div style={{ maxWidth: 900, margin: '2rem auto' }}>
      <h2>Photo Gallery</h2>
      <div style={{ display: 'flex', flexWrap: 'wrap', gap: 24, justifyContent: 'center' }}>
        {photos.map(photo => (
          <div key={photo.id} className="card card-animate" style={{ width: 220, textAlign: 'center' }}>
            <img 
              src={`${config.API_BASE_URL}${photo.imageUrl}`} 
              alt={photo.title} 
              style={{ width: 180, height: 180, objectFit: 'cover', borderRadius: 6 }} 
            />
            <div style={{ margin: '1rem 0 0.5rem' }}><b>{photo.title}</b></div>
            <div style={{ marginBottom: 8 }}>{photo.photographer}</div>
            {photo.price && photo.price > 0 ? (
              <div style={{ marginBottom: 8, color: '#e74c3c', fontWeight: 'bold' }}>
                ${photo.price}
              </div>
            ) : (
              <div style={{ marginBottom: 8, color: '#27ae60', fontWeight: 'bold' }}>
                Free
              </div>
            )}
            <button className="btn-animate" onClick={() => handleDownload(photo)}>
              <i className="fa-solid fa-arrow-down" style={{ marginRight: 8 }}></i>
              {photo.price && photo.price > 0 ? 'Buy & Download' : 'Download'}
            </button>
          </div>
        ))}
      </div>

      {purchaseModal.show && (
        <StripePayment
          photo={purchaseModal.photo}
          userEmail={userEmail}
          onSuccess={handlePurchaseSuccess}
          onCancel={closeModal}
        />
      )}
    </div>
  );
}

export default PhotoGallery; 