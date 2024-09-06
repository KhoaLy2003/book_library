import {
  Component,
  DestroyRef,
  inject,
  signal,
  ViewChild,
} from '@angular/core';
import { Router } from '@angular/router';
import { DividerModule } from 'primeng/divider';
import {
  BorrowingBook,
  BorrowingRequest,
} from '../../../models/borrowing.model';
import { MemberDetail } from '../../../models/member.model';
import { BorrowBookComponent } from './borrow-book/borrow-book.component';
import { BorrowMemberComponent } from './borrow-member/borrow-member.component';
import { SharedFormModule, SharedModule } from '../../../shared/shared.module';
import { NotificationService } from '../../../shared/notification.service';
import { BorrowingService } from '../../../services/borrowing.service';
import { calculateDaysBetweenDates } from '../../../utils/time.utils';

import pdfMake from 'pdfmake/build/pdfmake';
import pdfFonts from 'pdfmake/build/vfs_fonts';
pdfMake.vfs = pdfFonts.pdfMake.vfs;

@Component({
  selector: 'app-new-borrowing',
  standalone: true,
  imports: [
    SharedModule,
    SharedFormModule,
    DividerModule,
    BorrowBookComponent,
    BorrowMemberComponent,
  ],
  templateUrl: './new-borrowing.component.html',
  styleUrl: './new-borrowing.component.css',
})
export class NewBorrowingComponent {
  private router = inject(Router);
  private notificationService = inject(NotificationService);
  private borrowingService = inject(BorrowingService);
  private destroyRef = inject(DestroyRef);
  private timeoutHandle: any;
  isLoading = false;
  selectedBooks = signal<BorrowingBook[]>([]);
  selectedMember = signal<MemberDetail | undefined>(undefined);
  selectedDueDate: string | undefined;
  selectedBorrowingDate: string | undefined;

  @ViewChild(BorrowMemberComponent)
  borrowMemberComponent!: BorrowMemberComponent;

  @ViewChild(BorrowBookComponent)
  borrowBookComponent!: BorrowBookComponent;

  onResetForm() {
    this.selectedBooks.set([]);
    this.selectedMember.set(undefined);
    this.selectedDueDate = '';
    this.selectedBorrowingDate = '';

    this.borrowMemberComponent.resetFormData();
    this.borrowBookComponent.resetFormData();
  }

  navigateTo(route: string, state?: any): void {
    this.router.navigate([route], { state });
  }

  onMemberSelected(member: MemberDetail) {
    this.selectedMember.set(member);
    console.log('Selected member:', this.selectedMember());
  }

  onBooksSelected(books: BorrowingBook[]) {
    this.selectedBooks.set(books);
    console.log('Selected books:', this.selectedBooks());
  }

  onDueDateSelected(date: string) {
    this.selectedDueDate = date;
    console.log('Selected due date:', this.selectedDueDate);

    const daysBorrow = calculateDaysBetweenDates(
      this.selectedBorrowingDate!,
      this.selectedDueDate
    );
    if (daysBorrow > 3 || daysBorrow <= 0) {
      this.notificationService.showMessage(
        'error',
        'Invalid Borrow Period',
        'Please check again borrow date and due date'
      );
    }
  }

  onBorrowingDateSelected(date: string) {
    this.selectedBorrowingDate = date;
    console.log('Selected borrowing date:', this.selectedBorrowingDate);
  }

  onNewBorrowing() {
    console.log('Borrowing date', this.selectedBorrowingDate);
    console.log('Due date', this.selectedDueDate);
    console.log('Books', this.selectedBooks());
    console.log('Member', this.selectedMember());

    const member = this.selectedMember();
    const books = this.selectedBooks();

    // Validate selections
    if (
      !member ||
      books.length === 0 ||
      !this.selectedDueDate ||
      !this.selectedBorrowingDate
    ) {
      console.log('INVALID BORROWING ACTION');
      this.notificationService.showInvalidInputMessage();
      return;
    }

    const requestPayload: BorrowingRequest = {
      accountBorrowId: member.id,
      borrowingDate: this.selectedBorrowingDate,
      dueDate: this.selectedDueDate,
      bookList: books.map((borrowingBook) => ({
        bookId: borrowingBook.book?.id,
        borrowAmount: borrowingBook.borrowAmount,
      })),
    };

    console.log('Payload: ', requestPayload);
    this.createNewBorrowing(requestPayload);
  }

  createNewBorrowing(borrowingRequest: BorrowingRequest) {
    this.isLoading = true;

    this.timeoutHandle = setTimeout(() => {
      const subscription = this.borrowingService
        .newBorrowing(borrowingRequest)
        .subscribe({
          next: (response) => {
            console.log(response);
            clearTimeout(this.timeoutHandle);
            this.notificationService.showSuccessMessage(response.message!);
            setTimeout(() => {
              this.isLoading = false;
              this.navigateTo('borrowing');
            }, 1000);

            this.generatePDF();
          },
          error: (error: Error) => {
            console.log(error);
            clearTimeout(this.timeoutHandle);
            this.isLoading = false;
          },
        });

      this.destroyRef.onDestroy(() => {
        subscription.unsubscribe();
        clearTimeout(this.timeoutHandle);
      });
    }, 1500);
  }

  generatePDF() {
    const documentDefinition: any = {
      content: [
        {
          text: 'Borrowing Details',
          style: 'header',
          alignment: 'center',
        },
        {
          text: '\nMember Information',
          style: 'subheader',
        },
        {
          text: `Member Name: Khoa`,
          style: 'info',
        },
        {
          text: `Membership Number: MBKhoa202408197190`,
          style: 'info',
        },
        {
          text: `Borrowing Date: ${this.selectedBorrowingDate}`,
          style: 'info',
        },
        {
          text: `Due Date: ${this.selectedDueDate}`,
          style: 'info',
        },
        {
          text: '\nBorrowed Books',
          style: 'subheader',
        },
        {
          style: 'tableExample',
          table: {
            body: [
              [
                { text: 'Book Title', style: 'tableHeader' },
                { text: 'ISBN', style: 'tableHeader' },
                { text: 'Quantity', style: 'tableHeader' },
              ],
              ...this.selectedBooks().map((borrowingBook) => [
                borrowingBook.book?.title,
                borrowingBook.book?.isbn13,
                borrowingBook.borrowAmount,
              ]),
            ],
          },
          layout: 'lightHorizontalLines',
        },
      ],
      styles: {
        header: {
          fontSize: 20,
          bold: true,
        },
        subheader: {
          fontSize: 16,
          bold: true,
          margin: [0, 10, 0, 5], // top, right, bottom, left
        },
        info: {
          fontSize: 12,
          margin: [0, 5, 0, 5],
        },
        tableHeader: {
          bold: true,
          fontSize: 13,
          color: 'black',
        },
        tableExample: {
          margin: [0, 5, 0, 15],
        },
      },
    };

    pdfMake.createPdf(documentDefinition).print();
  }
}
