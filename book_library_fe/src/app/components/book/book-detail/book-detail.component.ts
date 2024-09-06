import {
  Component,
  DestroyRef,
  inject,
  input,
  OnInit,
  signal,
} from '@angular/core';

import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { CardModule } from 'primeng/card';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { TagModule } from 'primeng/tag';
import { InputNumberModule } from 'primeng/inputnumber';
import { ButtonModule } from 'primeng/button';
import { Router } from '@angular/router';
import { BookDetail } from '../../../models/book.model';
import { ToastModule } from 'primeng/toast';
import { BookService } from '../../../services/book.service';
import { getBookSeverity, getStatusValue } from '../../../utils/severity.utils';
import { BaseExceptionResponse } from '../../../shared/base-response.model';

@Component({
  selector: 'app-book-detail',
  standalone: true,
  imports: [
    CardModule,
    InputTextareaModule,
    TagModule,
    InputNumberModule,
    ButtonModule,
    ProgressSpinnerModule,
    ToastModule,
  ],
  templateUrl: './book-detail.component.html',
  styleUrl: './book-detail.component.css',
})
export class BookDetailComponent implements OnInit {
  private bookService = inject(BookService);
  private router = inject(Router);
  private timeoutHandle: any;
  private destroyRef = inject(DestroyRef);
  book = signal<BookDetail | undefined>(undefined);
  isbn = input.required<string>();

  loading: boolean = false;

  navigateTo(route: string): void {
    this.router.navigate([route]);
  }
  ngOnInit(): void {
    this.loadBookByIsbn(this.isbn());
  }

  getStatusValue = getStatusValue;
  getSeverity = getBookSeverity;

  loadBookByIsbn(isbn: string) {
    this.loading = true;

    this.timeoutHandle = setTimeout(() => {
      const subscription = this.bookService.fetchBookByIsbn(isbn).subscribe({
        next: (response) => {
          console.log(response);
          this.book.set(response.data);
          clearTimeout(this.timeoutHandle);
          this.loading = false;
        },
        error: (error: BaseExceptionResponse) => {
          console.log(error);
          clearTimeout(this.timeoutHandle);
          setTimeout(() => {
            this.navigateTo('books');
            this.loading = false;
          }, 2000);
        },
      });

      this.destroyRef.onDestroy(() => {
        subscription.unsubscribe();
        clearTimeout(this.timeoutHandle);
      });
    }, 1500);
  }
}
