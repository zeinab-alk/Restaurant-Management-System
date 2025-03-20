import React, { useState, useEffect } from "react";
import axios from "axios";
import { Button, Stack } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';


const KitchenPage = () => {
    const [orders, setOrders] = useState([]);

    // Fetch "Preparing" orders on page load
    useEffect(() => {
        fetchPreparingOrders();
    }, []);

    const fetchPreparingOrders = async () => {
        try {
            const response = await axios.get("http://localhost:2800/api/kitchen/preparing");
            setOrders(response.data);
        } catch (error) {
            console.error("Error fetching orders:", error);
        }
    };

    const markOrderAsReady = async (orderId) => {
        try {
            const { data: updatedOrder } = await axios.post(`http://localhost:2800/api/kitchen/${orderId}/ready`); // Mark order as "Ready"

            setOrders((prevOrders) => prevOrders.map((o) =>
                (o.orderNumber === updatedOrder.orderNumber ? updatedOrder : o))
            );
        } catch (error) {
            console.error("Error marking order as ready:", error);
        }
    };

    return (

        <div style={{padding: "1rem"}}>
            {/* Add a row with the Back to Home button */}
            <Stack direction="row" spacing={2} sx={{mb: 4}}>
                <Button
                    component={RouterLink}
                    to="/"
                    variant="contained"
                    color="success"
                >
                    Back to Home
                </Button>
            </Stack>

            <h1>Kitchen Dashboard</h1>
            <h2>Orders in Preparation</h2>
            {orders.length === 0 ? (
                <p>No orders currently being prepared.</p>
            ) : (
                <ul>
                    {orders.map(order => (
                        <li key={order.orderNumber}>
                            <span>Order #{order.orderNumber} - {order.status}</span>
                            {/* Display the contents of the order: items, etc. */}
                            {order.menuItems && order.menuItems.length > 0 && (
                                <ul style={{ marginLeft: "1em" }}>
                                    {order.menuItems.map((item) => (
                                        <li key={item.id}>
                                            {item.name} – £{item.price.toFixed(2)}
                                        </li>
                                    ))}
                                </ul>
                            )}

                            <button onClick={() => markOrderAsReady(order.orderNumber)}>Mark as Ready</button>
                        </li>
                    ))}
                </ul>

            )}

        </div>
    );
};

export default KitchenPage;
