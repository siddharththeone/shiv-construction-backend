import { create } from 'zustand';
import * as SecureStore from 'expo-secure-store';
import axios from 'axios';

type Role = 'OWNER' | 'CONTRACTOR' | 'SUPPLIER';
type User = { id: string; name: string; email?: string; phone?: string; role: Role } | null;

type AuthState = {
  user: User;
  token: string | null;
  initialize: () => Promise<void>;
  login: (params: { email?: string; phone?: string; password: string }) => Promise<void>;
  logout: () => Promise<void>;
  registerFcm: (token: string) => Promise<void>;
};

const API_BASE = process.env.EXPO_PUBLIC_API_BASE || 'https://shiv-construction-backend-production.up.railway.app/api';

export const useAuthStore = create<AuthState>((set, get) => ({
  user: null,
  token: null,
  initialize: async () => {
    const token = await SecureStore.getItemAsync('token');
    const userStr = await SecureStore.getItemAsync('user');
    if (token && userStr) set({ token, user: JSON.parse(userStr) });
  },
  login: async ({ email, phone, password }) => {
    const res = await axios.post(`${API_BASE}/auth/login`, { email, phone, password });
    const { token, user } = res.data;
    if (!token || !user) {
      throw new Error('Invalid response from server');
    }
    axios.defaults.headers.common.Authorization = `Bearer ${token}`;
    try {
      await SecureStore.setItemAsync('token', token);
      await SecureStore.setItemAsync('user', JSON.stringify(user));
    } catch (e) {
      // Fallback for web - use localStorage
      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(user));
    }
    set({ token, user });
  },
  logout: async () => {
    await SecureStore.deleteItemAsync('token');
    await SecureStore.deleteItemAsync('user');
    set({ token: null, user: null });
  },
  registerFcm: async (token) => {
    const authToken = get().token;
    if (!authToken) return;
    try {
      await axios.post(`${API_BASE}/auth/register-fcm`, { token }, { headers: { Authorization: `Bearer ${authToken}` } });
    } catch {}
  }
}));


