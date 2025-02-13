import React from "react";
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

function Menu() {
  const [menuItems, setMenuItems] = React.useState([]);
  const [error, setError] = React.useState();
  const theme = useTheme();
  const [selectedDietary, setSelectedDietary] = React.useState([]);
  const [selectedAllergens, setSelectedAllergens] = React.useState([]);
  const [minPrice, setMinPrice] = React.useState('');
  const [maxPrice, setMaxPrice] = React.useState('');
  const [sortBy, setSortBy] = React.useState('');

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

  React.useEffect(() => {
    updateMenu();
  }, []);

  function updateMenu() {
    axios
      .get(`${API_ENDPOINT}/api/menuItems`)
      .then((response) => {
        setMenuItems(response.data);
      })
      .catch((response) => {
        setError(response.message);
      });
  }

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
      </Stack>

      <Typography variant="h3" component="h1" gutterBottom>
        Our Menu
      </Typography>

      <Stack direction="row" spacing={2} sx={{ mb: 2 }}>
        <TextField
          label="Min Price"
          type="number"
          value={minPrice}
          onChange={(e) => setMinPrice(e.target.value)}
          sx={{ width: 120 }}
          InputProps={{ inputProps: { min: 0 } }}
        />
        <TextField
          label="Max Price"
          type="number"
          value={maxPrice}
          onChange={(e) => setMaxPrice(e.target.value)}
          sx={{ width: 120 }}
          InputProps={{ inputProps: { min: 0 } }}
        />
        <Autocomplete
          options={['Price (Low-High)', 'Price (High-Low)', 'Popularity', 'Calories']}
          value={sortBy}
          onChange={(_, newValue) => setSortBy(newValue)}
          renderInput={(params) => <TextField {...params} label="Sort By" />}
          sx={{ width: 200 }}
        />
        <Autocomplete
          multiple
          options={dietaryOptions}
          value={selectedDietary}
          onChange={(_, newValue) => setSelectedDietary(newValue)}
          renderInput={(params) => <TextField {...params} label="Filter by Dietary" />}
          sx={{ width: 300 }}
        />
        <Autocomplete
          multiple
          options={allergenOptions}
          value={selectedAllergens}
          onChange={(_, newValue) => setSelectedAllergens(newValue)}
          renderInput={(params) => <TextField {...params} label="Exclude Allergens" />}
          sx={{ width: 300 }}
        />
      </Stack>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      {!error && menuItems.length < 1 && (
        <Alert severity="warning" sx={{ mb: 2 }}>No menu items available</Alert>
      )}

      {menuItems.length > 0 && (
        <TableContainer component={Paper} elevation={3}>
          <Table sx={{ minWidth: 650 }}>
            <TableHead>
              <TableRow sx={{ backgroundColor: 'primary.main' }}>
                <TableCell sx={{ 
                  color: theme.palette.mode === 'dark' ? 'black' : 'white', 
                  fontWeight: 'bold' 
                }}>Name</TableCell>
                <TableCell sx={{ 
                  color: theme.palette.mode === 'dark' ? 'black' : 'white', 
                  fontWeight: 'bold' 
                }}>Price</TableCell>
                <TableCell sx={{ 
                  color: theme.palette.mode === 'dark' ? 'black' : 'white', 
                  fontWeight: 'bold' 
                }}>Calories</TableCell>
                <TableCell sx={{ 
                  color: theme.palette.mode === 'dark' ? 'black' : 'white', 
                  fontWeight: 'bold' 
                }}>Dietary Info</TableCell>
                <TableCell sx={{ 
                  color: theme.palette.mode === 'dark' ? 'black' : 'white', 
                  fontWeight: 'bold' 
                }}>Allergens</TableCell>
                <TableCell sx={{ 
                  color: theme.palette.mode === 'dark' ? 'black' : 'white', 
                  fontWeight: 'bold' 
                }}>Status</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {menuItems
                .filter(item => {
                  const price = item.price;
                  const meetsPrice = (!minPrice || price >= minPrice) && 
                                    (!maxPrice || price <= maxPrice);
                  const hasDietary = selectedDietary.every(d => item.dietaryRestrictions.includes(d));
                  const excludesAllergens = selectedAllergens.length === 0 || 
                                            !selectedAllergens.some(a => item.allergenInformation.includes(a));
                  return hasDietary && excludesAllergens && meetsPrice;
                })
                .sort((a, b) => {
                  if(sortBy === 'Price (Low-High)') return a.price - b.price;
                  if(sortBy === 'Price (High-Low)') return b.price - a.price;
                  if(sortBy === 'Popularity') return b.popularity - a.popularity;
                  if(sortBy === 'Calories') return a.caloricInfo - b.caloricInfo;
                  return 0;
                })
                .map((item) => (
                  <TableRow 
                    key={item.itemId}
                    sx={{ '&:nth-of-type(odd)': { backgroundColor: 'action.hover' } }}
                  >
                    <TableCell component="th" scope="row">
                      <Typography variant="subtitle1">{item.name}</Typography>
                    </TableCell>
                    <TableCell>£{item.price.toFixed(2)}</TableCell>
                    <TableCell>{item.caloricInfo} kcal</TableCell>
                    <TableCell>
                      <Stack direction="row" spacing={0.5}>
                        {item.dietaryRestrictions.map((restriction, index) => (
                          <Tooltip key={index} title={restriction} arrow>
                            <span style={{ 
                              fontSize: '1.2rem',
                              padding: '0 2px',
                              borderRadius: '50%',
                              backgroundColor: 'rgba(0,0,0,0.05)',
                              margin: '0 2px'
                            }}>
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
                  </TableRow>
                ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}
    </Container>
  );
}

export default Menu;