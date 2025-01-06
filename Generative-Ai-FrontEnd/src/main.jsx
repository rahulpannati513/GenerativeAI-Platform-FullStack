import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.jsx";
import { RouterProvider } from "react-router-dom";
import { createBrowserRouter } from "react-router-dom";
import ImageUploader from "./components/ImageUploader";
import AudioUploader from "./components/AudioUploader.jsx";
import RecipeUploader from "./components/RecipeUploader.jsx";
import AboutDeveloper from "./components/AboutDeveloper.jsx";
import Header from "./components/Header.jsx";
import "font-awesome/css/font-awesome.min.css";

const router = createBrowserRouter([
  {
    path: "/",
    element: (
      <>
        <Header /> <App />
      </>
    ),
  },
  {
    path: "/image",
    element: (
      <>
        <Header /> <ImageUploader />
      </>
    ),
  },
  {
    path: "/audio",
    element: (
      <>
        <Header /> <AudioUploader />
      </>
    ),
  },
  {
    path: "/recipe",
    element: (
      <>
        <Header /> <RecipeUploader />
      </>
    ),
  },
  {
    path: "aboutDeveloper",
    element: (
      <>
        <Header /> <AboutDeveloper />
      </>
    ),
  },
]);
createRoot(document.getElementById("root")).render(
  <>
    <RouterProvider router={router} />
  </>
);
