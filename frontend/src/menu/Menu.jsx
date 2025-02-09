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
  Tooltip
} from "@mui/material";
import { Link as RouterLink } from 'react-router-dom';
import { API_ENDPOINT } from "../config";
import { allergenSymbols, dietarySymbols } from '../utils/foodInfoSymbols';

function Menu() {
  const [menuItems, setMenuItems] = React.useState([]);
  const [error, setError] = React.useState();
  const theme = useTheme();

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
              {menuItems.map((item) => (
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