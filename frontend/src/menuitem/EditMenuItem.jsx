import React, { useState, useEffect } from 'react';
import { useParams, Link as RouterLink } from 'react-router-dom';
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
  Alert
} from "@mui/material";
import axios from 'axios';
import { API_ENDPOINT } from '../config';

const EditMenuItem = () => {
  const { id } = useParams();
  const [menuItem, setMenuItem] = useState({
    name: '',
    price: '',
    caloricInfo: '',
    dietaryRestrictions: [],
    allergenInformation: [],
    popularity: 0,
    available: true
  });
  const [error, setError] = useState(null);

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
    axios.get(`${API_ENDPOINT}/api/menuItems/${id}`)
      .then(response => {
        setMenuItem({
          ...response.data,
          price: response.data.price.toString(),
          caloricInfo: response.data.caloricInfo.toString()
        });
      })
      .catch(error => {
        setError('Error loading menu item');
        console.error(error);
      });
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
      alert('Menu item updated successfully!');
    } catch (error) {
      setError('Error updating menu item');
      console.error(error);
    }
  };

  return (
    <Container>
      <Stack direction="row" spacing={2} sx={{ mb: 4 }}>
        <Button
          component={RouterLink}
          to="/"
          variant="contained"
          color="success"
        >
          Back to Home
        </Button>
        <Button
          component={RouterLink}
          to="/waiter"
          variant="contained"
          color="secondary"
        >
          Back to Dashboard
        </Button>
        <Button
          component={RouterLink}
          to="/waiter/edit-items"
          variant="contained"
          color="primary"
        >
          Back to Menu Items
        </Button>
      </Stack>

      <Typography variant="h3" component="h1" gutterBottom>
        Edit Menu Item
      </Typography>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <form onSubmit={handleSubmit}>
        <Stack spacing={3}>
          <TextField
            label="Name"
            value={menuItem.name}
            onChange={(e) => setMenuItem({...menuItem, name: e.target.value})}
            required
            fullWidth
          />
          <TextField
            label="Price"
            type="number"
            inputProps={{ step: "0.01", min: "0" }}
            value={menuItem.price}
            onChange={(e) => setMenuItem({...menuItem, price: e.target.value})}
            required
            fullWidth
          />
          <TextField
            label="Calories"
            type="number"
            inputProps={{ min: "0" }}
            value={menuItem.caloricInfo}
            onChange={(e) => setMenuItem({...menuItem, caloricInfo: e.target.value})}
            required
            fullWidth
          />
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
          <FormControlLabel
            control={
              <Switch
                checked={menuItem.available}
                onChange={(e) => setMenuItem({...menuItem, available: e.target.checked})}
              />
            }
            label="Available"
          />
          <Button
            type="submit"
            variant="contained"
            color="primary"
            size="large"
          >
            Update Menu Item
          </Button>
        </Stack>
      </form>
    </Container>
  );
};

export default EditMenuItem; 