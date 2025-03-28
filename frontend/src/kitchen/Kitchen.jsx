import React, { useEffect, useState } from "react";
import axios from "axios";
import { Container, Typography, Paper, Table, TableHead, TableBody, Box, TableRow, TableCell, Button } from "@mui/material";
import { API_ENDPOINT } from "../config";
import { Link as RouterLink } from 'react-router-dom';

function KitchenPage() {
    const [orders, setOrders] = useState([]);
    const [readyOrders, setReadyOrders] = useState([]);

    const minutesSincePlaced = (placedTime) => {
        if (!placedTime) return "N/A";
        const now = new Date();
        const placed = new Date(placedTime);
        const diffMs = now - placed;

        if (diffMs < 0) {
            return "N/A";
        }

        const totalSeconds = Math.floor(diffMs / 1000);
        const minutes = Math.floor(totalSeconds / 60);
        const seconds = totalSeconds % 60;

        return `${minutes} min ${seconds} s ago`;
    };

    useEffect(() => {
        fetchOrders();
    }, []);

    const fetchOrders = async () => {
        try {
            const response = await axios.get('http://localhost:2800/api/orders');
            console.log("Orders response:", response.data);
            setOrders(response.data || []);
        } catch (err) {
            console.error("Error fetching orders:", err);
        }
    };

    const sendReadyNotification = async (orderId) => {
        try {
            await axios.post(`${API_ENDPOINT}/api/kitchen/mark-ready/${orderId}`);
            console.log(`Notified waiters that Order #${orderId} is ready.`);
    
            setReadyOrders(prev => [...prev, orderId]);
    
        } catch (error) {
            console.error("Error sending notification:", error);
        }
    };
    

    return (
        <Container maxWidth="md" sx={{ py: 4 }}>
            <Box sx={{ mb: 4, display: 'flex', justifyContent: 'flex-start' }}>
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
            </Box>

            {/* Kitchen Dashboard Heading */}
            <Typography
                variant="h4"
                align="center"
                sx={{ fontWeight: 'bold', mb: 3 }}
            >
                Kitchen Dashboard
            </Typography>
            <Paper elevation={3} sx={{ p: 3 }}>
                <Typography variant="h6" gutterBottom>
                    Orders
                </Typography>
                {orders.length === 0 ? (
                    <Typography variant="body1">No orders found.</Typography>
                ) : (
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell>Order #</TableCell>
                                <TableCell>Contents</TableCell>
                                <TableCell>Order Placed</TableCell>
                                <TableCell>Action</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {orders.map((order) => (
                                <TableRow key={order.orderNumber}>
                                    <TableCell>{order.orderNumber}</TableCell>
                                    <TableCell>
                                        {order.menuItems.map((item, index) => (
                                            <Typography key={index}>
                                                {item.name}
                                            </Typography>
                                        ))}
                                    </TableCell>
                                    <TableCell>
                                        {minutesSincePlaced(order.orderPlacedTime)}
                                    </TableCell>
                                    <TableCell>
                                        <Button 
                                            variant="contained" 
                                            color="success" 
                                            onClick={() => sendReadyNotification(order.orderNumber)}
                                        >
                                            Set as Ready
                                        </Button>
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                )}
            </Paper>
        </Container>
    );
}

export default KitchenPage;
