import { Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { BorrowingListComponent } from '../../components/borrowing/borrowing-list/borrowing-list.component';
import { SharedFormModule, SharedModule } from '../../shared/shared.module';
import { RouterLink } from '@angular/router';
import {
  BorrowingHistory,
  BorrowingStatusEnum,
} from '../../models/borrowing.model';
import { BorrowingService } from '../../services/borrowing.service';
import { DropdownChangeEvent } from 'primeng/dropdown';
import { formatDate } from '@angular/common';

@Component({
  selector: 'app-borrowing',
  standalone: true,
  imports: [BorrowingListComponent, SharedModule, RouterLink, SharedFormModule],
  templateUrl: './borrowing.page.html',
  styleUrl: './borrowing.page.css',
})
export class BorrowingPage implements OnInit {
  private timeoutHandle: any;
  private borrowingService = inject(BorrowingService);
  private destroyRef = inject(DestroyRef);
  borrowingList = signal<BorrowingHistory[]>([]);
  isLoading: boolean = false;
  first = signal<number>(0);
  rows: number = 10;
  totalElements = signal<number>(0);
  currentPage = signal<number>(0);
  selectedStatus: string | undefined;
  selectedStartDate: string | undefined;
  selectedEndDate: string | undefined;

  ngOnInit(): void {
    this.loadBorrowingHistory(this.currentPage());
  }

  generatePdf() {}

  onPageChange(event: any) {
    console.log('Event: ', event);
    this.currentPage.set(event.page);
    this.first.set(event.page * this.rows);
    this.loadBorrowingHistory(this.currentPage());
  }

  filters = Object.keys(BorrowingStatusEnum).map((key) => ({
    label: key.charAt(0) + key.slice(1).toLowerCase().replace(/_/g, ' '),
    value: BorrowingStatusEnum[key as keyof typeof BorrowingStatusEnum],
  }));

  onFilterChange(event: DropdownChangeEvent) {
    console.log('Filter changed to:', event.value);
    this.selectedStatus = event.value;

    if (this.selectedStartDate && this.selectedEndDate) {
      const formattedStartDate = formatDate(
        this.selectedStartDate,
        'yyyy-MM-dd',
        'en-US'
      );
      const formattedEndDate = formatDate(
        this.selectedEndDate,
        'yyyy-MM-dd',
        'en-US'
      );

      this.loadBorrowingHistory(
        this.currentPage(),
        this.selectedStatus,
        formattedStartDate,
        formattedEndDate
      );
    }

    this.loadBorrowingHistory(this.currentPage(), this.selectedStatus);
  }

  onStartDateChange(date: string) {
    console.log('Start: ', date);
    this.selectedStartDate = date;
  }

  onEndDateChange(date: string) {
    console.log('End: ', date);
    this.selectedEndDate = date;

    if (this.selectedStartDate && this.selectedEndDate) {
      console.log(
        'From: ' + this.selectedStartDate + ' to ' + this.selectedEndDate
      );

      const formattedStartDate = formatDate(
        this.selectedStartDate,
        'yyyy-MM-dd',
        'en-US'
      );
      const formattedEndDate = formatDate(
        this.selectedEndDate,
        'yyyy-MM-dd',
        'en-US'
      );

      this.loadBorrowingHistory(
        this.currentPage(),
        this.selectedStatus,
        formattedStartDate,
        formattedEndDate
      );
    }
  }

  onReset() {
    this.selectedStatus = '';
    this.selectedStartDate = '';
    this.selectedEndDate = '';
    this.loadBorrowingHistory(0);
  }

  loadBorrowingHistory(
    pageNo: number,
    status?: string,
    startDate?: string,
    endDate?: string
  ) {
    this.isLoading = true;
    console.log(status, startDate, endDate);

    this.timeoutHandle = setTimeout(() => {
      const subscription = this.borrowingService
        .fetchBorrowingHisotry(pageNo, status, startDate, endDate)
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
