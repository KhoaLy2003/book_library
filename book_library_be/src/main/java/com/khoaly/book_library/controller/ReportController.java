package com.khoaly.book_library.controller;

import com.khoaly.book_library.constant.MessageConstant;
import com.khoaly.book_library.constant.UriConstant;
import com.khoaly.book_library.dto.BaseResponse;
import com.khoaly.book_library.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(UriConstant.REPORT_BASE_URI)
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    public ResponseEntity<BaseResponse> getReportData() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), MessageConstant.GET_REPORT_DATA_SUCCESSFULLY, reportService.getReportData()));
    }
}
