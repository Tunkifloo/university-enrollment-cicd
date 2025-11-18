export interface LoginRequest {
    email: string;
    password: string;
}

export interface RegisterRequest {
    fullName: string;
    email: string;
    password: string;
}

export interface AuthResponse {
    token: string;
    type: string;
    userId: number;
    email: string;
    fullName: string;
    role: string;
}

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1';

export const authService = {
    async login(credentials: LoginRequest): Promise<AuthResponse> {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(credentials),
        });

        if (!response.ok) {
            const error = await response.json().catch(() => ({ message: 'Error al iniciar sesión' }));
            throw new Error(error.message || 'Credenciales inválidas');
        }

        return response.json();
    },

    async register(data: RegisterRequest): Promise<AuthResponse> {
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        });

        if (!response.ok) {
            const error = await response.json().catch(() => ({ message: 'Error al registrar usuario' }));
            throw new Error(error.message || 'Error en el registro');
        }

        return response.json();
    },

    logout() {
        localStorage.removeItem('auth_token');
        localStorage.removeItem('auth_user');
    },

    getToken(): string | null {
        const token = localStorage.getItem('auth_token');

        // Validar que el token exista y no esté vacío
        if (!token || token === 'null' || token === 'undefined') {
            return null;
        }

        return token;
    },

    getUser(): AuthResponse | null {
        const token = this.getToken();

        // Si no hay token válido, retornar null
        if (!token) {
            return null;
        }

        const userStr = localStorage.getItem('auth_user');

        // Validar que el usuario exista y no esté vacío
        if (!userStr || userStr === 'null' || userStr === 'undefined') {
            return null;
        }

        try {
            return JSON.parse(userStr);
        } catch (error) {
            console.error('Error parsing user from localStorage:', error);
            return null;
        }
    },

    saveAuth(authData: AuthResponse) {
        localStorage.setItem('auth_token', authData.token);
        localStorage.setItem('auth_user', JSON.stringify(authData));
    },

    isAuthenticated(): boolean {
        return !!this.getToken() && !!this.getUser();
    },
};