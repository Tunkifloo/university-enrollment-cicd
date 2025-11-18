import { GraduationCap, LogOut } from 'lucide-react';
import { useAuthStore } from '../store/authStore';

interface NavbarProps {
    activeTab: "facultades" | "carreras";
    onTabChange: (tab: "facultades" | "carreras") => void;
}

export const Navbar = ({ activeTab, onTabChange }: NavbarProps) => {
    const { user, logout } = useAuthStore();

    return (
        <nav className="bg-blue-600 text-white shadow-lg">
            <div className="container mx-auto px-4">
                <div className="flex items-center justify-between h-16">
                    <div className="flex items-center space-x-2">
                        <GraduationCap size={32} />
                        <span className="text-xl font-bold">Sistema de Matrículas</span>
                    </div>

                    <div className="flex items-center space-x-4">
                        <button
                            onClick={() => onTabChange("facultades")}
                            className={`px-4 py-2 rounded-lg transition-colors ${
                                activeTab === "facultades" ? "bg-blue-700" : "hover:bg-blue-500"
                            }`}
                        >
                            Facultades
                        </button>
                        <button
                            onClick={() => onTabChange("carreras")}
                            className={`px-4 py-2 rounded-lg transition-colors ${
                                activeTab === "carreras" ? "bg-blue-700" : "hover:bg-blue-500"
                            }`}
                        >
                            Carreras
                        </button>

                        {/* User Info */}
                        <div className="flex items-center space-x-4 ml-4 pl-4 border-l border-blue-500">
                            <div className="text-sm">
                                <div className="font-semibold">{user?.fullName}</div>
                                <div className="text-blue-200 text-xs">{user?.role}</div>
                            </div>
                            <button
                                onClick={logout}
                                className="p-2 hover:bg-blue-500 rounded-lg transition-colors"
                                title="Cerrar sesión"
                            >
                                <LogOut size={20} />
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </nav>
    );
};