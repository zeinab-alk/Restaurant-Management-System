import React, { useState, useEffect } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { Button, Container, Stack, Typography, Alert } from "@mui/material";
import axios from "axios";
import { API_ENDPOINT } from "../config";

const WaiterPage = () => {
  const [orderNotification, setOrderNotification] = useState(null);

  // Poll for latest order every 5 seconds
  useEffect(() => {
    const interval = setInterval(async () => {
      try {
        const response = await axios.get(`${API_ENDPOINT}/api/orders/latest`);
        if (response.data) {
          const { orderNumber, menuItems } = response.data;
          const orderDetails = menuItems.map(item => item.name).join(", ");
          setOrderNotification(`New Order #${orderNumber}: ${orderDetails}`);
        }
      } catch (error) {
        console.error("Error fetching latest order:", error);
      }
    }, 5000);

    return () => clearInterval(interval);
  }, []);

  return (
    <Container>
      <Stack direction="row" spacing={2} sx={{ mb: 4 }}>
        <Button component={RouterLink} to="/" variant="contained" color="success">
          Back to Home
        </Button>
        <Button component={RouterLink} to="/menu" variant="contained" color="primary">
          View Menu
        </Button>
      </Stack>

      <Typography variant="h3" component="h1" gutterBottom>
        Waiter Dashboard
      </Typography>

      <Stack spacing={2} direction="column">
        <Button component={RouterLink} to="/waiter/add-item" variant="contained" color="primary" size="large">
          Add New Menu Item
        </Button>
        <Button component={RouterLink} to="/waiter/edit-items" variant="contained" color="secondary" size="large">
          Change Menu Item
        </Button>
      </Stack>

      {/* Order Notification */}
      {orderNotification && (
        <Alert severity="info" sx={{ mt: 2 }}>
          {orderNotification}
        </Alert>
      )}
    </Container>
  );
};

export default WaiterPage;
