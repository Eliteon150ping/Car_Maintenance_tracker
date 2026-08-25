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
          <ProtectedRoute>
            <MainLayout>
              <DashboardPage />
            </MainLayout>
          </ProtectedRoute>
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
          <ProtectedRoute>
            <MainLayout>
              <ProfilePage />
            </MainLayout>
          </ProtectedRoute>
        } />

        <Route path="/vehicles" element={
          <ProtectedRoute>
            <MainLayout>
              <VehiclesPage />
            </MainLayout>
          </ProtectedRoute>
        } />

        <Route
          path="/vehicles/:id"
          element={
            <ProtectedRoute>
              <MainLayout>
                <VehicleDetailsPage />
              </MainLayout>
            </ProtectedRoute>
          }
        />

        <Route
          path="/service-history"
          element={
            <ProtectedRoute>
              <MainLayout>
                <ServiceHistoryPage />
              </MainLayout>
            </ProtectedRoute>
          }
        />

        <Route 
        path="/upcoming-services"
        element={
          <ProtectedRoute>
            <MainLayout>
              <UpcomingServicesPage />
            </MainLayout>
          </ProtectedRoute>
        }
        />

        <Route 
        path="/overdue-services"
        element={
          <ProtectedRoute>
            <MainLayout>
              <OverdueServicesPage />
            </MainLayout>
          </ProtectedRoute>
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

