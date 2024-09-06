package com.khoaly.book_library.dto;

import com.khoaly.book_library.enumeration.AccountStatusEnum;
import com.khoaly.book_library.enumeration.MembershipTypeEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MemberResponseDto {
    Integer id;
    String accountFirstName;
    String accountLastName;
    String accountEmail;
    String accountPhoneNumber;
    AccountStatusEnum accountStatus;
    String membershipNumber;
    MembershipTypeEnum membershipType;
}
