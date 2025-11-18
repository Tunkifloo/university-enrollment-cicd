import { create } from 'zustand';
import { authService, type AuthResponse } from '../services/authService';

interface AuthState {
    user: AuthResponse | null;
    isAuthenticated: boolean;
    isLoading: boolean;
    error: string | null;

    login: (email: string, password: string) => Promise<void>;
    register: (fullName: string, email: string, password: string) => Promise<void>;
    logout: () => void;
    initializeAuth: () => void;
    clearError: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
    user: null,
    isAuthenticated: false,
    isLoading: false,
    error: null,

    login: async (email: string, password: string) => {
        set({ isLoading: true, error: null });
        try {
            const authData = await authService.login({ email, password });
            authService.saveAuth(authData);
            set({
                user: authData,
                isAuthenticated: true,
                isLoading: false
            });
        } catch (error) {
            set({
                error: error instanceof Error ? error.message : 'Error al iniciar sesión',
                isLoading: false
            });
            throw error;
        }
    },

    register: async (fullName: string, email: string, password: string) => {
        set({ isLoading: true, error: null });
        try {
            const authData = await authService.register({ fullName, email, password });
            authService.saveAuth(authData);
            set({
                user: authData,
                isAuthenticated: true,
                isLoading: false
            });
        } catch (error) {
            set({
                error: error instanceof Error ? error.message : 'Error al registrar',
                isLoading: false
            });
            throw error;
        }
    },

    logout: () => {
        authService.logout();
        set({
            user: null,
            isAuthenticated: false,
            error: null
        });
    },

    initializeAuth: () => {
        const user = authService.getUser();
        if (user) {
            set({ user, isAuthenticated: true });
        }
    },

    clearError: () => set({ error: null }),
}));