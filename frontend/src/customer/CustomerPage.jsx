import React from "react";
import { API_ENDPOINT } from "../config";
import { Container, Typography, Paper, Box, Button } from "@mui/material";
import OrderTracking from "../ordertracking/OrderTracking.jsx";
import { Link as RouterLink } from 'react-router-dom';

function CustomerPage() {
  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Box sx={{ mb: 4, display: 'flex', justifyContent: 'flex-start' }}>
        <Button
          component={RouterLink}
          to="/"
          variant="contained"
          color="primary"
          startIcon={<span role="img" aria-label="home">🏠</span>}
          sx={{ fontWeight: 'medium' }}
        >
          Back to Home
        </Button>
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
