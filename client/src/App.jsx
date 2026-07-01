import { BrowserRouter, Routes, Route } from "react-router-dom"; // React Router components used for client-side routing
                                                                 // in a Single Page Application (SPA).
import DashboardPage from "./pages/DashboardPage"; // This is how to import and connect page file here to be rendered
import LoginPage from "./pages/LoginPage";
import ServiceRecordsPage from "./pages/ServiceRecordsPage";
import VehiclesPage from "./pages/VehiclesPage";
import MainLayout from "./layouts/MainLayout";

// Root component responsible for application routing
function App() {

  return (
    <BrowserRouter>
      <Routes>

        <Route path="/" element={
          <MainLayout>
            <DashboardPage />
          </MainLayout>
        } />

        <Route path="/login" element={
          <MainLayout>
            <LoginPage />
          </MainLayout>
        } />

        <Route path="/service-records" element={
          <MainLayout>
            <ServiceRecordsPage />
          </MainLayout>
        } />

        <Route path="/vehicles" element={
          <MainLayout>
            <VehiclesPage />
          </MainLayout>
        } />

      </Routes>
    </BrowserRouter>
  );
}

export default App;

/*

React Router works similarly to Spring Boot request mappings.

React:
<Route path="/vehicles" element={<VehiclesPage />} />

What is it?
- A library that maps URLs to React components.

Why does it exist?
- To allow navigation without reloading the page.

When do I use it?
- Whenever an application has multiple pages/screens.

Spring comparison
- Similar to @GetMapping or @RequestMapping,
  except it returns UI instead of JSON.



*/

/* 
   Spring Boot comparison:
   ===============+=================================================================================================
   React	        |                 Spring Boot Equivalent
   ===============+================================================================================================
   pages	        |                 Controllers (entry point for a route/view)
   ---------------+------------------------------------------------------------------------------------------------
   components	    |                 Reusable classes/services (building blocks used in multiple places)
   ---------------+------------------------------------------------------------------------------------------------
   layouts	      |                 Common page template (no direct Spring equivalent, but similar to shared view templates)
   ---------------+------------------------------------------------------------------------------------------------
   context	      |                 Singleton/shared application state (like a service managing logged-in user information)
   ---------------+------------------------------------------------------------------------------------------------
   utils	        |                 Utility/helper classes (DateUtils, ValidationUtils, etc.)
   ---------------+------------------------------------------------------------------------------------------------
   api	          |                 Your service layer that talks to the backend via HTTP                         */

