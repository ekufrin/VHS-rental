import { create } from "zustand";
import type { User } from "../types";


interface AuthStore {
  accessToken: string | null;
  user: User | null;
  isAuthenticated: boolean;

  setAccessToken: (token: string | null) => void;
  setUser: (user: User | null) => void;
  logout: () => void;
}

const isClient = typeof window !== "undefined" && typeof localStorage !== "undefined";

export const useAuthStore = create<AuthStore>((set) => ({
  accessToken: isClient ? localStorage.getItem("accessToken") : null,
  user: isClient
    ? (() => {
        const stored = localStorage.getItem("user");
        return stored ? JSON.parse(stored) : null;
      })()
    : null,
  isAuthenticated: isClient ? !!localStorage.getItem("accessToken") : false,

  setAccessToken: (token: string | null) => {
    if (isClient) {
      if (token) {
        localStorage.setItem("accessToken", token);
      } else {
        localStorage.removeItem("accessToken");
      }
    }
    set({ accessToken: token, isAuthenticated: !!token });
  },

  setUser: (user: User | null) => {
    if (isClient) {
      if (user) {
        localStorage.setItem("user", JSON.stringify(user));
      } else {
        localStorage.removeItem("user");
      }
    }
    set({ user });
  },

  logout: () => {
    if (isClient) {
      localStorage.removeItem("accessToken");
      localStorage.removeItem("user");
    }
    set({ accessToken: null, user: null, isAuthenticated: false });
  },
}));
