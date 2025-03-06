import React, { useState, useEffect } from "react";
import axios from "axios";

const KitchenPage = () => {
    const [orders, setOrders] = useState([]);

    // Fetch "Preparing" orders on page load
    useEffect(() => {
        fetchPreparingOrders();
    }, []);

    const fetchPreparingOrders = async () => {
        try {
            const response = await axios.get("/api/orders?status=Preparing"); // Fetch orders with "Preparing" status
            setOrders(response.data);
        } catch (error) {
            console.error("Error fetching orders:", error);
        }
    };

    const markOrderAsReady = async (orderId) => {
        try {
            await axios.post(`/api/orders/${orderId}/ready`); // Mark order as "Ready"
            setOrders(prevOrders => prevOrders.filter(order => order.orderNumber !== orderId)); // Remove from UI
        } catch (error) {
            console.error("Error marking order as ready:", error);
        }
    };

    return (
        <div>
            <h1>Kitchen Dashboard</h1>
            <h2>Orders in Preparation</h2>
            {orders.length === 0 ? (
                <p>No orders currently being prepared.</p>
            ) : (
                <ul>
                    {orders.map(order => (
                        <li key={order.orderNumber}>
                            <span>Order #{order.orderNumber} - {order.status}</span>
                            <button onClick={() => markOrderAsReady(order.orderNumber)}>Mark as Ready</button>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default KitchenPage;
