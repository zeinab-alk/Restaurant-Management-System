import React, { useState } from 'react';

/**
 * Component for waiters to add a menu item to an order.
 * Provides a form for inputting the order ID and menu item ID.
 */
function WaiterAddItem() {
  const [orderId, setOrderId] = useState('');
  const [menuItemId, setMenuItemId] = useState('');
  const [message, setMessage] = useState('');

  /**
   * Handles form submission.
   * Sends a POST request to the backend to add the menu item to the specified order.
   */
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch(
        `/api/orders/${orderId}/addItem?menuItemId=${menuItemId}`,
        { method: 'POST' }
      );
      if (response.ok) {
        setMessage('Item added successfully!');
      } else {
        setMessage('Failed to add item.');
      }
    } catch (err) {
      setMessage(`Error: ${err.message}`);
    }
  };

  return (
    <div>
      <h1>Add Item to Order</h1>
      <form onSubmit={handleSubmit}>
        <label>
          Order ID:
          <input
            type="text"
            value={orderId}
            onChange={(e) => setOrderId(e.target.value)}
          />
        </label>
        <br />
        <label>
          Menu Item ID:
          <input
            type="text"
            value={menuItemId}
            onChange={(e) => setMenuItemId(e.target.value)}
          />
        </label>
        <br />
        <button type="submit">Add Item</button>
      </form>
      <p>{message}</p>
    </div>
  );
}

export default WaiterAddItem;
