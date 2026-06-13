import ReactDOM from 'react-dom';
import React, { useState, useEffect, useRef } from 'react';
import PhoneInput from 'react-phone-input-2';
import 'react-phone-input-2/lib/style.css';
import config from './config';
import './Gallery.css';

function Cart({ cart, onRemove, onOrder }) {
  const [form, setForm] = useState({ name: '', email: '', phone: '', shippingAddress: '', country: '', city: '', postalCode: '' });
  const [status, setStatus] = useState('');
  const [emailError, setEmailError] = useState('');
  const [phoneError, setPhoneError] = useState('');
  const [addressError, setAddressError] = useState('');
  const [countryError, setCountryError] = useState('');
  const [cityError, setCityError] = useState('');
  const [publishableKey, setPublishableKey] = useState('');
  const [stripe, setStripe] = useState(null);
  const [isPaying, setIsPaying] = useState(false);
  const [postalCode, setPostalCode] = useState('');
  const elementsRef = useRef(null);
  const cardRef = useRef(null);
  const mountedRef = useRef(false);

  useEffect(() => {
    fetch(`${config.API_BASE_URL}/api/stripe/config`)
      .then(res => res.json())
      .then(data => {
        setPublishableKey(data.publishableKey);
        const stripeInstance = window.Stripe(data.publishableKey, { locale: 'en' });
        setStripe(stripeInstance);
      })
      .catch(() => {});
  }, []);

  useEffect(() => {
    if (!stripe || !isPaying || mountedRef.current) return;
    elementsRef.current = stripe.elements();
    const card = elementsRef.current.create('card', {
      hidePostalCode: true,
      style: {
        base: { fontSize: '16px', color: '#424770', '::placeholder': { color: '#aab7c4' } },
        invalid: { color: '#9e2146' }
      }
    });
    card.mount('#cart-card-element');
    cardRef.current = card;
    mountedRef.current = true;
  }, [stripe, isPaying]);

  const destroyCard = () => {
    try { cardRef.current && cardRef.current.destroy(); } catch(e){}
    cardRef.current = null;
    elementsRef.current = null;
    mountedRef.current = false;
  };

  const validateEmail = (email) => {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(String(email).toLowerCase());
  };

  const handleChange = e => {
    setForm({ ...form, [e.target.name]: e.target.value });
    if (e.target.name === 'email') setEmailError('');
    if (e.target.name === 'shippingAddress') setAddressError('');
    if (e.target.name === 'country') setCountryError('');
    if (e.target.name === 'city') setCityError('');
    if (e.target.name === 'postalCode') setPostalCode('');
  };

  const handlePhoneChange = phone => {
    setForm({ ...form, phone });
    setPhoneError('');
  };

  const startPayment = (e) => {
    e.preventDefault();
    setEmailError(''); setPhoneError(''); setAddressError(''); setCountryError(''); setCityError('');
    let hasErrors = false;
    if (!form.email.trim()) { setEmailError('Email is required.'); hasErrors = true; }
    else if (!validateEmail(form.email)) { setEmailError('Please enter a valid email address.'); hasErrors = true; }
    if (!form.phone.trim()) { setPhoneError('Phone number is required.'); hasErrors = true; }
    if (!form.country.trim()) { setCountryError('Country is required.'); hasErrors = true; }
    if (!form.city.trim()) { setCityError('City is required.'); hasErrors = true; }
    if (!form.shippingAddress.trim()) { setAddressError('Shipping address is required.'); hasErrors = true; }
    if (cart.length === 0) { setStatus('Your cart is empty'); hasErrors = true; }
    if (hasErrors) return;
    setIsPaying(true);
    setPostalCode(form.postalCode || '');
  };

  const handlePayAndPlaceOrder = async () => {
    try {
      setStatus('Initializing payment...');
      const amount = cart.reduce((sum, a) => sum + Number(a.price || 0), 0);
      const createIntentResponse = await fetch(`${config.API_BASE_URL}/api/stripe/create-order-payment-intent`, {
        method: 'POST', headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ amount, currency: 'usd', description: 'Artwork order' })
      });
      if (!createIntentResponse.ok) throw new Error('Failed to init payment');
      const { clientSecret, paymentIntentId } = await createIntentResponse.json();

      setStatus('Confirming payment...');
      const { error: confirmError } = await stripe.confirmCardPayment(clientSecret, {
        payment_method: { 
          card: cardRef.current, 
          billing_details: { 
            email: form.email,
            address: { postal_code: undefined }
          } 
        }
      });
      if (confirmError) throw new Error(confirmError.message);

      setStatus('Finalizing order...');
      const confirmOrderRes = await fetch(`${config.API_BASE_URL}/api/stripe/confirm-order-payment`, {
        method: 'POST', headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          paymentIntentId,
          customerName: form.name,
          customerEmail: form.email,
          phoneNumber: form.phone,
          shippingAddress: form.shippingAddress,
          country: form.country,
          city: form.city,
          postalCode: form.postalCode,
          artworkIds: cart.map(a => a.id)
        })
      });
      if (!confirmOrderRes.ok) throw new Error('Failed to confirm order');
      const data = await confirmOrderRes.json();
      if (data.success) {
        setStatus(`Order paid successfully! Order #${data.orderId}`);
        onOrder();
        setIsPaying(false);
        destroyCard();
      } else {
        throw new Error(data.error || 'Order failed');
      }
    } catch (err) {
      setStatus(err.message || 'Payment failed');
    }
  };

  const closePayment = () => {
    setIsPaying(false);
    destroyCard();
  };

  const isProcessing = isPaying && (!!status && (status.includes('Initializing') || status.includes('Confirming') || status.includes('Finalizing')));

  return (
    <div className="cart-container">
      <h2 className="cart-title">Cart</h2>
      <div className="card card-animate">
        {cart.length === 0 ? <div>Your cart is empty</div> : (
          <>
            <ul className="cart-list">
              {cart.map((art, idx) => (
                <li key={art.id + idx} className="cart-item">
                  <span className="cart-item-title">{art.title} — {art.artist}</span>
                  <button className="cart-remove-btn" onClick={() => onRemove(art.id)}>
                    <i className="fa-solid fa-trash"></i>Remove
                  </button>
                </li>
              ))}
            </ul>
            <form onSubmit={startPayment} className="cart-form">
              <div className="form-group">
                <input name="name" placeholder="Name" value={form.name} onChange={handleChange} required className="form-input" />
              </div>
              <div className="form-group">
                <input name="email" placeholder="Email *" value={form.email} onChange={handleChange} className="form-input" />
                {emailError && <div className="error-message">{emailError}</div>}
              </div>
              <div className="form-group">
                <PhoneInput
                  country={'ua'} value={form.phone} onChange={handlePhoneChange}
                  inputProps={{ name: 'phone', placeholder: 'Phone *' }}
                  containerClass="phone-input-container" inputClass="form-input"
                />
                {phoneError && <div className="error-message">{phoneError}</div>}
              </div>
              <div className="form-row">
                <div className="form-group half">
                  <input name="country" placeholder="Country *" value={form.country} onChange={handleChange} className="form-input" />
                  {countryError && <div className="error-message">{countryError}</div>}
                </div>
                <div className="form-group half">
                  <input name="city" placeholder="City *" value={form.city} onChange={handleChange} className="form-input" />
                  {cityError && <div className="error-message">{cityError}</div>}
                </div>
              </div>
              <div className="form-row">
                <div className="form-group half">
                  <input name="postalCode" placeholder="Postal code (optional)" value={form.postalCode} onChange={handleChange} className="form-input" />
                </div>
                <div className="form-group half">
                </div>
              </div>
              <div className="form-group">
                <textarea name="shippingAddress" placeholder="Shipping address *" value={form.shippingAddress} onChange={handleChange} className="form-input" rows={3} />
                {addressError && <div className="error-message">{addressError}</div>}
              </div>
              {!isPaying && (
                <button className="cart-submit-btn" type="submit">
                  <i className="fa-solid fa-credit-card"></i>Pay & Place order
                </button>
              )}
            </form>

            {isPaying && ReactDOM.createPortal(
              <div className="stripe-modal">
                <div className="stripe-content">
                  <h3 className="stripe-title">Payment</h3>
                  <div className="stripe-photo-info">
                    <div><strong>Items:</strong> {cart.length}</div>
                    <div><strong>Total:</strong> ${cart.reduce((s,a)=>s+Number(a.price||0),0).toFixed(2)}</div>
                  </div>
                  <input className="stripe-input" value={form.email} disabled />
                  {/* Postal code removed from payment modal; collected in address form */}
                  <div id="cart-card-element" className="stripe-card-element"></div>
                  <div className="stripe-buttons">
                    <button className="stripe-btn stripe-btn-secondary" onClick={closePayment}>Cancel</button>
                    <button className="stripe-btn stripe-btn-primary" onClick={handlePayAndPlaceOrder} disabled={!publishableKey}>Pay now</button>
                  </div>
                </div>
              </div>, document.body)
            }
          </>
        )}
        {status && <div style={{ marginTop: 16 }}>{status}</div>}
      </div>
      {isProcessing && ReactDOM.createPortal(
        <div className="loading-overlay">
          <div className="loading-box">
            <div className="spinner"></div>
            <div>{status || 'Processing payment...'}</div>
          </div>
        </div>, document.body
      )}
    </div>
  );
}

export default Cart; 