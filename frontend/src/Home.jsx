import React from "react";
import { Link as RouterLink } from "react-router-dom";
import { Button, Stack, Box } from "@mui/material";

function Home() {
  return (
    <Box
      sx={{
        height: '70vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        width: '100%',
        mt: -4
      }}
    >
      <Stack spacing={3} direction="column" sx={{ width: '33.33%', minWidth: '33.33%' }}>
        <Button
          component={RouterLink}
          to="/menu"
          variant="contained"
          color="primary"
          size="large"
          sx={{ 
            py: 2,
            fontSize: '1.2rem'
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
          sx={{ 
            py: 2,
            fontSize: '1.2rem'
          }}
        >
          Waiter Access
        </Button>
      </Stack>
    </Box>
  );
}

export default Home;