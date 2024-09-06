import { Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { Book, mapBookDetailToBook } from '../../../models/book.model';

import { InputGroupModule } from 'primeng/inputgroup';
import {
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { RouterLink } from '@angular/router';
import { SharedModule } from '../../../shared/shared.module';
import { NotificationService } from '../../../shared/notification.service';
import { BookService } from '../../../services/book.service';
import { getBookSeverity, getStatusValue } from '../../../utils/severity.utils';

@Component({
  selector: 'app-book-list',
  standalone: true,
  imports: [
    SharedModule,
    InputGroupModule,
    ReactiveFormsModule,
    FormsModule,
    RouterLink,
  ],
  templateUrl: './book-list.component.html',
  styleUrl: './book-list.component.css',
})
export class BookListComponent implements OnInit {
  private notificationService = inject(NotificationService);
  private bookService = inject(BookService);
  private destroyRef = inject(DestroyRef);
  private timeoutHandle: any;
  loading: boolean = false;
  books = signal<Book[]>([]);
  first = signal<number>(0);
  rows: number = 10;
  totalElements = signal<number>(0);
  currentPage = signal<number>(0);

  onPageChange(event: any) {
    console.log('Event: ', event);
    this.currentPage.set(event.page);
    this.first.set(event.page * this.rows);
    this.loadBooks(this.currentPage(), this.form.value.searchKeyword!);
  }

  form = new FormGroup({
    searchKeyword: new FormControl('', { validators: Validators.required }),
    filter: new FormControl<'isbn' | 'title'>('isbn', {
      validators: [Validators.required],
    }),
  });

  filters = [
    { label: 'ISBN', value: 'isbn' },
    { label: 'Title', value: 'title' },
  ];

  ngOnInit(): void {
    this.first.set(this.currentPage() * this.rows);
  }

  onReset() {
    this.form.reset();
    this.books.set([]);
  }

  onSearch() {
    if (this.form.invalid) {
      console.log('INVALID FORM');
      this.notificationService.showInvalidInputMessage();
      return;
    }

    console.log(this.form.value);

    if (this.form.value.filter! == 'title') {
      this.loadBooks(this.currentPage(), this.form.value.searchKeyword!);
    } else {
      this.loadBookByIsbn(this.form.value.searchKeyword!);
    }
  }

  getStatusValue = getStatusValue;
  getSeverity = getBookSeverity;

  loadBookByIsbn(isbn: string) {
    this.loading = true;

    this.timeoutHandle = setTimeout(() => {
      const subscription = this.bookService.fetchBookByIsbn(isbn).subscribe({
        next: (response) => {
          console.log(response.data);
          this.books.update((books) => [
            mapBookDetailToBook(response.data),
            ...books,
          ]);
          clearTimeout(this.timeoutHandle);
          this.loading = false;
        },
        error: (error: Error) => {
          console.log(error);
          clearTimeout(this.timeoutHandle);
          this.loading = false;
        },
      });

      this.destroyRef.onDestroy(() => {
        subscription.unsubscribe();
        clearTimeout(this.timeoutHandle);
      });
    }, 1500);
  }

  loadBooks(pageNo: number, title: string) {
    this.loading = true;
    console.log(`Fetching books for page: ${pageNo}`);

    this.timeoutHandle = setTimeout(() => {
      const subscription = this.bookService
        .fetchBooks(pageNo, title)
        .subscribe({
          next: (response) => {
            console.log(response);
            this.books.set(response.data.data!);
            this.totalElements.set(response.data.totalElements!);
            clearTimeout(this.timeoutHandle);
            this.loading = false;
          },
          error: (error: Error) => {
            console.log(error);
            clearTimeout(this.timeoutHandle);
            this.loading = false;
          },
        });

      this.destroyRef.onDestroy(() => {
        subscription.unsubscribe();
        clearTimeout(this.timeoutHandle);
      });
    }, 1500);
  }
}
