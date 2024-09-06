package com.khoaly.book_library.controller;

import com.khoaly.book_library.constant.MessageConstant;
import com.khoaly.book_library.constant.UriConstant;
import com.khoaly.book_library.dto.ReportDataDto;
import com.khoaly.book_library.service.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource("/test.properties")
class ReportControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    @Test
    void getReportData_ShouldReturnSuccess() throws Exception {
        ReportDataDto reportDataDto = initReportDataDto();

        when(reportService.getReportData()).thenReturn(reportDataDto);

        mockMvc.perform(MockMvcRequestBuilders.get(UriConstant.REPORT_BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value(MessageConstant.GET_REPORT_DATA_SUCCESSFULLY))
                .andExpect(MockMvcResultMatchers.jsonPath("data.totalBook").value(11121L))
                .andExpect(MockMvcResultMatchers.jsonPath("data.totalMember").value(11L))
                .andExpect(MockMvcResultMatchers.jsonPath("data.totalBorrowing").value(11L))
                .andExpect(MockMvcResultMatchers.jsonPath("data.borrowingStats['1']").value(0L))
                .andExpect(MockMvcResultMatchers.jsonPath("data.borrowingStats['2']").value(0L))
                .andExpect(MockMvcResultMatchers.jsonPath("data.borrowingStats['3']").value(0L))
                .andExpect(MockMvcResultMatchers.jsonPath("data.borrowingStats['4']").value(0L))
                .andExpect(MockMvcResultMatchers.jsonPath("data.borrowingStats['5']").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("data.borrowingStats['6']").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("data.borrowingStats['7']").value(3L))
                .andExpect(MockMvcResultMatchers.jsonPath("data.borrowingStats['8']").value(5L))
                .andExpect(MockMvcResultMatchers.jsonPath("data.borrowingStats['9']").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("data.borrowingStats['10']").value(0L))
                .andExpect(MockMvcResultMatchers.jsonPath("data.borrowingStats['11']").value(0L))
                .andExpect(MockMvcResultMatchers.jsonPath("data.borrowingStats['12']").value(0L));
    }

    private ReportDataDto initReportDataDto() {
        ReportDataDto reportDataDto = new ReportDataDto();
        reportDataDto.setTotalBook(11121L);
        reportDataDto.setTotalMember(11L);
        reportDataDto.setTotalBorrowing(11L);

        Map<Integer, Long> borrowingStats = new HashMap<>();
        borrowingStats.put(1, 0L);
        borrowingStats.put(2, 0L);
        borrowingStats.put(3, 0L);
        borrowingStats.put(4, 0L);
        borrowingStats.put(5, 1L);
        borrowingStats.put(6, 1L);
        borrowingStats.put(7, 3L);
        borrowingStats.put(8, 5L);
        borrowingStats.put(9, 1L);
        borrowingStats.put(10, 0L);
        borrowingStats.put(11, 0L);
        borrowingStats.put(12, 0L);

        reportDataDto.setBorrowingStats(borrowingStats);
        return reportDataDto;
    }
}
