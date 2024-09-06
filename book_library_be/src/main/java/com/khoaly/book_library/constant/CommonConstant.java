package com.khoaly.book_library.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonConstant {
    public static final Integer PAGE_SIZE = 10;
    public static final String BOOK_SORT_BY = "publicationDate";
    public static final String MEMBER_SORT_BY = "createdAt";
    public static final String BORROW_SORT_BY = "borrowingDate";
    public static final String PREFIX_MEMBERSHIP_NUMBER = "MB";
    public static final int DEFAULT_MEMBERSHIP_DURATION = 1;
    public static final int MAX_CODE_LENGTH = 4;
    public static final int RANDOM_BOUND_VALUE = 10;
    public static final int MAXIMUM_BORROW_LIMIT = 3;
    public static final int MAXIMUM_BORROW_PERIOD = 3;
}
