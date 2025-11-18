import { create } from "zustand";
import type { Facultad } from "../../../shared/types";
import { api, type FacultadCreateRequest } from "../../../shared/config/api";

interface FacultadState {
    facultades: Facultad[];
    loading: boolean;
    error: string | null;
    fetchFacultades: () => Promise<void>;
    createFacultad: (data: FacultadCreateRequest) => Promise<void>;
    updateFacultad: (id: number, data: Partial<FacultadCreateRequest>) => Promise<void>;
    deleteFacultad: (id: number) => Promise<void>;
}

export const useFacultadStore = create<FacultadState>((set) => ({
    facultades: [],
    loading: false,
    error: null,

    fetchFacultades: async () => {
        set({ loading: true, error: null });
        try {
            const data = await api.facultades.getAll();
            set({ facultades: data, loading: false });
        } catch (error) {
            set({ error: "Error al cargar facultades", loading: false });
        }
    },

    createFacultad: async (data) => {
        set({ loading: true, error: null });
        try {
            await api.facultades.create(data);
            const facultades = await api.facultades.getAll();
            set({ facultades, loading: false });
        } catch (error) {
            set({ error: "Error al crear facultad", loading: false });
            throw error;
        }
    },

    updateFacultad: async (id, data) => {
        set({ loading: true, error: null });
        try {
            await api.facultades.update(id, data);
            const facultades = await api.facultades.getAll();
            set({ facultades, loading: false });
        } catch (error) {
            set({ error: "Error al actualizar facultad", loading: false });
            throw error;
        }
    },

    deleteFacultad: async (id) => {
        set({ loading: true, error: null });
        try {
            await api.facultades.delete(id);
            const facultades = await api.facultades.getAll();
            set({ facultades, loading: false });
        } catch (error) {
            set({ error: "Error al eliminar facultad", loading: false });
            throw error;
        }
    },
}));