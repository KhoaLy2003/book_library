package com.khoaly.book_library.dto;

import com.khoaly.book_library.enumeration.AccountStatusEnum;
import com.khoaly.book_library.enumeration.MembershipTypeEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MemberDetailResponseDto {
    Integer id;
    String accountFirstName;
    String accountLastName;
    String accountEmail;
    String accountPhoneNumber;
    String accountAddress;
    String accountNotes;
    AccountStatusEnum accountStatus;
    String membershipNumber;
    LocalDate membershipStartDate;
    LocalDate membershipEndDate;
    MembershipTypeEnum membershipType;
    Integer totalBooksBorrowed;
    Integer totalLateReturns;
    Integer currentBorrowedBooks;
}
