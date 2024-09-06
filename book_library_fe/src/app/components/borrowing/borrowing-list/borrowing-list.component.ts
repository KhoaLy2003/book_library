import {
  Component,
  DestroyRef,
  EventEmitter,
  inject,
  input,
  Output,
} from '@angular/core';
import { RouterLink } from '@angular/router';
import { Location } from '@angular/common';
import { SharedModule } from '../../../shared/shared.module';
import { NotificationService } from '../../../shared/notification.service';
import { BorrowingHistory } from '../../../models/borrowing.model';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { BorrowingDetailComponent } from '../borrowing-detail/borrowing-detail.component';
import { ConfirmationService } from 'primeng/api';
import { BorrowingService } from '../../../services/borrowing.service';
import { BorrowingExtendComponent } from '../borrowing-extend/borrowing-extend.component';
import {
  getBorrowingSeverity,
  getStatusValue,
} from '../../../utils/severity.utils';

@Component({
  selector: 'app-borrowing-list',
  standalone: true,
  imports: [SharedModule, RouterLink],
  templateUrl: './borrowing-list.component.html',
  styleUrl: './borrowing-list.component.css',
  providers: [ConfirmationService, DialogService],
})
export class BorrowingListComponent {
  public message: string;
  private confirmationService = inject(ConfirmationService);
  private notificationService = inject(NotificationService);
  private dialogService = inject(DialogService);
  private borrowingService = inject(BorrowingService);
  private destroyRef = inject(DestroyRef);
  ref: DynamicDialogRef | undefined;

  borrowingList = input<BorrowingHistory[]>([]);
  isLoading = input<boolean>(false);

  @Output() reloadList = new EventEmitter<void>();
  @Output() cancelBorrowing = new EventEmitter<void>();

  constructor(private location: Location) {
    console.log('State: ', location.getState());
    this.message = (<Record<string, any>>this.location.getState())?.['message'];
  }

  getStatusValue = getStatusValue;
  getSeverity = getBorrowingSeverity;

  showExtendDialog(borrowing: BorrowingHistory) {
    this.ref = this.dialogService.open(BorrowingExtendComponent, {
      header: 'Borrowing Extend',
      modal: true,
      width: '50vw',
      breakpoints: {
        '960px': '75vw',
        '640px': '90vw',
      },
      maximizable: true,
      data: { dialogRef: this.ref, borrowing: borrowing },
    });

    this.ref.onClose.subscribe((result: any) => {
      if (result) {
        this.reloadList.emit();
      }
    });
  }

  showDialog(borrowing: BorrowingHistory) {
    this.ref = this.dialogService.open(BorrowingDetailComponent, {
      header: 'Borrowing Detail',
      modal: true,
      width: '50vw',
      breakpoints: {
        '960px': '75vw',
        '640px': '90vw',
      },
      maximizable: true,
      data: { dialogRef: this.ref, borrowing: borrowing },
    });

    this.ref.onClose.subscribe((result: any) => {
      if (result) {
        this.reloadList.emit();
      }
    });
  }

  confirmCancelBorrowing(event: Event, id: number) {
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: 'Do you want to cancel this borrowing?',
      icon: 'pi pi-info-circle',
      acceptButtonStyleClass: 'p-button-danger p-button-sm',
      accept: () => {
        this.onCancelBorrowing(id);
      },
    });
  }

  onCancelBorrowing(id: number) {
    const subscription = this.borrowingService.cancelBorrowing(id).subscribe({
      next: () => {
        this.notificationService.showMessage(
          'success',
          'Successfully',
          'Cancel borrowing successfully'
        );
        this.reloadList.emit();
      },
      error: (error: Error) => {
        console.log(error);
      },
    });
    this.destroyRef.onDestroy(() => {
      subscription.unsubscribe();
    });
  }
}
