import Navbar from "../components/Navbar";

function MainLayout({ children }) {

    return (
        <>
            <Navbar />
            <main>
                {children}
            </main>
        </>
    );
}

export default MainLayout;

/*

===========================================
MainLayout
===========================================

What is it?
- A layout component that provides a shared page structure.

Why does it exist?
- To avoid duplicating common UI (such as the Navbar)
  across multiple pages.

When should I use it?
- Whenever multiple pages share the same layout.

Java/Spring Boot comparison
- Similar in principle to placing shared logic inside
  a service instead of repeating it in every controller.
  Both follow the DRY (Don't Repeat Yourself) principle.


*/