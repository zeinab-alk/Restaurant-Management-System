# Restaurant Menu System - Frontend Documentation

## Technology Stack
- **React**: JavaScript library for building user interfaces
- **Material-UI (MUI)**: React UI framework for styled components
- **Axios**: Promise-based HTTP client for making API requests

## Core Components

### Menu Display (`Menu.jsx`)
```javascript:frontend/src/menu/Menu.jsx
startLine: 24
endLine: 42
```

Key Components Used:
- `<Container>`: MUI component that centers content with maximum width
- `<TableContainer>`: MUI wrapper for table overflow handling
- `<TableCell>`: Individual table cell component
- `<Stack>`: MUI component for managing layouts with flexbox
- `<Tooltip>`: Shows additional information on hover
- `<Chip>`: Compact element representing status or selection
- `useState`: React hook for managing component state
- `useEffect`: React hook for handling side effects
- `axios.get()`: Makes HTTP GET request to fetch menu items

### Add Menu Item (`AddMenuItem.jsx`)
```javascript:frontend/src/menuitem/AddMenuItem.jsx
startLine: 15
endLine: 72
```

Form Components:
- `<TextField>`: MUI text input component
- `<Autocomplete>`: MUI component for advanced select inputs
- `<Button>`: MUI styled button component
- `axios.post()`: Makes HTTP POST request to create new items
- `e.preventDefault()`: Prevents form default submission
- `parseFloat()`: Converts string to decimal number
- `parseInt()`: Converts string to integer

### Edit Menu Item (`EditMenuItem.jsx`)
```javascript:frontend/src/menuitem/EditMenuItem.jsx
startLine: 18
endLine: 82
```

Additional Components:
- `useParams`: React Router hook to access URL parameters
- `<Switch>`: MUI toggle component
- `<FormControlLabel>`: MUI wrapper for form controls with labels
- `axios.put()`: Makes HTTP PUT request to update items

## Utility Features

### Food Information Symbols
```javascript:frontend/src/utils/foodInfoSymbols.js
startLine: 1
endLine: 40
```

Symbol Types:
- Allergen symbols (🥛, 🥚, etc.): Represent food allergens
- Dietary symbols (🥗, 🌿, etc.): Represent dietary restrictions
- Tooltips: Provide text descriptions on hover

## API Integration

### Endpoints
- GET `/api/menuItems`: Fetch all menu items
- GET `/api/menuItems/{id}`: Fetch single item
- POST `/api/menuItems`: Create new item
- PUT `/api/menuItems/{id}`: Update existing item

### Axios Methods
- `axios.get()`: Retrieves data from server
- `axios.post()`: Sends data to create new resources
- `axios.put()`: Updates existing resources
- `.then()`: Handles successful responses
- `.catch()`: Handles errors

## Component State Management

### React Hooks
- `useState`: Creates state variables
  ```javascript
  const [menuItem, setMenuItem] = useState({});
  ```
- `useEffect`: Manages side effects
  ```javascript
  useEffect(() => {
    // Effect code
  }, [dependencies]);
  ```

### Form Handling
- `onChange`: Event handler for input changes
- `onSubmit`: Event handler for form submission
- `value`: Controlled component value
- `checked`: Boolean state for switches/checkboxes

## Material-UI Components

### Layout Components
- `<Container>`: Wrapper for content centering
- `<Stack>`: Flexbox-based layout manager
- `<TableContainer>`: Table wrapper with scrolling
- `<Paper>`: Elevated surface component

### Input Components
- `<TextField>`: Text input with styling
- `<Autocomplete>`: Enhanced select input
- `<Switch>`: Toggle button
- `<Chip>`: Compact element for information

### Display Components
- `<Typography>`: Text styling component
- `<Alert>`: Message display component
- `<Tooltip>`: Hover information display
- `<Button>`: Styled button component

## Data Flow
1. Component mounts
2. useEffect triggers API call
3. Response updates state
4. State changes trigger re-render
5. User interactions update state
6. Form submissions send updates to API

## Error Handling
- Try-catch blocks for API calls
- Error state management
- User-friendly error messages
- Console error logging
