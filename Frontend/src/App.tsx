import { useEffect, useState } from "react";
import { Layout } from "./shared/components/Layout";
import { FacultadesPage } from "./features/facultades/FacultadesPage";
import { CarrerasPage } from "./features/carreras/CarrerasPage";
import { LoginPage } from "./features/auth/LoginPage";
import { useAuthStore } from "./shared/store/authStore";
import { Loading } from "./shared/components/Loading";

function App() {
    const [activeTab, setActiveTab] = useState<"facultades" | "carreras">("facultades");
    const { isAuthenticated, initializeAuth } = useAuthStore();
    const [isInitializing, setIsInitializing] = useState(true);

    useEffect(() => {
        // Inicializar autenticación desde localStorage
        initializeAuth();
        setIsInitializing(false);
    }, [initializeAuth]);

    // Mostrar loading mientras se inicializa
    if (isInitializing) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-gray-50">
                <Loading />
            </div>
        );
    }

    // Si NO está autenticado, mostrar login
    if (!isAuthenticated) {
        return <LoginPage />;
    }

    // Si está autenticado, mostrar la app principal
    return (
        <Layout activeTab={activeTab} onTabChange={setActiveTab}>
            {activeTab === "facultades" ? <FacultadesPage /> : <CarrerasPage />}
        </Layout>
    );
}

export default App;