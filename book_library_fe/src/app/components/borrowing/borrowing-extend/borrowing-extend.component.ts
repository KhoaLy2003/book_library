import { Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import {
  BorrowingHistory,
  ExtendBorrowing,
} from '../../../models/borrowing.model';
import { SharedFormModule } from '../../../shared/shared.module';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { calculateDaysBetweenDates } from '../../../utils/time.utils';
import { formatDate } from '@angular/common';
import { NotificationService } from '../../../shared/notification.service';
import { BorrowingService } from '../../../services/borrowing.service';

@Component({
  selector: 'app-borrowing-extend',
  standalone: true,
  imports: [ProgressSpinnerModule, SharedFormModule],
  templateUrl: './borrowing-extend.component.html',
  styleUrl: './borrowing-extend.component.css',
})
export class BorrowingExtendComponent implements OnInit {
  private timeoutHandle: any;
  private dialogConfig = inject(DynamicDialogConfig);
  private notificationService = inject(NotificationService);
  private borrowingService = inject(BorrowingService);
  private destroyRef = inject(DestroyRef);
  private dialogRef = inject(DynamicDialogRef);
  selectedNewDueDate: Date | undefined;
  borrowing = signal<BorrowingHistory | undefined>(undefined);
  isLoading = false;

  ngOnInit(): void {
    if (this.dialogConfig.data.borrowing) {
      const borrowing = this.dialogConfig.data.borrowing as BorrowingHistory;
      console.log(borrowing);
      this.borrowing.set(borrowing);
      this.selectedNewDueDate = new Date(borrowing.dueDate!);
    }
  }

  onNewDueDateChange(date: Date) {
    console.log('End: ', date);
    this.selectedNewDueDate = date;

    const formattedNewDueDate = formatDate(
      this.selectedNewDueDate,
      'yyyy-MM-dd',
      'en-US'
    );

    const daysBorrow = calculateDaysBetweenDates(
      this.borrowing()?.dueDate!,
      formattedNewDueDate
    );
    if (daysBorrow > 3 || daysBorrow <= 0) {
      this.notificationService.showMessage(
        'error',
        'Invalid Borrow Period',
        'Please check again borrow date and due date'
      );
    }
  }

  onExtend() {
    const formattedNewDueDate = formatDate(
      this.selectedNewDueDate!,
      'yyyy-MM-dd',
      'en-US'
    );
    const extendBorrowing: ExtendBorrowing = {
      borrowingId: this.borrowing()?.id,
      newDueDate: formattedNewDueDate,
    };

    console.log(extendBorrowing);
    this.extendorrowing(extendBorrowing);
  }

  extendorrowing(extendBorrowing: ExtendBorrowing) {
    this.isLoading = true;

    this.timeoutHandle = setTimeout(() => {
      const subscription = this.borrowingService
        .extendBorrowing(extendBorrowing)
        .subscribe({
          next: (response) => {
            console.log(response);
            clearTimeout(this.timeoutHandle);
            this.notificationService.showSuccessMessage(response.message!);
            setTimeout(() => {
              this.isLoading = false;
              this.dialogRef.close(true);
            }, 1000);
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
}
