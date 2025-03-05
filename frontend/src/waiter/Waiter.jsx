import React, { useState, useEffect } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { Button, Container, Stack, Typography, Alert, Table, TableBody, TableCell, TableRow, TableContainer, TableHead, Paper, Box, Chip } from "@mui/material";
import axios from "axios";
import { API_ENDPOINT } from "../config";
import Collapse from '@mui/material/Collapse';
import IconButton from '@mui/material/IconButton';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp';
import { TextField } from "@mui/material";


const WaiterPage = () => {
  const [orderNotification, setOrderNotification] = useState(null);
  const [orders, setOrders] = useState([]);
  const [error, setError] = useState('');
  const [lastOrderId, setLastOrderId] = useState(null);
  const [expandedOrders, setExpandedOrders] = useState(new Set());
  const [assistanceWarning, setAssistanceWarning] = useState(null);
  const [tableNumber, setTableNumber] = useState('');


  // Poll for latest order every 5 seconds
  useEffect(() => {
    const interval = setInterval(async () => {
      try {
        const response = await axios.get(`${API_ENDPOINT}/api/orders/latest`);
        if (response.data && response.data.id !== lastOrderId) {
          const { orderNumber, menuItems, id } = response.data;
          const orderDetails = menuItems.map(item => item.name).join(", ");
          setOrderNotification(`New Order #${orderNumber}: ${orderDetails}`);
          setLastOrderId(id);
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
        const filtered = res.data.filter(order => 
          order.status !== 'Delivered'
        );
        setOrders(filtered);
      })
      .catch(err => {
        setError('Failed to load orders');
        console.error('Order fetch error:', err);
      });
  };

  React.useEffect(fetchOrders, []);

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
 
  // Assistance warning handler
  const sendWarning = () => {
    if (!tableNumber.trim()) {
        setError("Please enter a valid table number.");
        return;
    }

    setError(''); // Clear previous error

    axios.post(`${API_ENDPOINT}/api/waiters/send-warning`, { tableNumber: parseInt(tableNumber) }, {
        headers: {
            "Content-Type": "application/json"
        }
    })
    .then(() => {
        setAssistanceWarning(`Assistance requested for Table #${tableNumber}`);
        setTableNumber('');
    })
    .catch(err => {
        setError('Failed to send assistance warning: ' + (err.response?.data || err.message));
    });
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

<Stack direction="row" spacing={2} alignItems="center" sx={{ mb: 3 }}>
  <TextField
    label="Table Number"
    variant="outlined"
    size="small"
    value={tableNumber}
    onChange={(e) => setTableNumber(e.target.value)}
    sx={{ width: 150 }}
  />
  <Button variant="contained" color="error" onClick={sendWarning}>
    Send Warning
  </Button>
</Stack>

{/* Assistance Warning Notification */}
{assistanceWarning && (
  <Alert
    severity="warning"
    sx={{ mb: 3 }}
    action={
      <Button
        color="inherit"
        size="small"
        onClick={() => setAssistanceWarning(null)}
      >
        DISMISS
      </Button>
    }
  >
    {assistanceWarning}
  </Alert>
)}

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
    </Container>
  );
};

export default WaiterPage;

