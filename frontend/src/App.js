import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import Home from './Home';
import Gallery from './Gallery';
import Cart from './Cart';
import PhotoGallery from './PhotoGallery';
import VideoGallery from './VideoGallery';
import MyPurchases from './MyPurchases';
import AdminPanel from './AdminPanel';
import './App.css';

function App() {
  const [cart, setCart] = useState([]);
  const [menuOpen, setMenuOpen] = useState(false);
  const closeMenu = () => setMenuOpen(false);

  const handleAddToCart = (art) => {
    setCart((prev) => [...prev, art]);
  };

  const handleRemoveFromCart = (id) => {
    setCart((prev) => prev.filter((a) => a.id !== id));
  };

  const handleOrder = () => {
    setCart([]);
  };

  return (
    <Router>
      <div className="App">
        <button
          className={`nav-toggle${menuOpen ? ' open' : ''}`}
          onClick={() => setMenuOpen(o => !o)}
          aria-label="Menu"
          aria-expanded={menuOpen}
        >
          <span></span>
          <span></span>
          <span></span>
        </button>
        {menuOpen && <div className="nav-backdrop" onClick={closeMenu}></div>}
        <nav className={`app-nav${menuOpen ? ' open' : ''}`}>
          <Link to="/" className="btn-animate nav-btn" onClick={closeMenu}><i className="fa-solid fa-house"></i>Home</Link>
          <Link to="/gallery" className="btn-animate nav-btn" onClick={closeMenu}><i className="fa-solid fa-image"></i>Artworks</Link>
          <Link to="/photos" className="btn-animate nav-btn" onClick={closeMenu}><i className="fa-solid fa-camera"></i>Photos</Link>
          <Link to="/videos" className="btn-animate nav-btn" onClick={closeMenu}><i className="fa-solid fa-video"></i>Videos</Link>
          <Link to="/my-purchases" className="btn-animate nav-btn" onClick={closeMenu}><i className="fa-solid fa-shopping-bag"></i>Purchases</Link>
          <Link to="/cart" className="btn-animate nav-btn" onClick={closeMenu}><i className="fa-solid fa-cart-shopping"></i>Cart ({cart.length})</Link>
        </nav>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/gallery" element={<Gallery onAddToCart={handleAddToCart} />} />
          <Route path="/photos" element={<PhotoGallery />} />
          <Route path="/videos" element={<VideoGallery />} />
          <Route path="/my-purchases" element={<MyPurchases />} />
          <Route path="/cart" element={<Cart cart={cart} onRemove={handleRemoveFromCart} onOrder={handleOrder} />} />
          <Route path="/admin" element={<AdminPanel />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
