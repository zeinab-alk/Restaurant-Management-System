import React, { useState, useEffect } from 'react';

/**
 * A form for waiters to add a menu item to an existing order.
 * This component can receive the "orderId" from the URL (or from props).
 */
function WaiterAddItem() {
  const [orderId, setOrderId] = useState('');
  const [menuItemId, setMenuItemId] = useState('');
  const [message, setMessage] = useState('');

  /**
   * If you pass the orderId in the URL query param (like /waiter/add-item?orderId=10),
   * parse it once on mount so the user doesn't have to type the order number manually.
   */
  useEffect(() => {
    const searchParams = new URLSearchParams(window.location.search);
    const urlOrderId = searchParams.get('orderId');
    if (urlOrderId) {
      setOrderId(urlOrderId);
    }
  }, []);

  /**
   * Handles form submission.
   * Sends a POST request to the backend to add the menu item to the specified order.
   */
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!orderId || !menuItemId) {
      setMessage('Please provide both orderId and menuItemId.');
      return;
    }

    try {
      const response = await fetch(
        `/api/orders/${orderId}/addItem?menuItemId=${menuItemId}`,
        { method: 'POST' }
      );
      if (response.ok) {
        setMessage(`Item ${menuItemId} added successfully to Order #${orderId}!`);
      } else {
        setMessage(`Failed to add item to Order #${orderId}.`);
      }
    } catch (err) {
      setMessage(`Error: ${err.message}`);
    }
  };

  return (
    <div className="add-item-container" style={{ maxWidth: '400px', margin: '0 auto' }}>
      <h2 className="page-title">Add Item to Order</h2>
      <form onSubmit={handleSubmit} className="add-item-form">
        <div className="form-group">
          <label htmlFor="orderId">Order ID:</label>
          <input
            type="text"
            id="orderId"
            value={orderId}
            onChange={(e) => setOrderId(e.target.value)}
            className="form-control"
          />
        </div>
        <div className="form-group">
          <label htmlFor="menuItemId">Menu Item ID:</label>
          <input
            type="text"
            id="menuItemId"
            value={menuItemId}
            onChange={(e) => setMenuItemId(e.target.value)}
            className="form-control"
          />
        </div>
        <button type="submit" className="btn btn-primary">
          Add Item
        </button>
      </form>
      {message && <p className="feedback-message">{message}</p>}
    </div>
  );
}

export default WaiterAddItem;