import React from "react";
import { Link as RouterLink } from "react-router-dom";
import { Button, Stack, Box, Typography, Container } from "@mui/material";

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

        {/* Button Row */}
        <Stack
          spacing={3}
          direction={{ xs: "column", sm: "row" }}
          sx={{
            width: "100%",
            maxWidth: "600px",
            justifyContent: "center",
            px: 2,
          }}
        >
          <Button
            component={RouterLink}
            to="/menu"
            variant="contained"
            color="primary"
            size="large"
            startIcon={<span role="img" aria-label="menu">🍽️</span>}
            sx={{
              py: 2,
              px: 4,
              fontSize: { xs: "1rem", sm: "1.2rem" },
              borderRadius: "8px",
              fontWeight: "medium",
              width: "100%",
            }}
          >
            View Menu
          </Button>
          <Button
            component={RouterLink}
            to="/waiter"
            variant="contained"
            color="secondary"
            size="large"
            startIcon={<span role="img" aria-label="waiter">👨‍🍳</span>}
            sx={{
              py: 2,
              px: 4,
              fontSize: { xs: "1rem", sm: "1.2rem" },
              borderRadius: "8px",
              fontWeight: "medium",
              width: "100%",
            }}
          >
            Waiter Access
          </Button>
          <Button
            component={RouterLink}
            to="/kitchen"
            variant="contained"
            color="success"
            size="large"
            startIcon={<span role="img" aria-label="kitchen">🍳</span>}
            sx={{
              py: 2,
              px: 4,
              fontSize: { xs: "1rem", sm: "1.2rem" },
              borderRadius: "8px",
              fontWeight: "medium",
              width: "100%",
            }}
          >
            Kitchen Access
          </Button>
        </Stack>

        {/* Customer Page Button */}
        <Button
          component={RouterLink}
          to="/customer"
          variant="contained"
          color="info"
          fullWidth
          size="large"
          startIcon={<span role="img" aria-label="customer">🧍‍♂️</span>}
          sx={{
            mt: 4,
            py: 2,
            fontWeight: "medium",
            fontSize: "1.1rem",
            borderRadius: 2,
            width: { xs: "100%", sm: "300px" },
          }}
        >
          Customer Page
        </Button>
      </Box>
    </Container>
  );
}

export default Home;
