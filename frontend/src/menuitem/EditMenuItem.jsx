import React, { useState, useEffect } from 'react';
import { useParams, Link as RouterLink, useNavigate } from 'react-router-dom';
import { 
  Button, 
  Container, 
  TextField, 
  Typography, 
  Stack,
  Autocomplete,
  Chip,
  FormControlLabel,
  Switch,
  Alert,
  Box,
  Paper,
  Grid,
  Rating,
  Snackbar,
  CircularProgress
} from "@mui/material";
import axios from 'axios';
import { API_ENDPOINT } from '../config';

const EditMenuItem = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  
  const [menuItem, setMenuItem] = useState({
    name: '',
    price: '',
    caloricInfo: '',
    dietaryRestrictions: [],
    allergenInformation: [],
    popularity: 0,
    available: true
  });
  
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

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

  useEffect(() => {
    const fetchMenuItem = async () => {
      try {
        const response = await axios.get(`${API_ENDPOINT}/api/menuItems/${id}`);
        setMenuItem(response.data);
        setLoading(false);
      } catch (error) {
        setError(`Failed to load menu item: ${error.message}`);
        setLoading(false);
      }
    };

    fetchMenuItem();
  }, [id]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const formattedMenuItem = {
        ...menuItem,
        price: parseFloat(menuItem.price),
        caloricInfo: parseInt(menuItem.caloricInfo),
        popularity: parseInt(menuItem.popularity || '0')
      };
      
      await axios.put(`${API_ENDPOINT}/api/menuItems/${id}`, formattedMenuItem);
      setSuccess(true);
      setError(null);
      
      // Wait a moment to show success message before redirecting
      setTimeout(() => {
        navigate('/menuitem/change');
      }, 2000);
    } catch (error) {
      const errorMessage = error.response?.data || 'Error updating menu item';
      setError(errorMessage);
      setSuccess(false);
    }
  };

  return (
    <Container maxWidth="lg" sx={{ py: 3 }}>
      {/* Navigation */}
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
          component={RouterLink}
          to="/waiter"
          variant="contained"
          color="secondary"
          startIcon={<span role="img" aria-label="dashboard">📊</span>}
          sx={{ fontWeight: 'medium' }}
        >
          Waiter Dashboard
        </Button>
        <Button
          component={RouterLink}
          to="/menuitem/change"
          variant="contained"
          color="primary"
          startIcon={<span role="img" aria-label="list">📋</span>}
          sx={{ fontWeight: 'medium' }}
        >
          Menu Items List
        </Button>
      </Box>

      {/* Page Title */}
      <Typography 
        variant="h3" 
        component="h1" 
        gutterBottom 
        align="center" 
        sx={{ 
          mb: 4,
          fontWeight: 'bold',
          fontSize: { xs: '1.8rem', sm: '2.5rem', md: '3rem' }
        }}
      >
        Edit Menu Item
      </Typography>

      {/* Status Messages */}
      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {/* Loading State */}
      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', my: 5 }}>
          <CircularProgress />
        </Box>
      ) : (
        /* Form */
        <Paper 
          component="form" 
          onSubmit={handleSubmit} 
          elevation={3} 
          sx={{ 
            p: { xs: 2, sm: 4 },
            borderRadius: 2,
            mb: 4
          }}
        >
          <Grid container spacing={3}>
            {/* Basic Information */}
            <Grid item xs={12}>
              <Typography 
                variant="h5" 
                component="h2" 
                gutterBottom 
                sx={{ fontWeight: 'medium' }}
              >
                Basic Information
              </Typography>
            </Grid>
            
            <Grid item xs={12} sm={6}>
              <TextField
                label="Name"
                value={menuItem.name}
                onChange={(e) => setMenuItem({...menuItem, name: e.target.value})}
                required
                fullWidth
              />
            </Grid>
            
            <Grid item xs={12} sm={6} md={3}>
              <TextField
                label="Price (£)"
                type="number"
                inputProps={{ step: "0.01", min: "0" }}
                value={menuItem.price}
                onChange={(e) => setMenuItem({...menuItem, price: e.target.value})}
                required
                fullWidth
              />
            </Grid>
            
            <Grid item xs={12} sm={6} md={3}>
              <TextField
                label="Calories"
                type="number"
                inputProps={{ min: "0" }}
                value={menuItem.caloricInfo}
                onChange={(e) => setMenuItem({...menuItem, caloricInfo: e.target.value})}
                required
                fullWidth
              />
            </Grid>

            {/* Dietary and Allergen Information */}
            <Grid item xs={12} sx={{ mt: 2 }}>
              <Typography 
                variant="h5" 
                component="h2" 
                gutterBottom 
                sx={{ fontWeight: 'medium' }}
              >
                Dietary & Allergen Information
              </Typography>
            </Grid>
            
            <Grid item xs={12} md={6}>
              <Autocomplete
                multiple
                options={dietaryOptions}
                value={menuItem.dietaryRestrictions}
                onChange={(_, newValue) => setMenuItem({...menuItem, dietaryRestrictions: newValue})}
                renderTags={(tagValue, getTagProps) =>
                  tagValue.map((option, index) => {
                    const { key, ...otherProps } = getTagProps({ index });
                    return (
                      <Chip
                        key={key}
                        label={option}
                        color="primary"
                        variant="outlined"
                        {...otherProps}
                      />
                    );
                  })
                }
                renderInput={(params) => (
                  <TextField
                    {...params}
                    label="Dietary Restrictions"
                    placeholder="Select dietary restrictions"
                  />
                )}
              />
            </Grid>
            
            <Grid item xs={12} md={6}>
              <Autocomplete
                multiple
                options={allergenOptions}
                value={menuItem.allergenInformation}
                onChange={(_, newValue) => setMenuItem({...menuItem, allergenInformation: newValue})}
                renderTags={(tagValue, getTagProps) =>
                  tagValue.map((option, index) => {
                    const { key, ...otherProps } = getTagProps({ index });
                    return (
                      <Chip
                        key={key}
                        label={option}
                        color="error"
                        variant="outlined"
                        {...otherProps}
                      />
                    );
                  })
                }
                renderInput={(params) => (
                  <TextField
                    {...params}
                    label="Allergen Information"
                    placeholder="Select allergens"
                  />
                )}
              />
            </Grid>

            {/* Additional Settings */}
            <Grid item xs={12} sx={{ mt: 2 }}>
              <Typography 
                variant="h5" 
                component="h2" 
                gutterBottom 
                sx={{ fontWeight: 'medium' }}
              >
                Additional Settings
              </Typography>
            </Grid>
            
            <Grid item xs={12} sm={6}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <Typography component="legend">Popularity:</Typography>
                <Rating
                  name="popularity"
                  value={parseInt(menuItem.popularity)}
                  onChange={(_, newValue) => {
                    setMenuItem({...menuItem, popularity: newValue || 0});
                  }}
                  max={5}
                />
              </Box>
            </Grid>
            
            <Grid item xs={12} sm={6}>
              <FormControlLabel
                control={
                  <Switch
                    checked={menuItem.available}
                    onChange={(e) => setMenuItem({...menuItem, available: e.target.checked})}
                    color="primary"
                  />
                }
                label="Available for ordering"
              />
            </Grid>

            {/* Submit and Cancel Buttons */}
            <Grid item xs={12} sx={{ mt: 2, textAlign: 'center' }}>
              <Box sx={{ display: 'flex', gap: 2, justifyContent: 'center' }}>
                <Button
                  type="submit"
                  variant="contained"
                  color="primary"
                  size="large"
                  sx={{ 
                    py: 1.5, 
                    px: 4,
                    borderRadius: 2,
                    fontSize: '1.1rem'
                  }}
                >
                  Save Changes
                </Button>
                <Button
                  component={RouterLink}
                  to="/menuitem/change"
                  variant="outlined"
                  color="secondary"
                  size="large"
                  sx={{ 
                    py: 1.5, 
                    px: 4,
                    borderRadius: 2,
                    fontSize: '1.1rem'
                  }}
                >
                  Cancel
                </Button>
              </Box>
            </Grid>
          </Grid>
        </Paper>
      )}

      {/* Success Notification */}
      <Snackbar
        open={success}
        autoHideDuration={6000}
        onClose={() => setSuccess(false)}
      >
        <Alert 
          onClose={() => setSuccess(false)} 
          severity="success" 
          variant="filled"
          sx={{ width: '100%' }}
        >
          Menu item updated successfully! Redirecting...
        </Alert>
      </Snackbar>
    </Container>
  );
};

export default EditMenuItem; 