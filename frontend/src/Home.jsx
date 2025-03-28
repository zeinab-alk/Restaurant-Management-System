import React from "react";
import { Link as RouterLink } from "react-router-dom";
import { Button, Grid, Box, Typography, Container } from "@mui/material";

function Home() {
  return (
    <Container maxWidth="xl" sx={{ py: 3 }}>
      <Box
        sx={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          justifyContent: "center",
          minHeight: "80vh",
          textAlign: "center",
          px: 2,
        }}
      >
        <Typography
          variant="h2"
          component="h1"
          gutterBottom
          sx={{
            mb: 4,
            fontWeight: "bold",
            fontSize: { xs: "2rem", sm: "3rem", md: "4rem" },
            color: "primary.main",
          }}
        >
          OAXACA Restaurant
        </Typography>

        <Typography
          variant="h5"
          color="text.secondary"
          sx={{
            mb: 6,
            maxWidth: "800px",
            fontSize: { xs: "1rem", sm: "1.25rem" },
          }}
        >
          Welcome to our restaurant management system. Please select an option below to get started.
        </Typography>

        {/* Grid layout with 2 buttons per row */}
        <Grid container spacing={3} justifyContent="center" maxWidth="600px">
          <Grid item xs={12} sm={6}>
            <Button
              component={RouterLink}
              to="/menu"
              variant="contained"
              color="primary"
              fullWidth
              size="large"
              startIcon={<span role="img" aria-label="menu">🍽️</span>}
              sx={{ py: 2, fontWeight: "medium", fontSize: "1.1rem", borderRadius: 2 }}
            >
              View Menu
            </Button>
          </Grid>
          <Grid item xs={12} sm={6}>
            <Button
              component={RouterLink}
              to="/waiter"
              variant="contained"
              color="secondary"
              fullWidth
              size="large"
              startIcon={<span role="img" aria-label="waiter">👨‍🍳</span>}
              sx={{ py: 2, fontWeight: "medium", fontSize: "1.1rem", borderRadius: 2 }}
            >
              Waiter Access
            </Button>
          </Grid>
          <Grid item xs={12} sm={6}>
            <Button
              component={RouterLink}
              to="/kitchen"
              variant="contained"
              color="success"
              fullWidth
              size="large"
              startIcon={<span role="img" aria-label="kitchen">🍳</span>}
              sx={{ py: 2, fontWeight: "medium", fontSize: "1.1rem", borderRadius: 2 }}
            >
              Kitchen Access
            </Button>
          </Grid>
          <Grid item xs={12} sm={6}>
            <Button
              component={RouterLink}
              to="/customer"
              variant="contained"
              color="info"
              fullWidth
              size="large"
              startIcon={<span role="img" aria-label="customer">🧍‍♂️</span>}
              sx={{ py: 2, fontWeight: "medium", fontSize: "1.1rem", borderRadius: 2 }}
            >
              Customer Page
            </Button>
          </Grid>
        </Grid>
      </Box>
    </Container>
  );
}

export default Home;
