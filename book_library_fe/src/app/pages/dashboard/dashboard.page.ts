import { Component, DestroyRef, inject, signal } from '@angular/core';

import { ChartModule } from 'primeng/chart';
import { ReportService } from '../../services/report.service';
import { ReportData } from '../../models/report.model';
import { ProgressSpinnerModule } from 'primeng/progressspinner';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [ChartModule, ProgressSpinnerModule],
  templateUrl: './dashboard.page.html',
  styleUrl: './dashboard.page.css',
})
export class DashboardPage {
  private reportService = inject(ReportService);
  private timeoutHandle: any;
  private destroyRef = inject(DestroyRef);
  reportData = signal<ReportData | undefined>(undefined);
  loading: boolean = false;

  basicData: any;
  basicOptions: any;

  monthlyData: number[] | undefined;
  // monthlyData = [0, 0, 0, 2, 1, 1, 5, 9, 0, 0, 0, 0];

  ngOnInit() {
    this.loadReportData();

    const documentStyle = getComputedStyle(document.documentElement);
    const textColor = documentStyle.getPropertyValue('--text-color');
    const textColorSecondary = documentStyle.getPropertyValue(
      '--text-color-secondary'
    );
    const surfaceBorder = documentStyle.getPropertyValue('--surface-border');

    this.basicData = {
      labels: [
        'January',
        'February',
        'March',
        'April',
        'May',
        'June',
        'July',
        'August',
        'September',
        'October',
        'November',
        'December',
      ],
      datasets: [
        {
          label: 'Borrowings per month',
          data: this.monthlyData,
          backgroundColor: [
            'rgba(255, 159, 64, 0.2)',
            'rgba(75, 192, 192, 0.2)',
            'rgba(54, 162, 235, 0.2)',
            'rgba(153, 102, 255, 0.2)',
            'rgba(255, 206, 86, 0.2)',
            'rgba(201, 203, 207, 0.2)',
            'rgba(255, 99, 132, 0.2)',
            'rgba(54, 162, 235, 0.2)',
            'rgba(75, 192, 192, 0.2)',
          ],
          borderColor: [
            'rgb(255, 159, 64)',
            'rgb(75, 192, 192)',
            'rgb(54, 162, 235)',
            'rgb(153, 102, 255)',
            'rgb(255, 206, 86)',
            'rgb(201, 203, 207)',
            'rgb(255, 99, 132)',
            'rgb(54, 162, 235)',
            'rgb(75, 192, 192)',
          ],
          borderWidth: 1,
        },
      ],
    };

    this.basicOptions = {
      plugins: {
        legend: {
          labels: {
            color: textColor,
          },
        },
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            color: textColorSecondary,
          },
          grid: {
            color: surfaceBorder,
            drawBorder: false,
          },
        },
        x: {
          ticks: {
            color: textColorSecondary,
          },
          grid: {
            color: surfaceBorder,
            drawBorder: false,
          },
        },
      },
    };
  }

  loadReportData() {
    this.loading = true;

    this.timeoutHandle = setTimeout(() => {
      const subscription = this.reportService.fetchReportData().subscribe({
        next: (response) => {
          this.reportData.set(response.data);

          const plainObject = this.reportData()!.borrowingStats;
          const borrowingStatsMap = new Map<number, number>(
            Object.entries(plainObject).map(([key, value]) => [
              Number(key),
              value,
            ])
          );

          console.log([...borrowingStatsMap.values()]);
          this.monthlyData = [...borrowingStatsMap.values()];

          this.basicData.datasets[0].data = this.monthlyData;

          clearTimeout(this.timeoutHandle);
          this.loading = false;
        },
        error: (error: Error) => {
          console.log(error);
          clearTimeout(this.timeoutHandle);
          this.loading = false;
        },
      });

      this.destroyRef.onDestroy(() => {
        subscription.unsubscribe();
        clearTimeout(this.timeoutHandle);
      });
    }, 1500);
  }
}
