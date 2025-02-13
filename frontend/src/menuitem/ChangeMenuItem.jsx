import React, { useState, useEffect } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { 
  Button, 
  Container, 
  Typography, 
  Stack,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip,
  useTheme,
  Alert,
  Tooltip
} from "@mui/material";
import EditIcon from '@mui/icons-material/Edit';
import axios from 'axios';
import { API_ENDPOINT } from '../config';
import { dietarySymbols } from '../utils/foodInfoSymbols';

const ChangeMenuItem = () => {
  const [menuItems, setMenuItems] = useState([]);
  const [error, setError] = useState();
  const theme = useTheme();

  useEffect(() => {
    axios.get(`${API_ENDPOINT}/api/menuItems`)
      .then(response => {
        setMenuItems(response.data);
      })
      .catch(error => {
        setError(error.message);
      });
  }, []);

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
      </Stack>

      <Typography variant="h3" component="h1" gutterBottom>
        Change Menu Item
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
                }}>Status</TableCell>
                <TableCell sx={{ 
                  color: theme.palette.mode === 'dark' ? 'black' : 'white', 
                  fontWeight: 'bold' 
                }}>Edit</TableCell>
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
                    <Chip 
                      label={item.available ? "Available" : "Unavailable"}
                      color={item.available ? "success" : "error"}
                      size="small"
                    />
                  </TableCell>
                  <TableCell>
                    <Button
                      component={RouterLink}
                      to={`/waiter/edit-item/${item.itemId}`}
                      variant="contained"
                      color="primary"
                      size="small"
                      startIcon={<EditIcon />}
                    >
                      Edit
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}
    </Container>
  );
};

export default ChangeMenuItem; 