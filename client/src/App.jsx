import { BrowserRouter, Routes, Route } from "react-router-dom"; // React Router components used for client-side routing
// in a Single Page Application (SPA).
import DashboardPage from "./pages/DashboardPage"; // This is how to import and connect page file here to be rendered
import LoginPage from "./pages/LoginPage";
import VehicleDetailsPage from "./pages/VehicleDetailsPage";
import VehiclesPage from "./pages/VehiclesPage";
import MainLayout from "./layouts/MainLayout";
import ProtectedRoute from "./components/ProtectedRoute";
import RegisterPage from "./pages/RegisterPage";
import ProfilePage from "./pages/ProfilePage";
import ServiceHistoryPage from "./pages/ServiceHistoryPage";
import UpcomingServicesPage from "./pages/UpcomingServicesPage";
import OverdueServicesPage from "./pages/OverdueServicesPage";

// Root component responsible for application routing
function App() {

  return (
    <BrowserRouter>
      <Routes>

        <Route path="/" element={
          <MainLayout>
            <ProtectedRoute>
              <DashboardPage />
            </ProtectedRoute>
          </MainLayout>
        } />

        <Route path="/login" element={
          <MainLayout>
            <LoginPage />
          </MainLayout>
        } />

        <Route path="/register" element={
          <MainLayout>
            <RegisterPage />
          </MainLayout>
        } />

        <Route path="/profile" element={
          <MainLayout>
            <ProtectedRoute>
              <ProfilePage />
            </ProtectedRoute>
          </MainLayout>
        } />

        <Route path="/vehicles" element={
          <MainLayout>
            <ProtectedRoute>
              <VehiclesPage />
            </ProtectedRoute>
          </MainLayout>
        } />

        <Route
          path="/vehicles/:id"
          element={
            <MainLayout>
              <ProtectedRoute>
                <VehicleDetailsPage />
              </ProtectedRoute>
            </MainLayout>
          }
        />

        <Route
          path="/service-history"
          element={
            <MainLayout>
              <ProtectedRoute>
                <ServiceHistoryPage />
              </ProtectedRoute>
            </MainLayout>
          }
        />

        <Route
          path="/upcoming-services"
          element={
            <MainLayout>
              <ProtectedRoute>
                <UpcomingServicesPage />
              </ProtectedRoute>
            </MainLayout>
          }
        />

        <Route
          path="/overdue-services"
          element={
            <MainLayout>
              <ProtectedRoute>
                <OverdueServicesPage />
              </ProtectedRoute>
            </MainLayout>
          }
        />

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

