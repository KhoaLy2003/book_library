import { Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { ConfirmationService } from 'primeng/api';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { MemberFormComponent } from '../member-form/member-form.component';
import { Member } from '../../../models/member.model';
import { RouterLink } from '@angular/router';
import { SharedFormModule, SharedModule } from '../../../shared/shared.module';
import { MemberService } from '../../../services/member.service';
import { NotificationService } from '../../../shared/notification.service';
import {
  getMemberSeverity,
  getMembershipSeverity,
  getStatusValue,
} from '../../../utils/severity.utils';

@Component({
  selector: 'app-member-list',
  standalone: true,
  imports: [SharedModule, SharedFormModule, RouterLink],
  templateUrl: './member-list.component.html',
  styleUrl: './member-list.component.css',
  providers: [ConfirmationService, DialogService],
})
export class MemberListComponent implements OnInit {
  private memberService = inject(MemberService);
  private destroyRef = inject(DestroyRef);
  private confirmationService = inject(ConfirmationService);
  private dialogService = inject(DialogService);
  private notificationService = inject(NotificationService);
  private timeoutHandle: any;
  ref: DynamicDialogRef | undefined;
  loading: boolean = true;
  first = signal<number>(0);
  rows: number = 10;
  totalElements = signal<number>(0);
  currentPage = signal<number>(0);
  members = signal<Member[]>([]);

  ngOnInit(): void {
    this.loadMembers(this.currentPage());
  }

  getMemberSeverity = getMemberSeverity;
  getMembershipServerity = getMembershipSeverity;
  getStatusValue = getStatusValue;

  onPageChange(event: any) {
    console.log('Event: ', event);
    this.currentPage.set(event.page);
    this.first.set(event.page * this.rows);
    this.loadMembers(this.currentPage());
  }

  filters = [
    { label: 'ISBN', value: 'isbn' },
    { label: 'Title', value: 'title' },
  ];

  onReset() {
    this.loadMembers(0);
  }

  showDialog(member?: Member) {
    this.loading = true;
    this.ref = this.dialogService.open(MemberFormComponent, {
      header: 'Member Information',
      modal: true,
      width: '50vw',
      breakpoints: {
        '960px': '75vw',
        '640px': '90vw',
      },
      maximizable: true,
      data: { dialogRef: this.ref, member: member },
    });

    this.ref.onClose.subscribe((result: any) => {
      this.loading = false;
      this.loadMembers(0);

      if (result) {
        this.notificationService.showMessage(
          'success',
          'Success',
          member ? 'Member updated' : 'Member added successfully'
        );
      } else if (result === false) {
        this.notificationService.showMessage(
          'error',
          'Error',
          'Member added failed'
        );
      } else {
        this.notificationService.showMessage('info', 'Cancel', 'Cancel');
      }
    });
  }

  loadMembers(pageNo: number) {
    this.loading = true;

    this.timeoutHandle = setTimeout(() => {
      const subscription = this.memberService.fetchMembers(pageNo).subscribe({
        next: (response) => {
          console.log(response);
          this.members.set(response.data.data!);
          this.totalElements.set(response.data.totalElements!);
          clearTimeout(this.timeoutHandle);
          this.loading = false;
        },
        error: (error: Error) => {
          clearTimeout(this.timeoutHandle);
          this.loading = false;
          console.log(error);
        },
      });

      this.destroyRef.onDestroy(() => {
        subscription.unsubscribe();
        clearTimeout(this.timeoutHandle);
      });
    }, 1500);
  }

  confirmDeteleMember(event: Event, id: string) {
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: 'Do you want to delete this member?',
      icon: 'pi pi-info-circle',
      acceptButtonStyleClass: 'p-button-danger p-button-sm',
      accept: () => {
        this.notificationService.showMessage(
          'info',
          'Confirmed',
          'Member deleted'
        );
        this.onDeleteMember(id);
      },
      reject: () => {
        this.notificationService.showMessage(
          'error',
          'Rejected',
          'You have rejected'
        );
      },
    });
  }

  onDeleteMember(id: string) {
    // this.loading = true;
    // const subscription = this.memberService.deleteMember(id).subscribe({
    //   next: () => {
    //     this.loading = false;
    //     this.loadMembers();
    //   },
    //   error: (error: Error) => {
    //     this.loading = false;
    //     console.log(error);
    //   },
    // });
    // this.destroyRef.onDestroy(() => {
    //   subscription.unsubscribe();
    // });
  }
}
