import React, { useState, useRef, useEffect } from 'react';
import { Box, TextField, Button, Typography, Dialog, DialogTitle, DialogContent, DialogActions } from '@mui/material';
import { API_ENDPOINT } from '../config';

function Login({ onLogin }) {
  const [pin, setPin] = useState(['', '', '', '', '', '', '', '']);
  const [error, setError] = useState('');
  const inputRefs = useRef([]);

  useEffect(() => {
    if (inputRefs.current[0]) {
      inputRefs.current[0].focus();
    }
  }, []);

  const handlePinChange = (index, value) => {
    if (!/^\d*$/.test(value)) return;
    
    const newPin = [...pin];
    newPin[index] = value;
    setPin(newPin);

    // Move to next input if value is entered
    if (value && index < 7) {
      inputRefs.current[index + 1].focus();
    }
  };

  const handleKeyDown = (index, e) => {
    if (e.key === 'Backspace' && !pin[index] && index > 0) {
      inputRefs.current[index - 1].focus();
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    const pinString = pin.join('');
    if (pinString.length !== 8) return;

    try {
      const response = await fetch(`${API_ENDPOINT}/api/auth/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ pin: pinString }),
      });

      if (!response.ok) {
        throw new Error('Invalid PIN');
      }

      const data = await response.json();
      onLogin(data.role);
    } catch (err) {
      setError('Invalid PIN. Please try again.');
      setPin(['', '', '', '', '', '', '', '']);
      inputRefs.current[0].focus();
    }
  };

  return (
    <Dialog 
      open={true} 
      onClose={() => onLogin(null)}
      PaperProps={{
        sx: {
          bgcolor: 'rgba(0, 0, 0, 0.8)',
          color: '#fff',
          borderRadius: '12px',
          border: '1px solid rgba(255, 255, 255, 0.1)',
        }
      }}
    >
      <DialogTitle sx={{ 
        fontFamily: "'Playfair Display', serif",
        fontSize: '2rem',
        textAlign: 'center',
        color: '#fff',
        borderBottom: '1px solid rgba(255, 255, 255, 0.1)',
        pb: 2
      }}>
        Staff Login
      </DialogTitle>
      <DialogContent sx={{ pt: 3 }}>
        <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
          <Box sx={{ display: 'flex', gap: 1, justifyContent: 'center' }}>
            {pin.map((digit, index) => (
              <TextField
                key={index}
                inputRef={el => inputRefs.current[index] = el}
                value={digit}
                onChange={(e) => handlePinChange(index, e.target.value)}
                onKeyDown={(e) => handleKeyDown(index, e)}
                type="text"
                inputProps={{
                  maxLength: 1,
                  style: { 
                    width: '40px',
                    height: '40px',
                    padding: '0',
                    textAlign: 'center',
                    fontSize: '1.5rem',
                    color: '#fff',
                    fontFamily: "'Quicksand', sans-serif"
                  }
                }}
                sx={{
                  '& .MuiOutlinedInput-root': {
                    '& fieldset': {
                      borderColor: 'rgba(255, 255, 255, 0.3)',
                    },
                    '&:hover fieldset': {
                      borderColor: 'rgba(255, 255, 255, 0.5)',
                    },
                    '&.Mui-focused fieldset': {
                      borderColor: '#81c784',
                    },
                    '& input': {
                      color: '#fff',
                    },
                  },
                }}
              />
            ))}
          </Box>
          {error && (
            <Typography color="error" sx={{ textAlign: 'center', mt: 1 }}>
              {error}
            </Typography>
          )}
        </Box>
      </DialogContent>
      <DialogActions sx={{ 
        p: 2, 
        borderTop: '1px solid rgba(255, 255, 255, 0.1)',
        justifyContent: 'center'
      }}>
        <Button
          onClick={handleSubmit}
          disabled={pin.join('').length !== 8}
          sx={{
            bgcolor: '#81c784',
            color: '#fff',
            '&:hover': {
              bgcolor: '#66bb6a',
            },
            fontFamily: "'Quicksand', sans-serif",
            px: 4,
            py: 1,
            borderRadius: '8px',
            textTransform: 'uppercase',
            letterSpacing: '0.1em',
          }}
        >
          Login
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export default Login; 