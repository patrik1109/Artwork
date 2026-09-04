import React, { useState, useEffect } from 'react';
import config from './config';

const StripePayment = ({ photo, item, itemType = 'photo', userEmail, onSuccess, onCancel }) => {
  // Support both legacy `photo` prop and generic `item` prop
  const product = item || photo;
  const type = item ? itemType : 'photo';

  const [stripe, setStripe] = useState(null);
  const [card, setCard] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [email, setEmail] = useState(userEmail || '');
  const [publishableKey, setPublishableKey] = useState('');

  useEffect(() => {
    if (userEmail) {
      setEmail(userEmail);
    }
  }, [userEmail]);

  useEffect(() => {
    fetch(`${config.API_BASE_URL}/api/stripe/config`)
      .then(res => res.json())
      .then(data => {
        setPublishableKey(data.publishableKey);
        const stripeInstance = window.Stripe(data.publishableKey, { locale: 'en' });
        setStripe(stripeInstance);
      })
      .catch(err => {
        console.error('Failed to load Stripe config:', err);
        setError('Failed to load payment configuration');
      });
  }, []);

  useEffect(() => {
    if (stripe && publishableKey) {
      const elementsInstance = stripe.elements();

      const cardElement = elementsInstance.create('card', {
        style: {
          base: {
            fontSize: '16px',
            color: '#424770',
            '::placeholder': {
              color: '#aab7c4',
            },
          },
          invalid: {
            color: '#9e2146',
          },
        },
      });

      cardElement.mount('#card-element');
      setCard(cardElement);
    }
  }, [stripe, publishableKey]);

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!email) {
      setError('Please enter your email');
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const isVideo = type === 'video';
      const createIntentUrl = isVideo
        ? `${config.API_BASE_URL}/api/stripe/create-video-payment-intent`
        : `${config.API_BASE_URL}/api/stripe/create-payment-intent`;
      const confirmUrl = isVideo
        ? `${config.API_BASE_URL}/api/stripe/confirm-video-payment`
        : `${config.API_BASE_URL}/api/stripe/confirm-payment`;
      const downloadUrl = isVideo
        ? `${config.API_BASE_URL}/api/video-purchases/download-file`
        : `${config.API_BASE_URL}/api/photo-purchases/download-file`;

      const intentBody = {
        amount: product.price,
        currency: 'usd',
        description: `Purchase: ${product.title}`,
        customerEmail: email,
      };
      if (isVideo) {
        intentBody.videoId = product.id;
      } else {
        intentBody.photoId = product.id;
      }

      const createIntentResponse = await fetch(createIntentUrl, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(intentBody),
      });

      if (!createIntentResponse.ok) {
        throw new Error('Failed to create payment intent');
      }

      const { clientSecret, paymentIntentId } = await createIntentResponse.json();

      const { error: confirmError } = await stripe.confirmCardPayment(clientSecret, {
        payment_method: {
          card: card,
          billing_details: { email: email },
        },
      });

      if (confirmError) {
        throw new Error(confirmError.message);
      }

      const confirmBody = {
        customerEmail: email,
        paymentIntentId: paymentIntentId,
        amount: product.price,
        currency: 'usd',
      };
      if (isVideo) {
        confirmBody.videoId = product.id;
      } else {
        confirmBody.photoId = product.id;
      }

      const confirmResponse = await fetch(confirmUrl, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(confirmBody),
      });

      if (!confirmResponse.ok) {
        throw new Error('Failed to confirm purchase');
      }

      const result = await confirmResponse.json();

      if (result.success) {
        onSuccess(result.purchase);
        if (result.purchase && result.purchase.status === 'COMPLETED' && result.purchase.downloadToken) {
          fetch(`${downloadUrl}?downloadToken=${result.purchase.downloadToken}`)
            .then(res => {
              if (!res.ok) throw new Error('Download failed: ' + res.status);
              return res.blob();
            })
            .then(blob => {
              const url = window.URL.createObjectURL(blob);
              const link = document.createElement('a');
              link.href = url;
              link.download = product.title || (isVideo ? 'video.mp4' : 'photo.jpg');
              document.body.appendChild(link);
              link.click();
              document.body.removeChild(link);
              window.URL.revokeObjectURL(url);
            })
            .catch(err => {
              console.error('Download error:', err);
            });
        } else {
          alert('Payment is not confirmed yet. Please wait for confirmation email.');
        }
      } else {
        throw new Error(result.error || 'Purchase failed');
      }

    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      backgroundColor: 'rgba(0,0,0,0.5)',
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      zIndex: 1000
    }}>
      <div style={{
        backgroundColor: 'white',
        padding: '2rem',
        borderRadius: '8px',
        maxWidth: '500px',
        width: '90%',
        maxHeight: '90vh',
        overflowY: 'auto'
      }}>
        <h3>Complete Your Purchase</h3>
        <p><strong>{product.title}</strong></p>
        <p>Price: <strong>${product.price}</strong></p>

        {error && (
          <div style={{
            backgroundColor: '#fee',
            color: '#c33',
            padding: '0.75rem',
            borderRadius: '4px',
            marginBottom: '1rem'
          }}>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: '1rem' }}>
            <label>Email:</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              style={{
                width: '100%',
                padding: '0.75rem',
                marginTop: '0.5rem',
                border: '1px solid #ddd',
                borderRadius: '4px',
                fontSize: '16px',
                backgroundColor: userEmail ? '#f5f5f5' : 'white'
              }}
              placeholder="Enter your email"
              readOnly={!!userEmail}
              required
            />
          </div>

          <div style={{ marginBottom: '1rem' }}>
            <label>Card Details:</label>
            <div
              id="card-element"
              style={{
                padding: '0.75rem',
                marginTop: '0.5rem',
                border: '1px solid #ddd',
                borderRadius: '4px',
                backgroundColor: 'white'
              }}
            />
          </div>

          <div style={{ display: 'flex', gap: '1rem', justifyContent: 'flex-end' }}>
            <button
              type="button"
              onClick={onCancel}
              style={{
                padding: '0.75rem 1.5rem',
                border: '1px solid #ddd',
                backgroundColor: 'white',
                borderRadius: '4px',
                cursor: 'pointer',
                color: '#333'
              }}
              disabled={loading}
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading || !stripe}
              style={{
                padding: '0.75rem 1.5rem',
                backgroundColor: loading ? '#ccc' : '#6772e5',
                color: 'white',
                border: 'none',
                borderRadius: '4px',
                cursor: loading ? 'not-allowed' : 'pointer',
                fontSize: '16px'
              }}
            >
              {loading ? 'Processing...' : `Pay $${product.price}`}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default StripePayment;
