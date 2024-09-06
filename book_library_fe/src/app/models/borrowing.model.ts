import { Book } from './book.model';

export interface BorrowingRequest {
  accountBorrowId: number | undefined;
  dueDate: string | undefined;
  borrowingDate: string | undefined;
  bookList: BorrowingDetail[] | [];
}

export interface BorrowingDetail {
  bookId: number | undefined;
  borrowAmount: number | undefined;
}

export interface BorrowingBook {
  book: Book | undefined;
  borrowAmount: number | undefined;
}

export interface BorrowingHistory {
  id: number | undefined;
  memberMembershipNumber: string | undefined;
  status: BorrowingStatusEnum | undefined;
  borrowingDate: string | undefined;
  returnDate: string | undefined;
  dueDate: string | undefined;
  totalBook: number | undefined;
}

export interface BorrowingHistoryDetail {
  id: number | undefined;
  bookId: number | undefined;
  bookTitle: string | undefined;
  bookIsbn13: string | undefined;
  borrowAmount: number | undefined;
  returnAmount: number | undefined;
}

export interface ReturnBorrowing {
  borrowingId: number | undefined;
  bookList: ReturnDetail[] | [];
}

export interface ReturnDetail {
  bookId: number | undefined;
  returnAmount: number | undefined;
}

export interface ExtendBorrowing {
  borrowingId: number | undefined;
  newDueDate: string | undefined;
}

export enum BorrowingStatusEnum {
  CREATED = 'CREATED',
  CANCELLED = 'CANCELLED',
  BORROWED = 'BORROWED',
  RETURNED = 'RETURNED',
  OVERDUE = 'OVERDUE',
}
