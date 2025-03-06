import React, { useState, useEffect } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { 
  Button, 
  Container, 
  Stack, 
  Typography, 
  Alert, 
  Table, 
  TableBody, 
  TableCell, 
  TableRow, 
  TableContainer, 
  TableHead, 
  Paper, 
  Box, 
  Chip, 
  Select, 
  MenuItem, 
  FormControl, 
  Snackbar,
  useTheme,
  Grid
} from "@mui/material";
import axios from "axios";
import { API_ENDPOINT } from "../config";
import Collapse from '@mui/material/Collapse';
import IconButton from '@mui/material/IconButton';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp';

const WaiterPage = () => {
  const theme = useTheme();
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
      .catch((error) => {
        console.error('Status update error:', error);
        setError(`Failed to update order status: ${error.message}`);
      });
  };

  // Mark an order as delivered
  const markDelivered = (orderId) => {
    updateOrderStatus(orderId, 'DELIVERED');
  };

  const toggleExpansion = (orderId) => {
    setExpandedOrders(prev => {
      const newSet = new Set(prev);
      if (newSet.has(orderId)) {
        newSet.delete(orderId);
      } else {
        newSet.add(orderId);
      }
      return newSet;
    });
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

  return (
    <Container maxWidth="xl" sx={{ py: 3 }}>
      {/* Navigation and Header */}
      <Box sx={{ mb: 4 }}>
        <Box sx={{ 
          display: 'flex', 
          flexDirection: { xs: 'column', sm: 'row' },
          gap: 2,
          mb: 3
        }}>
          <Button 
            component={RouterLink} 
            to="/" 
            variant="contained" 
            color="success"
            startIcon={<span role="img" aria-label="home">🏠</span>}
            sx={{ fontWeight: 'medium' }}
          >
            Back to Home
          </Button>
          
          <Button 
            component={RouterLink} 
            to="/menuitem/change" 
            variant="contained" 
            color="primary"
            startIcon={<span role="img" aria-label="edit">✏️</span>}
            sx={{ fontWeight: 'medium' }}
          >
            Edit Menu
          </Button>
          
          <Button 
            component={RouterLink} 
            to="/menuitem/add" 
            variant="contained" 
            color="secondary"
            startIcon={<span role="img" aria-label="add">➕</span>}
            sx={{ fontWeight: 'medium' }}
          >
            Add Menu Item
          </Button>
        </Box>
        
        <Typography 
          variant="h3" 
          component="h1" 
          gutterBottom 
          align="center" 
          sx={{ 
            mb: 3,
            fontWeight: 'bold',
            fontSize: { xs: '1.8rem', sm: '2.2rem', md: '2.8rem' }
          }}
        >
          Waiter Dashboard
        </Typography>
      </Box>
      
      {/* Notifications */}
      <Box sx={{ mb: 4 }}>
        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}
        
        {orderNotification && (
          <Snackbar
            open={newOrderAlert}
            autoHideDuration={6000}
            onClose={() => setNewOrderAlert(false)}
          >
            <Alert 
              onClose={() => setNewOrderAlert(false)} 
              severity="info" 
              variant="filled"
            >
              {orderNotification}
            </Alert>
          </Snackbar>
        )}
      </Box>
      
      {/* Orders Section */}
      <Typography 
        variant="h5" 
        gutterBottom 
        sx={{ 
          mb: 2,
          fontWeight: 'medium' 
        }}
      >
        Current Orders
      </Typography>
      
      {orders.length === 0 ? (
        <Paper sx={{ p: 4, textAlign: 'center', borderRadius: 1, mb: 4 }} elevation={2}>
          <Typography variant="h6" color="text.secondary">
            No orders found
          </Typography>
        </Paper>
      ) : (
        <Paper sx={{ 
          overflow: 'hidden',
          mb: 4,
          boxShadow: 2,
          borderRadius: 1
        }} elevation={3}>
          <Box sx={{ 
            overflowX: 'auto', 
            width: '100%'
          }}>
            <Table>
              <TableHead>
                <TableRow sx={{ backgroundColor: 'primary.main' }}>
                  <TableCell sx={{ 
                    width: '50px', 
                    color: theme.palette.mode === 'dark' ? 'black' : 'white',
                    fontWeight: 'bold'
                  }}></TableCell>
                  <TableCell sx={{ 
                    color: theme.palette.mode === 'dark' ? 'black' : 'white',
                    fontWeight: 'bold',
                    whiteSpace: 'nowrap'
                  }}>
                    Order #
                  </TableCell>
                  <TableCell sx={{ 
                    color: theme.palette.mode === 'dark' ? 'black' : 'white',
                    fontWeight: 'bold',
                    whiteSpace: 'nowrap',
                    display: { xs: 'none', sm: 'table-cell' }
                  }}>
                    Time
                  </TableCell>
                  <TableCell sx={{ 
                    color: theme.palette.mode === 'dark' ? 'black' : 'white',
                    fontWeight: 'bold',
                    whiteSpace: 'nowrap'
                  }}>
                    Status
                  </TableCell>
                  <TableCell sx={{ 
                    color: theme.palette.mode === 'dark' ? 'black' : 'white',
                    fontWeight: 'bold',
                    whiteSpace: 'nowrap'
                  }}>
                    Actions
                  </TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {orders.map((order) => (
                  <React.Fragment key={order.orderNumber}>
                    <TableRow
                      sx={{
                        backgroundColor: theme => (
                          expandedOrders.has(order.orderNumber)
                            ? theme.palette.action.hover
                            : 'inherit'
                        ),
                        '&:hover': {
                          backgroundColor: theme => theme.palette.action.hover
                        }
                      }}
                    >
                      <TableCell>
                        <IconButton
                          aria-label="expand row"
                          size="small"
                          onClick={() => toggleExpansion(order.orderNumber)}
                        >
                          {expandedOrders.has(order.orderNumber) ? <KeyboardArrowUpIcon /> : <KeyboardArrowDownIcon />}
                        </IconButton>
                      </TableCell>
                      <TableCell component="th" scope="row" sx={{ fontWeight: 'medium' }}>
                        {order.orderNumber}
                      </TableCell>
                      <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' } }}>
                        {new Date(order.orderTime).toLocaleString()}
                      </TableCell>
                      <TableCell>
                        <Chip
                          label={order.status}
                          color={getStatusColor(order.status)}
                          size="small"
                          sx={{ fontWeight: 'medium' }}
                        />
                      </TableCell>
                      <TableCell>
                        <Box sx={{ 
                          display: 'flex',
                          flexDirection: { xs: 'column', md: 'row' },
                          gap: 1
                        }}>
                          <FormControl 
                            variant="outlined" 
                            size="small"
                            sx={{ minWidth: 150 }}
                          >
                            <Select
                              value=""
                              displayEmpty
                              onChange={(e) => updateOrderStatus(order.orderNumber, e.target.value)}
                              sx={{ fontSize: '0.875rem' }}
                            >
                              <MenuItem value="" disabled>
                                Update Status
                              </MenuItem>
                              {orderStatuses
                                .filter(status => status !== order.status) // Don't show current status
                                .map(status => (
                                  <MenuItem key={status} value={status}>
                                    {status.replace(/_/g, ' ')}
                                  </MenuItem>
                                ))
                              }
                            </Select>
                          </FormControl>
                          
                          {order.status === 'READY_FOR_DELIVERY' && (
                            <Button
                              variant="contained"
                              color="success"
                              size="small"
                              onClick={() => markDelivered(order.orderNumber)}
                              sx={{ whiteSpace: 'nowrap' }}
                            >
                              Mark Delivered
                            </Button>
                          )}
                        </Box>
                      </TableCell>
                    </TableRow>
                    <TableRow>
                      <TableCell style={{ paddingBottom: 0, paddingTop: 0 }} colSpan={6}>
                        <Collapse in={expandedOrders.has(order.orderNumber)} timeout="auto" unmountOnExit>
                          <Box sx={{ m: 2 }}>
                            <Typography variant="h6" gutterBottom component="div">
                              Order Details
                            </Typography>
                            <Table size="small" aria-label="order details">
                              <TableHead>
                                <TableRow>
                                  <TableCell>Item</TableCell>
                                  <TableCell align="right">Price</TableCell>
                                </TableRow>
                              </TableHead>
                              <TableBody>
                                {order.menuItems.map((item, index) => (
                                  <TableRow key={index}>
                                    <TableCell component="th" scope="row">
                                      {item.name}
                                    </TableCell>
                                    <TableCell align="right">£{item.price.toFixed(2)}</TableCell>
                                  </TableRow>
                                ))}
                                <TableRow>
                                  <TableCell sx={{ fontWeight: 'bold' }}>Total</TableCell>
                                  <TableCell align="right" sx={{ fontWeight: 'bold' }}>
                                    £{order.menuItems.reduce((sum, item) => sum + item.price, 0).toFixed(2)}
                                  </TableCell>
                                </TableRow>
                              </TableBody>
                            </Table>
                          </Box>
                        </Collapse>
                      </TableCell>
                    </TableRow>
                  </React.Fragment>
                ))}
              </TableBody>
            </Table>
          </Box>
        </Paper>
      )}
    </Container>
  );
};

export default WaiterPage;
