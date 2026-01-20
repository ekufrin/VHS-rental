import apiClient from "./client";
import type { ApiResponse, PageResponse, Genre } from "../types";
import { handleApiError } from "./errorHandler";
import type { PaginationQuery } from "../types";

export const genreApi = {
  createGenre: async (data: { name: string }): Promise<Genre> => {
    try {
      const response = await apiClient.post<ApiResponse<Genre>>(
        "/genres",
        data
      );
      return response.data.data;
    } catch (error) {
      throw handleApiError(error);
    }
  },

  getGenreList: async (
    params: PaginationQuery
  ): Promise<PageResponse<Genre>> => {
    try {
      const response = await apiClient.get<ApiResponse<PageResponse<Genre>>>(
        "/genres",
        { params }
      );
      return response.data.data;
    } catch (error) {
      throw handleApiError(error);
    }
  },

  getGenreDetail: async (id: string): Promise<Genre> => {
    try {
      const response = await apiClient.get<ApiResponse<Genre>>(
        `/genres/${id}`
      );
      return response.data.data;
    } catch (error) {
      throw handleApiError(error);
    }
  },
};
