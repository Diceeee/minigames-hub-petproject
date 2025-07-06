import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

const EmailVerification: React.FC = () => {
  const { tokenId } = useParams<{ tokenId: string }>();
  const [status, setStatus] = useState<'pending' | 'success' | 'error'>('pending');
  const [message, setMessage] = useState('Verifying your email...');
  const navigate = useNavigate();

  useEffect(() => {
    if (!tokenId) {
      setStatus('error');
      setMessage('Invalid verification link.');
      return;
    }
    fetch(`/api/email/verification/${tokenId}`)
      .then(res => {
        if (res.ok) {
          setStatus('success');
          setMessage('Your email has been verified! You can now log in.');
          setTimeout(() => navigate('/login'), 2000);
        } else {
          setStatus('error');
          setMessage('Verification failed or link expired.');
        }
      })
      .catch(() => {
        setStatus('error');
        setMessage('Network error.');
      });
  }, [tokenId, navigate]);

  return (
    <div style={{ maxWidth: 400, margin: '2rem auto', padding: '2rem', background: 'white', borderRadius: '1rem', boxShadow: '0 4px 24px rgba(0,0,0,0.08)', textAlign: 'center' }}>
      <h2>Email Verification</h2>
      <div style={{ color: status === 'success' ? 'green' : status === 'error' ? 'red' : '#2d3748', margin: '1rem 0' }}>{message}</div>
    </div>
  );
};

export default EmailVerification; 