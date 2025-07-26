import React, { useState } from 'react';
import PhoneInput from 'react-phone-input-2';
import 'react-phone-input-2/lib/style.css';
import config from './config';

function Cart({ cart, onRemove, onOrder }) {
  const [form, setForm] = useState({ name: '', email: '', phone: '' });
  const [status, setStatus] = useState('');
  const [emailError, setEmailError] = useState('');
  const [phoneError, setPhoneError] = useState('');

  const validateEmail = (email) => {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(String(email).toLowerCase());
  };

  const handleChange = e => {
    setForm({ ...form, [e.target.name]: e.target.value });
    if (e.target.name === 'email') {
      setEmailError('');
    }
  };

  const handlePhoneChange = phone => {
    setForm({ ...form, phone });
    setPhoneError('');
  };

  const handleSubmit = e => {
    e.preventDefault();
    
    // Reset errors
    setEmailError('');
    setPhoneError('');
    
    let hasErrors = false;
    
    // Check if email is empty
    if (!form.email.trim()) {
      setEmailError('Email is required.');
      hasErrors = true;
    } else if (!validateEmail(form.email)) {
      setEmailError('Please enter a valid email address.');
      hasErrors = true;
    }
    
    // Check if phone is empty
    if (!form.phone.trim()) {
      setPhoneError('Phone number is required.');
      hasErrors = true;
    }
    
    if (hasErrors) {
      return;
    }
    
    setStatus('Sending...');
    fetch(`${config.API_BASE_URL}/api/orders`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        customerName: form.name,
        customerEmail: form.email,
        phoneNumber: form.phone,
        artworkIds: cart.map(art => art.id)
      })
    })
      .then(res => {
        if (!res.ok) throw new Error('Order failed');
        return res.json();
      })
      .then(() => {
        setStatus('Order placed successfully!');
        onOrder();
      })
      .catch(() => setStatus('An error occurred. Please try again.'));
  };

  return (
    <div style={{ maxWidth: 600, margin: '2rem auto' }}>
      <h2>Cart</h2>
      <div className="card card-animate">
        {cart.length === 0 ? <div>Your cart is empty</div> : (
          <>
            <ul style={{ paddingLeft: 0, listStyle: 'none' }}>
              {cart.map((art, idx) => (
                <li key={art.id + idx} style={{ marginBottom: 8, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                  <span>{art.title} — {art.artist}</span>
                  <button className="btn-animate" onClick={() => onRemove(art.id)} style={{ marginLeft: 16, color: '#d32f2f', background: '#fff', border: '1px solid #d32f2f', borderRadius: 6, padding: '0.3rem 0.8rem' }}>
                    <i className="fa-solid fa-trash" style={{ marginRight: 6 }}></i>Remove
                  </button>
                </li>
              ))}
            </ul>
            <form onSubmit={handleSubmit} style={{ marginTop: 24 }}>
              <div className="form-group">
                <input name="name" placeholder="Name" value={form.name} onChange={handleChange} required className="form-input" />
              </div>
              <div className="form-group">
                <input name="email" placeholder="Email *" value={form.email} onChange={handleChange} className="form-input" />
                {emailError && <div className="error-message">{emailError}</div>}
              </div>
              <div className="form-group">
                <PhoneInput
                  country={'ua'}
                  value={form.phone}
                  onChange={handlePhoneChange}
                  inputProps={{
                    name: 'phone',
                    placeholder: 'Phone *',
                  }}
                  containerClass="phone-input-container"
                  inputClass="form-input"
                />
                {phoneError && <div className="error-message">{phoneError}</div>}
              </div>
              <button className="btn-animate" type="submit" style={{ marginTop: 8, padding: '0.5rem 1.2rem', borderRadius: 6, background: '#388e3c', color: '#fff', border: 'none', cursor: 'pointer', width: '100%' }}>
                <i className="fa-solid fa-paper-plane" style={{ marginRight: 8 }}></i>Place order
              </button>
            </form>
          </>
        )}
        {status && <div style={{ marginTop: 16 }}>{status}</div>}
      </div>
    </div>
  );
}

export default Cart; 