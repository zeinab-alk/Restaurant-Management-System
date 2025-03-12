import React, { useState, useRef, useEffect } from "react";
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
  Box,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions
} from "@mui/material";
import { Link as RouterLink } from 'react-router-dom';
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import ArrowDownwardIcon from '@mui/icons-material/ArrowDownward';
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

function Menu({ isEmbedded = false }) {
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
  const [caloriesRange, setCaloriesRange] = useState([0, 2000]);
  const [maxCaloriesValue, setMaxCaloriesValue] = useState(2000);
  const [sortBy, setSortBy] = useState('');
  const [sortDirection, setSortDirection] = useState('asc');

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

  useEffect(() => {
    axios
      .get(`${API_ENDPOINT}/api/menuItems`)
      .then((response) => {
        setMenuItems(response.data);
      })
      .catch((response) => {
        setError(response.message);
      });
  }, []);

  useEffect(() => {
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

  const placeOrder = async () => {
    if (cart.length === 0) {
      alert("Your order is empty!");
      return;
    }

    const orderData = {
      orderTime: new Date().toISOString(),
      status: "PENDING",
      menuItems: cart.map(item => ({
        itemId: item.itemId,
        name: item.name,
        price: item.price
      }))
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

  const callWaiter = () => {
    setOpenDialog(true);
  };

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
      const calories = item.caloricInfo;
      const meetsPrice = price >= priceRange[0] && price <= priceRange[1];
      const meetsCalories = calories >= caloriesRange[0] && calories <= caloriesRange[1];
      const hasDietary = selectedDietary.every(d => item.dietaryRestrictions.includes(d));
      const excludesAllergens = selectedAllergens.length === 0 || 
                                !selectedAllergens.some(a => item.allergenInformation.includes(a));
      return hasDietary && excludesAllergens && meetsPrice && meetsCalories;
    })
    .sort((a, b) => {
      const availabilitySort = Number(b.available) - Number(a.available);
      
      if (availabilitySort !== 0) return availabilitySort;
      
      let comparison = 0;
      
      if(sortBy === 'Alphabetical') {
        comparison = a.name.localeCompare(b.name);
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
      
      return sortDirection === 'asc' ? comparison : -comparison;
    });

  const priceBounds = menuItems.reduce(
    (acc, item) => [Math.min(acc[0], item.price), Math.max(acc[1], item.price)],
    [Infinity, -Infinity]
  );

  const renderDynamicTags = (containerWidth) => (value, getTagProps) => {
    const CHIP_WIDTH = 90;
    const COUNTER_WIDTH = 50;
    const availableWidth = containerWidth - COUNTER_WIDTH;
    
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
    <Container maxWidth="xl" sx={{ py: 3 }}>
      {!isEmbedded && (
        <>
          <Box sx={{ 
            display: 'flex', 
            flexDirection: { xs: 'column', sm: 'row' },
            gap: 2,
            mb: 4
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
              variant="contained" 
              color="primary" 
              onClick={callWaiter}
              startIcon={<span role="img" aria-label="waiter">👨‍🍳</span>}
              sx={{ fontWeight: 'medium' }}
            >
              Request Waiter
            </Button>
          </Box>

          <Box sx={{ mb: 4 }}>
            {waiterSuccess && <Alert severity="success" sx={{ mb: 2 }}>{waiterSuccess}</Alert>}
            {waiterError && <Alert severity="error" sx={{ mb: 2 }}>{waiterError}</Alert>}
          </Box>
        </>
      )}

      {!isEmbedded && (
        <Typography 
          variant="h3" 
          component="h1" 
          gutterBottom 
          align="center" 
          sx={{ 
            mb: 4,
            fontWeight: 'bold',
            fontSize: { xs: '2rem', sm: '2.5rem', md: '3rem' }
          }}
        >
          Our Menu
        </Typography>
      )}

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      {!error && menuItems.length < 1 && (
        <Alert severity="warning" sx={{ mb: 2 }}>No menu items available</Alert>
      )}

      {!isEmbedded && (
        <Box sx={{ 
          mb: 3, 
          width: '100%',
          display: 'grid', 
          gridTemplateColumns: {
            xs: '1fr',
            sm: 'repeat(2, 1fr)',
            md: 'repeat(2, 1fr)'
          },
          gap: 2
        }}>
          <Box sx={{ 
            p: 2,
            bgcolor: 'background.paper',
            borderRadius: 1,
            boxShadow: 1
          }}>
            <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
              Price: £{priceRange[0]} - £{priceRange[1]}
            </Typography>
            
            <Slider
              getAriaLabel={() => 'Price range'}
              value={priceRange}
              onChange={(_, newValue) => setPriceRange(newValue)}
              valueLabelDisplay="auto"
              min={0}
              max={maxPriceValue}
              sx={{ color: '#3a8589', mb: 2 }}
              valueLabelFormat={(value) => `£${value}`}
            />
            
            <Box sx={{ display: 'flex', gap: 2 }}>
              <TextField 
                label="Min £" 
                type="number"
                size="small"
                value={priceRange[0]}
                inputProps={{ min: 0, max: priceRange[1], step: 0.5 }}
                onChange={(e) => {
                  const value = parseFloat(e.target.value);
                  if (!isNaN(value) && value >= 0 && value <= priceRange[1]) {
                    setPriceRange([value, priceRange[1]]);
                  }
                }}
                sx={{ flex: 1 }}
              />
              <TextField
                label="Max £"
                type="number"
                size="small"
                value={priceRange[1]}
                inputProps={{ min: priceRange[0], max: maxPriceValue, step: 0.5 }}
                onChange={(e) => {
                  const value = parseFloat(e.target.value);
                  if (!isNaN(value) && value >= priceRange[0] && value <= maxPriceValue) {
                    setPriceRange([priceRange[0], value]);
                  }
                }}
                sx={{ flex: 1 }}
              />
            </Box>
          </Box>

          <Box sx={{ 
            p: 2,
            bgcolor: 'background.paper',
            borderRadius: 1,
            boxShadow: 1
          }}>
            <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
              Calories: {caloriesRange[0]} - {caloriesRange[1]} kcal
            </Typography>
            
            <Slider
              getAriaLabel={() => 'Calories range'}
              value={caloriesRange}
              onChange={(_, newValue) => setCaloriesRange(newValue)}
              valueLabelDisplay="auto"
              min={0}
              max={maxCaloriesValue}
              sx={{ color: '#3a8589', mb: 2 }}
              valueLabelFormat={(value) => `${value} kcal`}
            />
            
            <Box sx={{ display: 'flex', gap: 2 }}>
              <TextField
                label="Min kcal"
                type="number"
                size="small"
                value={caloriesRange[0]}
                inputProps={{ min: 0, max: caloriesRange[1], step: 10 }}
                onChange={(e) => {
                  const value = parseInt(e.target.value);
                  if (!isNaN(value) && value >= 0 && value <= caloriesRange[1]) {
                    setCaloriesRange([value, caloriesRange[1]]);
                  }
                }}
                sx={{ flex: 1 }}
              />
              <TextField
                label="Max kcal"
                type="number"
                size="small"
                value={caloriesRange[1]}
                inputProps={{ min: caloriesRange[0], max: maxCaloriesValue, step: 10 }}
                onChange={(e) => {
                  const value = parseInt(e.target.value);
                  if (!isNaN(value) && value >= caloriesRange[0] && value <= maxCaloriesValue) {
                    setCaloriesRange([caloriesRange[0], value]);
                  }
                }}
                sx={{ flex: 1 }}
              />
            </Box>
          </Box>
        </Box>
      )}

      {!isEmbedded && (
        <Box sx={{ 
          mb: 3, 
          width: '100%',
          display: 'grid', 
          gridTemplateColumns: {
            xs: '1fr',
            sm: 'repeat(2, 1fr)',
            md: 'repeat(3, 1fr)'
          },
          gap: 2
        }}>
          <Box sx={{ 
            p: 2,
            bgcolor: 'background.paper',
            borderRadius: 1,
            boxShadow: 1
          }}>
            <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
              Sort
            </Typography>
            
            <Box sx={{ display: 'flex', gap: 1, alignItems: 'center' }}>
              <FormControl fullWidth>
                <InputLabel>Sort By</InputLabel>
                <Select
                  value={sortBy}
                  onChange={(e) => setSortBy(e.target.value)}
                  label="Sort By"
                  size="small"
                >
                  <MenuItem value="">
                    <em>None</em>
                  </MenuItem>
                  <MenuItem value="Alphabetical">Alphabetical</MenuItem>
                  <MenuItem value="Price">Price</MenuItem>
                  <MenuItem value="Popularity">Popularity</MenuItem>
                  <MenuItem value="Calories">Calories</MenuItem>
                </Select>
              </FormControl>
              <IconButton 
                onClick={() => setSortDirection(prev => prev === 'asc' ? 'desc' : 'asc')}
                color="primary"
                sx={{ 
                  border: '1px solid',
                  borderColor: 'divider',
                  p: '8px',
                  borderRadius: '4px',
                  backgroundColor: 'background.paper',
                  minWidth: '80px',
                  display: 'flex',
                  justifyContent: 'center',
                  gap: 0.5
                }}
              >
                {sortDirection === 'asc' ? (
                  <>
                    <ArrowUpwardIcon fontSize="small" />
                    <Typography variant="caption" sx={{ fontWeight: 'bold' }}>ASC</Typography>
                  </>
                ) : (
                  <>
                    <ArrowDownwardIcon fontSize="small" />
                    <Typography variant="caption" sx={{ fontWeight: 'bold' }}>DESC</Typography>
                  </>
                )}
              </IconButton>
            </Box>
          </Box>

          <Box sx={{ 
            p: 2,
            bgcolor: 'background.paper',
            borderRadius: 1,
            boxShadow: 1
          }}>
            <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
              Dietary Restrictions
            </Typography>
            
            <Autocomplete
              multiple
              options={dietaryOptions}
              value={selectedDietary}
              onChange={(_, newValue) => setSelectedDietary(newValue)}
              renderInput={(params) => <TextField {...params} label="Dietary" size="small" />}
              renderTags={renderDynamicTags(containerWidth)}
              limitTags={3}
            />
          </Box>

          <Box sx={{ 
            p: 2,
            bgcolor: 'background.paper',
            borderRadius: 1,
            boxShadow: 1
          }}>
            <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
              Allergen Information
            </Typography>
            
            <Autocomplete
              multiple
              options={allergenOptions}
              value={selectedAllergens}
              onChange={(_, newValue) => setSelectedAllergens(newValue)}
              renderInput={(params) => (
                <TextField 
                  {...params} 
                  label="Exclude Allergens" 
                  size="small"
                />
              )}
              renderTags={renderDynamicTags(containerWidth)}
              limitTags={2}
            />
          </Box>
        </Box>
      )}

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

      {!isEmbedded && (
        <Paper elevation={3} sx={{ 
          p: 3, 
          mb: 3, 
          borderRadius: 1,
          boxShadow: 2
        }}>
          <Typography variant="h5" gutterBottom>Your Order</Typography>
          
          {cart.length === 0 ? (
            <Typography variant="body1" color="text.secondary">No items in cart</Typography>
          ) : (
            <Box>
              <Typography variant="subtitle1" gutterBottom sx={{ fontWeight: 'medium' }}>
                Items: {cart.length}
              </Typography>
              
              <Stack spacing={2} sx={{ mb: 3 }}>
                {cart.map((item, index) => (
                  <Paper key={index} sx={{ 
                    p: 2, 
                    display: 'flex', 
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    boxShadow: 1
                  }}>
                    <Box>
                      <Typography variant="body1">{item.name}</Typography>
                      <Typography variant="body2" color="text.secondary">£{item.price.toFixed(2)}</Typography>
                    </Box>
                    <Button 
                      variant="outlined" 
                      color="error" 
                      size="small" 
                      onClick={() => removeFromCart(index)}
                      startIcon={<span role="img" aria-label="remove">🗑️</span>}>
                      Remove
                    </Button>
                  </Paper>
                ))}
              </Stack>
              
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Typography variant="h6">
                  Total: £{cart.reduce((sum, item) => sum + item.price, 0).toFixed(2)}
                </Typography>
                <Button 
                  variant="contained" 
                  color="success" 
                  size="large"
                  onClick={placeOrder}
                  startIcon={<span role="img" aria-label="order">✅</span>}>
                  Place Order
                </Button>
              </Box>
            </Box>
          )}
          
          {orderSuccess && (
            <Alert severity="success" sx={{ mt: 2 }}>
              Order placed successfully! A waiter will be with you shortly.
            </Alert>
          )}
        </Paper>
      )}

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