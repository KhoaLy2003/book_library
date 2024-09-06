package com.khoaly.book_library.enumeration;

import lombok.Getter;

@Getter
public enum MembershipTypeEnum {
    BASIC,       // Basic membership with standard borrowing limits
    PREMIUM,     // Premium membership with higher borrowing limits and additional benefits
    STUDENT,     // Student membership with special discounts and benefits
    SENIOR,      // Senior membership with special benefits for senior citizens
    FAMILY,      // Family membership allowing multiple family members to borrow books
    LIFETIME     // Lifetime membership with permanent benefits and no expiration
}
