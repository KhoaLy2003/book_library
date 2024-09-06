import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { BaseResponse, PageResponse } from '../shared/base-response.model';
import { Book, BookDetail } from '../models/book.model';

@Injectable({
  providedIn: 'root',
})
export class BookService {
  private httpClient = inject(HttpClient);
  private books = signal<Book[]>([]);

  loadedBooks = this.books.asReadonly();

  fetchBookByIsbn(isbn: string) {
    return this.httpClient.get<BaseResponse<BookDetail>>(
      `http://localhost:8080/api/v1/books/isbn?isbn=${isbn}`
    );
  }

  fetchBooks(pageNo: number, title: string) {
    return this.httpClient.get<BaseResponse<PageResponse<Book>>>(
      `http://localhost:8080/api/v1/books?pageNo=${pageNo}&title=${title}`
    );
  }
}
