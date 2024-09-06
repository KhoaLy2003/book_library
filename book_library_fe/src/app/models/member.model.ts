export interface NewMember {
  id?: string;
  firstName: string | undefined;
  lastName: string | undefined;
  email: string | undefined;
  phoneNumber: string | undefined;
  address: string | undefined;
  status?: string;
}

export interface Member {
  id?: string;
  accountFirstName: string | undefined;
  accountLastName: string | undefined;
  accountEmail: string | undefined;
  accountPhoneNumber: string | undefined;
  accountAddress: string | undefined;
  accountStatus?: string;
  membershipNumber: string | undefined;
  membershipType: MembershipTypeEnum | undefined;
}

export interface MemberDetail {
  id: number | undefined;
  accountFirstName: string | undefined;
  accountLastName: string | undefined;
  accountEmail: string | undefined;
  accountPhoneNumber: string | undefined;
  accountAddress: string | undefined;
  accountNotes: string | undefined;
  accountStatus: MembertatusEnum | undefined;
  membershipNumber: string | undefined;
  membershipStartDate: string | undefined;
  membershipEndDate: string | undefined;
  membershipType: MembershipTypeEnum | undefined;
  totalBooksBorrowed: number | undefined;
  totalLateReturns: number | undefined;
  currentBorrowedBooks: number | undefined;
}

export enum MembertatusEnum {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  PENDING = 'PENDING',
}

export enum MembershipTypeEnum {
  BASIC = 'BASIC',
  PREMIUM = 'PREMIUM',
  STUDENT = 'STUDENT',
  SENIOR = 'SENIOR',
  FAMILY = 'FAMILY',
  LIFETIME = 'LIFETIME',
}
