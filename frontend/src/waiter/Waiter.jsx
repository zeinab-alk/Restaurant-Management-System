import React, { useState, useEffect } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { Button, Container, Stack, Typography, Alert } from "@mui/material";
import axios from "axios";
import { API_ENDPOINT } from "../config";

const WaiterPage = () => {
  const [orderNotification, setOrderNotification] = useState(null);
  const [helpRequests, setHelpRequests] = useState([]);

  useEffect(() => {
    const orderInterval = setInterval(async () => {
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

    return () => clearInterval(orderInterval);
  }, []);

  useEffect(() => {
    const fetchHelpRequests = async () => {
      try {
        const response = await axios.get(`${API_ENDPOINT}/api/help-requests`);
        if (Array.isArray(response.data)) {
          setHelpRequests(response.data);
        } else {
          console.error("Unexpected response format:", response.data);
        }
      } catch (error) {
        console.error("Error fetching help requests:", error);
      }
    };

    fetchHelpRequests();
    const helpInterval = setInterval(fetchHelpRequests, 5000);

    return () => clearInterval(helpInterval);
  }, []);

  const resolveRequest = async (id) => {
    try {
      // Optimistically update UI
      setHelpRequests((prevRequests) =>
        prevRequests.map((req) =>
          req.id === id ? { ...req, resolved: true } : req
        )
      );
      
      // Send API request
      const response = await axios.put(`${API_ENDPOINT}/api/help-requests/${id}/resolve`);

      if (response.status !== 200) {
        throw new Error("Failed to mark as resolved.");
      }
    } catch (error) {
      console.error("Error resolving help request:", error);
      
      // Revert UI change if API call fails
      setHelpRequests((prevRequests) =>
        prevRequests.map((req) =>
          req.id === id ? { ...req, resolved: false } : req
        )
      );
    }
  };

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

      {orderNotification && (
        <Alert severity="info" sx={{ mt: 2 }}>
          {orderNotification}
        </Alert>
      )}

      <Typography variant="h4" sx={{ mt: 4 }}>
        Active Help Requests
      </Typography>
      {helpRequests.length === 0 ? (
        <Alert severity="success" sx={{ mt: 2 }}>
          No active help requests.
        </Alert>
      ) : (
        helpRequests.map((req) => (
          <Alert
            key={req.id}
            severity={req.resolved ? "success" : "warning"}
            sx={{ mt: 2 }}
            action={
              <Button
                onClick={() => resolveRequest(req.id)}
                variant="contained"
                color="success"
                disabled={req.resolved}
              >
                {req.resolved ? "Resolved" : "Mark as Resolved"}
              </Button>
            }
          >
            {req.resolved 
              ? `Table ${req.tableNumber} - Resolved` 
              : `⚠️ Table ${req.tableNumber} needs assistance!`}
          </Alert>
        ))
      )}
    </Container>
  );
};

export default WaiterPage;