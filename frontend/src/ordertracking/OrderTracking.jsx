import React, { useEffect, useState } from "react";

const OrderTracking = () => {
  const [order, setOrder] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchLatestOrder = async () => {
      try {
        const response = await fetch("http://localhost:8080/api/orders/latest");
        if (response.status === 204) {
          setOrder(null); // No content
        } else {
          const data = await response.json();
          setOrder(data);
        }
      } catch (err) {
        setError("Unable to fetch order status.");
      }
    };

    fetchLatestOrder();
    const interval = setInterval(fetchLatestOrder, 10000); // Poll every 10s
    return () => clearInterval(interval);
  }, []);

  if (error) return <div>{error}</div>;
  if (!order) return <div>No recent orders found.</div>;

  const placed = new Date(order.orderPlacedTime);
  const eta = new Date(order.estimatedCompletionTime);
  const now = new Date();
  const progress = Math.min(((now - placed) / (eta - placed)) * 100, 100);

  return (
    <div className="bg-white p-4 rounded-xl shadow-lg max-w-md mx-auto mt-4">
      <h2 className="text-xl font-bold mb-2">Tracking Order #{order.orderNumber}</h2>
      <p>Status: <strong>{order.status}</strong></p>
      <p>Placed: {placed.toLocaleTimeString()}</p>
      <p>ETA: {eta.toLocaleTimeString()}</p>

      <div className="mt-4">
        <div className="w-full bg-gray-200 h-4 rounded-full">
          <div
            className="bg-green-500 h-4 rounded-full"
            style={{ width: `${progress}%` }}
          ></div>
        </div>
        <p className="text-sm mt-1">{Math.round(progress)}% completed</p>
      </div>
    </div>
  );
};

export default OrderTracking;
