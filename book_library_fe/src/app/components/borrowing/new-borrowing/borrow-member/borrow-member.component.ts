import {
  Component,
  DestroyRef,
  EventEmitter,
  inject,
  Output,
  signal,
} from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MemberDetail } from '../../../../models/member.model';
import { formatDate } from '@angular/common';
import {
  SharedFormModule,
  SharedModule,
} from '../../../../shared/shared.module';
import { NotificationService } from '../../../../shared/notification.service';
import { MemberService } from '../../../../services/member.service';

@Component({
  selector: 'app-borrow-member',
  standalone: true,
  imports: [SharedModule, SharedFormModule],
  templateUrl: './borrow-member.component.html',
  styleUrl: './borrow-member.component.css',
})
export class BorrowMemberComponent {
  private notificationService = inject(NotificationService);
  private memberService = inject(MemberService);
  private destroyRef = inject(DestroyRef);
  private timeoutHandle: any;
  selectedMember = signal<MemberDetail | undefined>(undefined);
  selectedBorrowingDate: string | undefined;
  selectedDueDate: string | undefined;
  isLoading = false;
  hasBorrowDate = true;

  @Output() memberSelected = new EventEmitter<MemberDetail>();
  @Output() dueDateSelected = new EventEmitter<string>();
  @Output() borrowingDateSelected = new EventEmitter<string>();

  searchMemberForm = new FormGroup({
    searchMemberKeyword: new FormControl('', {
      validators: Validators.required,
    }),
  });

  resetFormData() {
    this.searchMemberForm.reset();
    this.selectedDueDate = '';
    this.selectedBorrowingDate = '';
    this.selectedMember.set(undefined);
  }

  onDueDateChange(date: string) {
    this.selectedDueDate = date;
    this.dueDateSelected.emit(
      formatDate(this.selectedDueDate, 'yyyy-MM-dd', 'en-US')
    );
  }

  onBorrowingDateChange(date: string) {
    this.selectedBorrowingDate = date;
    this.hasBorrowDate = false;
    this.borrowingDateSelected.emit(
      formatDate(this.selectedBorrowingDate, 'yyyy-MM-dd', 'en-US')
    );
  }

  onSearchMember() {
    console.log('Member search', this.searchMemberForm.value);
    if (this.searchMemberForm.invalid) {
      console.log('INVALID FORM');
      this.notificationService.showInvalidInputMessage();
      return;
    }

    this.loadMemberByMembershipNum(
      this.searchMemberForm.value.searchMemberKeyword!
    );
  }

  loadMemberByMembershipNum(membershipNumber: string) {
    this.isLoading = true;
    this.selectedMember.set(undefined);

    this.timeoutHandle = setTimeout(() => {
      const subscription = this.memberService
        .fetchMemberByMembershipNum(membershipNumber)
        .subscribe({
          next: (response) => {
            console.log(response);
            this.selectedMember.set(response.data);
            this.memberSelected.emit(response.data);
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
