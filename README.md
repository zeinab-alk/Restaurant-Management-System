# Restaurant Management System

This document explains how to start the backend and frontend services for the Restaurant Management System.

## Prerequisites

- Java JDK 17 or higher installed
- Node.js and npm installed
- Maven installed (or use the included Maven wrapper)

## Starting the Application

### Starting the Backend (Spring Boot)

1. Open a terminal/command prompt
2. Navigate to the backend directory:
   ```
   cd backend
   ```
3. Start the Spring Boot application:
   ```
   mvn spring-boot:run
   ```
4. The backend server will start on http://localhost:8080

### Starting the Frontend (React)

1. Open a new terminal/command prompt window
2. Navigate to the frontend directory:
   ```
   cd frontend
   ```
3. Install dependencies (if not already installed):
   ```
   npm install
   ```
4. Start the development server:
   ```
   npm run dev
   ```
5. The frontend server will start on http://localhost:5173

## Accessing the Application

Once both services are running, you can access:
- Frontend: http://localhost:5173
- Backend API: http://localhost:8080

## Troubleshooting

If you encounter any issues:

1. Make sure both backend and frontend dependencies are installed:
   ```
   # For backend
   cd backend && mvn clean install
   
   # For frontend
   cd frontend && npm install
   ```

2. Check if ports 8080 and 5173 are already in use by other applications

3. Review the console output for specific error messages