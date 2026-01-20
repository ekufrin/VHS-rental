import apiClient from "./client";
import type { ApiResponse, PageResponse, Rental } from "../types";
import { handleApiError } from "./errorHandler";
import type { PaginationQuery } from "../types";

export const rentalApi = {
  createRental: async (data: {
    vhsId: string;
    dueDate: string;
  }): Promise<Rental> => {
    try {
      const response = await apiClient.post<ApiResponse<Rental>>(
        "/rentals",
        data
      );
      return response.data.data;
    } catch (error) {
      throw handleApiError(error);
    }
  },

  finishRental: async (rentalId: string): Promise<Rental> => {
    try {
      const response = await apiClient.patch<ApiResponse<Rental>>(
        `/rentals/${rentalId}/finish`
      );
      return response.data.data;
    } catch (error) {
      throw handleApiError(error);
    }
  },

  getRentalList: async (
    params: PaginationQuery
  ): Promise<PageResponse<Rental>> => {
    try {
      const response = await apiClient.get<ApiResponse<PageResponse<Rental>>>(
        "/rentals",
        { params }
      );
      return response.data.data;
    } catch (error) {
      throw handleApiError(error);
    }
  },

  getRentalDetail: async (rentalId: string): Promise<Rental> => {
    try {
      const response = await apiClient.get<ApiResponse<Rental>>(
        `/rentals/${rentalId}`
      );
      return response.data.data;
    } catch (error) {
      throw handleApiError(error);
    }
  },
};
