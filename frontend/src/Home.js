import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import './Home.css';

const Home = () => {
  const [imgError, setImgError] = useState(false);

  return (
    <div className="home-container">
      {/* Hero Section */}
      <div className="hero-section">
        <div className="hero-tabs">
          <Link to="/gallery" className="tab-link">
            <i className="fa-solid fa-palette"></i>
            <span>Artworks Gallery</span>
          </Link>
          <Link to="/photos" className="tab-link">
            <i className="fa-solid fa-camera"></i>
            <span>Photo Gallery</span>
          </Link>
          <Link to="/my-purchases" className="tab-link">
            <i className="fa-solid fa-shopping-bag"></i>
            <span>My Purchases</span>
          </Link>
          <Link to="/cart" className="tab-link">
            <i className="fa-solid fa-shopping-cart"></i>
            <span>Cart</span>
          </Link>
        </div>
        <div className="hero-content-text">
          <h1 className="hero-title">
            <span className="title-line">Welcome to</span>
            <span className="title-accent">Iryna Patrikieiev</span>
            <span className="title-subtitle">Art Gallery</span>
          </h1>
          <p className="hero-subtitle">
            Discover unique artworks and stunning photography by Iryna Patrikieiev. 
            Each piece tells a story and captures the beauty of the world through an artist's lens.
          </p>
        </div>
        <div className="hero-content-photo">
          {imgError ? (
            <div style={{width: '100%', height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#aaa', fontSize: 32}}>
              No Image
            </div>
          ) : (
            <img src="/profile.jpg" alt="Profile" onError={() => setImgError(true)} />
          )}
        </div>
        <div className="hero-decoration">
          <div className="floating-shapes">
            <div className="shape shape-1"></div>
            <div className="shape shape-2"></div>
            <div className="shape shape-3"></div>
          </div>
        </div>
      </div>

      {/* Features Section */}
      <div className="features-section">
        <h2 className="section-title">What You Can Do</h2>
        <div className="features-grid">
          <div className="feature-card">
            <div className="feature-icon">
              <i className="fa-solid fa-paintbrush"></i>
            </div>
            <h3>Original Artworks</h3>
            <p>Unique paintings and drawings created by Iryna Patrikieiev, each piece available for purchase or download</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon">
              <i className="fa-solid fa-camera-retro"></i>
            </div>
            <h3>Photography</h3>
            <p>Stunning photographs capturing moments and beauty, available for order or digital download</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon">
              <i className="fa-solid fa-download"></i>
            </div>
            <h3>Digital Downloads</h3>
            <p>Get instant access to high-resolution digital versions of artworks and photographs</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon">
              <i className="fa-solid fa-shipping-fast"></i>
            </div>
            <h3>Physical Orders</h3>
            <p>Order original artworks and printed photographs with worldwide shipping</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Home; 