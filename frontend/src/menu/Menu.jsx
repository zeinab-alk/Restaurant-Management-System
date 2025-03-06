import React, { useState, useEffect } from "react";
import axios from "axios";
import { 
  Typography, 
  Alert, 
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Container,
  Stack,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField
} from "@mui/material";
import { Link as RouterLink } from "react-router-dom";
import { API_ENDPOINT } from "../config";

function Menu() {
  const [menuItems, setMenuItems] = useState([]);
  const [error, setError] = useState(null);
  const [cart, setCart] = useState([]);
  const [orderSuccess, setOrderSuccess] = useState(false);
  
  // States for requesting a waiter
  const [openDialog, setOpenDialog] = useState(false);
  const [tableNumber, setTableNumber] = useState("");
  const [waiterSuccess, setWaiterSuccess] = useState(null);
  const [waiterError, setWaiterError] = useState(null);

  useEffect(() => {
    axios
      .get(`${API_ENDPOINT}/api/menuItems`)
      .then((response) => setMenuItems(response.data))
      .catch((err) => setError(err.message));
  }, []);

  // Add to cart
  const addToCart = (item) => setCart((prevCart) => [...prevCart, item]);

  // Remove from cart
  const removeFromCart = (index) => 
    setCart((prevCart) => prevCart.filter((_, i) => i !== index));

  // Place order
  const placeOrder = async () => {
    if (cart.length === 0) {
      alert("Your order is empty!");
      return;
    }

    const orderData = {
      orderTime: new Date().toISOString(),
      status: "Pending",
      menuItems: cart.map(item => ({ itemId: item.itemId }))
    };

    try {
      await axios.post(`${API_ENDPOINT}/api/orders`, orderData);
      setOrderSuccess(true);
      setCart([]); 
    } catch (error) {
      console.error("Error placing order:", error);
      alert("Failed to place order.");
    }
  };

  // Open the request waiter dialog
  const callWaiter = () => {
    setOpenDialog(true);
  };

  // Submit waiter request
  const submitWaiterRequest = async () => {
    if (!tableNumber) {
      setWaiterError("Please enter a valid table number.");
      return;
    }
  
    try {
      const response = await axios.post(
        `${API_ENDPOINT}/api/help-requests`, 
        { tableNumber: parseInt(tableNumber, 10), waiterId: 1 }, 
        { headers: { "Content-Type": "application/json" } }
      );
  
      setWaiterSuccess(`Waiter has been notified you need assistance at table #${tableNumber}`);
      setWaiterError(null);
    } catch (err) {
      console.error("API Error:", err.response?.data || err.message);
      setWaiterSuccess(null);
      setWaiterError("Error notifying waiter. Please try again.");
    }
  
    setOpenDialog(false);
    setTableNumber("");
  };  

  return (
    <Container>
      <Stack direction="row" spacing={2} sx={{ mb: 2 }}>
        <Button component={RouterLink} to="/" variant="contained" color="success">
          Back to Home
        </Button>
      </Stack>

      <Stack direction="row" spacing={2} sx={{ mb: 4 }}>
        <Button variant="contained" color="primary" onClick={callWaiter}>
          Request Waiter
        </Button>
      </Stack>

      {waiterSuccess && <Alert severity="success" sx={{ mb: 2 }}>{waiterSuccess}</Alert>}
      {waiterError && <Alert severity="error" sx={{ mb: 2 }}>{waiterError}</Alert>}

      <Typography variant="h3" component="h1" gutterBottom>
        Our Menu
      </Typography>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      {!error && menuItems.length < 1 && (
        <Alert severity="warning" sx={{ mb: 2 }}>No menu items available</Alert>
      )}

      {/* Menu Items Table (Kept Everything You Had) */}
      {menuItems.length > 0 && (
        <TableContainer component={Paper} elevation={3}>
          <Table sx={{ minWidth: 650 }}>
            <TableHead>
              <TableRow sx={{ backgroundColor: 'primary.main' }}>
                <TableCell sx={{ fontWeight: 'bold', color: 'white' }}>Name</TableCell>
                <TableCell sx={{ fontWeight: 'bold', color:'white' }}>Price</TableCell>
                <TableCell sx={{ fontWeight: 'bold', color:'white' }}>Calories</TableCell>
                <TableCell sx={{ fontWeight: 'bold', color:'white' }}>Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {menuItems.map((item) => (
                <TableRow key={item.itemId}>
                  <TableCell>{item.name}</TableCell>
                  <TableCell>£{item.price.toFixed(2)}</TableCell>
                  <TableCell>{item.caloricInfo} kcal</TableCell>
                  <TableCell>
                    {item.available && (
                      <Button 
                        variant="contained" 
                        color="primary"
                        onClick={() => addToCart(item)}
                      >
                        Add to Order
                      </Button>
                    )}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      {/* Order Summary */}
      <Typography variant="h4" sx={{ mt: 4 }}>Your Order</Typography>
      {cart.length === 0 ? (
        <Typography>No items in cart</Typography>
      ) : (
        <Paper sx={{ p: 2, mt: 2 }}>
          <Typography variant="h6">
            Items: {cart.length}
          </Typography>
          {cart.map((item, index) => (
            <Stack key={index} direction="row" justifyContent="space-between">
              <Typography>{item.name} - £{item.price.toFixed(2)}</Typography>
              <Button 
                variant="contained" 
                color="error" 
                size="small" 
                onClick={() => removeFromCart(index)}
              >
                Remove
              </Button>
            </Stack>
          ))}
          <Button 
            variant="contained" 
            color="success" 
            sx={{ mt: 2 }} 
            onClick={placeOrder}
          >
            Place Order
          </Button>
        </Paper>
      )}

      {orderSuccess && <Alert severity="success" sx={{ mt: 2 }}>Order placed successfully!</Alert>}

      {/* Request Waiter Dialog */}
      <Dialog open={openDialog} onClose={() => setOpenDialog(false)}>
        <DialogTitle>Request Waiter</DialogTitle>
        <DialogContent>
          <TextField
            label="Which table are you at?"
            type="number"
            fullWidth
            value={tableNumber}
            onChange={(e) => setTableNumber(e.target.value)}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)}>Cancel</Button>
          <Button onClick={submitWaiterRequest} color="primary">OK</Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
}

export default Menu;
