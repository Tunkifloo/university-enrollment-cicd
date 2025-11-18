import { authService } from '../services/authService';
import type { Facultad, Carrera } from '../types';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1';

const log = (message: string, data?: unknown) => {
    if (import.meta.env.DEV) {
        console.log(`[API] ${message}`, data || '');
    }
};

const getAuthHeaders = (): HeadersInit => {
    const token = authService.getToken();
    const headers: HeadersInit = {
        'Content-Type': 'application/json',
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    return headers;
};

const handleResponse = async <T>(response: Response): Promise<T | null> => {
    if (response.status === 401) {
        authService.logout();
        window.location.reload();
        throw new Error('Sesión expirada');
    }

    if (!response.ok) {
        const error = await response.json().catch(() => ({
            message: `Error ${response.status}`
        }));
        throw new Error(error.message || `Error ${response.status}`);
    }

    if (response.status === 204) {
        return null;
    }

    return response.json();
};

// Tipos para las peticiones
interface FacultadCreateRequest {
    nombre: string;
    descripcion?: string;
    ubicacion?: string;
    decano?: string;
    activo?: boolean;
}

interface CarreraCreateRequest {
    facultadId: number;
    nombre: string;
    descripcion?: string;
    duracionSemestres: number;
    tituloOtorgado?: string;
    activo?: boolean;
}

export const api = {
    baseURL: API_BASE_URL,

    facultades: {
        getAll: async (): Promise<Facultad[]> => {
            log('GET /matriculas/facultades');
            return fetch(`${API_BASE_URL}/matriculas/facultades`, {
                headers: getAuthHeaders(),
            }).then(response => handleResponse<Facultad[]>(response)) as Promise<Facultad[]>;
        },

        getById: async (id: number): Promise<Facultad> => {
            log(`GET /matriculas/facultades/${id}`);
            return fetch(`${API_BASE_URL}/matriculas/facultades/${id}`, {
                headers: getAuthHeaders(),
            }).then(response => handleResponse<Facultad>(response)) as Promise<Facultad>;
        },

        create: async (data: FacultadCreateRequest): Promise<Facultad> => {
            log('POST /matriculas/facultades', data);
            return fetch(`${API_BASE_URL}/matriculas/facultades`, {
                method: 'POST',
                headers: getAuthHeaders(),
                body: JSON.stringify(data)
            }).then(response => handleResponse<Facultad>(response)) as Promise<Facultad>;
        },

        update: async (id: number, data: Partial<FacultadCreateRequest>): Promise<Facultad> => {
            log(`PUT /matriculas/facultades/${id}`, data);
            return fetch(`${API_BASE_URL}/matriculas/facultades/${id}`, {
                method: 'PUT',
                headers: getAuthHeaders(),
                body: JSON.stringify(data)
            }).then(response => handleResponse<Facultad>(response)) as Promise<Facultad>;
        },

        delete: async (id: number): Promise<void> => {
            log(`DELETE /matriculas/facultades/${id}`);
            await fetch(`${API_BASE_URL}/matriculas/facultades/${id}`, {
                method: 'DELETE',
                headers: getAuthHeaders(),
            }).then(response => handleResponse<void>(response));
        }
    },

    carreras: {
        getAll: async (): Promise<Carrera[]> => {
            log('GET /matriculas/carreras');
            return fetch(`${API_BASE_URL}/matriculas/carreras`, {
                headers: getAuthHeaders(),
            }).then(response => handleResponse<Carrera[]>(response)) as Promise<Carrera[]>;
        },

        getById: async (id: number): Promise<Carrera> => {
            log(`GET /matriculas/carreras/${id}`);
            return fetch(`${API_BASE_URL}/matriculas/carreras/${id}`, {
                headers: getAuthHeaders(),
            }).then(response => handleResponse<Carrera>(response)) as Promise<Carrera>;
        },

        getByFacultad: async (facultadId: number): Promise<Carrera[]> => {
            log(`GET /matriculas/carreras/facultad/${facultadId}`);
            return fetch(`${API_BASE_URL}/matriculas/carreras/facultad/${facultadId}`, {
                headers: getAuthHeaders(),
            }).then(response => handleResponse<Carrera[]>(response)) as Promise<Carrera[]>;
        },

        create: async (data: CarreraCreateRequest): Promise<Carrera> => {
            log('POST /matriculas/carreras', data);
            return fetch(`${API_BASE_URL}/matriculas/carreras`, {
                method: 'POST',
                headers: getAuthHeaders(),
                body: JSON.stringify(data)
            }).then(response => handleResponse<Carrera>(response)) as Promise<Carrera>;
        },

        update: async (id: number, data: Partial<CarreraCreateRequest>): Promise<Carrera> => {
            log(`PUT /matriculas/carreras/${id}`, data);
            return fetch(`${API_BASE_URL}/matriculas/carreras/${id}`, {
                method: 'PUT',
                headers: getAuthHeaders(),
                body: JSON.stringify(data)
            }).then(response => handleResponse<Carrera>(response)) as Promise<Carrera>;
        },

        delete: async (id: number): Promise<void> => {
            log(`DELETE /matriculas/carreras/${id}`);
            await fetch(`${API_BASE_URL}/matriculas/carreras/${id}`, {
                method: 'DELETE',
                headers: getAuthHeaders(),
            }).then(response => handleResponse<void>(response));
        }
    }
};

export const config = {
    apiBaseURL: API_BASE_URL,
    appName: import.meta.env.VITE_APP_NAME || 'Sistema de Matrículas',
    appVersion: import.meta.env.VITE_APP_VERSION || '1.0.0',
    enableLogs: import.meta.env.DEV
};

// Exportar los tipos para uso en otros archivos
export type { FacultadCreateRequest, CarreraCreateRequest };