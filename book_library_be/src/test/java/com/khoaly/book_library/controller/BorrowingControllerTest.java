package com.khoaly.book_library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.khoaly.book_library.constant.CommonConstant;
import com.khoaly.book_library.constant.MessageConstant;
import com.khoaly.book_library.constant.UriConstant;
import com.khoaly.book_library.dto.BorrowBookHistoryDetailDto;
import com.khoaly.book_library.dto.BorrowBookHistoryDto;
import com.khoaly.book_library.dto.BorrowingDetailDto;
import com.khoaly.book_library.dto.BorrowingDto;
import com.khoaly.book_library.dto.ExtendBorrowingDto;
import com.khoaly.book_library.dto.PageResponse;
import com.khoaly.book_library.dto.ReturnBorrowingDto;
import com.khoaly.book_library.dto.ReturnDetailDto;
import com.khoaly.book_library.enumeration.BorrowingStatusEnum;
import com.khoaly.book_library.exception.InvalidAccountBorrowingException;
import com.khoaly.book_library.exception.InvalidBookToBorrowException;
import com.khoaly.book_library.exception.InvalidBorrowPeriodExtensionException;
import com.khoaly.book_library.exception.MaximumBorrowLimitException;
import com.khoaly.book_library.exception.MaximumBorrowPeriodException;
import com.khoaly.book_library.service.BorrowingService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource("/test.properties")
class BorrowingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BorrowingService borrowingService;

    @Test
    void createBorrowing_ValidRequest_ShouldReturnSuccess() throws Exception {
        doNothing().when(borrowingService).createBorrowing(ArgumentMatchers.any());

        mockMvc.perform(MockMvcRequestBuilders.post(UriConstant.BORROWING_BASE_URI + UriConstant.BORROWING_BORROW_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(initBorrowingDto()))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(HttpStatus.CREATED.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value(MessageConstant.CREATE_BORROWING_SUCCESSFULLY));

        verify(borrowingService, times(1)).createBorrowing(ArgumentMatchers.any());
    }

    @Test
    void createBorrowing_InValidRequest_ShouldReturnFail() throws Exception {
        BorrowingDto borrowingDto = new BorrowingDto();
        borrowingDto.setAccountBorrowId(1);
        borrowingDto.setBorrowingDate(LocalDate.now());
        borrowingDto.setDueDate(LocalDate.now().minusDays(3));

        BorrowingDetailDto borrowingDetailDto1 = new BorrowingDetailDto(1, -1);
        BorrowingDetailDto borrowingDetailDto2 = new BorrowingDetailDto(2, 1);
        List<BorrowingDetailDto> bookList = List.of(borrowingDetailDto1, borrowingDetailDto2);
        borrowingDto.setBookList(bookList);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(borrowingDto);

        mockMvc.perform(MockMvcRequestBuilders.post(UriConstant.BORROWING_BASE_URI + UriConstant.BORROWING_BORROW_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value(MessageConstant.INVALID_INPUT_DATA))
                .andExpect(MockMvcResultMatchers.jsonPath("content.dueDate").value("Due date must at future"))
                .andExpect(MockMvcResultMatchers.jsonPath("content['bookList[0].borrowAmount']").value("Book quantity must equal or greater than 1"));
    }

    @Test
    void createBorrowing_InvalidAccountBorrowing_ShouldReturnFail() throws Exception {
        doThrow(new InvalidAccountBorrowingException("This account not found or can not borrow book")).when(borrowingService).createBorrowing(ArgumentMatchers.any());

        mockMvc.perform(MockMvcRequestBuilders.post(UriConstant.BORROWING_BASE_URI + UriConstant.BORROWING_BORROW_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(initBorrowingDto()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("This account not found or can not borrow book"));
    }

    @Test
    void createBorrowing_MaximumBorrowLimit_ShouldReturnFail() throws Exception {
        doThrow(new MaximumBorrowLimitException("Cannot borrow more than 3 books at a time")).when(borrowingService).createBorrowing(ArgumentMatchers.any());

        mockMvc.perform(MockMvcRequestBuilders.post(UriConstant.BORROWING_BASE_URI + UriConstant.BORROWING_BORROW_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(initBorrowingDto()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Cannot borrow more than 3 books at a time"));
    }

    @Test
    void createBorrowing_MaximumBorrowPeriodException_ShouldReturnFail() throws Exception {
        doThrow(new MaximumBorrowPeriodException("The borrowing period cannot exceed 3 days")).when(borrowingService).createBorrowing(ArgumentMatchers.any());

        mockMvc.perform(MockMvcRequestBuilders.post(UriConstant.BORROWING_BASE_URI + UriConstant.BORROWING_BORROW_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(initBorrowingDto()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("The borrowing period cannot exceed 3 days"));
    }

    @Test
    void createBorrowing_InvalidBookToBorrowException_ShouldReturnFail() throws Exception {
        doThrow(new InvalidBookToBorrowException("Book list for borrowing includes one or more book which can not borrow")).when(borrowingService).createBorrowing(ArgumentMatchers.any());

        mockMvc.perform(MockMvcRequestBuilders.post(UriConstant.BORROWING_BASE_URI + UriConstant.BORROWING_BORROW_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(initBorrowingDto()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Book list for borrowing includes one or more book which can not borrow"));
    }

    @Test
    void extendBorrowing_ValidRequest_ShouldReturnSuccess() throws Exception {
        doNothing().when(borrowingService).extendBorrowing(ArgumentMatchers.any());

        mockMvc.perform(MockMvcRequestBuilders.post(UriConstant.BORROWING_BASE_URI + UriConstant.BORROWING_EXTEND_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(initExtendBorrowingDto()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value(MessageConstant.EXTEND_BORROWING_SUCCESSFULLY));

        verify(borrowingService, times(1)).extendBorrowing(ArgumentMatchers.any());
    }

    @Test
    void extendBorrowing_InValidRequest_ShouldReturnFail() throws Exception {
        ExtendBorrowingDto extendBorrowingDto = new ExtendBorrowingDto();
        extendBorrowingDto.setBorrowingId(1);
        extendBorrowingDto.setNewDueDate(LocalDate.now().minusDays(3));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(extendBorrowingDto);

        mockMvc.perform(MockMvcRequestBuilders.post(UriConstant.BORROWING_BASE_URI + UriConstant.BORROWING_EXTEND_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value(MessageConstant.INVALID_INPUT_DATA))
                .andExpect(MockMvcResultMatchers.jsonPath("content.newDueDate").value("The new due date must be in the future"));
    }

    @Test
    void extendBorrowing_InvalidBorrowPeriodExtensionException_ShouldReturnFail() throws Exception {
        doThrow(new InvalidBorrowPeriodExtensionException("The extension exceeds the maximum allowable period of 3 days or is not a valid extension date")).when(borrowingService).extendBorrowing(ArgumentMatchers.any());

        mockMvc.perform(MockMvcRequestBuilders.post(UriConstant.BORROWING_BASE_URI + UriConstant.BORROWING_EXTEND_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(initExtendBorrowingDto()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("The extension exceeds the maximum allowable period of 3 days or is not a valid extension date"));
    }

    @Test
    void cancelBorrowing_ShouldReturnSuccess() throws Exception {
        doNothing().when(borrowingService).cancelBorrowing(anyInt());

        mockMvc.perform(MockMvcRequestBuilders.post(UriConstant.BORROWING_BASE_URI + UriConstant.BORROWING_CANCEL_URI, 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value(MessageConstant.CANCEL_BORROWING_SUCCESSFULLY));

        verify(borrowingService, times(1)).cancelBorrowing(anyInt());
    }

    @Test
    void returnBorrowing_ValidRequest_ShouldReturnSuccess() throws Exception {
        doNothing().when(borrowingService).returnBorrowing(ArgumentMatchers.any());

        ReturnBorrowingDto returnBorrowingDto = new ReturnBorrowingDto();
        returnBorrowingDto.setBorrowingId(1);

        ReturnDetailDto returnDetailDto = new ReturnDetailDto();
        returnDetailDto.setBookId(1);
        returnDetailDto.setReturnAmount(2);
        List<ReturnDetailDto> returnDetailDtoList = List.of(returnDetailDto);
        returnBorrowingDto.setBookList(returnDetailDtoList);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(returnBorrowingDto);

        mockMvc.perform(MockMvcRequestBuilders.post(UriConstant.BORROWING_BASE_URI + UriConstant.BORROWING_RETURN_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value(MessageConstant.RETURN_BORROWING_SUCCESSFULLY));

        verify(borrowingService, times(1)).returnBorrowing(ArgumentMatchers.any());
    }

    @Test
    void returnBorrowing_InValidRequest_ShouldReturnFail() throws Exception {
        ReturnBorrowingDto returnBorrowingDto = new ReturnBorrowingDto();
        returnBorrowingDto.setBorrowingId(1);

        ReturnDetailDto returnDetailDto = new ReturnDetailDto();
        returnDetailDto.setBookId(1);
        returnDetailDto.setReturnAmount(-1);
        List<ReturnDetailDto> returnDetailDtoList = List.of(returnDetailDto);
        returnBorrowingDto.setBookList(returnDetailDtoList);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(returnBorrowingDto);

        mockMvc.perform(MockMvcRequestBuilders.post(UriConstant.BORROWING_BASE_URI + UriConstant.BORROWING_RETURN_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value(MessageConstant.INVALID_INPUT_DATA))
                .andExpect(MockMvcResultMatchers.jsonPath("content['bookList[0].returnAmount']").value("Book quantity must equal or greater than 0"));
    }

    @Test
    void getBorrowingHistoryList_ShouldReturnSuccess() throws Exception {
        List<BorrowBookHistoryDto> borrowBookHistoryDtoList = new ArrayList<>();
        int pageSize = CommonConstant.PAGE_SIZE;
        for (int i = 1; i <= pageSize; i++) {
            BorrowBookHistoryDto borrowBookHistoryDto = initBorrowBookHistoryDto(i);
            borrowBookHistoryDtoList.add(borrowBookHistoryDto);
        }

        int totalPages = (int) Math.ceil((double) borrowBookHistoryDtoList.size() / pageSize);

        PageResponse<BorrowBookHistoryDto> pageResponse = new PageResponse<>();
        pageResponse.setCurrentPage(0);
        pageResponse.setPageSize(pageSize);
        pageResponse.setTotalPages(totalPages);
        pageResponse.setTotalElements(borrowBookHistoryDtoList.size());
        pageResponse.setData(borrowBookHistoryDtoList);

        when(borrowingService.getBorrowBookHistory(anyInt(), any(), any(), any())).thenReturn(pageResponse);

        mockMvc.perform(MockMvcRequestBuilders.get(UriConstant.BORROWING_BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("pageNo", "0"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value(MessageConstant.GET_BORROWING_HISTORY_LIST_SUCCESSFULLY))
                .andExpect(MockMvcResultMatchers.jsonPath("data.totalElements").value(10));
    }

    @Test
    void getBorrowingHistoryForReader_ShouldReturnSuccess() throws Exception {
        List<BorrowBookHistoryDto> borrowBookHistoryDtoList = new ArrayList<>();
        int pageSize = CommonConstant.PAGE_SIZE;
        for (int i = 1; i <= pageSize; i++) {
            BorrowBookHistoryDto borrowBookHistoryDto = initBorrowBookHistoryDto(i);
            borrowBookHistoryDtoList.add(borrowBookHistoryDto);
        }

        int totalPages = (int) Math.ceil((double) borrowBookHistoryDtoList.size() / pageSize);

        PageResponse<BorrowBookHistoryDto> pageResponse = new PageResponse<>();
        pageResponse.setCurrentPage(0);
        pageResponse.setPageSize(pageSize);
        pageResponse.setTotalPages(totalPages);
        pageResponse.setTotalElements(borrowBookHistoryDtoList.size());
        pageResponse.setData(borrowBookHistoryDtoList);

        when(borrowingService.getBorrowBookHistoryForReader(anyInt(), anyInt(), any())).thenReturn(pageResponse);

        mockMvc.perform(MockMvcRequestBuilders.get(UriConstant.BORROWING_BASE_URI + UriConstant.BORROWING_MEMBER_HISTORY_URI, 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("pageNo", "0"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value(MessageConstant.GET_BORROWING_HISTORY_LIST_FOR_READER_SUCCESSFULLY))
                .andExpect(MockMvcResultMatchers.jsonPath("data.totalElements").value(10));
    }

    @Test
    void getBorrowingHistoryDetail_ShouldReturnSuccess() throws Exception {
        BorrowBookHistoryDetailDto borrowBookHistoryDetailDto = new BorrowBookHistoryDetailDto();
        borrowBookHistoryDetailDto.setId(1);
        borrowBookHistoryDetailDto.setBookId(101);
        borrowBookHistoryDetailDto.setBookTitle("Harry Potter and the Philosopher's Stone");
        borrowBookHistoryDetailDto.setBookIsbn13("978-3-16-148410-0");
        borrowBookHistoryDetailDto.setBorrowAmount(2);
        borrowBookHistoryDetailDto.setReturnAmount(2);


        when(borrowingService.getBorrowBookHistoryDetail(anyInt())).thenReturn(List.of(borrowBookHistoryDetailDto, borrowBookHistoryDetailDto));

        mockMvc.perform(MockMvcRequestBuilders.get(UriConstant.BORROWING_BASE_URI + UriConstant.BORROWING_DETAIL_URI, 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value(MessageConstant.GET_BORROWING_HISTORY_DETAIL_SUCCESSFULLY));
    }

    private BorrowBookHistoryDto initBorrowBookHistoryDto(int i) {
        BorrowBookHistoryDto borrowBookHistoryDto = new BorrowBookHistoryDto();
        borrowBookHistoryDto.setId(i);
        borrowBookHistoryDto.setMemberMembershipNumber("MB" + i);
        borrowBookHistoryDto.setStatus(BorrowingStatusEnum.BORROWED);
        borrowBookHistoryDto.setBorrowingDate(LocalDate.of(2023, 8, 1));
        borrowBookHistoryDto.setReturnDate(LocalDate.of(2023, 8, 10));
        borrowBookHistoryDto.setDueDate(LocalDate.of(2023, 8, 7));
        borrowBookHistoryDto.setTotalBook(3);

        return borrowBookHistoryDto;
    }

    private String initBorrowingDto() throws Exception {
        BorrowingDto borrowingDto = new BorrowingDto();
        borrowingDto.setAccountBorrowId(1);
        borrowingDto.setBorrowingDate(LocalDate.now());
        borrowingDto.setDueDate(LocalDate.now().plusDays(3));

        BorrowingDetailDto borrowingDetailDto1 = new BorrowingDetailDto(1, 1);
        BorrowingDetailDto borrowingDetailDto2 = new BorrowingDetailDto(2, 1);
        List<BorrowingDetailDto> bookList = List.of(borrowingDetailDto1, borrowingDetailDto2);
        borrowingDto.setBookList(bookList);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.writeValueAsString(borrowingDto);
    }

    private String initExtendBorrowingDto() throws Exception {
        ExtendBorrowingDto extendBorrowingDto = new ExtendBorrowingDto();
        extendBorrowingDto.setBorrowingId(1);
        extendBorrowingDto.setNewDueDate(LocalDate.now().plusDays(3));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.writeValueAsString(extendBorrowingDto);
    }
}
