package com.khoaly.book_library.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UriConstant {
    public static final String BOOK_BASE_URI = "/api/v1/books";
    public static final String BOOK_DETAIL_URI = "/isbn";
    public static final String MEMBER_BASE_URI = "/api/v1/members";
    public static final String MEMBER_DETAIL_URI = "/detail";
    public static final String BORROWING_BASE_URI = "/api/v1/borrowings";
    public static final String BORROWING_DETAIL_URI = "/{borrowingId}";
    public static final String BORROWING_BORROW_URI = "/borrow";
    public static final String BORROWING_RETURN_URI = "/return";
    public static final String BORROWING_EXTEND_URI = "/extend";
    public static final String BORROWING_CANCEL_URI = "/{borrowingId}/cancel";
    public static final String BORROWING_MEMBER_HISTORY_URI = "/{accountBorrowId}/list";
    public static final String REPORT_BASE_URI = "/api/v1/reports";
}
