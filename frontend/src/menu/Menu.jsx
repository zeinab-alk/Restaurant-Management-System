import React, { useState, useRef, useEffect } from "react";
import { Dialog, DialogTitle, DialogContent, DialogActions } from "@mui/material";
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
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  TextField,
  FormGroup,
  FormControlLabel,
  Checkbox,
  Slider,
  Grid,
  ListItemText,
  Autocomplete,
  Box
} from "@mui/material";
import { Link as RouterLink } from 'react-router-dom';
import { API_ENDPOINT } from "../config";
import { allergenSymbols, dietarySymbols } from '../utils/foodInfoSymbols';
import { styled } from '@mui/material/styles';

const useContainerWidth = () => {
  const containerRef = useRef(null);
  const [width, setWidth] = useState(0);

  useEffect(() => {
    const updateWidth = () => {
      if (containerRef.current) {
        setWidth(containerRef.current.offsetWidth);
      }
    };
    
    updateWidth();
    window.addEventListener('resize', updateWidth);
    return () => window.removeEventListener('resize', updateWidth);
  }, []);

  return [containerRef, width];
};

function Menu() {
  const [menuItems, setMenuItems] = useState([]);
  const [error, setError] = useState();
  const theme = useTheme();

  const [cart, setCart] = useState([]);
  const [orderSuccess, setOrderSuccess] = useState(false);

  const [openDialog, setOpenDialog] = useState(false);
  const [tableNumber, setTableNumber] = useState("");
  const [waiterSuccess, setWaiterSuccess] = useState(null);
  const [waiterError, setWaiterError] = useState(null);

  const [selectedDietary, setSelectedDietary] = useState([]);
  const [selectedAllergens, setSelectedAllergens] = useState([]);
  const [priceRange, setPriceRange] = useState([0, 100]);
  const [maxPriceValue, setMaxPriceValue] = useState(100);
  const [sortBy, setSortBy] = useState('');

  const PriceSlider = styled(Slider)({
    color: '#3a8589',
    height: 8,
    '& .MuiSlider-thumb': {
      height: 24,
      width: 24,
    },
  });

  const dietaryOptions = [
    'Vegetarian',
    'Vegan',
    'Gluten-Free',
    'Dairy-Free',
    'Halal',
    'Kosher'
  ];

  const allergenOptions = [
    'Milk',
    'Eggs',
    'Fish',
    'Shellfish',
    'Tree Nuts',
    'Peanuts',
    'Wheat',
    'Soy',
    'Sesame'
  ];

  const [containerRef, containerWidth] = useContainerWidth();

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

  React.useEffect(() => {
    if (menuItems.length > 0) {
      const maxPrice = Math.ceil(Math.max(...menuItems.map(item => item.price)));
      setMaxPriceValue(maxPrice);
      setPriceRange([0, maxPrice]);
    }
  }, [menuItems]);

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
      orderTime: new Date().toISOString(),
      status: "Pending",
      menuItems: cart.map(item => ({
        itemId: item.itemId,
        name: item.name,
        price: item.price
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

  const filteredMenuItems = menuItems
    .filter(item => {
      const price = item.price;
      const meetsPrice = price >= priceRange[0] && price <= priceRange[1];
      const hasDietary = selectedDietary.every(d => item.dietaryRestrictions.includes(d));
      const excludesAllergens = selectedAllergens.length === 0 || 
                                !selectedAllergens.some(a => item.allergenInformation.includes(a));
      return hasDietary && excludesAllergens && meetsPrice;
    })
    .sort((a, b) => {
      // First sort by availability (available items first)
      const availabilitySort = Number(b.available) - Number(a.available);
      
      // Then apply the selected sort criteria
      if(sortBy === 'Price (Low-High)') {
        return availabilitySort || a.price - b.price;
      }
      if(sortBy === 'Price (High-Low)') {
        return availabilitySort || b.price - a.price;
      }
      if(sortBy === 'Popularity') {
        return availabilitySort || b.popularity - a.popularity;
      }
      if(sortBy === 'Calories') {
        return availabilitySort || a.caloricInfo - b.caloricInfo;
      }
      return availabilitySort;
    });

  const priceBounds = menuItems.reduce(
    (acc, item) => [Math.min(acc[0], item.price), Math.max(acc[1], item.price)],
    [Infinity, -Infinity]
  );

  const renderDynamicTags = (containerWidth) => (value, getTagProps) => {
    const CHIP_WIDTH = 90; // Average width per tag
    const COUNTER_WIDTH = 50; // Width reserved for "+X" counter
    const availableWidth = containerWidth - COUNTER_WIDTH;
    
    // Calculate max visible tags that can fit
    const maxVisible = Math.max(1, Math.floor(availableWidth / CHIP_WIDTH));
    const visibleTags = value.slice(0, maxVisible);
    const remainingTags = value.length - maxVisible;

    return (
      <div style={{ 
        display: 'flex',
        flexWrap: 'nowrap',
        alignItems: 'center',
        gap: '4px',
        padding: '2px 0',
        maxWidth: '100%',
        overflow: 'hidden',
        height: 32
      }}>
        {visibleTags.map((option, index) => (
          <Chip
            {...getTagProps({ index })}
            key={option}
            label={option}
            size="small"
            sx={{ 
              maxWidth: CHIP_WIDTH,
              textOverflow: 'ellipsis',
              overflow: 'hidden',
              '& .MuiChip-label': { 
                padding: '0 6px',
                whiteSpace: 'nowrap'
              }
            }}
          />
        ))}
        {remainingTags > 0 && (
          <Chip
            label={`+${remainingTags}`}
            size="small"
            sx={{ 
              bgcolor: 'warning.light',
              color: 'warning.contrastText',
              fontWeight: 'bold',
              px: 0.5,
              flexShrink: 0,
              minWidth: COUNTER_WIDTH
            }}
            title={value.slice(maxVisible).join(', ')}
          />
        )}
      </div>
    );
  };

  return (
    <Container>
      {/* Back to Home Button */}
      <Stack direction="row" spacing={2} sx={{ mb: 2 }}>
        <Button component={RouterLink} to="/" variant="contained" color="success">
          Back to Home
        </Button>
      </Stack>

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

      <Stack 
        direction="row" 
        spacing={2} 
        sx={{ 
          mb: 2, 
          alignItems: 'center',
          justifyContent: 'space-between',
          width: '100%',
          '& > *': { flex: 1, maxWidth: 220 }
        }}
      >
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <Typography variant="body1" sx={{ minWidth: 100 }}>
            £{priceRange[0]} - £{priceRange[1]}
          </Typography>
          <PriceSlider
            value={priceRange}
            onChange={(_, newValue) => setPriceRange(newValue)}
            valueLabelDisplay="auto"
            min={0}
            max={maxPriceValue}
            sx={{ flex: 2 }}
          />
        </Box>

        <FormControl sx={{ width: 200 }}>
          <InputLabel>Sort By</InputLabel>
          <Select
            value={sortBy}
            onChange={(e) => setSortBy(e.target.value)}
            label="Sort By"
          >
            <MenuItem value="">
              <em>None</em>
            </MenuItem>
            <MenuItem value="Price (Low-High)">Price (Low-High)</MenuItem>
            <MenuItem value="Price (High-Low)">Price (High-Low)</MenuItem>
            <MenuItem value="Popularity">Popularity</MenuItem>
            <MenuItem value="Calories">Calories</MenuItem>
          </Select>
        </FormControl>

        <Autocomplete
          multiple
          options={dietaryOptions}
          value={selectedDietary}
          onChange={(_, newValue) => setSelectedDietary(newValue)}
          renderInput={(params) => <TextField {...params} label="Dietary" />}
          renderTags={renderDynamicTags(containerWidth)}
          limitTags={2}
          sx={{ 
            width: 200,
            '& .MuiAutocomplete-inputRoot': {
              flexWrap: 'nowrap'
            }
          }}
          size="small"
        />

        <Autocomplete
          multiple
          options={allergenOptions}
          value={selectedAllergens}
          onChange={(_, newValue) => setSelectedAllergens(newValue)}
          renderInput={(params) => (
            <TextField 
              {...params} 
              label="Exclude Allergens" 
              sx={{ 
                '& .MuiInputBase-root': { 
                  minHeight: 40, // Fixed height
                  py: 0.5 // Vertical padding
                }
              }}
            />
          )}
          renderTags={renderDynamicTags(containerWidth)}
          sx={{ 
            width: 200,
            '& .MuiAutocomplete-inputRoot': {
              flexWrap: 'nowrap',
              paddingRight: '8px !important' // Prevent icon overlap
            }
          }}
          size="small"
        />
      </Stack>

      {filteredMenuItems.length === 0 ? (
        <Typography variant="h6" align="center" sx={{ mt: 4 }}>
          No items match your filters
        </Typography>
      ) : (
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
              {filteredMenuItems.map((item) => (
                <TableRow 
                  key={item.itemId}
                  sx={{ 
                    opacity: item.available ? 1 : 0.5,
                    backgroundColor: theme => 
                      item.available 
                        ? theme.palette.mode === 'dark' 
                          ? 'rgba(255, 255, 255, 0.02)' 
                          : 'rgba(0, 0, 0, 0.02)'
                        : 'transparent',
                    '&:hover': {
                      backgroundColor: theme => 
                        item.available 
                          ? theme.palette.action.hover
                          : 'transparent'
                    }
                  }}
                >
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