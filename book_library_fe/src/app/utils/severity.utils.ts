import { BookStatusEnum } from '../models/book.model';
import { BorrowingStatusEnum } from '../models/borrowing.model';
import { MembershipTypeEnum, MembertatusEnum } from '../models/member.model';

export const getMemberSeverity = (status: MembertatusEnum | undefined) => {
  switch (status) {
    case MembertatusEnum.ACTIVE:
      return 'success';
    case MembertatusEnum.INACTIVE:
      return 'danger';
    default:
      return 'warning';
  }
};

export const getBookSeverity = (status: BookStatusEnum | undefined) => {
  switch (status) {
    case BookStatusEnum.AVAILABLE:
      return 'success';
    case BookStatusEnum.BORROWED:
      return 'info';
    case BookStatusEnum.RESERVED:
      return 'warning';
    case BookStatusEnum.LOST:
      return 'danger';
    case BookStatusEnum.NORMAL:
      return 'secondary';
    case BookStatusEnum.DAMAGED:
      return 'warning';
    case BookStatusEnum.CAN_NOT_BORROW:
      return 'danger';
    case BookStatusEnum.OUT_OF_STOCK:
      return 'contrast';
    default:
      return 'warning';
  }
};

export const getBorrowingSeverity = (
  status: BorrowingStatusEnum | undefined
) => {
  switch (status) {
    case BorrowingStatusEnum.CREATED:
      return 'info';
    case BorrowingStatusEnum.CANCELLED:
      return 'warning';
    case BorrowingStatusEnum.BORROWED:
      return 'info';
    case BorrowingStatusEnum.RETURNED:
      return 'success';
    case BorrowingStatusEnum.OVERDUE:
      return 'danger';
    default:
      return 'warning';
  }
};

export const getMembershipSeverity = (type: MembershipTypeEnum | undefined) => {
  switch (type) {
    case MembershipTypeEnum.BASIC:
      return 'secondary';
    case MembershipTypeEnum.PREMIUM:
      return 'success';
    case MembershipTypeEnum.STUDENT:
      return 'info';
    case MembershipTypeEnum.SENIOR:
      return 'warning';
    case MembershipTypeEnum.FAMILY:
      return 'info';
    case MembershipTypeEnum.LIFETIME:
      return 'danger';
    default:
      return 'warning';
  }
};

export const getStatusValue = (status: any): string => {
  if (typeof status === 'object' && status !== null) {
    return status.value;
  }
  return status;
};
