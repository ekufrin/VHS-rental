import apiClient from "./client";
import type { ApiResponse, PageResponse, Review } from "../types";
import { handleApiError } from "./errorHandler";
import type { PaginationQuery } from "../types";

export const reviewApi = {
  createReview: async (data: {
    rentalId: string;
    rating: number;
    comment?: string;
  }): Promise<Review> => {
    try {
      const response = await apiClient.post<ApiResponse<Review>>(
        "/reviews",
        data
      );
      return response.data.data;
    } catch (error) {
      throw handleApiError(error);
    }
  },

  getReviewsByVHS: async (
    vhsId: string,
    params: PaginationQuery
  ): Promise<PageResponse<Review>> => {
    try {
      const response = await apiClient.get<ApiResponse<PageResponse<Review>>>(
        `/reviews/vhs/${vhsId}`,
        { params }
      );
      return response.data.data;
    } catch (error) {
      throw handleApiError(error);
    }
  },

  getReviewDetail: async (id: string): Promise<Review> => {
    try {
      const response = await apiClient.get<ApiResponse<Review>>(
        `/reviews/${id}`
      );
      return response.data.data;
    } catch (error) {
      throw handleApiError(error);
    }
  },

  updateReview: async (
    id: string,
    data: { rating: number; comment?: string }
  ): Promise<void> => {
    try {
      await apiClient.put(`/reviews/${id}`, data);
    } catch (error) {
      throw handleApiError(error);
    }
  },

  deleteReview: async (id: string): Promise<void> => {
    try {
      await apiClient.delete(`/reviews/${id}`);
    } catch (error) {
      throw handleApiError(error);
    }
  },
};
