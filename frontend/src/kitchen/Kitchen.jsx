import React, { useEffect, useState } from "react";
import axios from "axios";
import {
  Container,
  Typography,
  Paper,
  Table,
  TableHead,
  TableBody,
  Box,
  TableRow,
  TableCell,
  Button,
  Divider,
  Stack,
  Select,
  MenuItem,
  FormControl,
  Chip,
  Alert
} from "@mui/material";
import { alpha } from '@mui/material/styles';
import { API_ENDPOINT } from "../config";
import { Link as RouterLink } from "react-router-dom";

function KitchenPage() {
  const [orders, setOrders] = useState([]);
  const [readyOrders, setReadyOrders] = useState([]);
  const [now, setNow] = useState(new Date());
  const [removedOrders, setRemovedOrders] = useState([]);
  const [error, setError] = useState('');

  // Define available order statuses
  const orderStatuses = [
    'PENDING',
    'IN_PROGRESS',
    'READY_FOR_DELIVERY',
    'COMPLETED',
    'DELIVERED',
    'PROCESSING',
    'CANCELLED',
    'PAID'
  ];

  useEffect(() => {
    const interval = setInterval(() => {
      setNow(new Date());
    }, 1000);
    return () => clearInterval(interval);
  }, []);

  const fetchOrders = async () => {
    try {
      const response = await axios.get(`${API_ENDPOINT}/api/orders`);
      if (Array.isArray(response.data)) {
        // Filter out completed and delivered orders
        const activeOrders = response.data.filter(order => 
          !['COMPLETED', 'DELIVERED', 'PAID'].includes(order.status)
        );
        setOrders(activeOrders);
      } else {
        console.warn("Unexpected response format:", response.data);
        setOrders([]);
      }
    } catch (err) {
      if (err.response?.status === 404) {
        setOrders([]);
      } else {
        console.error("Error fetching orders:", err);
        setError("Failed to load orders");
      }
    }
  };

  // Poll for orders more frequently for real-time updates
  useEffect(() => {
    fetchOrders();
    const interval = setInterval(fetchOrders, 1000); // Poll every second
    return () => clearInterval(interval);
  }, []);

  const updateOrderStatus = async (orderId, newStatus) => {
    try {
      await axios.post(`${API_ENDPOINT}/api/waiters/orders/${orderId}/update-status`, {
        status: newStatus.trim().toUpperCase()
      }, {
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        }
      });
      
      // If marking as ready, send notification
      if (newStatus === 'READY_FOR_DELIVERY') {
        await sendReadyNotification(orderId);
      }
      
      fetchOrders(); // Refresh the orders list
      setError(''); // Clear any previous errors
    } catch (error) {
      console.error("Error updating order status:", error);
      const errorMessage = error.response?.data || error.message;
      setError(`Failed to update order status: ${errorMessage}`);
    }
  };

  const sendReadyNotification = async (orderId) => {
    try {
      await axios.post(`${API_ENDPOINT}/api/kitchen/mark-ready/${orderId}`);
      setReadyOrders((prev) => [...prev, orderId]);
    } catch (error) {
      console.error("Error sending notification:", error);
      setError("Failed to send ready notification");
    }
  };

  const handleRemoveOrder = (orderId) => {
    setRemovedOrders((prev) => [...prev, orderId]);
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'PENDING':
        return 'warning';
      case 'IN_PROGRESS':
      case 'PROCESSING':
        return 'info';
      case 'READY_FOR_DELIVERY':
        return 'primary';
      case 'COMPLETED':
      case 'DELIVERED':
        return 'success';
      case 'CANCELLED':
        return 'error';
      default:
        return 'default';
    }
  };

  const minutesSincePlaced = (placedTime) => {
    if (!placedTime) return "N/A";
    const placed = new Date(placedTime);
    const diffMs = now - placed;

    if (diffMs < 0) return "N/A";

    const totalSeconds = Math.floor(diffMs / 1000);
    const minutes = Math.floor(totalSeconds / 60);
    const seconds = totalSeconds % 60;

    return `${minutes} min ${seconds} s ago`;
  };

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
      <Container maxWidth="md" sx={{ py: 4 }}>
        <Box
          sx={{
            mb: 4,
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center"
          }}
        >
          <Typography variant="h4" sx={(theme) => ({
              fontWeight: "bold",
              color: theme.palette.secondary.main
          })}>
            🌮 Oaxaca Kitchen 🌯
          </Typography>
          <Button
            component={RouterLink}
            to="/"
            variant="contained"
            color="success"
            endIcon={<span role="img" aria-label="home">🏠</span>}
            sx={{ fontWeight: "medium" }}
          >
            Back to Home
          </Button>
        </Box>

        <Typography
          variant="h5"
          sx={(theme) => ({
            fontWeight: "bold",
            color: theme.palette.primary.main,
            display: "flex",
            alignItems: "center",
            gap: 1,
            mb: 2
          })}
        >
          🍴 Kitchen Dashboard 🌶️
        </Typography>

        <Divider sx={{ mb: 3 }} />

        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        <Paper elevation={4} sx={{ p: 3 }}>
          <Typography variant="h6" gutterBottom>
            Active Orders
          </Typography>

          {orders.length === 0 ? (
            <Typography variant="body1">No orders found.</Typography>
          ) : (
            <Box sx={{ overflowX: "auto" }}>
              <Table>
                <TableHead>
                  <TableRow sx={(theme) => ({
                    backgroundColor: theme.palette.action.hover
                  })}>
                    <TableCell sx={{ fontWeight: "bold", color: "text.primary" }}>#️⃣ Order</TableCell>
                    <TableCell sx={{ fontWeight: "bold", color: "text.primary" }}>🥙 Contents</TableCell>
                    <TableCell sx={{ fontWeight: "bold", color: "text.primary" }}>⏰ Time Placed</TableCell>
                    <TableCell sx={{ fontWeight: "bold", color: "text.primary" }}>📋 Status</TableCell>
                    <TableCell sx={{ fontWeight: "bold", color: "text.primary" }}>✔️ Actions</TableCell>
                  </TableRow>
                </TableHead>

                <TableBody>
                  {orders
                    .filter(order => !removedOrders.includes(order.orderNumber))
                    .map((order) => (
                      <TableRow
                        key={order.orderNumber}
                        sx={(theme) => ({
                          backgroundColor: readyOrders.includes(order.orderNumber)
                            ? alpha(theme.palette.success.main, 0.1)
                            : alpha(theme.palette.error.main, 0.1)
                        })}
                      >
                        <TableCell>{order.orderNumber}</TableCell>
                        <TableCell>
                          <ul style={{ margin: 0, paddingLeft: "1rem" }}>
                            {order.menuItems.map((item, index) => (
                              <li key={index}>
                                <Typography variant="body2">{item.name}</Typography>
                              </li>
                            ))}
                          </ul>
                        </TableCell>
                        <TableCell>
                          <Typography variant="body2" color="text.secondary">
                            {minutesSincePlaced(order.orderPlacedTime)}
                          </Typography>
                        </TableCell>
                        <TableCell>
                          <Chip
                            label={order.status}
                            color={getStatusColor(order.status)}
                            size="small"
                            sx={{ fontWeight: "medium" }}
                          />
                        </TableCell>
                        <TableCell>
                          <Stack spacing={1} direction="row">
                            {order.status === 'PENDING' && (
                              <Button
                                variant="contained"
                                color="primary"
                                onClick={() => updateOrderStatus(order.orderNumber, 'IN_PROGRESS')}
                                startIcon={<span role="img" aria-label="cook">👨‍🍳</span>}
                              >
                                Start Cooking
                              </Button>
                            )}
                            {order.status === 'IN_PROGRESS' && (
                              <Button
                                variant="contained"
                                color="success"
                                onClick={() => updateOrderStatus(order.orderNumber, 'READY_FOR_DELIVERY')}
                                startIcon={<span role="img" aria-label="ready">✅</span>}
                              >
                                Mark Ready for Delivery
                              </Button>
                            )}
                            {/* Only show the full status dropdown for other statuses */}
                            {!['PENDING', 'IN_PROGRESS'].includes(order.status) && (
                              <FormControl size="small" sx={{ minWidth: 150 }}>
                                <Select
                                  value=""
                                  displayEmpty
                                  onChange={(e) => updateOrderStatus(order.orderNumber, e.target.value)}
                                >
                                  <MenuItem value="" disabled>
                                    Update Status
                                  </MenuItem>
                                  {orderStatuses
                                    .filter(status => status !== order.status)
                                    .map(status => (
                                      <MenuItem key={status} value={status}>
                                        {status.replace(/_/g, ' ')}
                                      </MenuItem>
                                    ))
                                  }
                                </Select>
                              </FormControl>
                            )}
                            <Button
                              variant="outlined"
                              color="error"
                              onClick={() => handleRemoveOrder(order.orderNumber)}
                              startIcon={<span role="img" aria-label="remove">🗑️</span>}
                            >
                              Remove
                            </Button>
                          </Stack>
                        </TableCell>
                      </TableRow>
                    ))}
                </TableBody>
              </Table>
            </Box>
          )}
        </Paper>
      </Container>
    </Box>
  );
}

export default KitchenPage;
