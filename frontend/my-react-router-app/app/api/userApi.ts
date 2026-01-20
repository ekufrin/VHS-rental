import apiClient from "./client";
import type { ApiResponse, PageResponse, User, Genre } from "../types";
import { handleApiError } from "./errorHandler";
import type { PaginationQuery } from "../types";

export const userApi = {
  getMe: async (): Promise<User> => {
    try {
      const response = await apiClient.get<ApiResponse<User>>("/users/me");
      return response.data.data;
    } catch (error) {
      throw handleApiError(error);
    }
  },

  getUserList: async (params: PaginationQuery): Promise<PageResponse<User>> => {
    try {
      const response = await apiClient.get<ApiResponse<PageResponse<User>>>(
        "/users",
        { params }
      );
      return response.data.data;
    } catch (error) {
      throw handleApiError(error);
    }
  },

  getUserDetail: async (id: string): Promise<User> => {
    try {
      const response = await apiClient.get<ApiResponse<User>>(`/users/${id}`);
      return response.data.data;
    } catch (error) {
      throw handleApiError(error);
    }
  },

  updateFavoriteGenres: async (favoriteGenres: string[]): Promise<User> => {
    try {
      const response = await apiClient.patch<ApiResponse<User>>(
        "/users/me/favorite-genres",
        { favoriteGenres }
      );
      return response.data.data;
    } catch (error) {
      throw handleApiError(error);
    }
  },
};
