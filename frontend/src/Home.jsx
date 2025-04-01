import React from "react";
import { Link as RouterLink } from "react-router-dom";
import { Button, Stack, Box, Typography, Container } from "@mui/material";
import { motion } from "framer-motion";

function Home() {
  return (
      <Box
          sx={{
              position: "absolute",
              inset: 0,
              backgroundImage: "url('/img_1.png')",
              backgroundSize: "cover",
              backgroundPosition: "center",
              backgroundRepeat: "no-repeat",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              textAlign: "center",
              '&::before': {
                  content: '""',
                  position: "absolute",
                  inset: 0,
                  backgroundColor: "rgba(0,0,0,0.6)", //for some reason using american speeling "color" darkens the page which i think better suits theme
                  zIndex: 1,
              },
          }}
      >
          <motion.div
              initial={{opacity: 0}}
              animate={{opacity: 1}}
              transition={{duration: 1}}
          >
              <Container
                  maxWidth="md"
                  sx={{
                      position: "relative",
                      zIndex: 2,
                      px: 2,
                      py: {xs: 6, md: 10},
                  }}
              >

                  <Typography
                      variant="h2"
                      component="h1"
                      gutterBottom
                      sx={{
                          fontFamily: "'Playfair Display', serif",
                          fontWeight: "700",
                          fontSize: {xs: "2.5rem", sm: "3.5rem", md: "4.5rem"},
                          mb: 3,
                          color: "#fff",
                      }}
                  >
                      OAXACA Restaurant
                  </Typography>

                  <Typography
                      variant="h6"
                      sx={{
                          fontFamily: "'Quicksand', sans-serif",
                          mb: 5,
                          maxWidth: "600px",
                          mx: "auto",
                          color: "#f0e8dc",
                      }}
                  >
                      Discover delicious meals, manage orders, and streamline your experience.
                  </Typography>

                  {/* Button Row */}
                  <Stack
                      direction={{xs: "column", sm: "row"}}
                      spacing={3}
                      justifyContent="center"
                      sx={{mb: 3}}
                  >
                      <Button
                          component={RouterLink}
                          to="/menu"
                          variant="contained"
                          size="large"
                          sx={{
                              fontFamily: "'Playfair Display', sans-serif",
                              px: 4,
                              py: 1.5,
                              fontWeight: "600",
                              backgroundColor: "#81c784", // green
                              color: "#fff",
                              '&:hover': {
                                  backgroundColor: "#66bb6a", // darker green on hover
                              },
                              borderRadius: "12px",
                              textTransform: "uppercase",
                          }}
                      >
                          🍽️ View Menu
                      </Button>
                      {/* Customer Page Button */}
                      <Button
                          component={RouterLink}
                          to="/customer"
                          variant="contained"
                          color="info"
                          size="large"
                          sx={{
                              fontFamily: "'Playfair Display', sans-serif",
                              px: 4,
                              py: 1.5,
                              fontWeight: "600",
                              backgroundColor: "#81c784", // green
                              color: "#fff",
                              '&:hover': {
                                  backgroundColor: "#66bb6a",
                              },
                              borderRadius: "12px",
                              textTransform: "uppercase",
                              alignSelf: "center",
                          }}
                      >
                          🧍‍♂️Track Order
                      </Button>
                      <Button
                          component={RouterLink}
                          to="/waiter"
                          variant="contained"
                          size="large"
                          sx={{
                              fontFamily: "'Quicksand', sans-serif",
                              px: 4,
                              py: 1.5,
                              fontWeight: "600",
                              backgroundColor: "#d18b47",
                              color: "#fff",
                              '&:hover': {
                                  backgroundColor: "#b37034",
                              },
                              borderRadius: "12px",
                              textTransform: "uppercase",
                          }}
                      >
                          👨‍🍳 Waiter Access
                      </Button>
                      <Button
                          component={RouterLink}
                          to="/kitchen"
                          variant="contained"
                          size="large"
                          sx={{
                              fontFamily: "'Quicksand', sans-serif",
                              px: 1,
                              py: 0.8,
                              fontWeight: "600",
                              backgroundColor: "#d18b47",
                              color: "#fff",
                              '&:hover': {
                                  backgroundColor: "#b37034",
                              },
                              borderRadius: "12px",
                              textTransform: "uppercase",
                          }}
                      >
                          🍳 Kitchen Access
                      </Button>

                  </Stack>
              </Container>
          </motion.div>
      </Box>
);
}

export default Home;
