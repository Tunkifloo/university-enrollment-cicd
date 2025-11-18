import { create } from "zustand";
import type { Carrera } from "../../../shared/types";
import { api, type CarreraCreateRequest } from "../../../shared/config/api";

interface CarreraState {
    carreras: Carrera[];
    loading: boolean;
    error: string | null;
    fetchCarreras: () => Promise<void>;
    fetchCarrerasByFacultad: (facultadId: number) => Promise<void>;
    createCarrera: (data: CarreraCreateRequest) => Promise<void>;
    updateCarrera: (id: number, data: Partial<CarreraCreateRequest>) => Promise<void>;
    deleteCarrera: (id: number) => Promise<void>;
}

export const useCarrerasStore = create<CarreraState>((set) => ({
    carreras: [],
    loading: false,
    error: null,

    fetchCarreras: async () => {
        set({ loading: true, error: null });
        try {
            const data = await api.carreras.getAll();
            set({ carreras: data, loading: false });
        } catch {
            set({ error: "Error al cargar carreras", loading: false });
        }
    },

    fetchCarrerasByFacultad: async (facultadId: number) => {
        set({ loading: true, error: null });
        try {
            const data = await api.carreras.getByFacultad(facultadId);
            set({ carreras: data, loading: false });
        } catch {
            set({ error: "Error al cargar carreras por facultad", loading: false });
        }
    },

    createCarrera: async (data) => {
        set({ loading: true, error: null });
        try {
            await api.carreras.create(data);
            const carreras = await api.carreras.getAll();
            set({ carreras, loading: false });
        } catch (error) {
            set({ error: "Error al crear carrera", loading: false });
            throw error;
        }
    },

    updateCarrera: async (id, data) => {
        set({ loading: true, error: null });
        try {
            await api.carreras.update(id, data);
            const carreras = await api.carreras.getAll();
            set({ carreras, loading: false });
        } catch (error) {
            set({ error: "Error al actualizar carrera", loading: false });
            throw error;
        }
    },

    deleteCarrera: async (id) => {
        set({ loading: true, error: null });
        try {
            await api.carreras.delete(id);
            const carreras = await api.carreras.getAll();
            set({ carreras, loading: false });
        } catch (error) {
            set({ error: "Error al eliminar carrera", loading: false });
            throw error;
        }
    },
}));