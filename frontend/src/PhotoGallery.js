import React, { useEffect, useState } from 'react';
import StripePayment from './StripePayment';

function PhotoGallery() {
  const [photos, setPhotos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [purchaseModal, setPurchaseModal] = useState({ show: false, photo: null });
  const [userEmail, setUserEmail] = useState(''); // Додаємо стан для email користувача

  useEffect(() => {
    fetch('http://localhost:8080/api/photos')
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
      
      // Перевірити, чи вже куплене фото
      try {
        const canDownloadResponse = await fetch(`http://localhost:8080/api/photo-purchases/can-download?email=${encodeURIComponent(userEmail)}&photoId=${photo.id}`);
        const canDownload = await canDownloadResponse.json();
        
        if (canDownload) {
          // Фото вже куплене — одразу скачати
          const purchasesResponse = await fetch(`http://localhost:8080/api/photo-purchases/user/${encodeURIComponent(userEmail)}`);
          const purchases = await purchasesResponse.json();
          const existingPurchase = purchases.find(p => p.photoId === photo.id && p.status === 'COMPLETED' && p.downloadToken);

          if (existingPurchase && existingPurchase.downloadToken) {
            const downloadResponse = await fetch(`http://localhost:8080/api/photo-purchases/download-file?downloadToken=${existingPurchase.downloadToken}`);
            if (downloadResponse.ok) {
              const blob = await downloadResponse.blob();
              const url = window.URL.createObjectURL(blob);
              const link = document.createElement('a');
              link.href = url;
              link.download = photo.title || 'photo.jpg';
              document.body.appendChild(link);
              link.click();
              document.body.removeChild(link);
              window.URL.revokeObjectURL(url);
              alert('Download completed!');
            } else {
              alert('Download failed. Please check your email for the download link.');
            }
          } else {
            alert('Purchase found but download token is missing. Please check your email for the download link.');
          }
        } else {
          setPurchaseModal({ show: true, photo: photo });
        }
      } catch (error) {
        console.error('Error checking purchase status:', error);
        // Якщо не вдалося перевірити, все одно відкрити модальне вікно
        setPurchaseModal({ show: true, photo: photo });
      }
    } else {
      await downloadPhoto(photo);
    }
  };

  const downloadPhoto = async (photo) => {
    try {
      const response = await fetch(`http://localhost:8080${photo.imageUrl}`);
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
    <div style={{ maxWidth: 900, margin: '2rem auto' }}>
      <h2>Photo Gallery</h2>
      <div style={{ display: 'flex', flexWrap: 'wrap', gap: 24, justifyContent: 'center' }}>
        {photos.map(photo => (
          <div key={photo.id} className="card card-animate" style={{ width: 220, textAlign: 'center' }}>
            <img 
              src={`http://localhost:8080${photo.imageUrl}`} 
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