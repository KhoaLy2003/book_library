import {
  Component,
  DestroyRef,
  inject,
  input,
  OnInit,
  signal,
} from '@angular/core';

import { TabViewModule } from 'primeng/tabview';
import { DialogModule } from 'primeng/dialog';
import { AccordionModule } from 'primeng/accordion';
import { BorrowingHistory } from '../../../models/borrowing.model';
import { MemberDetail } from '../../../models/member.model';
import { switchMap } from 'rxjs';
import { SharedModule } from '../../../shared/shared.module';
import { MemberService } from '../../../services/member.service';
import { BorrowingService } from '../../../services/borrowing.service';
import {
  getMemberSeverity,
  getMembershipSeverity,
  getStatusValue,
} from '../../../utils/severity.utils';
import { BorrowingListComponent } from '../../borrowing/borrowing-list/borrowing-list.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-member-detail',
  standalone: true,
  imports: [
    SharedModule,
    TabViewModule,
    DialogModule,
    AccordionModule,
    BorrowingListComponent,
  ],
  templateUrl: './member-detail.component.html',
  styleUrl: './member-detail.component.css',
})
export class MemberDetailComponent implements OnInit {
  private timeoutHandle: any;
  private memberService = inject(MemberService);
  private borrowingService = inject(BorrowingService);
  private destroyRef = inject(DestroyRef);
  private router = inject(Router);
  borrowingList = signal<BorrowingHistory[]>([]);
  currentMember = signal<MemberDetail | undefined>(undefined);
  membershipNumber = input.required<string>();
  first = signal<number>(0);
  rows: number = 10;
  totalElements = signal<number>(0);
  currentPage = signal<number>(0);
  isLoading: boolean = false;

  ngOnInit(): void {
    this.loadMemberByMembershipNum(this.membershipNumber());
  }

  navigateTo(route: string): void {
    this.router.navigate([route]);
  }

  getStatusValue = getStatusValue;
  getMemberSeverity = getMemberSeverity;
  getMembershipServerity = getMembershipSeverity;

  onPageChange(event: any) {
    console.log('Event: ', event);
    this.currentPage.set(event.page);
    this.first.set(event.page * this.rows);
    this.loadMemberBorrowingHistory(
      this.currentMember()?.id || 0,
      this.currentPage()
    );
  }

  loadMemberBorrowingHistory(accountId: number, pageNo: number) {
    this.isLoading = true;

    this.timeoutHandle = setTimeout(() => {
      const subscription = this.borrowingService
        .fetchMemberBorrowingHisotry(accountId, pageNo)
        .subscribe({
          next: (response) => {
            console.log(response);
            this.borrowingList.set(response.data.data || []);
            this.totalElements.set(response.data.totalElements!);
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

  loadMemberByMembershipNum(membershipNumber: string) {
    this.isLoading = true;

    this.timeoutHandle = setTimeout(() => {
      const subscription = this.memberService
        .fetchMemberByMembershipNum(membershipNumber)
        .pipe(
          // Switch to the borrowing history request once the member details are fetched
          switchMap((response) => {
            console.log(response);
            this.currentMember.set(response.data);
            const accountId = this.currentMember()?.id || 0;
            console.log('Account id: ', accountId);

            // Fetch borrowing history only if we have a valid accountId
            if (accountId > 0) {
              return this.borrowingService.fetchMemberBorrowingHisotry(
                accountId,
                this.currentPage()
              );
            } else {
              throw new Error('Invalid account ID');
            }
          })
        )
        .subscribe({
          next: (response) => {
            console.log(response);
            this.borrowingList.set(response.data.data || []);
            this.totalElements.set(response.data.totalElements!);
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
