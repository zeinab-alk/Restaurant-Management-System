import React from 'react';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { Box, IconButton, Typography, useTheme } from '@mui/material';
import Brightness4Icon from '@mui/icons-material/Brightness4';
import Brightness7Icon from '@mui/icons-material/Brightness7';
import "./App.css";
import { Container } from "@mui/material";

export const ColorModeContext = React.createContext({ toggleColorMode: () => {} });

function App({ children }) {
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

  const theme = React.useMemo(
    () =>
      createTheme({
        palette: {
          mode,
        },
      }),
    [mode],
  );

  return (
    <ColorModeContext.Provider value={colorMode}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <Box sx={{ minHeight: '100vh', pb: 4, width: '100%' }}>
          <Box sx={{ 
            display: 'flex', 
            alignItems: 'center', 
            justifyContent: 'center',
            position: 'relative',
            p: 3,
            width: '100%'
          }}>
            <Typography 
              variant="h1" 
              component="h1" 
              color="primary"
              sx={{ 
                width: '50%',
                minWidth: '50%',
                textAlign: 'center',
                fontSize: '4.5rem',
                fontWeight: 'bold',
                letterSpacing: '0.2em'
              }}
            >
              OAXACA
            </Typography>
            <IconButton 
              onClick={colorMode.toggleColorMode} 
              color="inherit"
              sx={{ position: 'absolute', right: 16 }}
            >
              {theme.palette.mode === 'dark' ? <Brightness7Icon /> : <Brightness4Icon />}
            </IconButton>
          </Box>
          {children}
        </Box>
      </ThemeProvider>
    </ColorModeContext.Provider>
  );
}

export default App;
