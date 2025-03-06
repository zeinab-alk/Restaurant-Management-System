#!/bin/bash

echo "Starting Restaurant Management System..."
echo ""

# Function to handle cleanup on script exit
cleanup() {
  echo ""
  echo "Shutting down servers..."
  kill $BACKEND_PID $FRONTEND_PID 2>/dev/null
  exit 0
}

# Set up trap to catch termination signals
trap cleanup SIGINT SIGTERM

# Start the Spring Boot backend
echo "[1/2] Starting Spring Boot Backend..."
cd backend && ./mvnw spring-boot:run &
BACKEND_PID=$!

# Return to root directory
cd ..

# Start the React frontend
echo "[2/2] Starting React Frontend..."
cd frontend && npm run dev &
FRONTEND_PID=$!

echo ""
echo "Both services are running!"
echo "Press Ctrl+C to stop both servers."

# Wait for either process to exit
wait $BACKEND_PID $FRONTEND_PID 