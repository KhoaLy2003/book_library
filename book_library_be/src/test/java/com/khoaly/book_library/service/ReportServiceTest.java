package com.khoaly.book_library.service;

import com.khoaly.book_library.repository.AccountRepository;
import com.khoaly.book_library.repository.BookRepository;
import com.khoaly.book_library.repository.BorrowingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource("/test.properties")
class ReportServiceTest {
    @Autowired
    private ReportService reportService;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private BorrowingRepository borrowingRepository;

    @Test
    void getReportData_ShouldReturnSuccess() {
        when(bookRepository.count()).thenReturn(11121L);
        when(accountRepository.countByStatus(any())).thenReturn(11L);
        when(borrowingRepository.count()).thenReturn(11L);

        var response = reportService.getReportData();

        assertNotNull(response);
        assertEquals(12, response.getBorrowingStats().size());
        assertEquals(11121L, response.getTotalBook());
        assertEquals(11L, response.getTotalMember());
        assertEquals(11L, response.getTotalBorrowing());
    }
}
