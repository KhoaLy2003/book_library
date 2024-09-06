package com.khoaly.book_library.service;

import com.khoaly.book_library.dto.BorrowBookHistoryDetailDto;
import com.khoaly.book_library.dto.BorrowBookHistoryDto;
import com.khoaly.book_library.dto.BorrowingDto;
import com.khoaly.book_library.dto.ExtendBorrowingDto;
import com.khoaly.book_library.dto.PageResponse;
import com.khoaly.book_library.dto.ReturnBorrowingDto;
import com.khoaly.book_library.enumeration.BorrowingStatusEnum;

import java.time.LocalDate;
import java.util.List;

public interface BorrowingService {
    void createBorrowing(BorrowingDto borrowingDto);

    void returnBorrowing(ReturnBorrowingDto returnBorrowingDto);

    void extendBorrowing(ExtendBorrowingDto extendBorrowingDto);

    void cancelBorrowing(int borrowingId);

    PageResponse<BorrowBookHistoryDto> getBorrowBookHistory(int pageNo, LocalDate startDate, LocalDate endDate, BorrowingStatusEnum borrowingStatusEnum);

    List<BorrowBookHistoryDetailDto> getBorrowBookHistoryDetail(int borrowingId);

    PageResponse<BorrowBookHistoryDto> getBorrowBookHistoryForReader(int accountBorrowId, int pageNo, BorrowingStatusEnum borrowingStatusEnum);

    void scheduleOverdueBorrowing();
}
