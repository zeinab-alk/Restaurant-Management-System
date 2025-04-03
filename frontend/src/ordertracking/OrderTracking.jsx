import React, { useState } from "react";
import { API_ENDPOINT } from "../config";

const OrderTracking = () => {
  const [orderNumberInput, setOrderNumberInput] = useState("");
  const [trackedOrders, setTrackedOrders] = useState([]);
  const [error, setError] = useState("");
  const [searching, setSearching] = useState(false);

  const allowedStatuses = ["PROCESSING", "IN_PROGRESS", "READY_FOR_DELIVERY", "DELIVERED"];

  const handleTrackOrder = async () => {
    if (!orderNumberInput) return;
    setSearching(true);
    setError("");
    try {
      const res = await fetch(`${API_ENDPOINT}/api/orders/number/${orderNumberInput}`);
      if (res.status === 404) {
        setError(`Order #${orderNumberInput} not found.`);
      } else {
        const data = await res.json();
        if (!allowedStatuses.includes(data.status)) {
          setError(`Order #${orderNumberInput} is not yet being processed.`);
        } else {
          setTrackedOrders((prev) => {
            const exists = prev.find((o) => o.orderNumber === data.orderNumber);
            return exists ? prev : [...prev, data];
          });
        }
      }
    } catch (err) {
      setError("Failed to fetch order.");
    } finally {
      setSearching(false);
      setOrderNumberInput("");
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'PENDING':
        return 'text-yellow-400';
      case 'IN_PROGRESS':
      case 'PROCESSING':
        return 'text-blue-400';
      case 'READY_FOR_DELIVERY':
        return 'text-green-400';
      case 'DELIVERED':
      case 'COMPLETED':
        return 'text-emerald-400';
      case 'CANCELLED':
        return 'text-red-400';
      default:
        return 'text-white';
    }
  };

  return (
    <div className="bg-[#3b2f2f] p-8 rounded-xl shadow-2xl max-w-4xl mx-auto mt-8 animate-fade-in">
      <div className="mb-8 text-center">
        <div style={{ color: "#1a1a1a" }}>
        <input
          type="number"
          placeholder="Enter your Order Number"
          value={orderNumberInput}
          onChange={(e) => setOrderNumberInput(e.target.value)}
          className="border border-yellow-300 bg-[#5c4438] text-white p-3 rounded w-72 focus:outline-none focus:ring-2 focus:ring-yellow-400 transition duration-300 ease-in-out"
        />
        <button
          onClick={handleTrackOrder}
          className="ml-4 bg-yellow-400 hover:bg-yellow-500 text-[#3b2f2f] font-bold px-5 py-3 rounded shadow hover:scale-105 transform transition duration-300"
        >
          Track
        </button>
      </div>

      {searching && <p className="text-center animate-pulse" style={{ color: "#1a1a1a" }}>Loading...</p>}
      {error && <p className="text-center mb-4 animate-shake"style={{ color: "#1a1a1a" }}>{error}</p>}

      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
        {trackedOrders.map((order) => {
          const placed = new Date(order.orderPlacedTime);

          return (
            <div
              key={order.orderNumber}
              className="bg-[#4d3a3a] p-6 rounded-lg shadow-lg text-center border border-yellow-400 transform hover:scale-105 transition duration-300 animate-slide-up"
            >
              <h3 className="text-xl font-bold mb-2 tracking-wide"style={{ color: "#1a1a1a" }}>
                Order #{order.orderNumber}
              </h3>
              <p className="text-lg font-medium mb-1" style={{ color: "#1a1a1a" }}>
                Status: <span className={`italic font-semibold ${getStatusColor(order.status)}`}>{order.status.replace(/_/g, ' ')}</span>
              </p>
              <p className="text-sm italic" style={{ color: "#1a1a1a" }}>Placed: {placed.toLocaleTimeString()}</p>
            </div>
          );
        })}
      </div>
      </div>
    </div>
  );
};

export default OrderTracking;
