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
  Stack
} from "@mui/material";
import { API_ENDPOINT } from "../config";
import { Link as RouterLink } from "react-router-dom";

function KitchenPage() {
  const [orders, setOrders] = useState([]);
  const [readyOrders, setReadyOrders] = useState([]);
  const [now, setNow] = useState(new Date());
  const [removedOrders, setRemovedOrders] = useState([]);

  useEffect(() => {
    const interval = setInterval(() => {
      setNow(new Date());
    }, 1000);
    return () => clearInterval(interval);
  }, []);

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

  useEffect(() => {
    fetchOrders();
  }, []);

  useEffect(() => {
    fetchOrders();
    const interval = setInterval(fetchOrders, 10000);
    return () => clearInterval(interval);
  }, []);

  const fetchOrders = async () => {
    try {
      const response = await axios.get("http://localhost:2800/api/orders");
      setOrders(response.data || []);
    } catch (err) {
      console.error("Error fetching orders:", err);
    }
  };

  const sendReadyNotification = async (orderId) => {
    try {
      await axios.post(`${API_ENDPOINT}/api/kitchen/mark-ready/${orderId}`);
      setReadyOrders((prev) => [...prev, orderId]);
    } catch (error) {
      console.error("Error sending notification:", error);
    }
  };

  const handleRemoveOrder = (orderId) => {
    setRemovedOrders((prev) => [...prev, orderId]);
  };

  return (
    <Box
      sx={{
        minHeight: "100vh",
        backgroundColor: "sage",
        backgroundImage:
          'linear-gradient(135deg, rgba(255,245,157,0.2) 25%, transparent 25%),' +
          'linear-gradient(225deg, rgba(255,245,157,0.2) 25%, transparent 25%),' +
          'linear-gradient(45deg, rgba(255,245,157,0.2) 25%, transparent 25%),' +
          'linear-gradient(315deg, rgba(255,245,157,0.2) 25%, #fff8e1 25%)',
        backgroundSize: "40px 40px"
      }}
    >
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Box sx={{ mb: 4, display: "flex", justifyContent: "flex-start" }}>
        <Button
          component={RouterLink}
          to="/"
          variant="contained"
          color="success"
          startIcon={<span role="img" aria-label="home">🏠</span>}
          sx={{ fontWeight: "medium" }}
        >
          Back to Home
        </Button>
      </Box>

      <Typography variant="h4" align="center" sx={{ fontWeight: "bold", mb: 2 }}>
        Kitchen Dashboard
      </Typography>

      <Divider sx={{ mb: 3 }} />

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
                <TableRow sx={{ backgroundColor: "#f5f5f5" }}>
                  <TableCell sx={{ fontWeight: "bold" }}>Order #</TableCell>
                  <TableCell sx={{ fontWeight: "bold" }}>Contents</TableCell>
                  <TableCell sx={{ fontWeight: "bold" }}>Order Placed</TableCell>
                  <TableCell sx={{ fontWeight: "bold" }}>Action</TableCell>
                </TableRow>
              </TableHead>

              <TableBody>
                {orders
                  .filter(order => !removedOrders.includes(order.orderNumber))
                  .map((order) => (
                    <TableRow key={order.orderNumber}>
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
                        {readyOrders.includes(order.orderNumber) ? (
                          <Stack spacing={1}>
                            <Typography color="success.main" fontWeight="medium">
                              Ready ✅
                            </Typography>
                            <Button
                              variant="outlined"
                              color="error"
                              onClick={() => handleRemoveOrder(order.orderNumber)}
                            >
                              Remove
                            </Button>
                          </Stack>
                        ) : (
                          <Button
                            variant="contained"
                            color="success"
                            onClick={() => sendReadyNotification(order.orderNumber)}
                          >
                            Set as Ready
                          </Button>
                        )}
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
