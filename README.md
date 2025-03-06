# Restaurant Management System Quick Start

This document explains how to start both the backend and frontend servers with a single command.

## Prerequisites

- Java JDK 17 or higher installed
- Node.js and npm installed
- Maven installed (or use the included Maven wrapper)

## Starting the Application

### For Windows Users

1. Open Command Prompt or PowerShell
2. Navigate to the project root directory
3. Run the batch file:
   ```
   start-app.bat
   ```
4. Two separate command windows will open - one for the backend and one for the frontend

### For Linux/macOS Users

1. Open Terminal
2. Navigate to the project root directory
3. Make sure the script is executable:
   ```
   chmod +x start-app.sh
   ```
4. Run the shell script:
   ```
   ./start-app.sh
   ```
5. Both servers will start in the same terminal window
6. Press Ctrl+C to stop both servers

### For WSL Users (Windows Subsystem for Linux)

1. Open Command Prompt or PowerShell
2. Navigate to the project root directory
3. Run the WSL batch file:
   ```
   start-app-wsl.bat
   ```
4. Two separate WSL windows will open - one for the backend and one for the frontend

## Accessing the Application

- Frontend: http://localhost:5173
- Backend API: http://localhost:8080

## Troubleshooting

If you encounter any issues starting the application:

1. Make sure both backend and frontend dependencies are installed:
   ```
   cd backend && ./mvnw clean install
   cd frontend && npm install
   ```

2. Check if ports 8080 and 5173 are already in use by other applications

3. Review the console output for specific error messages