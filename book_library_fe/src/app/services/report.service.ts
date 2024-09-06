import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { BaseResponse } from '../shared/base-response.model';
import { ReportData } from '../models/report.model';

@Injectable({
  providedIn: 'root',
})
export class ReportService {
  private httpClient = inject(HttpClient);
  private chartData = signal<number[]>([]);

  loadedChartData = this.chartData.asReadonly();

  fetchReportData() {
    return this.httpClient.get<BaseResponse<ReportData>>(
      `http://localhost:8080/api/v1/reports`
    );
  }
}
