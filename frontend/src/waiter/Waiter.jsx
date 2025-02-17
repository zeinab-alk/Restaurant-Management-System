import React from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { Button, Container, Stack, Typography } from "@mui/material";

const WaiterPage = () => {
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
          to="/menu"
          variant="contained"
          color="primary"
        >
          View Menu
        </Button>
      </Stack>

      <Typography variant="h3" component="h1" gutterBottom>
        Waiter Dashboard
      </Typography>

      <Stack spacing={2} direction="column">
        <Button
          component={RouterLink}
          to="/waiter/add-item"
          variant="contained"
          color="primary"
          size="large"
        >
          Add New Menu Item
        </Button>
        <Button
          component={RouterLink}
          to="/waiter/edit-items"
          variant="contained"
          color="secondary"
          size="large"
        >
          Change Menu Item
        </Button>
      </Stack>
    </Container>
  );
};

export default WaiterPage; 