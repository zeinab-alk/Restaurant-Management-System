import React from 'react';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { Box, IconButton, Typography, useTheme } from '@mui/material';
import Brightness4Icon from '@mui/icons-material/Brightness4';
import Brightness7Icon from '@mui/icons-material/Brightness7';
import "./App.css";
import { Container } from "@mui/material";
import { useLocation } from 'react-router-dom';
import { AuthProvider, useAuth } from './auth/AuthContext';
import Login from './auth/Login';

export const ColorModeContext = React.createContext({ toggleColorMode: () => {} });

function AppContent({ children }) {
  const location = useLocation();
  const isHomePage = location.pathname === "/";
  const { userRole } = useAuth();
  const [mode, setMode] = React.useState(() => {
    return localStorage.getItem('colorMode') || 'light';
  });

  const colorMode = React.useMemo(
    () => ({
      toggleColorMode: () => {
        setMode((prevMode) => {
          const newMode = prevMode === 'light' ? 'dark' : 'light';
          localStorage.setItem('colorMode', newMode);
          return newMode;
        });
      },
    }),
    [],
  );

  const theme = React.useMemo(() => createTheme({
    palette: {
      mode,
      ...(mode === 'light'
        ? {
            primary: { main: '#d84315' }, 
            secondary: { main: '#ffb300' }, 
            background: {
              default: '#fff8e1',
              paper: '#fff3e0'
            },
          }
        : {
            primary: { main: '#ffcc80' },
            secondary: { main: '#ff7043' },
            background: {
              default: '#3e2723',
              paper: '#4e342e'
            },
          }),
    },
    typography: {
      fontFamily: `'Quicksand', 'Cinzel Decorative', cursive`,
      h1: {
        fontFamily: `'Cinzel Decorative', cursive`,
      },
    },
    shape: {
      borderRadius: 10
    }
  }), [mode]);

  // Show login page for staff routes if not logged in
  if (location.pathname.startsWith('/waiter') || location.pathname.startsWith('/kitchen')) {
    if (!userRole) {
      return <Login />;
    }
  }

  return (
    <ColorModeContext.Provider value={colorMode}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <>
          {isHomePage ? (
            <>
              {children}
            </>
          ) : (
            <Box
              sx={{
                minHeight: '100vh',
                pb: 4,
                background: mode === 'light'
                  ? 'linear-gradient(to bottom, #fff9e3, #ffecb3)'
                  : 'linear-gradient(to bottom, #3e2723, #4e342e)',
              }}
            >
              <Box
                sx={{
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  position: 'relative',
                  p: 3,
                  width: '100%',
                }}
              >
                <IconButton
                  onClick={colorMode.toggleColorMode}
                  color="inherit"
                  sx={{ position: 'absolute', right: 16, top: 16 }}
                >
                  {mode === 'dark' ? <Brightness7Icon /> : <Brightness4Icon />}
                </IconButton>
                <Typography variant="h4" component="h1" sx={{ textAlign: 'center' }}>
                  Oaxaca Restaurant
                </Typography>
              </Box>
              <Container maxWidth="lg">
                {children}
              </Container>
            </Box>
          )}
        </>
      </ThemeProvider>
    </ColorModeContext.Provider>
  );
}

function App({ children }) {
  return (
    <AuthProvider>
      <AppContent>{children}</AppContent>
    </AuthProvider>
  );
}

export default App;
