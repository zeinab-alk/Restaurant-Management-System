import React, { useState, useEffect, useCallback } from 'react';
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
  const [assignedTables, setAssignedTables] = useState({});
  const [selectedWaiter, setSelectedWaiter] = useState(null);
  const [newTable, setNewTable] = useState("");
  const [waiters, setWaiters] = useState([]);
  const [editingWaiter, setEditingWaiter] = useState(null);
  const [editedTable, setEditedTable] = useState("");
  const [editingWaiters, setEditingWaiters] = useState({});
  const [newWaiter, setNewWaiter] = useState({ id: "", name: "", assignedTables: [] });

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

    const fetchAssignedTables = async (waiterId) => {
      try {
        const response = await axios.get(`${API_ENDPOINT}/${waiterId}/assigned-tables`);
        setAssignedTables((prevTables) => ({
          ...prevTables,
          [waiterId]: response.data,
        }));
        setSelectedWaiter(waiterId);
      } catch (error) {
        console.error("Error fetching assigned tables:", error);
      }
    };    

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


    const fetchWaiters = async () => {
      try {
        const response = await axios.get(`${API_ENDPOINT}/api/waiters`);
        const waitersData = response.data;
    
        // Fetch assigned tables for each waiter
        const tablesData = await Promise.all(
          waitersData.map(async (waiter) => {
            try {
              const tablesRes = await axios.get(`${API_ENDPOINT}/api/waiters/${waiter.id}/assigned-tables`);
              return { ...waiter, assignedTables: tablesRes.data || [] };
            } catch (error) {
              console.error(`Error fetching tables for waiter ${waiter.id}:`, error);
              return { ...waiter, assignedTables: [] }; // Fail gracefully
            }
          })
        );
    
        setWaiters(tablesData);
      } catch (error) {
        console.error("Error fetching waiters:", error);
        setWaiters([]); // Ensure state is updated even on failure
      }
    };
    
    // Call fetchWaiters properly
    useEffect(() => {
      fetchWaiters();
    }, []); // Empty dependency array ensures it runs once on mount
    



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

  const assignTableToWaiter = async (waiterId) => {
    if (!newTable) {
      alert("Please select a table!");
      return;
    }
  
    try {
      await axios.post(`${API_ENDPOINT}/api/waiters/${waiterId}/assign-table/${parseInt(newTable)}`);
  
      setWaiters((prevWaiters) =>
        prevWaiters.map((waiter) =>
          waiter.id === waiterId
            ? { ...waiter, assignedTables: [...waiter.assignedTables, newTable] }
            : waiter
        )
      );
  
      setNewTable(""); // Reset dropdown after assignment
    } catch (error) {
      console.error("Error assigning table:", error);
  
      if (error.response) {
        alert(`Error: ${error.response.data}`);
      } else {
        alert("Failed to assign table. Please try again.");
      }
    }
  };

  const startEditing = (waiter) => {
  setEditingWaiters((prev) => ({
    ...prev,
    [waiter.id]: { id: waiter.id, name: waiter.name }, // Store current values
  }));
};


  const saveEditedWaiter = (waiterId) => {
    setWaiters((prevWaiters) =>
      prevWaiters.map((waiter) =>
        waiter.id === waiterId
          ? { ...waiter, ...editingWaiters[waiterId] } // Update with edited data
          : waiter
      )
    );
  
    // Exit editing mode for this waiter
    setEditingWaiters((prev) => {
      const updatedEditingWaiters = { ...prev };
      delete updatedEditingWaiters[waiterId]; // Remove from editing state
      return updatedEditingWaiters;
    });
  };
  
  const removeAssignedTable = (waiterId, tableToRemove) => {
    setWaiters((prevWaiters) =>
      prevWaiters.map((waiter) =>
        waiter.id === waiterId
          ? { 
              ...waiter, 
              assignedTables: waiter.assignedTables.filter((table) => table !== tableToRemove) 
            }
          : waiter
      )
    );
  };
  
  
  
  const saveEditedTable = async (waiterId) => {
    try {
      await axios.post(`${API_ENDPOINT}/api/waiters/${waiterId}/assign-table/${parseInt(editedTable)}`);
      
      // Update UI immediately
      setWaiters(prevWaiters =>
        prevWaiters.map(waiter =>
          waiter.id === waiterId ? { ...waiter, assignedTables: [editedTable] } : waiter
        )
      );
  
      setEditingWaiter(null); // Exit edit mode
    } catch (error) {
      console.error("Error updating table:", error);
      alert("Failed to update table. Please try again.");
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
        variant="h5" 
        gutterBottom 
        sx={{ 
          mb: 2, fontWeight: 'medium' }}>

        Waiter Table Assignments
</Typography>
<Button
  onClick={() => setWaiters([...waiters, { ...newWaiter, id: waiters.length + 1 }])}
  variant="contained"
  color="primary"
  sx={{ mb: 2 }}
>
  Add New Waiter
</Button>
<Paper sx={{ overflow: 'hidden', mb: 4, boxShadow: 2, borderRadius: 1 }} elevation={3}>
  <Box sx={{ overflowX: 'auto', width: '100%' }}>
    <Table>
      <TableHead>
        <TableRow sx={{ backgroundColor: 'primary.main' }}>
          <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Waiter ID</TableCell>
          <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Name</TableCell>
          <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Assigned Tables</TableCell>
          <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Actions</TableCell>
        </TableRow>
      </TableHead>
      <TableBody>
        {waiters.map((waiter) => (
          <TableRow key={waiter.id}>
            <TableCell>
  {editingWaiters[waiter.id] ? (
    <input
      type="text"
      value={editingWaiters[waiter.id].id}
      onChange={(e) =>
        setEditingWaiters(prev => ({
          ...prev,
          [waiter.id]: { ...prev[waiter.id], id: e.target.value }
        }))
      }
      style={{ width: "50px", padding: "4px" }}
    />
  ) : (
    waiter.id
  )}
</TableCell>

<TableCell>
  {editingWaiters[waiter.id] ? (
    <input
      type="text"
      value={editingWaiters[waiter.id].name}
      onChange={(e) =>
        setEditingWaiters(prev => ({
          ...prev,
          [waiter.id]: { ...prev[waiter.id], name: e.target.value }
        }))
      }
      style={{ width: "150px", padding: "4px" }}
    />
  ) : (
    waiter.name
  )}
</TableCell>

<TableCell>
  {editingWaiter === waiter.id ? (
    <Box sx={{ display: "flex", flexWrap: "wrap", gap: 1 }}>
      {/* Display assigned tables with remove option */}
      {waiter.assignedTables.map((table, index) => (
        <Chip
          key={index}
          label={`Table ${table}`}
          onDelete={() => removeAssignedTable(waiter.id, table)} // Enables removal
          color="primary"
          sx={{ cursor: "pointer" }}
        />
      ))}
      {/* Dropdown for adding new tables */}
      <FormControl size="small">
        <Select
          value={editedTable}
          onChange={(e) => setEditedTable(e.target.value)}
          displayEmpty
        >
          <MenuItem value="" disabled>Select Table</MenuItem>
          {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((table) => (
            <MenuItem key={table} value={table}>{table}</MenuItem>
          ))}
        </Select>
      </FormControl>
      <Button 
        onClick={() => assignTableToWaiter(waiter.id, editedTable)} 
        variant="contained" 
        color="success"
        sx={{ ml: 1 }}
      >
        Add
      </Button>
    </Box>
  ) : (
    waiter.assignedTables.length > 0 ? waiter.assignedTables.join(", ") : "No tables assigned"
  )}
</TableCell>


            <TableCell>
              <FormControl size="small">
                <Select 
                  value={newTable} 
                  onChange={(e) => setNewTable(e.target.value)}
                  displayEmpty
                >
                  <MenuItem value="" disabled>Select Table</MenuItem>
                  {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((table) => (
                    <MenuItem key={table} value={table}>{table}</MenuItem>
                  ))}
                </Select>
              </FormControl>
              {editingWaiter === waiter.id ? (
  <Button 
    onClick={() => saveEditedTable(waiter.id)} 
    variant="contained" 
    color="success"
    sx={{ ml: 1 }}
  >
    Save
  </Button>
) : (
  <>
    <Button 
      onClick={() => assignTableToWaiter(waiter.id)} 
      variant="contained" 
      color="primary"
      sx={{ ml: 1 }}
    >
      Assign
    </Button>
    {editingWaiters[waiter.id] ? (
  <Button 
    onClick={() => saveEditedWaiter(waiter.id)} 
    variant="contained" 
    color="success"
    sx={{ ml: 1 }}
  >
    Save
  </Button>
) : (
  <Button 
    onClick={() => startEditing(waiter)} 
    variant="contained" 
    color="secondary"
    sx={{ ml: 1 }}
  >
    Edit
  </Button>
)}

<Button
  onClick={() => setWaiters(waiters.filter(w => w.id !== waiter.id))}
  variant="contained"
  color="error"
  sx={{ ml: 1 }}
>
  Remove
</Button>
  </>
)}

            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  </Box>
</Paper>

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
