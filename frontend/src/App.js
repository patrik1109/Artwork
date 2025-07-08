import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import Home from './Home';
import Gallery from './Gallery';
import Cart from './Cart';
import PhotoGallery from './PhotoGallery';
import MyPurchases from './MyPurchases';
import AdminPanel from './AdminPanel';
import './App.css';

function App() {
  const [cart, setCart] = useState([]);

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
        <nav style={{ padding: '1rem', borderBottom: '1px solid #eee', marginBottom: 24, display: 'flex', gap: 16, justifyContent: 'center', flexWrap: 'wrap' }}>
          <Link to="/" className="btn-animate nav-btn"><i className="fa-solid fa-house" style={{ marginRight: 6 }}></i>Home</Link>
          <Link to="/gallery" className="btn-animate nav-btn"><i className="fa-solid fa-image" style={{ marginRight: 6 }}></i>Artworks Gallery</Link>
          <Link to="/photos" className="btn-animate nav-btn"><i className="fa-solid fa-camera" style={{ marginRight: 6 }}></i>Photo Gallery</Link>
          <Link to="/art-prints" className="btn-animate nav-btn"><i className="fa-solid fa-print" style={{ marginRight: 6 }}></i>Art Prints</Link>
          <Link to="/photo-prints" className="btn-animate nav-btn"><i className="fa-solid fa-image" style={{ marginRight: 6 }}></i>Photo Prints</Link>
          <Link to="/my-purchases" className="btn-animate nav-btn"><i className="fa-solid fa-shopping-bag" style={{ marginRight: 6 }}></i>My Purchases</Link>
          <Link to="/cart" className="btn-animate nav-btn"><i className="fa-solid fa-cart-shopping" style={{ marginRight: 6 }}></i>Cart ({cart.length})</Link>
          <Link to="/admin" className="btn-animate nav-btn" style={{ background: '#dc3545', color: 'white' }}><i className="fa-solid fa-cog" style={{ marginRight: 6 }}></i>Admin</Link>
        </nav>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/gallery" element={<Gallery onAddToCart={handleAddToCart} />} />
          <Route path="/photos" element={<PhotoGallery />} />
          <Route path="/art-prints" element={<Gallery onAddToCart={handleAddToCart} />} />
          <Route path="/photo-prints" element={<PhotoGallery />} />
          <Route path="/my-purchases" element={<MyPurchases />} />
          <Route path="/cart" element={<Cart cart={cart} onRemove={handleRemoveFromCart} onOrder={handleOrder} />} />
          <Route path="/admin" element={<AdminPanel />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
