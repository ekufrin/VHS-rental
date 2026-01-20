import { AxiosError } from "axios";
import type { ProblemDetail, ApiResponse } from "../types";

export class ApiErrorHandler extends Error {
  public status: number;
  public title: string;
  public detail: string;
  public timestamp: string;

  constructor(error: AxiosError) {
    let message = "An error occurred";
    let status = error.response?.status || 0;
    let title = "Error";
    let detail = "An unexpected error occurred";
    let timestamp = new Date().toISOString();

    if (error.response?.data) {
      let data = error.response.data as any;

      if (typeof data === "string") {
        try {
          data = JSON.parse(data);
        } catch {
          detail = data;
          title = data;
          message = data;
          super(message);
          this.status = status;
          this.title = title;
          this.detail = detail;
          this.timestamp = timestamp;
          Object.setPrototypeOf(this, ApiErrorHandler.prototype);
          return;
        }
      }

      if (data && typeof data === "object") {
        if (data.detail && typeof data.detail === "string") {
          detail = data.detail;
        }
        else if (data.message && typeof data.message === "string") {
          detail = data.message;
        }
        

        if (data.status) {
          status = data.status;
        }
        if (data.title) {
          title = data.title;
        }
        if (data.timestamp) {
          timestamp = data.timestamp;
        }
        
        message = detail || title || message;
      }
    } else if (error.message) {
      message = error.message;
      detail = error.message;
    }

    super(message);
    this.status = status;
    this.title = title;
    this.detail = detail;
    this.timestamp = timestamp;

    Object.setPrototypeOf(this, ApiErrorHandler.prototype);
  }
}

export const handleApiError = (error: unknown): ApiErrorHandler => {
  if (error instanceof ApiErrorHandler) {
    return error;
  }

  if (error instanceof AxiosError) {
    return new ApiErrorHandler(error);
  }

  const err = new Error();
  const handler = new ApiErrorHandler(
    new AxiosError(err.message, undefined, undefined, undefined)
  );
  return handler;
};
