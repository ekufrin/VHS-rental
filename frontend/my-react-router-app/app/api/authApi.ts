import apiClient from "./client";
import type { ApiResponse, AuthResponse, LoginRequest, RegisterRequest } from "../types";
import { handleApiError } from "./errorHandler";

export const authApi = {
  login: async (credentials: LoginRequest): Promise<AuthResponse> => {
    try {
      const response = await apiClient.post<ApiResponse<AuthResponse>>(
        "/auth/login",
        credentials
      );
      return response.data.data;
    } catch (error) {
      throw handleApiError(error);
    }
  },

  register: async (data: RegisterRequest): Promise<AuthResponse> => {
    try {
      const response = await apiClient.post<ApiResponse<AuthResponse>>(
        "/auth/register",
        data
      );
      return response.data.data;
    } catch (error) {
      throw handleApiError(error);
    }
  },

  logout: async (): Promise<void> => {
    try {
      await apiClient.post("/auth/logout");
    } catch (error) {
      throw handleApiError(error);
    }
  },

  refreshToken: async (): Promise<AuthResponse> => {
    try {
      const response = await apiClient.post<ApiResponse<AuthResponse>>(
        "/auth/refresh-token"
      );
      return response.data.data;
    } catch (error) {
      throw handleApiError(error);
    }
  },
};
