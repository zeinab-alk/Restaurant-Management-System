import React from "react";
import { API_ENDPOINT } from "../config";
import { Container, Typography, Paper, Box, Button } from "@mui/material";
import OrderTracking from "../ordertracking/OrderTracking.jsx";
import { Link as RouterLink } from 'react-router-dom';

function CustomerPage() {
  return (
    <Container maxWidth="md" sx={{ py: 6 }}>
        <Box sx={{
            display: 'flex',
            justifyContent: 'flex-end',
            mb: 3
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
        variant="h4"
        align="center"
        sx={{ fontWeight: 'bold', mb: 3 }}
      >
        Customer Dashboard
      </Typography>

      <Paper elevation={3} sx={{ p: 3 }}>
        <Typography variant="h6" gutterBottom>
          Track Your Order
        </Typography>

        {/* Embed the OrderTracking component */}
        <OrderTracking />
      </Paper>
    </Container>
  );
}

export default CustomerPage;
