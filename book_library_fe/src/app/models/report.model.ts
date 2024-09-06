export interface ReportData {
  totalBook: number | undefined;
  totalMember: number | undefined;
  totalBorrowing: number | undefined;
  borrowingStats: Map<number, number>;
}
