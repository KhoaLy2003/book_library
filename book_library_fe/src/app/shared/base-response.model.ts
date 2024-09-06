export interface BaseResponse<T> {
  status: number | undefined;
  message: string | undefined;
  data: T;
}

export interface BaseExceptionResponse {
  status: number | undefined;
  message: string | undefined;
  content: string | undefined;
}

export interface PageResponse<T> {
  currentPage: number | undefined;
  totalPages: number | undefined;
  pageSize: number | undefined;
  totalElements: number | undefined;
  data: T[] | undefined;
}
