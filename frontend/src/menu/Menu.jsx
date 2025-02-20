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
  Tooltip,
  Autocomplete,
  TextField
} from "@mui/material";
import { Link as RouterLink } from 'react-router-dom';
import { API_ENDPOINT } from "../config";
import { allergenSymbols, dietarySymbols } from '../utils/foodInfoSymbols';
import { Box } from "@mui/material";
import { Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, CircularProgress } from "@mui/material";


function Menu() {
  const [menuItems, setMenuItems] = React.useState([]);
  const [error, setError] = React.useState();
  const theme = useTheme();
  const [searchQuery, setSearchQuery] = useState('');
  const [openConfirmDialog, setOpenConfirmDialog] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [orderError, setOrderError] = useState(null);


  // Cart State
  const [cart, setCart] = useState([]);
  const [orderSuccess, setOrderSuccess] = useState(false);
  const clearCart = () => {
    setCart([]);  // Simply clears the cart by setting it to an empty array
  };

  React.useEffect(() => {
    axios
      .get(`${API_ENDPOINT}/api/menuItems`)
      .then((response) => {
        const itemsWithCategories = response.data.map(item => ({
          ...item,
          category: item.category || (item.caloricInfo > 500 ? 'Mains' : 'Drinks')  // Use API category if available
        }));
        setMenuItems(itemsWithCategories);
      })
      .catch((response) => {
        setError(response.message);
      });
  }, []);

  // Add item to cart
  const addToCart = (item) => {
    const existingItemIndex = cart.findIndex(cartItem => cartItem.itemId === item.itemId);
    if (existingItemIndex >= 0) {
      const updatedCart = [...cart];
      updatedCart[existingItemIndex].quantity += 1;
      setCart(updatedCart);
    } else {
      setCart([...cart, { ...item, quantity: 1 }]);
    }
  };

  // Remove item from cart
  const removeFromCart = (itemId) => {
    setCart((prevCart) => prevCart.filter(item => item.itemId !== itemId));
  };

  // New function to update quantities
  const updateQuantity = (itemId, change) => {
    setCart((prevCart) => prevCart.map(item => 
      item.itemId === itemId ? { ...item, quantity: Math.max(item.quantity + change, 1) } : item
    ));
  };

  // Place order
  const handleConfirmOrder = async () => {
    setOpenConfirmDialog(false);  // Close the dialog
    setIsLoading(true);  // Show loading spinner
    setOrderError(null); // Reset errors
  
    if (cart.length === 0) {
      alert("Your order is empty!");
      setIsLoading(false);
      return;
    }
  
    const orderData = {
      customerName: "Guest",
      menuItemIds: cart.map(item => item.itemId),
    };
  
    try {
      await axios.post(`${API_ENDPOINT}/api/orders`, orderData);
      setOrderSuccess(true);
      setCart([]);
    } catch (error) {
      console.error("Error placing order:", error);
      setOrderError("Failed to place order. Please try again.");
    } finally {
      setIsLoading(false);  // Hide loading spinner
    }
  };
  

  return (
    <Container>
      <Stack direction="row" spacing={2} sx={{ mb: 4 }}>
        <Button component={RouterLink} to="/" variant="contained" color="success">
          Back to Home
        </Button>
      </Stack>

      <Typography variant="h3" component="h1" gutterBottom>
        Our Menu
      </Typography>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      {!error && menuItems.length < 1 && (
        <Alert severity="warning" sx={{ mb: 2 }}>No menu items available</Alert>
      )}

      {menuItems.length > 0 && (
        <>
          <TextField
          label="Search Menu"
          variant="outlined"
          fullWidth
          margin="normal"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          placeholder="Search for a menu item..."
        />
        <TableContainer component={Paper} elevation={3}>
          <Table sx={{ minWidth: 650 }}>
            <TableHead>
              <TableRow sx={{ backgroundColor: 'primary.main' }}>
                <TableCell sx={{ color: theme.palette.mode === 'dark' ? 'black' : 'white', fontWeight: 'bold' }}>Category</TableCell>
                <TableCell sx={{ color: theme.palette.mode === 'dark' ? 'black' : 'white', fontWeight: 'bold' }}>Name</TableCell>
                <TableCell sx={{ color: theme.palette.mode === 'dark' ? 'black' : 'white', fontWeight: 'bold' }}>Price</TableCell>
                <TableCell sx={{ color: theme.palette.mode === 'dark' ? 'black' : 'white', fontWeight: 'bold' }}>Calories</TableCell>
                <TableCell sx={{ color: theme.palette.mode === 'dark' ? 'black' : 'white', fontWeight: 'bold' }}>Dietary Info</TableCell>
                <TableCell sx={{ color: theme.palette.mode === 'dark' ? 'black' : 'white', fontWeight: 'bold' }}>Allergens</TableCell>
                <TableCell sx={{ color: theme.palette.mode === 'dark' ? 'black' : 'white', fontWeight: 'bold' }}>Status</TableCell>
                <TableCell sx={{ color: theme.palette.mode === 'dark' ? 'black' : 'white', fontWeight: 'bold' }}>Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
  {menuItems
    .filter((item) =>
      item.name.toLowerCase().includes(searchQuery.toLowerCase())
    )
    .map((item) => (
      <TableRow key={item.itemId}>
        <TableCell>{item.category}</TableCell>  {/* Category is now correctly placed */}
        <TableCell>{item.name}</TableCell>
        <TableCell>£{item.price.toFixed(2)}</TableCell>
        <TableCell>{item.caloricInfo} kcal</TableCell>
        <TableCell>
          <Stack direction="row" spacing={0.5}>
            {item.dietaryRestrictions.map((restriction, index) => (
              <Tooltip key={index} title={`Dietary Restriction: ${restriction}`} arrow>
              <span style={{ fontSize: '1.2rem' }} aria-label={`Dietary Restriction: ${restriction}`}>
                {dietarySymbols[restriction]}
              </span>
            </Tooltip>
            

            ))}
          </Stack>
        </TableCell>
        <TableCell>
          <Stack direction="row" spacing={0.5}>
            {item.allergenInformation.map((allergen, index) => (
              <Tooltip key={index} title={`Allergen: ${allergen}`} arrow>
              <span style={{ fontSize: '1.2rem' }} aria-label={`Allergen: ${allergen}`}>
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
            <Tooltip title={`Add ${item.name} to order`} arrow>
            <Button 
              variant="contained" 
              color="primary" 
              onClick={() => addToCart(item)}
              aria-label={`Add ${item.name} to order`}
            >
              Add to Order
            </Button>
          </Tooltip>
          

          )}
        </TableCell>
      </TableRow>
    ))}
</TableBody>

          </Table>
        </TableContainer>
        </>
      )}
  <Dialog
  open={openConfirmDialog}
  onClose={() => setOpenConfirmDialog(false)}
>
  <DialogTitle>Confirm Your Order</DialogTitle>
  <DialogContent>
    <DialogContentText>
      Are you sure you want to place this order?
    </DialogContentText>
  </DialogContent>
  <DialogActions>
    <Button onClick={() => setOpenConfirmDialog(false)} color="secondary">
      Cancel
    </Button>
    <Button onClick={handleConfirmOrder} color="primary">
      Confirm
    </Button>
  </DialogActions>
</Dialog>


      {/* Cart Summary */}
      <Box sx={{
        position: 'sticky',
        top: '80px', // Adjust based on your header height
        height: '100vh',
        overflowY: 'auto',
        backgroundColor: 'background.paper',
        p: 2,
        boxShadow: 3,
        borderRadius: 2
        }}>
          <Typography variant="h5" gutterBottom>
            Cart Summary
            </Typography>
            <Typography variant="subtitle1">
              Items: {cart.reduce((count, item) => count + item.quantity, 0)}
              </Typography>
              <Typography variant="subtitle1">
                Total: £{cart.reduce((total, item) => total + item.price * item.quantity, 0).toFixed(2)}
                </Typography>
      {/* Cart Items List */}
      {cart.map((item) => (
  <Stack key={item.itemId} direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 1 }}>
    <Typography tabIndex={0} aria-label={`${item.name}, quantity ${item.quantity}`}>
      {item.name} ({item.quantity})
    </Typography>
    <Typography>£{(item.price * item.quantity).toFixed(2)}</Typography>
    
    {/* Quantity Controls */}
    <Stack direction="row" spacing={1}>
      <Button size="small" onClick={() => updateQuantity(item.itemId, -1)}>-</Button>
      <Button size="small" onClick={() => updateQuantity(item.itemId, 1)}>+</Button>
      <Button size="small" color="error" onClick={() => removeFromCart(item.itemId)}>Remove</Button>
    </Stack>
  </Stack>
))}

      {/* Clear Cart and Place Order Buttons */}

      <Tooltip title="Remove all items from the cart" arrow>
  <Button 
    variant="contained" 
    color="secondary" 
    sx={{ mt: 2, mr: 2 }} 
    onClick={clearCart}
    aria-label="Remove all items from the cart"
  >
    Clear Cart
  </Button>
</Tooltip>

<Tooltip title="Confirm and place your order" arrow>
  <Button 
    variant="contained" 
    color="success" 
    sx={{ mt: 2 }} 
    onClick={() => setOpenConfirmDialog(true)}
    disabled={isLoading}
    aria-label="Confirm and place your order"
  >
    {isLoading ? <CircularProgress size={24} /> : "Place Order"}
  </Button>
</Tooltip>






     </Box>
      
      {/* Order Confirmation Message */}
      {orderSuccess && (
        <Alert severity="success" sx={{ mt: 2 }}>
          Order placed successfully!
        </Alert>
      )}
      {orderError && (
        <Alert severity="error" sx={{ mt: 2 }}>
          {orderError}
        </Alert>
)}

    </Container>
  );
}

export default Menu;


