export enum VHSStatus {
  AVAILABLE = "AVAILABLE",
  OUT_OF_STOCK = "OUT_OF_STOCK",
  DAMAGED = "DAMAGED",
  LOST = "LOST",
}

export interface Genre {
  id: string;
  name: string;
}

export interface GenreDTO {
  id: string;
  name: string;
}

export interface User {
  id: string;
  name: string;
  email: string;
  favoriteGenres: Genre[];
}

export interface UserDTO {
  id: string;
  name: string;
  email: string;
  favoriteGenres?: Genre[];
}


export interface VHS {
  id: string;
  title: string;
  releaseDate: string; // ISO 8601
  genre: GenreDTO;
  rentalPrice: number;
  stockLevel: number;
  imageUrl: string | null;
  status: VHSStatus;
}

export interface VHSDTO {
  id: string;
  title: string;
  releaseDate: string;
  genre: GenreDTO;
  rentalPrice: number;
  stockLevel: number;
  imageUrl: string | null;
  status: VHSStatus;
}

export interface Rental {
  id: string;
  vhs: VHSDTO;
  user: UserDTO;
  rentalDate: string;
  dueDate: string;
  returnDate: string | null;
  price: number | null;
}

export interface Review {
  id: string;
  rating: number;
  comment: string | null;
  user: { email: string };
  vhs: { id: string; title: string; genre: string; releaseDate: string };
}

export interface PageResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: string;
    offset: number;
    paged: boolean;
    unpaged: boolean;
  };
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
  number: number;
  size: number;
  numberOfElements: number;
  empty: boolean;
}

export interface ApiResponse<T> {
  status: string;
  message: string;
  timestamp: string;
  data: T;
}

export interface ProblemDetail {
  status: number;
  title: string;
  detail: string;
  timestamp: string;
  path?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
}

export interface PaginationQuery {
  page: number;
  size: number;
  sort?: string;
}
