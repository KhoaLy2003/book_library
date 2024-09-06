import { Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { AccordionModule } from 'primeng/accordion';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { BorrowingService } from '../../../services/borrowing.service';
import { SharedFormModule } from '../../../shared/shared.module';
import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import {
  BorrowingHistory,
  BorrowingHistoryDetail,
  BorrowingStatusEnum,
  ReturnBorrowing,
} from '../../../models/borrowing.model';
import { NgFor } from '@angular/common';
import { NotificationService } from '../../../shared/notification.service';
import { Router } from '@angular/router';
import { MemberDetail } from '../../../models/member.model';
import { MemberService } from '../../../services/member.service';

@Component({
  selector: 'app-member-borrowing-history-detail',
  standalone: true,
  imports: [AccordionModule, ProgressSpinnerModule, SharedFormModule, NgFor],
  templateUrl: './borrowing-detail.component.html',
  styleUrl: './borrowing-detail.component.html',
})
export class BorrowingDetailComponent implements OnInit {
  [x: string]: any;
  private borrowingService = inject(BorrowingService);
  private memberService = inject(MemberService);
  private timeoutHandle: any;
  private destroyRef = inject(DestroyRef);
  private dialogConfig = inject(DynamicDialogConfig);
  private dialogRef = inject(DynamicDialogRef);
  private notificationService = inject(NotificationService);
  private router = inject(Router);
  borrowingDetailHistoryList =
    this.borrowingService.loadedMemberBorrowingHisotryDetail;
  isLoading: boolean = false;
  isCancelledBorrowing: boolean = false;
  isBorrowingUrl: boolean = false;
  currentBorrowing: BorrowingHistory | undefined;
  currentMember = signal<MemberDetail | undefined>(undefined);

  ngOnInit(): void {
    if (this.dialogConfig.data.borrowing) {
      this.currentBorrowing = this.dialogConfig.data
        .borrowing as BorrowingHistory;
      console.log(this.currentBorrowing);
      this.loadMemberBorrowingHistoryDetail(this.currentBorrowing.id!);

      const currentUrl = this.router.url;
      if (currentUrl === '/borrowing') {
        this.isBorrowingUrl = true;
        this.loadMemberByMembershipNum(
          this.currentBorrowing.memberMembershipNumber!
        );
      }

      if (this.currentBorrowing.status === BorrowingStatusEnum.CANCELLED) {
        this.isCancelledBorrowing = true;
      }
    }
  }

  get bookList(): FormArray {
    return this.form.get('bookList') as FormArray;
  }

  form = new FormGroup({
    bookList: new FormArray([]),
  });

  initForm() {
    const bookListFormArray = this.form.get('bookList') as FormArray;
    console.log('HERE', this.borrowingDetailHistoryList());
    this.borrowingDetailHistoryList().forEach(
      (borrowingDetail: BorrowingHistoryDetail) => {
        bookListFormArray.push(
          new FormGroup({
            bookId: new FormControl<number>(borrowingDetail.bookId!, [
              Validators.required,
            ]),
            returnAmount: new FormControl<number>(
              borrowingDetail.returnAmount!,
              [Validators.required]
            ),
          })
        );
      }
    );
  }

  onSubmit() {
    console.log(this.form.value);
    if (this.form.invalid) {
      console.log('INVALID FORM');
      return;
    }

    const returnBorrowing: ReturnBorrowing = {
      borrowingId: this.dialogConfig.data.borrowingId,
      bookList: this.form.value.bookList!.map((book: any) => ({
        bookId: book.bookId,
        returnAmount: book.returnAmount,
      })),
    };

    console.log(returnBorrowing);
    this.returnBorrowing(returnBorrowing);
  }

  loadMemberBorrowingHistoryDetail(borrowingId: number) {
    this.isLoading = true;

    this.timeoutHandle = setTimeout(() => {
      const subscription = this.borrowingService
        .fetchMemberBorrowingHisotryDetail(borrowingId)
        .subscribe({
          next: (response) => {
            console.log(response);
            this.initForm();
            clearTimeout(this.timeoutHandle);
            this.isLoading = false;
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

  returnBorrowing(returnBorrowing: ReturnBorrowing) {
    this.isLoading = true;

    this.timeoutHandle = setTimeout(() => {
      const subscription = this.borrowingService
        .returnBorrowing(returnBorrowing)
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

  loadMemberByMembershipNum(membershipNumber: string) {
    this.isLoading = true;

    this.timeoutHandle = setTimeout(() => {
      const subscription = this.memberService
        .fetchMemberByMembershipNum(membershipNumber)
        .subscribe({
          next: (response) => {
            console.log(response);
            this.currentMember.set(response.data);
            clearTimeout(this.timeoutHandle);
            this.isLoading = false;
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
