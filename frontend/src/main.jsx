import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import "@fontsource/roboto/300.css";
import "@fontsource/roboto/400.css";
import "@fontsource/roboto/500.css";
import "@fontsource/roboto/700.css";
import App from "./App";
import Home from "./Home";
import Menu from "./menu/Menu";
import WaiterPage from "./waiter/Waiter";
import KitchenPage from "./Kitchen/KitchenPage";
import AddMenuItem from "./menuitem/AddMenuItem";
import ChangeMenuItem from "./menuitem/ChangeMenuItem";
import EditMenuItem from "./menuitem/EditMenuItem";

const router = createBrowserRouter([
  {
    path: "/",
    element: <App><Home /></App>,
  },
  {
    path: "/menu",
    element: <App><Menu /></App>,
  },
  {
    path: "/waiter",
    element: <App><WaiterPage /></App>,
  },
  {
    path: "/waiter/add-item",
    element: <App><AddMenuItem /></App>,
  },
  {
    path: "/waiter/edit-items",
    element: <App><ChangeMenuItem /></App>,
  },
  {
    path: "/waiter/edit-item/:id",
    element: <App><EditMenuItem /></App>,
  },
  {
    path: "/menuitem/add",
    element: <App><AddMenuItem /></App>,
  },
  {
    path: "/menuitem/change",
    element: <App><ChangeMenuItem /></App>,
  },
  {
    path: "/menuitem/edit/:id",
    element: <App><EditMenuItem /></App>,
  },
]);

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <RouterProvider router={router} />
  </StrictMode>,
);