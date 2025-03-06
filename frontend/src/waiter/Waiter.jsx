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
