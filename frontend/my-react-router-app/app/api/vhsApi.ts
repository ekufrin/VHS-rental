import apiClient from "./client";
import type { ApiResponse, PageResponse, VHS } from "../types";
import { handleApiError } from "./errorHandler";
import type { PaginationQuery } from "../types";

export const vhsApi = {
  createVHS: async (
    formData: FormData
  ): Promise<VHS> => {
    try {
      const response = await apiClient.post<ApiResponse<VHS>>(
        "/vhs",
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        }
      );
      return response.data.data;
    } catch (error) {
      throw handleApiError(error);
    }
  },

  getVHSList: async (params: PaginationQuery): Promise<PageResponse<VHS>> => {
    try {
      const response = await apiClient.get<ApiResponse<PageResponse<VHS>>>(
        "/vhs",
        { params }
      );
      return response.data.data;
    } catch (error) {
      throw handleApiError(error);
    }
  },

  getVHSDetail: async (id: string): Promise<VHS> => {
    try {
      const response = await apiClient.get<ApiResponse<VHS>>(`/vhs/${id}`);
      return response.data.data;
    } catch (error) {
      throw handleApiError(error);
    }
  },
};
