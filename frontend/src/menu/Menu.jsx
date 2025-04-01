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
  DialogActions,
  CardMedia,
  Popover
} from "@mui/material";
import { Link as RouterLink } from 'react-router-dom';
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import ArrowDownwardIcon from '@mui/icons-material/ArrowDownward';
import { API_ENDPOINT } from "../config";
import { allergenSymbols, dietarySymbols } from '../utils/foodInfoSymbols';
import { styled } from '@mui/material/styles';
import { toast } from 'react-hot-toast';
import Fab from '@mui/material/Fab';

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
  const [activeRequestId, setActiveRequestId] = useState(null);
  const [requestStatus, setRequestStatus] = useState(null);

  const [selectedDietary, setSelectedDietary] = useState([]);
  const [selectedAllergens, setSelectedAllergens] = useState([]);
  const [priceRange, setPriceRange] = useState([0, 100]);
  const [maxPriceValue, setMaxPriceValue] = useState(100);
  const [caloriesRange, setCaloriesRange] = useState([0, 2000]);
  const [maxCaloriesValue, setMaxCaloriesValue] = useState(2000);
  const [sortBy, setSortBy] = useState('');
  const [sortDirection, setSortDirection] = useState('asc');
  
  const [anchorEl, setAnchorEl] = useState(null);
  const [popoverImage, setPopoverImage] = useState(null);

  const handlePopoverOpen = (event, imageUrl) => {
    setAnchorEl(event.currentTarget);
    setPopoverImage(imageUrl);
  };

  const handlePopoverClose = () => {
    setAnchorEl(null);
    setPopoverImage(null);
  };

  const open = Boolean(anchorEl);

  const [orderNumber, setOrderNumber] = useState(null); 
  const [cardNumber, setCardNumber] = useState("");
  const [expiryDate, setExpiryDate] = useState("");
  const [cvv, setCvv] = useState("");
  const [cardHolderName, setCardHolderName] = useState("");
  const [paymentMessage, setPaymentMessage] = useState("");
  const [paymentError, setPaymentError] = useState(false);
  const [showFilters, setShowFilters] = useState(false);

  const PriceSlider = styled(Slider)({
    color: theme.palette.primary.main,
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

  useEffect(() => {
    let pollInterval;
    
    if (activeRequestId) {
      // Poll for request status every 5 seconds
      pollInterval = setInterval(async () => {
        try {
          const response = await axios.get(`${API_ENDPOINT}/api/help-requests/${activeRequestId}`);
          const status = response.data.status;
          setRequestStatus(status);
          
          // If request is resolved, clear the notification
          if (status === 'RESOLVED') {
            setWaiterSuccess(null);
            setActiveRequestId(null);
            setRequestStatus(null);
          }
        } catch (err) {
          console.error("Error polling request status:", err);
        }
      }, 5000);
    }

    return () => {
      if (pollInterval) {
        clearInterval(pollInterval);
      }
    };
  }, [activeRequestId]);

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
      const response = await axios.post(`${API_ENDPOINT}/api/orders`, orderData); 
     setOrderSuccess(true);
     setOrderNumber(response.data.orderNumber); 
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
      await axios.post(
        `${API_ENDPOINT}/api/help-requests`, 
        { tableNumber: parseInt(tableNumber, 10), waiterId: 1 }, 
        { headers: { "Content-Type": "application/json" } }
      );
  
      setWaiterSuccess(`Waiter has been notified you need assistance at table #${tableNumber}`);
      setWaiterError(null);
      
      // Clear the success message after 5 seconds
      setTimeout(() => {
        setWaiterSuccess(null);
      }, 5000);
    } catch (err) {
      console.error("API Error:", err.response?.data || err.message);
      setWaiterSuccess(null);
      setWaiterError("Error notifying waiter. Please try again.");
    }
  
    setOpenDialog(false);
    setTableNumber("");
  };

  const cancelWaiterRequest = async () => {
    try {
      if (!activeRequestId) {
        console.error("No active request to cancel");
        return;
      }
      await axios.post(`${API_ENDPOINT}/api/help-requests/${activeRequestId}/cancel`);
      setWaiterSuccess(null);
      setActiveRequestId(null);
      setRequestStatus(null);
      toast.success("Request cancelled successfully");
    } catch (err) {
      console.error("Error cancelling request:", err);
      toast.error("Error cancelling request. Please try again.");
    }
  };

  const handlePayment = async () => {
    try {
      const response = await axios.post(`${API_ENDPOINT}/api/orders/pay`, {
        orderNumber,
        cardNumber: cardNumber.replace(/\s/g, ""), 
        expiryDate,
        cardHolderName,
        cvv
      });
  
      setPaymentMessage(response.data);
      setPaymentError(false);
    } catch (err) {
      console.error("Payment error:", err);
      setPaymentMessage(err.response?.data || "Error processing payment.");
      setPaymentError(true);
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
    <>
      <Box
  sx={{
    display: 'flex',
    justifyContent: 'flex-end',
    position: 'sticky',
    top: 0,
    zIndex: 1100,
    padding: 2,
  }}
>
  <Box
    sx={{
      background: 'linear-gradient(to right, #d48856, #f2b67b)',
      borderRadius: 5,
      px: 2,
      py: 1,
      boxShadow: 3,
    }}
  >
    <Button
      component={RouterLink}
      to="/"
      variant="contained"
      color="success"
      startIcon={<span role="img" aria-label="home">🏡</span>}
      sx={{
        borderRadius: 4,
        fontWeight: 'bold',
        px: 3,
        backgroundColor: '#81c784',
        '&:hover': {
          backgroundColor: '#66bb6a',
        },
      }}
    >
      BACK TO HOME
    </Button>
  </Box>
</Box>

  
      <Container maxWidth="xl" sx={{ py: 3, position: 'relative' }} ref={containerRef}>
      {!isEmbedded && (
        <>
          <Box
  sx={{
    mb: 4,
    px: 2,
    py: 3,
    borderRadius: 3,
    background: 'linear-gradient(to right, #fff3e0, #ffe0b2)',
    boxShadow: 3,
    display: 'flex',
    flexDirection: { xs: 'column', sm: 'row' },
    alignItems: 'center',
    justifyContent: 'center', 
    gap: 2,
    position: 'relative',
  }}
>
  <Typography
    variant="h3"
    component="h1"
    gutterBottom
    sx={{
      fontWeight: 'bold',
      fontFamily: 'cursive',
      color: 'black',
      textShadow: '1px 1px 2px #00000033',
      fontSize: { xs: '2rem', sm: '2.5rem', md: '3rem' },
      display: 'flex',
      alignItems: 'center',
      gap: 1,
      textAlign: 'center'
    }}
  >
    🍸 Our Menu 🌯
  </Typography>

</Box>

        </>
      )}


      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      {!error && menuItems.length < 1 && (
        <Alert severity="warning" sx={{ mb: 2 }}>No menu items available</Alert>
      )}

<Button
  variant="contained"
  onClick={() => setShowFilters(prev => !prev)}
  sx={{
    mb: 2,
    fontWeight: 'bold',
    bgcolor: '#212121',
    color: 'white',
    '&:hover': {
      bgcolor: '#424242'
    },
    px: 3,
    py: 1,
    borderRadius: 3,
    textTransform: 'none',
    fontSize: '1rem'
  }}
>
  ☰ FILTERS
</Button>
 
{showFilters && (
  <>
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
              sx={{ color: theme.palette.primary.main, mb: 2 }}
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
              sx={{ color: theme.palette.primary.main, mb: 2 }}
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
    </>
)}


      {filteredMenuItems.length === 0 ? (
        <Typography variant="h6" align="center" sx={{ mt: 4 }}>
          No items match your filters
        </Typography>
      ) : (
        <Grid container spacing={4} sx={{ mb: 4 }}>
  {filteredMenuItems.map(item => (
    <Grid item xs={12} sm={6} md={4} key={item.itemId}>
      <Paper elevation={3} sx={{
        p: 2,
        borderRadius: 3,
        backgroundColor: '#faf3e0',
        '&:hover': { boxShadow: 6 }
      }}>
        <Box sx={{ height: 180, overflow: 'hidden', borderRadius: 2, mb: 2 }}>
          <img 
            src={item.imageUrl || 'https://via.placeholder.com/150'} 
            alt={item.name}
            style={{ width: '100%', height: '100%', objectFit: 'cover' }}
          />
        </Box>
        <Typography variant="h6" sx={{ fontFamily: 'Raleway', color: '#2e2e2e', fontWeight: 'bold' }}>
          {item.name}
        </Typography>
        <Typography variant="body2" sx={{ mb: 1, color: 'black' }}>
          {item.caloricInfo} kcal • £{item.price.toFixed(2)}
        </Typography>
        <Stack direction="row" spacing={0.5} sx={{ mb: 1 }} flexWrap="wrap">
        {item.dietaryRestrictions?.map((d, i) => (
  <Tooltip title={d} key={i}>
    <span style={{ fontSize: '1.2rem', color: 'black' }}>{dietarySymbols[d]}</span>
  </Tooltip>
          ))}
          {item.allergenInformation?.map((a, i) => (
            <Tooltip title={`${a} (Allergen)`} key={i}>
              <span style={{ fontSize: '1.2rem', color: theme.palette.warning.main }}>
                {allergenSymbols[a]}
              </span>
            </Tooltip>
          ))}
        </Stack>
        <Button 
          fullWidth 
          variant="contained" 
          disabled={!item.available}
          sx={{
            bgcolor: item.available ? '#d72638' : 'grey.400',
            color: 'white',
            mt: 1,
            fontWeight: 600,
            '&:hover': { bgcolor: '#b71c1c' }
          }}
          onClick={() => addToCart(item)}
        >
          {item.available ? 'Add to Order' : 'Unavailable'}
        </Button>
      </Paper>
    </Grid>
  ))}
</Grid>
      )}

      {!isEmbedded && (
        <>
          <Paper elevation={3} sx={{ p: 3, mb: 3, borderRadius: 1, boxShadow: 2 }}>
            <Typography variant="h5" component="h2" sx={{ color: 'primary.main', mb: 2 }}>
              Your Order
            </Typography>
      
            {cart.length === 0 ? (
              <Typography variant="body1" color="text.secondary">No items in cart</Typography>
            ) : (
              <>
                <Stack spacing={2} sx={{ mb: 3 }}>
                  {cart.map((item, index) => (
                    <Paper key={index} sx={{ p: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center', boxShadow: 1 }}>
                      <Box>
                        <Typography variant="body1">{item.name}</Typography>
                        <Typography variant="body2" color="text.secondary">£{item.price.toFixed(2)}</Typography>
                      </Box>
                      <Button 
                        variant="outlined" 
                        color="error" 
                        size="small" 
                        onClick={() => removeFromCart(index)}
                        startIcon={<span role="img" aria-label="remove">🗑️</span>}
                      >
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
                    startIcon={<span role="img" aria-label="order">✅</span>}
                  >
                    Place Order
                  </Button>
                </Box>
              </>
            )}
          </Paper>
      
          {orderSuccess && (
            <>
              <Alert severity="success" sx={{ mt: 2 }}>
                Order placed successfully! A waiter will be with you shortly.
              </Alert>
      
              <Paper
  elevation={4}
  sx={{
    mt: 5,
    px: { xs: 2, sm: 4 },
    py: 4,
    borderRadius: 3, 
    background: 'linear-gradient(to right, #fff3e0, #ffe0b2)',
    boxShadow: 6,
  }}
>

  <Typography variant="h4" gutterBottom align="center" sx={{ fontWeight: 'bold', mb: 3 , color:'black' }}>
    💳 Pay Your Bill
  </Typography>

  <Grid container spacing={2}>
    <Grid item xs={12} sm={6}>
      <TextField
        fullWidth
        label="Card Number"
        type="tel"
        value={cardNumber}
        InputLabelProps={{ style: { color: 'black' } }}
        sx={{
          '& .MuiOutlinedInput-root': {
            '& fieldset': {
              borderColor: 'black',
            },
            '&:hover fieldset': {
              borderColor: 'black',
            },
            '&.Mui-focused fieldset': {
              borderColor: 'black',
            },
          },
          input: { color: 'black' }
        }}
        onChange={(e) => {
          const raw = e.target.value.replace(/\D/g, "").slice(0, 16);
          const spaced = raw.replace(/(.{4})/g, "$1 ").trim();
          setCardNumber(spaced);
        }}
      />
    </Grid>
    <Grid item xs={6} sm={3}>
      <TextField
        fullWidth
        label="Expiry (MM/YY)"
        type="tel"
        value={expiryDate}
        InputLabelProps={{ style: { color: 'black' } }}
        sx={{
          '& .MuiOutlinedInput-root': {
            '& fieldset': {
              borderColor: 'black',
            },
            '&:hover fieldset': {
              borderColor: 'black',
            },
            '&.Mui-focused fieldset': {
              borderColor: 'black',
            },
          },
          input: { color: 'black' }
        }}
        onChange={(e) => {
          let value = e.target.value.replace(/\D/g, "").slice(0, 4);
          if (value.length >= 3) {
            value = value.slice(0, 2) + "/" + value.slice(2);
          }
          setExpiryDate(value);
        }}
      />
    </Grid>
    <Grid item xs={6} sm={3}>
      <TextField
        fullWidth
        label="CVV"
        type="password"
        value={cvv}
        InputLabelProps={{ style: { color: 'black' } }}
        sx={{
          '& .MuiOutlinedInput-root': {
            '& fieldset': {
              borderColor: 'black',
            },
            '&:hover fieldset': {
              borderColor: 'black',
            },
            '&.Mui-focused fieldset': {
              borderColor: 'black',
            },
          },
          input: { color: 'black' }
        }}
        onChange={(e) => setCvv(e.target.value)}
      />
    </Grid>
    <Grid item xs={12}>
      <TextField
        fullWidth
        label="Cardholder Name"
        value={cardHolderName}
        InputLabelProps={{ style: { color: 'black' } }}
        sx={{
          '& .MuiOutlinedInput-root': {
            '& fieldset': {
              borderColor: 'black',
            },
            '&:hover fieldset': {
              borderColor: 'black',
            },
            '&.Mui-focused fieldset': {
              borderColor: 'black',
            },
          },
          input: { color: 'black' }
        }}

        onChange={(e) => setCardHolderName(e.target.value)}
      />
    </Grid>
    <Grid item xs={12} sx={{ textAlign: 'center', mt: 2 }}>
      <Button
        variant="contained"
        size="large"
        color="success"
        onClick={handlePayment}
        sx={{ borderRadius: 2, px: 5, py: 1.5 }}
      >
        💰 Pay Now
      </Button>
    </Grid>
    {paymentMessage && (
      <Grid item xs={12}>
        <Alert severity={paymentError ? "error" : "success"} sx={{ mt: 2 }}>
          {paymentMessage}
        </Alert>
      </Grid>
    )}
  </Grid>
</Paper>

            </>
          )}
        </>
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

      <Popover
        id="mouse-over-popover"
        sx={{
          pointerEvents: 'none',
        }}
        open={open}
        anchorEl={anchorEl}
        anchorOrigin={{
          vertical: 'bottom',
          horizontal: 'left',
        }}
        transformOrigin={{
          vertical: 'top',
          horizontal: 'left',
        }}
        onClose={handlePopoverClose}
        disableRestoreFocus
      >
        {popoverImage && (
          <Box
            component="img"
            src={popoverImage}
            alt="Preview"
            sx={{
              width: '20vw',
              height: 'auto',
              objectFit: 'contain',
              p: 1, 
              backgroundColor: 'background.paper',
              boxShadow: 3
            }}
          />
        )}
      </Popover>

      <Box
  sx={{
    position: 'fixed',
    bottom: 24,
    right: 24,
    zIndex: 1300,
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    gap: 1,
  }}
>
  <Fab
    onClick={callWaiter}
    sx={{
      width: 72,
      height: 72,
      fontSize: '1.9rem',
      fontWeight: 'bold',
      background: 'linear-gradient(to right, #ff7043, #ffb74d)', // warm Mexican orange
      color: 'white',
      boxShadow: 6,
      '&:hover': {
        background: 'linear-gradient(to right, #f4511e, #ffa726)',
      },
    }}
  >
    🧑‍🍳
  </Fab>
  <Typography 
    variant="caption" 
    sx={{ 
      color: 'white', 
      fontWeight: 'bold', 
      backgroundColor: '#f4511e', 
      px: 1.8, 
      py: 0.6, 
      borderRadius: 2, 
      fontSize: '0.75rem', 
      boxShadow: 2,
      textAlign: 'center'
    }}
  >
    Request Waiter
  </Typography>
</Box>




    </Container>
    </>
);

}
export default Menu;
