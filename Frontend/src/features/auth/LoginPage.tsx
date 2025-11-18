import { useState } from 'react';
import { GraduationCap, Mail, Lock, Loader2 } from 'lucide-react';
import { useAuthStore } from '../../shared/store/authStore';
import { Button } from '../../shared/components/Button';

export const LoginPage = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [showRegister, setShowRegister] = useState(false);
    const [fullName, setFullName] = useState('');

    const { login, register, isLoading, error, clearError } = useAuthStore();

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        clearError();

        try {
            await login(email, password);
            // La navegación se manejará automáticamente en App.tsx
        } catch (error) {
            // El error ya está manejado en el store
            console.error('Error en login:', error);
        }
    };

    const handleRegister = async (e: React.FormEvent) => {
        e.preventDefault();
        clearError();

        try {
            await register(fullName, email, password);
            // La navegación se manejará automáticamente en App.tsx
        } catch (error) {
            console.error('Error en registro:', error);
        }
    };

    return (
        <div className="min-h-screen bg-gradient-to-br from-blue-600 to-blue-800 flex items-center justify-center p-4">
            <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md p-8">
                {/* Header */}
                <div className="flex flex-col items-center mb-8">
                    <div className="bg-blue-600 p-4 rounded-full mb-4">
                        <GraduationCap size={48} className="text-white" />
                    </div>
                    <h1 className="text-3xl font-bold text-gray-800">Sistema de Matrículas</h1>
                    <p className="text-gray-600 mt-2">
                        {showRegister ? 'Crear una cuenta' : 'Iniciar sesión'}
                    </p>
                </div>

                {/* Error Message */}
                {error && (
                    <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-4">
                        {error}
                    </div>
                )}

                {/* Login Form */}
                {!showRegister ? (
                    <form onSubmit={handleLogin} className="space-y-4">
                        {/* Email Input */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Correo Electrónico
                            </label>
                            <div className="relative">
                                <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                                <input
                                    type="email"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    placeholder="tu@email.com"
                                    required
                                    disabled={isLoading}
                                />
                            </div>
                        </div>

                        {/* Password Input */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Contraseña
                            </label>
                            <div className="relative">
                                <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                                <input
                                    type="password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    placeholder="••••••••"
                                    required
                                    disabled={isLoading}
                                />
                            </div>
                        </div>

                        {/* Login Button */}
                        <Button
                            type="submit"
                            disabled={isLoading}
                            className="w-full flex items-center justify-center"
                        >
                            {isLoading ? (
                                <>
                                    <Loader2 className="animate-spin mr-2" size={20} />
                                    Iniciando sesión...
                                </>
                            ) : (
                                'Iniciar Sesión'
                            )}
                        </Button>
                    </form>
                ) : (
                    /* Register Form */
                    <form onSubmit={handleRegister} className="space-y-4">
                        {/* Full Name Input */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Nombre Completo
                            </label>
                            <input
                                type="text"
                                value={fullName}
                                onChange={(e) => setFullName(e.target.value)}
                                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                                placeholder="Juan Pérez"
                                required
                                disabled={isLoading}
                            />
                        </div>

                        {/* Email Input */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Correo Electrónico
                            </label>
                            <div className="relative">
                                <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                                <input
                                    type="email"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    placeholder="tu@email.com"
                                    required
                                    disabled={isLoading}
                                />
                            </div>
                        </div>

                        {/* Password Input */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Contraseña
                            </label>
                            <div className="relative">
                                <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                                <input
                                    type="password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    placeholder="••••••••"
                                    required
                                    minLength={6}
                                    disabled={isLoading}
                                />
                            </div>
                        </div>

                        {/* Register Button */}
                        <Button
                            type="submit"
                            disabled={isLoading}
                            className="w-full flex items-center justify-center"
                        >
                            {isLoading ? (
                                <>
                                    <Loader2 className="animate-spin mr-2" size={20} />
                                    Registrando...
                                </>
                            ) : (
                                'Registrarse'
                            )}
                        </Button>
                    </form>
                )}

                {/* Toggle Register/Login */}
                <div className="mt-6 text-center">
                    <button
                        type="button"
                        onClick={() => {
                            setShowRegister(!showRegister);
                            clearError();
                        }}
                        className="text-blue-600 hover:text-blue-700 font-medium"
                        disabled={isLoading}
                    >
                        {showRegister
                            ? '¿Ya tienes cuenta? Inicia sesión'
                            : '¿No tienes cuenta? Regístrate'}
                    </button>
                </div>
            </div>
        </div>
    );
};