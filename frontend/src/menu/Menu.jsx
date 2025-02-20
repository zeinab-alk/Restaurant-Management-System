import React, { useState } from "react";
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
  Chip,
  Stack,
  Button,
  useTheme,
  Tooltip
} from "@mui/material";
import { Link as RouterLink } from 'react-router-dom';
import { API_ENDPOINT } from "../config";
import { allergenSymbols, dietarySymbols } from '../utils/foodInfoSymbols';

function Menu() {
  const [menuItems, setMenuItems] = useState([]);
  const [error, setError] = useState();
  const theme = useTheme();

  const [cart, setCart] = useState([]);
  const [orderSuccess, setOrderSuccess] = useState(false);

  const [showWaiterButton, setShowWaiterButton] = useState(true);
  const [waiterMessage, setWaiterMessage] = useState(null);
  const [waiterError, setWaiterError] = useState(null);

  React.useEffect(() => {
    axios
      .get(`${API_ENDPOINT}/api/menuItems`)
      .then((response) => {
        setMenuItems(response.data);
      })
      .catch((response) => {
        setError(response.message);
      });
  }, []);

  const addToCart = (item) => {
    setCart((prevCart) => [...prevCart, item]); 
  };

  const removeFromCart = (index) => {
    setCart((prevCart) => prevCart.filter((_, i) => i !== index)); 
  };

  // ---- FIXED placeOrder() ----
  const placeOrder = async () => {
    if (cart.length === 0) {
      alert("Your order is empty!");
      return;
    }

    // Build an order object that your Spring Boot Order entity can parse
    const orderData = {
      orderTime: new Date().toISOString(),   // or any string you like
      status: "Pending",                    // match your Order’s 'status' field
      // "menuItems" must be an array of objects with at least { "itemId": ... }
      menuItems: cart.map(item => ({
        itemId: item.itemId 
      }))
    };

    try {
      await axios.post(`${API_ENDPOINT}/api/orders`, orderData);
      setOrderSuccess(true);
      setCart([]); // Clear cart after successful order
    } catch (error) {
      console.error("Error placing order:", error);
      alert("Failed to place order.");
    }
  };

  const callWaiter = async () => {
    try {
      const response = await axios.post(`${API_ENDPOINT}/api/waiters/call-waiter?tableNumber=5`);
      setWaiterMessage(response.data);
      setWaiterError(null);
      setTimeout(() => setShowWaiterButton(false), 1000);
    } catch (err) {
      setWaiterMessage(null);
      setWaiterError("No available waiters at the moment. Please try again later.");
    }
  };

  return (
    <Container>
      {/* Back to Home Button */}
      <Stack direction="row" spacing={2} sx={{ mb: 2 }}>
        <Button component={RouterLink} to="/" variant="contained" color="success">
          Back to Home
        </Button>
      </Stack>

      {/* Request Waiter Button */}
      {showWaiterButton && (
        <Stack direction="row" spacing={2} sx={{ mb: 4 }}>
          <Button variant="contained" color="primary" onClick={callWaiter}>
            Request Waiter
          </Button>
        </Stack>
      )}

      {/* Success/Error Messages */}
      {waiterMessage && <Alert severity="success" sx={{ mb: 2 }}>{waiterMessage}</Alert>}
      {waiterError && <Alert severity="error" sx={{ mb: 2 }}>{waiterError}</Alert>}

      <Typography variant="h3" component="h1" gutterBottom>
        Our Menu
      </Typography>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      {!error && menuItems.length < 1 && (
        <Alert severity="warning" sx={{ mb: 2 }}>No menu items available</Alert>
      )}

      {menuItems.length > 0 && (
        <TableContainer component={Paper} elevation={3}>
          <Table sx={{ minWidth: 650 }}>
            <TableHead>
              <TableRow sx={{ backgroundColor: 'primary.main' }}>
                <TableCell sx={{ color: theme.palette.mode === 'dark' ? 'black' : 'white', fontWeight: 'bold' }}>
                  Name
                </TableCell>
                <TableCell sx={{ color: theme.palette.mode === 'dark' ? 'black' : 'white', fontWeight: 'bold' }}>
                  Price
                </TableCell>
                <TableCell sx={{ color: theme.palette.mode === 'dark' ? 'black' : 'white', fontWeight: 'bold' }}>
                  Calories
                </TableCell>
                <TableCell sx={{ color: theme.palette.mode === 'dark' ? 'black' : 'white', fontWeight: 'bold' }}>
                  Dietary Info
                </TableCell>
                <TableCell sx={{ color: theme.palette.mode === 'dark' ? 'black' : 'white', fontWeight: 'bold' }}>
                  Allergens
                </TableCell>
                <TableCell sx={{ color: theme.palette.mode === 'dark' ? 'black' : 'white', fontWeight: 'bold' }}>
                  Status
                </TableCell>
                <TableCell sx={{ color: theme.palette.mode === 'dark' ? 'black' : 'white', fontWeight: 'bold' }}>
                  Actions
                </TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {menuItems.map((item) => (
                <TableRow key={item.itemId}>
                  <TableCell>{item.name}</TableCell>
                  <TableCell>£{item.price.toFixed(2)}</TableCell>
                  <TableCell>{item.caloricInfo} kcal</TableCell>
                  <TableCell>
                    <Stack direction="row" spacing={0.5}>
                      {item.dietaryRestrictions.map((restriction, index) => (
                        <Tooltip key={index} title={restriction} arrow>
                          <span style={{ fontSize: '1.2rem' }}>
                            {dietarySymbols[restriction]}
                          </span>
                        </Tooltip>
                      ))}
                    </Stack>
                  </TableCell>
                  <TableCell>
                    <Stack direction="row" spacing={0.5}>
                      {item.allergenInformation.map((allergen, index) => (
                        <Tooltip key={index} title={allergen} arrow>
                          <span style={{ fontSize: '1.2rem' }}>
                            {allergenSymbols[allergen]}
                          </span>
                        </Tooltip>
                      ))}
                    </Stack>
                  </TableCell>
                  <TableCell>
                    <Chip 
                      label={item.available ? "Available" : "Unavailable"} 
                      color={item.available ? "success" : "error"} 
                      size="small"
                    />
                  </TableCell>
                  <TableCell>
                    {item.available && (
                      <Button 
                        variant="contained" 
                        color="primary" 
                        onClick={() => addToCart(item)}>
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
                onClick={() => removeFromCart(index)}>
                Remove
              </Button>
            </Stack>
          ))}
          <Button 
            variant="contained" 
            color="success" 
            sx={{ mt: 2 }} 
            onClick={placeOrder}>
            Place Order
          </Button>
        </Paper>
      )}

      {orderSuccess && <Alert severity="success" sx={{ mt: 2 }}>Order placed successfully!</Alert>}
    </Container>
  );
}

export default Menu;
