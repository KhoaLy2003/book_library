import {
  Component,
  DestroyRef,
  EventEmitter,
  inject,
  Output,
  signal,
} from '@angular/core';

import { FormControl, FormGroup, Validators } from '@angular/forms';
import {
  Book,
  BookStatusEnum,
  mapBookDetailToBook,
} from '../../../../models/book.model';

import { BorrowingBook } from '../../../../models/borrowing.model';
import {
  SharedFormModule,
  SharedModule,
} from '../../../../shared/shared.module';
import { NotificationService } from '../../../../shared/notification.service';
import { BookService } from '../../../../services/book.service';

@Component({
  selector: 'app-borrow-book',
  standalone: true,
  imports: [SharedModule, SharedFormModule],
  templateUrl: './borrow-book.component.html',
  styleUrl: './borrow-book.component.css',
})
export class BorrowBookComponent {
  private notificationService = inject(NotificationService);
  private bookService = inject(BookService);
  private destroyRef = inject(DestroyRef);
  private timeoutHandle: any;
  books = signal<Book[]>([]);
  selectedBooks = signal<BorrowingBook[]>([]);
  isValidBorrowAmount: boolean = false;
  isLoading = false;

  @Output() booksSelected = new EventEmitter<BorrowingBook[]>();

  searchBookForm = new FormGroup({
    searchBookKeyword: new FormControl('', { validators: Validators.required }),
    // filter: new FormControl<'isbn' | 'title'>('isbn', {
    //   validators: [Validators.required],
    // }),
  });

  resetFormData() {
    this.searchBookForm.reset();
    this.selectedBooks.set([]);
    this.books.set([]);
  }

  onSelectBook(book: Book) {
    if (book.status !== BookStatusEnum.AVAILABLE) {
      this.notificationService.showMessage('error', 'Can not borrow this book');
      return;
    }

    const currentSelection = this.selectedBooks();
    const isSelected = currentSelection.some(
      (selectedBook) => selectedBook.book?.id === book.id
    );

    if (!isSelected) {
      const borrowingBook: BorrowingBook = {
        book: book,
        borrowAmount: 1,
      };

      const updatedSelection = [...currentSelection, borrowingBook];
      this.selectedBooks.set(updatedSelection);

      const totalBorrowAmount = updatedSelection.reduce((total, item) => {
        return total + (item.borrowAmount ?? 0);
      }, 0);

      if (totalBorrowAmount > 3) {
        const filteredSelection = updatedSelection.filter(
          (selectedBook) => selectedBook.book?.id !== book.id
        );
        this.selectedBooks.set(filteredSelection);

        this.notificationService.showMessage(
          'error',
          'Maximum borrow amount',
          'Cannot borrow more than 3 books at a time'
        );
      }
    }
    console.log('Selected book: ', this.selectedBooks());

    this.booksSelected.emit(this.selectedBooks());
  }

  onBorrowAmountChange(borrowingBook: BorrowingBook, newAmount: number) {
    this.isValidBorrowAmount = true;
    if (borrowingBook.book?.currentQuantity! < newAmount) {
      this.isValidBorrowAmount = false;
      console.log('isValidBorrowAmount', this.isValidBorrowAmount);
      this.notificationService.showInvalidInputMessage();
      return;
    }

    borrowingBook.borrowAmount = newAmount;
    const totalBorrowAmount = this.selectedBooks().reduce((total, item) => {
      return total + (item.borrowAmount ?? 0);
    }, 0);

    if (totalBorrowAmount > 3) {
      this.notificationService.showMessage(
        'error',
        'Maximum borrow amount',
        'Cannot borrow more than 3 books at a time'
      );
    }

    this.selectedBooks.set([...this.selectedBooks()]);
    console.log(this.selectedBooks());

    this.booksSelected.emit(this.selectedBooks());
  }

  onUnSelectBook(book: Book) {
    console.log('Selected book: ', book);

    const currentSelection = this.selectedBooks();
    const updatedSelection = currentSelection.filter(
      (selectedBook) => selectedBook.book?.id !== book.id
    );
    this.selectedBooks.set(updatedSelection);
    this.booksSelected.emit(this.selectedBooks());
  }

  onSearchBook() {
    console.log('Book search', this.searchBookForm.value);
    if (this.searchBookForm.invalid) {
      console.log('INVALID FORM');
      this.notificationService.showInvalidInputMessage();
      return;
    }

    this.loadBookByIsbn(this.searchBookForm.value.searchBookKeyword!);
  }

  loadBookByIsbn(isbn: string) {
    this.isLoading = true;

    this.timeoutHandle = setTimeout(() => {
      const subscription = this.bookService.fetchBookByIsbn(isbn).subscribe({
        next: (response) => {
          console.log(response);

          if (!this.books().some((b) => b.id == response.data.id)) {
            this.books.update((books) => [
              mapBookDetailToBook(response.data),
              ...books,
            ]);
          }

          console.log('Loading ended');
          this.isLoading = false;
          clearTimeout(this.timeoutHandle);
        },
        error: (error: Error) => {
          console.log(error);
          this.isLoading = false;
          clearTimeout(this.timeoutHandle);
        },
      });

      this.destroyRef.onDestroy(() => {
        subscription.unsubscribe();
      });
    }, 1500);
  }
}
