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
  IconButton
} from "@mui/material";
import { Link as RouterLink } from 'react-router-dom';
import { API_ENDPOINT } from "../config";
import { allergenSymbols, dietarySymbols } from '../utils/foodInfoSymbols';
import { styled } from '@mui/material/styles';
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import ArrowDownwardIcon from '@mui/icons-material/ArrowDownward';

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

function Menu({ isEmbedded }) {
  const [menuItems, setMenuItems] = useState([]);
  const [error, setError] = useState();
  const theme = useTheme();

  const [cart, setCart] = useState([]);
  const [orderSuccess, setOrderSuccess] = useState(false);

  const [showWaiterButton, setShowWaiterButton] = useState(true);
  const [waiterMessage, setWaiterMessage] = useState(null);
  const [waiterError, setWaiterError] = useState(null);

  const [selectedDietary, setSelectedDietary] = useState([]);
  const [selectedAllergens, setSelectedAllergens] = useState([]);
  const [priceRange, setPriceRange] = useState([0, 100]);
  const [maxPriceValue, setMaxPriceValue] = useState(100);
  const [caloriesRange, setCaloriesRange] = useState([0, 1000]);
  const [maxCaloriesValue, setMaxCaloriesValue] = useState(1000);
  const [sortBy, setSortBy] = useState('');
  const [sortDirection, setSortDirection] = useState('asc');

  const FilterSlider = styled(Slider)({
    color: '#3a8589',
    height: 8,
    '& .MuiSlider-thumb': {
      height: 24,
      width: 24,
      backgroundColor: '#fff',
      border: '2px solid currentColor',
      '&:focus, &:hover, &.Mui-active': {
        boxShadow: '0 0 0 8px rgba(58, 133, 137, 0.16)',
      },
    },
    '& .MuiSlider-track': {
      height: 8,
      borderRadius: 4,
    },
    '& .MuiSlider-rail': {
      height: 8,
      borderRadius: 4,
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
      
      const maxCalories = Math.ceil(Math.max(...menuItems.map(item => item.caloricInfo)));
      setMaxCaloriesValue(maxCalories);
      setCaloriesRange([0, maxCalories]);
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
      // First sort by availability (available items first)
      const availabilitySort = Number(b.available) - Number(a.available);
      
      // If items have different availability, that takes precedence
      if (availabilitySort !== 0) return availabilitySort;
      
      // Then apply the selected sort criteria with direction
      let comparison = 0;
      
      if(sortBy === 'Alphabetical') {
        comparison = a.name.localeCompare(b.name);
      }
      else if(sortBy === 'Price') {
        comparison = a.price - b.price;
      }
      else if(sortBy === 'Popularity') {
        comparison = b.popularity - a.popularity;
      }
      else if(sortBy === 'Calories') {
        comparison = a.caloricInfo - b.caloricInfo;
      }
      
      // Apply sorting direction
      return sortDirection === 'asc' ? comparison : -comparison;
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
    <Container maxWidth="xl" sx={{ py: 3 }}>
      {/* Only show navigation buttons when not embedded */}
      {!isEmbedded && (
        <>
          {/* Navigation Buttons */}
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

            {showWaiterButton && (
              <Button 
                variant="contained" 
                color="primary" 
                onClick={callWaiter}
                startIcon={<span role="img" aria-label="waiter">👨‍🍳</span>}
                sx={{ fontWeight: 'medium' }}
              >
                Request Waiter
              </Button>
            )}
          </Box>

          {/* Notifications */}
          <Box sx={{ mb: 4 }}>
            {waiterMessage && <Alert severity="success" sx={{ mb: 2 }}>{waiterMessage}</Alert>}
            {waiterError && <Alert severity="error" sx={{ mb: 2 }}>{waiterError}</Alert>}
          </Box>
        </>
      )}

      {/* Page Title - Show only when not embedded */}
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

      {/* Filter Controls - Show only when not embedded */}
      {!isEmbedded && (
        <Box sx={{ 
          mb: 3, 
          width: '100%',
          display: 'grid', 
          gridTemplateColumns: {
            xs: '1fr',           // Stack everything on mobile
            sm: 'repeat(2, 1fr)', // 2 items per row on tablet
            md: 'repeat(2, 1fr)'  // 2 items per row on desktop
          },
          gap: 2
        }}>
          {/* Price Range */}
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

          {/* Calories Range */}
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

      {/* Filter Dropdowns */}
      {!isEmbedded && (
        <Box sx={{ 
          mb: 3, 
          width: '100%',
          display: 'grid', 
          gridTemplateColumns: {
            xs: '1fr',           // Stack everything on mobile
            sm: 'repeat(2, 1fr)', // 2 items per row on tablet
            md: 'repeat(3, 1fr)'  // 3 items per row on desktop
          },
          gap: 2
        }}>
          {/* Filters and Sorting */}
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

          {/* Dietary Restrictions */}
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

          {/* Allergens */}
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
            <Table sx={{ minWidth: 650 }}>
              <TableHead>
                <TableRow sx={{ backgroundColor: 'primary.main' }}>
                  <TableCell sx={{ 
                    color: theme.palette.mode === 'dark' ? 'black' : 'white', 
                    fontWeight: 'bold',
                    whiteSpace: 'nowrap' 
                  }}>
                    Name
                  </TableCell>
                  <TableCell sx={{ 
                    color: theme.palette.mode === 'dark' ? 'black' : 'white', 
                    fontWeight: 'bold',
                    whiteSpace: 'nowrap' 
                  }}>
                    Price
                  </TableCell>
                  <TableCell sx={{ 
                    color: theme.palette.mode === 'dark' ? 'black' : 'white', 
                    fontWeight: 'bold',
                    whiteSpace: 'nowrap',
                    display: { xs: 'none', sm: 'table-cell' } 
                  }}>
                    Calories
                  </TableCell>
                  <TableCell sx={{ 
                    color: theme.palette.mode === 'dark' ? 'black' : 'white', 
                    fontWeight: 'bold',
                    whiteSpace: 'nowrap',
                    display: { xs: 'none', md: 'table-cell' } 
                  }}>
                    Dietary Info
                  </TableCell>
                  <TableCell sx={{ 
                    color: theme.palette.mode === 'dark' ? 'black' : 'white', 
                    fontWeight: 'bold',
                    whiteSpace: 'nowrap',
                    display: { xs: 'none', md: 'table-cell' } 
                  }}>
                    Allergens
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
                    <TableCell sx={{ whiteSpace: 'nowrap' }}>{item.name}</TableCell>
                    <TableCell sx={{ whiteSpace: 'nowrap' }}>£{item.price.toFixed(2)}</TableCell>
                    <TableCell sx={{ 
                      whiteSpace: 'nowrap',
                      display: { xs: 'none', sm: 'table-cell' } 
                    }}>
                      {item.caloricInfo} kcal
                    </TableCell>
                    <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }}>
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
                    <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }}>
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
                          size="small"
                          onClick={() => addToCart(item)}>
                          Add to Order
                        </Button>
                      )}
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </Box>
        </Paper>
      )}

      {/* Only show order section when not embedded */}
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
    </Container>
  );
}

// Set default props
Menu.defaultProps = {
  isEmbedded: false
};

export default Menu;
