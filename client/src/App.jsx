import { BrowserRouter, Routes, Route } from "react-router-dom"; // Imported liabaries that is standard for making SPA's
import DashboardPage from "./pages/DashboardPage"; // This is how to import and connect page file here to be rendered
import LoginPage from "./pages/LoginPage";
import ServiceRecordsPage from "./pages/ServiceRecordsPage";
import VehiclesPage from "./pages/VehiclesPage";

// Root component responsible for application routing
function App() {

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<DashboardPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/service-records" element={<ServiceRecordsPage />} />
        <Route path="/vehicles" element={<VehiclesPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;

/*

React Router works similarly to Spring Boot request mappings.

Spring:
@GetMapping("/vehicles")

React:
<Route path="/vehicles" element={<VehiclesPage />} />

Difference:
Spring routes return data.
React routes return UI.




*/
