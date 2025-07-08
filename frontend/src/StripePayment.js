import React, { useState, useEffect } from 'react';

const StripePayment = ({ photo, onSuccess, onCancel }) => {
  const [stripe, setStripe] = useState(null);
  const [elements, setElements] = useState(null);
  const [card, setCard] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [email, setEmail] = useState('');
  const [publishableKey, setPublishableKey] = useState('');

  useEffect(() => {
    // Отримати публічний ключ Stripe
    fetch('http://localhost:8080/api/stripe/config')
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
      setElements(elementsInstance);
      
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
      // Створити платіжний інтент
      const createIntentResponse = await fetch('http://localhost:8080/api/stripe/create-payment-intent', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          amount: photo.price,
          currency: 'usd',
          description: `Purchase: ${photo.title}`,
        }),
      });

      if (!createIntentResponse.ok) {
        throw new Error('Failed to create payment intent');
      }

      const { clientSecret, paymentIntentId } = await createIntentResponse.json();

      // Підтвердити платіж
      const { error: confirmError } = await stripe.confirmCardPayment(clientSecret, {
        payment_method: {
          card: card,
          billing_details: {
            email: email,
          },
        },
      });

      if (confirmError) {
        throw new Error(confirmError.message);
      }

      // Підтвердити покупку на сервері
      const confirmResponse = await fetch('http://localhost:8080/api/stripe/confirm-payment', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          photoId: photo.id,
          customerEmail: email,
          paymentIntentId: paymentIntentId,
          amount: photo.price,
          currency: 'usd',
        }),
      });

      if (!confirmResponse.ok) {
        throw new Error('Failed to confirm purchase');
      }

      const result = await confirmResponse.json();
      
      if (result.success) {
        onSuccess(result.purchase);
        console.log('purchase:', result.purchase);
        if (result.purchase && result.purchase.status === 'COMPLETED' && result.purchase.downloadToken) {
          // Download via backend
          fetch(`http://localhost:8080/api/photo-purchases/download-file?downloadToken=${result.purchase.downloadToken}`)
            .then(res => {
              console.log('Download request status:', res.status);
              if (!res.ok) throw new Error('Download failed: ' + res.status);
              return res.blob();
            })
            .then(blob => {
              const url = window.URL.createObjectURL(blob);
              const link = document.createElement('a');
              link.href = url;
              link.download = photo.title || 'photo.jpg';
              document.body.appendChild(link);
              link.click();
              document.body.removeChild(link);
              window.URL.revokeObjectURL(url);
            })
            .catch(err => {
              console.error('Download error:', err);
            });
        } else {
          // Not completed yet
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
        <p><strong>{photo.title}</strong></p>
        <p>Price: <strong>${photo.price}</strong></p>
        
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
                fontSize: '16px'
              }}
              placeholder="Enter your email"
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
              {loading ? 'Processing...' : `Pay $${photo.price}`}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

function triggerDownload(url, filename) {
  const link = document.createElement('a');
  link.href = url;
  link.download = filename || 'photo';
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}

export default StripePayment; 