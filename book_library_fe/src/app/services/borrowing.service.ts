import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';

import { tap } from 'rxjs';
import { BaseResponse, PageResponse } from '../shared/base-response.model';
import {
  BorrowingHistory,
  BorrowingHistoryDetail,
  BorrowingRequest,
  ExtendBorrowing,
  ReturnBorrowing,
} from '../models/borrowing.model';

@Injectable({
  providedIn: 'root',
})
export class BorrowingService {
  private httpClient = inject(HttpClient);
  private borrowingDetailHistoryList = signal<BorrowingHistoryDetail[]>([]);

  loadedMemberBorrowingHisotryDetail =
    this.borrowingDetailHistoryList.asReadonly();

  fetchBorrowingHisotry(
    pageNo: number,
    status?: string,
    startDate?: string,
    endDate?: string
  ) {
    let url = `http://localhost:8080/api/v1/borrowings?pageNo=${pageNo}`;

    if (status) {
      url += `&status=${status}`;
    }

    if (startDate && endDate) {
      url += `&startDate=${encodeURIComponent(
        startDate
      )}&endDate=${encodeURIComponent(endDate)}`;
    }

    return this.httpClient.get<BaseResponse<PageResponse<BorrowingHistory>>>(
      url
    );
  }

  fetchMemberBorrowingHisotry(accountId: number, pageNo: number) {
    return this.httpClient.get<BaseResponse<PageResponse<BorrowingHistory>>>(
      `http://localhost:8080/api/v1/borrowings/${accountId}/list?pageNo=${pageNo}`
    );
  }

  fetchMemberBorrowingHisotryDetail(borrowingId: number) {
    return this.httpClient
      .get<BaseResponse<BorrowingHistoryDetail[]>>(
        `http://localhost:8080/api/v1/borrowings/${borrowingId}`
      )
      .pipe(
        tap((response) => {
          this.borrowingDetailHistoryList.set(response.data);
        })
      );
  }

  newBorrowing(borrowingRequest: BorrowingRequest) {
    return this.httpClient.post<BaseResponse<string>>(
      'http://localhost:8080/api/v1/borrowings/borrow',
      borrowingRequest
    );
  }

  returnBorrowing(returnBorrowing: ReturnBorrowing) {
    return this.httpClient.post<BaseResponse<string>>(
      'http://localhost:8080/api/v1/borrowings/return',
      returnBorrowing
    );
  }

  cancelBorrowing(borrowingId: number) {
    return this.httpClient.post<BaseResponse<string>>(
      `http://localhost:8080/api/v1/borrowings/${borrowingId}/cancel`,
      null
    );
  }

  extendBorrowing(extendBorrowing: ExtendBorrowing) {
    return this.httpClient.post<BaseResponse<string>>(
      `http://localhost:8080/api/v1/borrowings/extend`,
      extendBorrowing
    );
  }
}
