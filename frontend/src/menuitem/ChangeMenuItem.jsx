import React, { useState, useEffect } from "react";
import { Link as RouterLink } from "react-router-dom";
import { 
  Button, 
  Container, 
  Typography, 
  Table, 
  TableBody, 
  TableCell, 
  TableHead, 
  TableRow, 
  Paper, 
  Stack, 
  Chip, 
  Tooltip,
  Box,
  useTheme,
  Alert,
  CircularProgress,
  Switch,
  FormControlLabel,
  FormControl,
  InputLabel,
  Select,
  MenuItem as MuiMenuItem
} from "@mui/material";
import axios from "axios";
import { API_ENDPOINT } from "../config";
import { dietarySymbols, allergenSymbols } from '../utils/foodInfoSymbols';
// Import the Menu component for customer view consistency
import Menu from "../menu/Menu";

const ChangeMenuItem = () => {
  const theme = useTheme();
  const [menuItems, setMenuItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [viewAsCustomer, setViewAsCustomer] = useState(false);

  useEffect(() => {
    axios
      .get(`${API_ENDPOINT}/api/menuItems`)
      .then((response) => {
        setMenuItems(response.data);
        setLoading(false);
      })
      .catch((error) => {
        setError("Failed to load menu items: " + error.message);
        setLoading(false);
      });
  }, []);

  // Fixed toggleAvailability function to properly update the item
  const toggleAvailability = (itemId, currentAvailability) => {
    const newAvailability = !currentAvailability;
    
    // Use a complete PUT request with the full item data, like in EditMenuItem
    const itemToUpdate = menuItems.find(item => item.itemId === itemId);
    
    if (!itemToUpdate) return;
    
    const updatedItem = {
      ...itemToUpdate,
      available: newAvailability
    };
    
    axios
      .put(`${API_ENDPOINT}/api/menuItems/${itemId}`, updatedItem)
      .then((response) => {
        setMenuItems(
          menuItems.map((item) =>
            item.itemId === itemId
              ? { ...item, available: newAvailability }
              : item
          )
        );
      })
      .catch((error) => {
        setError("Failed to update availability: " + error.message);
      });
  };

  // Toggle between Staff Edit view and Customer view
  const handleToggleView = () => {
    setViewAsCustomer(!viewAsCustomer);
  };

  // Customer view using the actual Menu component
  const renderCustomerView = () => {
    // We simply render the Menu component here to ensure perfect consistency
    return <Menu isEmbedded={true} />;
  };

  // Admin view with table layout
  const renderAdminView = () => {
    return (
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
          <Table>
            <TableHead>
              <TableRow sx={{ backgroundColor: 'primary.main' }}>
                <TableCell sx={{ 
                  color: theme.palette.mode === 'dark' ? 'black' : 'white', 
                  fontWeight: 'bold',
                  whiteSpace: 'nowrap'
                }}>Name</TableCell>
                <TableCell sx={{ 
                  color: theme.palette.mode === 'dark' ? 'black' : 'white', 
                  fontWeight: 'bold',
                  whiteSpace: 'nowrap'
                }}>Price</TableCell>
                <TableCell sx={{ 
                  color: theme.palette.mode === 'dark' ? 'black' : 'white', 
                  fontWeight: 'bold',
                  whiteSpace: 'nowrap',
                  display: { xs: 'none', sm: 'table-cell' }
                }}>Calories</TableCell>
                <TableCell sx={{ 
                  color: theme.palette.mode === 'dark' ? 'black' : 'white', 
                  fontWeight: 'bold',
                  whiteSpace: 'nowrap',
                  display: { xs: 'none', md: 'table-cell' }
                }}>Dietary Info</TableCell>
                <TableCell sx={{ 
                  color: theme.palette.mode === 'dark' ? 'black' : 'white', 
                  fontWeight: 'bold',
                  whiteSpace: 'nowrap'
                }}>Status</TableCell>
                <TableCell sx={{ 
                  color: theme.palette.mode === 'dark' ? 'black' : 'white', 
                  fontWeight: 'bold',
                  whiteSpace: 'nowrap'
                }}>Actions</TableCell>
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
                  <TableCell sx={{ whiteSpace: 'nowrap' }}>£{item.price.toFixed(2)}</TableCell>
                  <TableCell sx={{ 
                    whiteSpace: 'nowrap',
                    display: { xs: 'none', sm: 'table-cell' }
                  }}>{item.caloricInfo} kcal</TableCell>
                  <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }}>
                    <Stack direction="row" spacing={0.5}>
                      {item.dietaryRestrictions?.map((restriction, index) => (
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
                    <FormControl variant="outlined" size="small" sx={{ minWidth: 130 }}>
                      <Select
                        value={item.available ? "available" : "unavailable"}
                        onChange={(e) => toggleAvailability(item.itemId, e.target.value === "unavailable")}
                        displayEmpty
                        sx={{ 
                          fontSize: '0.875rem',
                          backgroundColor: item.available ? 'rgba(46, 125, 50, 0.1)' : 'rgba(211, 47, 47, 0.1)',
                          '& .MuiSelect-select': {
                            color: item.available ? 'success.main' : 'error.main',
                            fontWeight: 'medium'
                          }
                        }}
                      >
                        <MuiMenuItem value="available">Available</MuiMenuItem>
                        <MuiMenuItem value="unavailable">Unavailable</MuiMenuItem>
                      </Select>
                    </FormControl>
                  </TableCell>
                  <TableCell>
                    <Button
                      component={RouterLink}
                      to={`/menuitem/edit/${item.itemId}`}
                      variant="contained"
                      color="primary"
                      size="small"
                      sx={{ whiteSpace: 'nowrap' }}
                    >
                      Edit
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </Box>
      </Paper>
    );
  };

  return (
    <Container maxWidth="xl" sx={{ py: 3 }}>
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
          to="/menuitem/add"
          variant="contained"
          color="primary"
          startIcon={<span role="img" aria-label="add">➕</span>}
          sx={{ fontWeight: 'medium' }}
        >
          Add Menu Item
        </Button>
      </Box>

      {/* Page Title and View Toggle */}
      <Box sx={{ 
        display: 'flex', 
        flexDirection: { xs: 'column', sm: 'row' },
        justifyContent: 'space-between',
        alignItems: { xs: 'flex-start', sm: 'center' },
        mb: 4
      }}>
        <Typography 
          variant="h3" 
          component="h1" 
          gutterBottom 
          sx={{ 
            fontWeight: 'bold',
            fontSize: { xs: '1.8rem', sm: '2.5rem', md: '3rem' },
            mb: { xs: 2, sm: 0 }
          }}
        >
          {viewAsCustomer ? 'Customer Menu Preview' : 'Manage Menu Items'}
        </Typography>

        <FormControlLabel
          control={
            <Switch
              checked={viewAsCustomer}
              onChange={handleToggleView}
              color="primary"
            />
          }
          label={
            <Typography sx={{ fontWeight: 'medium' }}>
              {viewAsCustomer ? "Currently: Customer View" : "Currently: Staff View"}
            </Typography>
          }
          sx={{ 
            border: '1px dashed',
            borderColor: 'divider',
            borderRadius: 1,
            px: 2,
            py: 0.5
          }}
        />
      </Box>

      {/* Status Messages */}
      {!viewAsCustomer && error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {/* Loading State */}
      {!viewAsCustomer && loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', my: 5 }}>
          <CircularProgress />
        </Box>
      ) : !viewAsCustomer && menuItems.length === 0 ? (
        <Paper sx={{ p: 4, textAlign: 'center', borderRadius: 1 }} elevation={2}>
          <Typography variant="h6" color="text.secondary">
            No menu items found
          </Typography>
        </Paper>
      ) : (
        viewAsCustomer ? renderCustomerView() : renderAdminView()
      )}
    </Container>
  );
};

export default ChangeMenuItem; 