import React, { useEffect, useState } from 'react';
import StripePayment from './StripePayment';
import config from './config';
import './Gallery.css';

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
    if (photo.price && photo.price > 0) {
      // Запитати email користувача, якщо він ще не введений
      if (!userEmail) {
        const email = prompt('Please enter your email to purchase this photo:');
        if (!email) return; // Користувач скасував
        setUserEmail(email);
      }
      
      // Завжди дозволяємо нові покупки - відкриваємо модальне вікно
      setPurchaseModal({ show: true, photo: photo });
    } else {
      await downloadPhoto(photo);
    }
  };

  const downloadPhoto = async (photo) => {
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
    alert(`Purchase successful! Transaction ID: ${purchase.transactionId}\nCheck your email for download link.`);
    setPurchaseModal({ show: false, photo: null });
  };

  const closeModal = () => {
    setPurchaseModal({ show: false, photo: null });
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div className="gallery-container">
      <h2 className="gallery-title">Photo Gallery</h2>
      <div className="gallery-grid">
        {photos.map(photo => (
          <div key={photo.id} className="gallery-card">
            <img 
              src={`${config.API_BASE_URL}${photo.imageUrl}`} 
              alt={photo.title} 
              className="gallery-image"
            />
            <div className="gallery-title-text">{photo.title}</div>
            <div className="gallery-subtitle">{photo.photographer}</div>
            {photo.price && photo.price > 0 ? (
              <div className="gallery-price paid">
                ${photo.price}
              </div>
            ) : (
              <div className="gallery-price free">
                Free
              </div>
            )}
            <button className="gallery-button" onClick={() => handleDownload(photo)}>
              <i className="fa-solid fa-arrow-down"></i>
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