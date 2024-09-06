package com.khoaly.book_library.controller;

import com.khoaly.book_library.constant.MessageConstant;
import com.khoaly.book_library.constant.UriConstant;
import com.khoaly.book_library.dto.BaseResponse;
import com.khoaly.book_library.dto.BorrowingDto;
import com.khoaly.book_library.dto.ExtendBorrowingDto;
import com.khoaly.book_library.dto.ReturnBorrowingDto;
import com.khoaly.book_library.enumeration.BorrowingStatusEnum;
import com.khoaly.book_library.service.BorrowingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping(UriConstant.BORROWING_BASE_URI)
@RequiredArgsConstructor
public class BorrowingController {
    private final BorrowingService borrowingService;

    @PostMapping(UriConstant.BORROWING_BORROW_URI)
    public ResponseEntity<BaseResponse> createBorrowing(@Valid @RequestBody BorrowingDto borrowingDto) {
        borrowingService.createBorrowing(borrowingDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BaseResponse(HttpStatus.CREATED.value(), MessageConstant.CREATE_BORROWING_SUCCESSFULLY, ""));
    }

    @PostMapping(UriConstant.BORROWING_EXTEND_URI)
    public ResponseEntity<BaseResponse> extendBorrowing(@Valid @RequestBody ExtendBorrowingDto extendBorrowingDto) {
        borrowingService.extendBorrowing(extendBorrowingDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), MessageConstant.EXTEND_BORROWING_SUCCESSFULLY, ""));
    }

    @PostMapping(UriConstant.BORROWING_CANCEL_URI)
    public ResponseEntity<BaseResponse> cancelBorrowing(@PathVariable int borrowingId) {
        borrowingService.cancelBorrowing(borrowingId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), MessageConstant.CANCEL_BORROWING_SUCCESSFULLY, ""));
    }

    @PostMapping(UriConstant.BORROWING_RETURN_URI)
    public ResponseEntity<BaseResponse> returnBorrowing(@Valid @RequestBody ReturnBorrowingDto returnBorrowingDto) {
        borrowingService.returnBorrowing(returnBorrowingDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), MessageConstant.RETURN_BORROWING_SUCCESSFULLY, ""));
    }

    @GetMapping
    public ResponseEntity<BaseResponse> getBorrowingHistoryList(
            @RequestParam(name = "pageNo") int pageNo,
            @RequestParam(name = "status", required = false) BorrowingStatusEnum status,
            @RequestParam(name = "startDate", required = false) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) LocalDate endDate) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(
                        HttpStatus.OK.value(), MessageConstant.GET_BORROWING_HISTORY_LIST_SUCCESSFULLY, borrowingService.getBorrowBookHistory(pageNo, startDate, endDate, status)));
    }

    @GetMapping(UriConstant.BORROWING_MEMBER_HISTORY_URI)
    public ResponseEntity<BaseResponse> getBorrowingHistoryForReader(
            @PathVariable int accountBorrowId,
            @RequestParam(name = "pageNo") int pageNo,
            @RequestParam(name = "status", required = false) BorrowingStatusEnum status) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(
                        HttpStatus.OK.value(), MessageConstant.GET_BORROWING_HISTORY_LIST_FOR_READER_SUCCESSFULLY, borrowingService.getBorrowBookHistoryForReader(accountBorrowId, pageNo, status)));
    }

    @GetMapping(UriConstant.BORROWING_DETAIL_URI)
    public ResponseEntity<BaseResponse> getBorrowingHistoryDetail(@PathVariable int borrowingId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(
                        HttpStatus.OK.value(), MessageConstant.GET_BORROWING_HISTORY_DETAIL_SUCCESSFULLY, borrowingService.getBorrowBookHistoryDetail(borrowingId)));
    }
}
