import React from "react";
import { API_ENDPOINT } from "../config";
import { Container, Typography, Paper, Box, Button } from "@mui/material";
import OrderTracking from "../ordertracking/OrderTracking.jsx";
import { Link as RouterLink } from 'react-router-dom';
import { useTheme } from "@mui/material/styles";

function CustomerPage() {
    const theme = useTheme();
  return (
      <Box
          sx={(theme) => ({
              minHeight: "100vh",
              backgroundColor: theme.palette.background.paper,
              backgroundImage: theme.palette.mode === 'light'
                  ? 'linear-gradient(135deg, rgba(0,0,0,0.05) 25%, transparent 25%),' +
                  'linear-gradient(225deg, rgba(0,0,0,0.05) 25%, transparent 25%),' +
                  'linear-gradient(45deg, rgba(0,0,0,0.05) 25%, transparent 25%),' +
                  `linear-gradient(315deg, rgba(0,0,0,0.05) 25%, ${theme.palette.background.paper} 25%)`
                  : 'linear-gradient(135deg, rgba(255,255,255,0.05) 25%, transparent 25%),' +
                  'linear-gradient(225deg, rgba(255,255,255,0.05) 25%, transparent 25%),' +
                  'linear-gradient(45deg, rgba(255,255,255,0.05) 25%, transparent 25%),' +
                  `linear-gradient(315deg, rgba(255,255,255,0.05) 25%, ${theme.palette.background.paper} 25%)`,
              backgroundSize: "40px 40px",
          })}
      >
    <Container maxWidth="md" sx={{ py: 6 }}>
        <Box sx={{
            display: 'flex',
            justifyContent: 'flex-end',
            mb: 4
        }}>
            <Box sx={{
                display: 'inline-flex',
                alignItems: 'center',
                justifyContent: 'center',
                p: 1,
                borderRadius: '999px',
                backgroundColor: '#f1a165', // peach/orange outer pill
                boxShadow: '0px 2px 6px rgba(0,0,0,0.3)',
            }}>
                <Button
                    component={RouterLink}
                    to="/"
                    startIcon={<span role="img" aria-label="home">🏠</span>}
                    sx={{
                        px: 4,
                        py: 1.2,
                        fontWeight: 'bold',
                        fontSize: '1rem',
                        fontFamily: "'Quicksand', sans-serif",
                        backgroundColor: '#87c87b', // green inner button
                        color: '#2c1e0f',
                        borderRadius: '999px',
                        textTransform: 'none',
                        boxShadow: 'none',
                        '&:hover': {
                            backgroundColor: '#7fc572',
                            boxShadow: 'none',
                        },
                    }}
                >
                    Back to Home
                </Button>
            </Box>
        </Box>

      <Typography
        variant="h3"
        align="center"
        sx={{
            fontWeight: 'bold',
            fontFamily: "'Playfair Display, serif",
            color: theme.palette.mode === 'light' ? '#111' : '#fff',
            mb: 4,
            textShadow: theme.palette.mode === 'dark' ? '1px 1px #000' : 'none',
        }}
      >
        Customer Dashboard
      </Typography>

        <Box
            sx={{
                background: 'linear-gradient(to right, #fff3dc, #ffecc7)',
                borderRadius: '48px',
                boxShadow: '0 10px 25px rgba(0, 0, 0, 0.15)',
                px: { xs: 3, md: 6 },
                py: { xs: 4, md: 5 },
                textAlign: 'center',
                mt: 4,
            }}
        >
            <Typography
                variant="h6"
                sx={{
                    fontWeight: 'bold',
                    fontFamily: "'Quicksand', sans-serif",
                    mb: 2,
                    color: '#1a1a1a',
                }}
            >
                📦 Track Your Order
            </Typography>

            <OrderTracking />
        </Box>
    </Container>
      </Box>
  );
}

export default CustomerPage;
