import React, { useEffect, useState } from 'react';
import config from './config';
import './Gallery.css';

function Gallery({ onAddToCart }) {
  const [artworks, setArtworks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [toast, setToast] = useState("");
  const [toastHide, setToastHide] = useState(false);
  const [selectedArtwork, setSelectedArtwork] = useState(null);

  useEffect(() => {
    fetch(`${config.API_BASE_URL}/api/artworks`)
      .then(res => {
        if (!res.ok) throw new Error('Failed to load artworks');
        return res.json();
      })
      .then(data => {
        setArtworks(data);
        setLoading(false);
      })
      .catch(e => {
        setError(e.message);
        setLoading(false);
      });
  }, []);

  const handleAdd = (art) => {
    onAddToCart(art);
    setToast(`"${art.title}" added to cart!`);
    setToastHide(false);
    setTimeout(() => setToastHide(true), 1200);
    setTimeout(() => setToast(""), 1800);
  };

  const openModal = (artwork) => {
    setSelectedArtwork(artwork);
  };

  const closeModal = () => {
    setSelectedArtwork(null);
  };

  const handleModalClick = (e) => {
    if (e.target.classList.contains('modal-overlay')) {
      closeModal();
    }
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div className="gallery-container">
      <h2 className="gallery-title">Artworks Gallery</h2>
      {toast && <div className={`toast${toastHide ? ' hide' : ''}`}>{toast}</div>}
      <div className="gallery-grid">
        {artworks.map(art => (
          <div key={art.id} className="gallery-card">
            <img 
              src={`${config.API_BASE_URL}${art.imageUrl}`} 
              alt={art.title} 
              className="gallery-image"
              style={{ cursor: 'pointer' }}
              onClick={() => openModal(art)}
            />
            <div className="gallery-title-text">{art.title}</div>
            <div className="artwork-details">
              <span className="artwork-price">${art.price}</span>
              <span className="artwork-size">{art.sizeX} x {art.sizeY} cm</span>
            </div>
            <button className="gallery-button" onClick={() => handleAdd(art)}>
              <i className="fa-solid fa-cart-plus"></i>
              Add to cart
            </button>
          </div>
        ))}
      </div>

      {/* Modal */}
      {selectedArtwork && (
        <div className="modal-overlay" onClick={handleModalClick}>
          <div className="modal-content">
            <button className="modal-close" onClick={closeModal}>
              <i className="fa-solid fa-times"></i>
            </button>
            <img 
              src={`${config.API_BASE_URL}${selectedArtwork.imageUrl}`} 
              alt={selectedArtwork.title} 
              className="modal-image"
            />
            <div className="modal-info">
              <h3>{selectedArtwork.title}</h3>
              <p className="modal-artist">{selectedArtwork.artist}</p>
              <button className="btn-animate modal-add-btn" onClick={() => handleAdd(selectedArtwork)}>
                <i className="fa-solid fa-cart-plus" style={{ marginRight: 8 }}></i>
                Add to cart
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default Gallery; 