import React, { useState, useEffect } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { Button, Container, Stack, Typography, Alert, Table, TableBody, TableCell, TableRow, TableContainer, TableHead, Paper, Box, Chip, Select, MenuItem, FormControl, Snackbar } from "@mui/material";
import axios from "axios";
import { API_ENDPOINT } from "../config";
import Collapse from '@mui/material/Collapse';
import IconButton from '@mui/material/IconButton';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp';

const WaiterPage = () => {
  const [orderNotification, setOrderNotification] = useState(null);
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

  // Poll for latest order every 5 seconds
  useEffect(() => {
    const interval = setInterval(async () => {
      try {
        const response = await axios.get(`${API_ENDPOINT}/api/orders/latest`);
        if (response.data && response.data.orderNumber !== lastOrderId) {
          const { orderNumber, menuItems } = response.data;
          // Get a list of item names
          const orderDetails = menuItems.map(item => item.name).join(", ");
          setOrderNotification(`New Order #${orderNumber}: ${orderDetails}`);
          setLastOrderId(orderNumber);
          setNewOrderAlert(true);
          
          // Auto-refresh orders when a new one arrives
          fetchOrders();
        }
      } catch (error) {
        console.error("Error fetching latest order:", error);
      }
    }, 5000);

    return () => clearInterval(interval);
  }, [lastOrderId]);

  // Enhanced fetch with error handling
  const fetchOrders = () => {
    axios.get(`${API_ENDPOINT}/api/orders`)
      .then(res => {
        setOrders(res.data);
      })
      .catch(err => {
        setError('Failed to load orders');
        console.error('Order fetch error:', err);
      });
  };

  // Fetch orders on component mount and every 30 seconds
  useEffect(() => {
    fetchOrders();
    
    // Set up interval to refresh orders every 30 seconds
    const refreshInterval = setInterval(fetchOrders, 30000);
    
    // Clean up interval on component unmount
    return () => clearInterval(refreshInterval);
  }, []);

  // Enhanced status update handler
  const updateOrderStatus = (orderId, newStatus) => {
    console.log(`Attempting to update order ${orderId} to status: ${newStatus}`);
    
    // Format the payload exactly as expected by the backend
    const payload = { 
      status: newStatus.trim().toUpperCase() // Ensure it matches the Java enum format
    };
    
    console.log('Sending payload:', payload);
    
    // Add explicit headers and prevent browsers from caching
    axios.post(`${API_ENDPOINT}/api/waiters/orders/${orderId}/update-status`, payload, {
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'Cache-Control': 'no-cache',
        'Pragma': 'no-cache'
      }
    })
      .then((response) => {
        console.log('Update successful:', response);
        fetchOrders(); // Refresh list
        setOrderNotification(`Order #${orderId} updated to ${newStatus}`);
      })
      .catch(err => {
        console.error('Status update error:', err);
        console.error('Response data:', err.response?.data);
        console.error('Response status:', err.response?.status);
        console.error('Response headers:', err.response?.headers);
        
        // More specific error message based on the status code
        let errorMsg = 'Failed to update order status';
        
        if (err.response) {
          if (err.response.status === 403) {
            errorMsg += ': Access forbidden. You may not have permission to perform this action.';
          } else if (err.response.status === 404) {
            errorMsg += ': Order not found';
          } else if (err.response.status === 400) {
            errorMsg += ': Invalid request format';
          } else {
            errorMsg += ': ' + (err.response.data || err.message);
          }
        } else {
          errorMsg += ': ' + err.message;
        }
        
        setError(errorMsg);
      });
  };

  // Legacy handler for backward compatibility
  const markDelivered = (orderId) => {
    updateOrderStatus(orderId, 'DELIVERED');
  };

  const toggleExpansion = (orderId) => {
    setExpandedOrders(prev => {
      const next = new Set(prev);
      next.has(orderId) ? next.delete(orderId) : next.add(orderId);
      return next;
    });
  };

  // Helper function to get color based on status
  const getStatusColor = (status) => {
    switch(status) {
      case 'PENDING':
      case 'PROCESSING':
        return 'warning';
      case 'IN_PROGRESS':
        return 'info';
      case 'READY_FOR_DELIVERY':
        return 'secondary';
      case 'COMPLETED':
      case 'DELIVERED':
        return 'success';
      case 'CANCELLED':
        return 'error';
      default:
        return 'default';
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
        <Button 
          variant="contained" 
          color="primary" 
          onClick={fetchOrders}
          sx={{ ml: 'auto' }}
        >
          Refresh Orders
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

      {/* New Order Notification */}
      <Snackbar
        open={newOrderAlert}
        autoHideDuration={6000}
        onClose={() => setNewOrderAlert(false)}
        message="New order received! The list has been updated."
      />

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
            {orders.map((order) => {
              // Use orderNumber as the key and ID for all operations
              const orderId = order.orderNumber;
              
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
                        color={getStatusColor(order.status)}
                        size="small"
                      />
                    </TableCell>
                    <TableCell align="center" sx={{ width: 180 }}>
                      <FormControl 
                        size="small" 
                        sx={{ minWidth: 150 }}
                        onClick={(e) => e.stopPropagation()}
                      >
                        <Select
                          value=""
                          displayEmpty
                          onChange={(e) => {
                            updateOrderStatus(orderId, e.target.value);
                          }}
                          sx={{ 
                            fontSize: '0.75rem',
                            height: 32
                          }}
                        >
                          <MenuItem value="" disabled>
                            <em>Update Status</em>
                          </MenuItem>
                          {orderStatuses.map(status => (
                            <MenuItem 
                              key={status} 
                              value={status}
                              disabled={order.status === status}
                            >
                              {status.replace(/_/g, ' ')}
                            </MenuItem>
                          ))}
                        </Select>
                      </FormControl>
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
                                return (
                                  <TableRow key={`${orderId}-item-${itemIndex}`}>
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
        sx={{ mt: 3, color: 'text.secondary', flexWrap: 'wrap' }}
      >
        <Box display="flex" alignItems="center" sx={{ m: 1 }}>
          <Box sx={{ 
            width: 12, 
            height: 12, 
            bgcolor: 'warning.main', 
            borderRadius: '50%', 
            mr: 1 
          }} />
          Pending
        </Box>
        <Box display="flex" alignItems="center" sx={{ m: 1 }}>
          <Box sx={{ 
            width: 12, 
            height: 12, 
            bgcolor: 'info.main', 
            borderRadius: '50%', 
            mr: 1 
          }} />
          In Progress
        </Box>
        <Box display="flex" alignItems="center" sx={{ m: 1 }}>
          <Box sx={{ 
            width: 12, 
            height: 12, 
            bgcolor: 'secondary.main', 
            borderRadius: '50%', 
            mr: 1 
          }} />
          Ready For Delivery
        </Box>
        <Box display="flex" alignItems="center" sx={{ m: 1 }}>
          <Box sx={{ 
            width: 12, 
            height: 12, 
            bgcolor: 'success.main', 
            borderRadius: '50%', 
            mr: 1 
          }} />
          Delivered
        </Box>
        <Box display="flex" alignItems="center" sx={{ m: 1 }}>
          <Box sx={{ 
            width: 12, 
            height: 12, 
            bgcolor: 'error.main', 
            borderRadius: '50%', 
            mr: 1 
          }} />
          Cancelled
        </Box>
      </Stack>
    </Container>
  );
};

export default WaiterPage;
