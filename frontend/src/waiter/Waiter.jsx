import React, { useState, useEffect, useCallback } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { Button, Container, Stack, Typography, Alert, Table, TableBody, TableCell, TableRow, TableContainer, TableHead, Paper, Box, Chip } from "@mui/material";
import axios from "axios";
import { API_ENDPOINT } from "../config";
import Collapse from '@mui/material/Collapse';
import IconButton from '@mui/material/IconButton';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp';

const WaiterPage = () => {
  const [orderNotification, setOrderNotification] = useState(null);
  const [helpRequests, setHelpRequests] = useState([]);
  const [helpRequests, setHelpRequests] = useState([]);
  const [orders, setOrders] = useState([]);
  const [error, setError] = useState('');
  const [lastOrderId, setLastOrderId] = useState(null);
  const [expandedOrders, setExpandedOrders] = useState(new Set());
  const [newOrderAlert, setNewOrderAlert] = useState(false);

  // Define available order statuses
  const orderStatuses = [
    'PENDING', 
    'IN_PROGRESS', 
    'READY_FOR_DELIVERY', 
    'COMPLETED', 
    'DELIVERED', 
    'PROCESSING', 
    'CANCELLED'
  ];

  // Define fetchOrders first using useCallback
  const fetchOrders = useCallback(() => {
    setError(''); // Clear any previous errors
    
    axios.get(`${API_ENDPOINT}/api/orders`)
      .then(res => {
        if (Array.isArray(res.data)) {
          const filtered = res.data.filter(order => 
            order.status !== 'Delivered'
          );
          setOrders(filtered);
        } else {
          console.warn("Unexpected response format from orders API:", res.data);
          setOrders([]);
        }
      })
      .catch(err => {
        // Don't show error for 404 (no orders)
        if (err.response && err.response.status === 404) {
          setOrders([]);
          return;
        }
        
        setError('Failed to load orders');
        console.error('Order fetch error:', err);
        // Set empty array on error so UI doesn't break
        setOrders([]);
      });
  }, []);

  // Initial orders fetch on component mount
  useEffect(() => {
    fetchOrders();
    
    // Also fetch orders periodically in case of missed updates
    const ordersInterval = setInterval(fetchOrders, 3000); // Every 3 seconds
    
    return () => clearInterval(ordersInterval);
  }, [fetchOrders]);

  // Poll for latest order more frequently (2 seconds is a good balance)
  useEffect(() => {
    const fetchLatestOrder = async () => {
      try {
        const response = await axios.get(`${API_ENDPOINT}/api/orders/latest`);
        // Only update if we have a valid response with data
        if (response.data && response.data.id !== lastOrderId) {
          const { orderNumber, menuItems, id } = response.data;
          const orderDetails = menuItems.map(item => item.name).join(", ");
          setOrderNotification(`New Order #${orderNumber}: ${orderDetails}`);
          setLastOrderId(id);
          
          // Immediately fetch all orders when a new one is detected
          fetchOrders();
        }
      } catch (error) {
        // Don't show errors for 404 (no orders) or 204 (no content)
        if (error.response && error.response.status !== 404 && error.response.status !== 204) {
          console.error("Error fetching latest order:", error);
        }
      }
    };

    // Run immediately on mount
    fetchLatestOrder();
    
    // Poll every 2 seconds for a good balance of responsiveness and efficiency
    const interval = setInterval(fetchLatestOrder, 2000);
    return () => clearInterval(interval);
  }, [lastOrderId, fetchOrders]);

  // Also set the help requests polling interval to 2 seconds
  useEffect(() => {
    const fetchHelpRequests = async () => {
      try {
        const response = await axios.get(`${API_ENDPOINT}/api/help-requests`);
        if (Array.isArray(response.data)) {
          setHelpRequests(response.data);
        } else {
          console.error("Unexpected response format:", response.data);
          // Set empty array instead of showing error
          setHelpRequests([]);
        }
      } catch (error) {
        // Don't log 404 errors (no help requests)
        if (error.response && error.response.status !== 404) {
          console.error("Error fetching help requests:", error);
        }
        // Set empty array on error so UI doesn't break
        setHelpRequests([]);
      }
    };

    // Run immediately on mount
    fetchHelpRequests();
    
    // Poll every 2 seconds
    const helpInterval = setInterval(fetchHelpRequests, 2000);

    return () => clearInterval(helpInterval);
  }, []);

  // Enhanced delivery handler
  const markDelivered = (orderId) => {
    axios.post(`${API_ENDPOINT}/api/orders/${orderId}/mark-delivered`)
      .then(() => {
        fetchOrders(); // Refresh list
        setOrderNotification(`Order ${orderId} marked delivered`);
      })
      .catch(err => setError('Failed to update order status'));
  };

  const toggleExpansion = (orderId) => {
    setExpandedOrders(prev => {
      const next = new Set(prev);
      next.has(orderId) ? next.delete(orderId) : next.add(orderId);
      return next;
    });
  };
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

  // Add resolveRequest function
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
    <Container sx={{ py: 4 }}>
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

      <Box sx={{ 
        display: 'flex', 
        justifyContent: 'center', 
        gap: 2, 
        mb: 4,
        '& .MuiButton-root': {
          minWidth: 0,
          width: 150,
          height: 150,
          p: 1.5,
          borderRadius: 2,
          flexDirection: 'column'
        }
      }}>
        <Button 
          component={RouterLink} 
          to="/waiter/add-item" 
          variant="contained" 
          color="primary"
          sx={{ boxShadow: 3 }}
        >
          <Typography variant="h6" sx={{ fontWeight: 600 }}>Add</Typography>
          <Typography variant="caption">New Item</Typography>
        </Button>
        
        <Button 
          component={RouterLink} 
          to="/waiter/edit-items" 
          variant="contained" 
          color="secondary"
          sx={{ boxShadow: 3 }}
        >
          <Typography variant="h6" sx={{ fontWeight: 600 }}>Edit</Typography>
          <Typography variant="caption">Menu Items</Typography>
        </Button>
      </Box>

      {/* Add error display */}
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      {/* Custom Action Alert */}
      {orderNotification && (
        <Alert
          severity="info"
          sx={{ mb: 3, '& .MuiAlert-action': { pt: 0.5 } }}
          action={
            <Button
              color="inherit"
              size="small"
              onClick={() => setOrderNotification(null)}
              sx={{ textTransform: 'none' }}
            >
              DISMISS
            </Button>
          }
        >
          {orderNotification}
        </Alert>

        
      )}

      {/* Updated Table Action */}
      <TableContainer component={Paper} sx={{ 
        maxWidth: '800px',
        mx: 'auto',
        boxShadow: 3
      }}>
        <Table size="small">
          <TableHead>
            <TableRow sx={{ bgcolor: 'background.default' }}>
              <TableCell sx={{ width: 40 }}></TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>Order #</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>Items</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>Status</TableCell>
              <TableCell align="center" sx={{ fontWeight: 'bold' }}>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {orders.map((order, index) => {
              const orderId = order.id || `temp-${index}`;
              return (
                <React.Fragment key={`order-${orderId}`}>
                  <TableRow 
                    hover 
                    onClick={() => toggleExpansion(orderId)}
                    sx={{ cursor: 'pointer' }}
                  >
                    <TableCell>
                      <IconButton size="small">
                        {expandedOrders.has(orderId) ? 
                          <KeyboardArrowUpIcon /> : 
                          <KeyboardArrowDownIcon />
                        }
                      </IconButton>
                    </TableCell>
                    <TableCell>#{order.orderNumber}</TableCell>
                    <TableCell sx={{ maxWidth: 200 }}>
                      <Typography noWrap>
                        {order.menuItems?.length} items
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Chip 
                        label={order.status} 
                        color={order.status === 'Pending' ? 'warning' : 'success'}
                        size="small"
                      />
                    </TableCell>
                    <TableCell align="center" sx={{ width: 140 }}>
                      <Button
                        variant="contained"
                        color="success"
                        onClick={(e) => {
                          e.stopPropagation();
                          markDelivered(orderId);
                        }}
                        sx={{
                          px: 1.5,
                          py: 0.5,
                          fontSize: '0.75rem',
                          fontWeight: 600,
                          letterSpacing: 0.5,
                          borderRadius: 1
                        }}
                      >
                        MARK DELIVERED
                      </Button>
                    </TableCell>
                  </TableRow>

                  <TableRow>
                    <TableCell colSpan={6} sx={{ p: 0, border: 0 }}>
                      <Collapse in={expandedOrders.has(orderId)}>
                        <Box sx={{ 
                          p: 2, 
                          bgcolor: 'background.paper',
                          borderTop: '1px solid',
                          borderColor: 'divider'
                        }}>
                          <Typography variant="subtitle1" gutterBottom>
                            Order Details
                          </Typography>
                          <Table size="small">
                            <TableHead>
                              <TableRow>
                                <TableCell>Item</TableCell>
                                <TableCell align="right">Quantity</TableCell>
                                <TableCell>Notes</TableCell>
                              </TableRow>
                            </TableHead>
                            <TableBody>
                              {order.menuItems?.map((item, itemIndex) => {
                                const itemSafeId = item.id || `temp-item-${itemIndex}-${Date.now()}`;
                                return (
                                  <TableRow key={`${orderId}-${itemSafeId}`}>
                                    <TableCell>{item.name}</TableCell>
                                    <TableCell align="right">{item.quantity || 1}</TableCell>
                                    <TableCell sx={{ fontStyle: 'italic' }}>
                                      {item.notes || 'No special requests'}
                                    </TableCell>
                                  </TableRow>
                                );
                              })}
                            </TableBody>
                          </Table>
                        </Box>
                      </Collapse>
                    </TableCell>
                  </TableRow>
                </React.Fragment>
              );
            })}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Status Legend */}
      <Stack 
        direction="row" 
        spacing={2} 
        justifyContent="center" 
        sx={{ mt: 3, color: 'text.secondary' }}
      >
        <Box display="flex" alignItems="center">
          <Box sx={{ 
            width: 12, 
            height: 12, 
            bgcolor: 'warning.main', 
            borderRadius: '50%', 
            mr: 1 
          }} />
          Pending
        </Box>
        <Box display="flex" alignItems="center">
          <Box sx={{ 
            width: 12, 
            height: 12, 
            bgcolor: 'success.main', 
            borderRadius: '50%', 
            mr: 1 
          }} />
          Delivered
        </Box>
      </Stack>
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