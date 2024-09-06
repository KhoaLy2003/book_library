package com.khoaly.book_library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.khoaly.book_library.constant.CommonConstant;
import com.khoaly.book_library.constant.MessageConstant;
import com.khoaly.book_library.constant.UriConstant;
import com.khoaly.book_library.dto.MemberDetailResponseDto;
import com.khoaly.book_library.dto.MemberRequestDto;
import com.khoaly.book_library.dto.MemberResponseDto;
import com.khoaly.book_library.dto.PageResponse;
import com.khoaly.book_library.enumeration.AccountStatusEnum;
import com.khoaly.book_library.enumeration.MembershipTypeEnum;
import com.khoaly.book_library.exception.DuplicateEmailException;
import com.khoaly.book_library.exception.NotFoundException;
import com.khoaly.book_library.service.MemberService;
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

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource("/test.properties")
class MemberControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Test
    void createMember_ValidRequest_ShouldReturnSuccess() throws Exception {
        when(memberService.createMember(ArgumentMatchers.any())).thenReturn("MEMBER001");

        mockMvc.perform(MockMvcRequestBuilders.post(UriConstant.MEMBER_BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(initCreateMemberRequest()))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(HttpStatus.CREATED.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value(MessageConstant.CREATE_MEMBER_SUCCESSFULLY))
                .andExpect(MockMvcResultMatchers.jsonPath("data").value("MEMBER001"));
    }

    @Test
    void createMember_InValidRequest_ShouldReturnFail() throws Exception {
        MemberRequestDto memberRequestDto = new MemberRequestDto();
        memberRequestDto.setFirstName("John");
        memberRequestDto.setLastName("Doe");
        memberRequestDto.setEmail("johndoeexample.com");
        memberRequestDto.setPhoneNumber("123456789");
        memberRequestDto.setAddress("123 Main St, Anytown, USA");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(memberRequestDto);

        mockMvc.perform(MockMvcRequestBuilders.post(UriConstant.MEMBER_BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value(MessageConstant.INVALID_INPUT_DATA))
                .andExpect(MockMvcResultMatchers.jsonPath("content.email").value("Email must be a valid email address"))
                .andExpect(MockMvcResultMatchers.jsonPath("content.phoneNumber").value("Invalid phone format"));
    }

    @Test
    void createMember_DuplicateEmail_ShouldReturnFail() throws Exception {
        when(memberService.createMember(ArgumentMatchers.any())).thenThrow(new DuplicateEmailException("Email is duplicate, can not create new account"));

        mockMvc.perform(MockMvcRequestBuilders.post(UriConstant.MEMBER_BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(initCreateMemberRequest()))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(HttpStatus.CONFLICT.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Email is duplicate, can not create new account"));
    }

    @Test
    void getMembers_ShouldReturnSuccess() throws Exception {
        List<MemberResponseDto> memberList = new ArrayList<>();
        int pageSize = CommonConstant.PAGE_SIZE;
        for (int i = 1; i <= pageSize; i++) {
            MemberResponseDto memberResponseDto = initMemberResponseDto(i);
            memberList.add(memberResponseDto);
        }

        int totalPages = (int) Math.ceil((double) memberList.size() / pageSize);

        PageResponse<MemberResponseDto> pageResponse = new PageResponse<>();
        pageResponse.setCurrentPage(0);
        pageResponse.setPageSize(pageSize);
        pageResponse.setTotalPages(totalPages);
        pageResponse.setTotalElements(memberList.size());
        pageResponse.setData(memberList);

        when(memberService.getMembers(anyInt())).thenReturn(pageResponse);

        mockMvc.perform(MockMvcRequestBuilders.get(UriConstant.MEMBER_BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("pageNo", "0"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value(MessageConstant.GET_MEMBER_LIST_SUCCESSFULLY))
                .andExpect(MockMvcResultMatchers.jsonPath("data.totalElements").value(10));
    }

    @Test
    void getMemberDetail_ShouldReturnSuccess() throws Exception {
        MemberDetailResponseDto memberDetailResponseDto = new MemberDetailResponseDto();
        memberDetailResponseDto.setId(1);
        memberDetailResponseDto.setAccountFirstName("John");
        memberDetailResponseDto.setAccountLastName("Doe");
        memberDetailResponseDto.setAccountEmail("johndoe@example.com");
        memberDetailResponseDto.setAccountPhoneNumber("1234567890");
        memberDetailResponseDto.setAccountAddress("123 Main St, Apt 1");
        memberDetailResponseDto.setAccountNotes("This is a sample note for John Doe.");
        memberDetailResponseDto.setAccountStatus(AccountStatusEnum.ACTIVE);
        memberDetailResponseDto.setMembershipNumber("MEMBER001");
        memberDetailResponseDto.setMembershipStartDate(LocalDate.now().minusYears(2));
        memberDetailResponseDto.setMembershipEndDate(LocalDate.now().plusYears(1));
        memberDetailResponseDto.setMembershipType(MembershipTypeEnum.BASIC);
        memberDetailResponseDto.setTotalBooksBorrowed(20);
        memberDetailResponseDto.setTotalLateReturns(2);
        memberDetailResponseDto.setCurrentBorrowedBooks(3);

        when(memberService.getMemberDetailByMembershipNumber(anyString())).thenReturn(memberDetailResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.get(UriConstant.MEMBER_BASE_URI + UriConstant.MEMBER_DETAIL_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("membershipNumber", "MEMBER001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value(MessageConstant.GET_MEMBER_DETAIL_SUCCESSFULLY))
                .andExpect(MockMvcResultMatchers.jsonPath("data.id").value(1));
    }

    @Test
    void getMemberDetail_ShouldReturnFail() throws Exception {
        String membershipNumber = "MEMBER001";
        when(memberService.getMemberDetailByMembershipNumber(anyString())).thenThrow(new NotFoundException("Not found member with membership number " + membershipNumber));

        mockMvc.perform(MockMvcRequestBuilders.get(UriConstant.MEMBER_BASE_URI + UriConstant.MEMBER_DETAIL_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("membershipNumber", "MEMBER001"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Not found member with membership number " + membershipNumber));
    }

    private String initCreateMemberRequest() throws Exception {
        MemberRequestDto memberRequestDto = new MemberRequestDto();
        memberRequestDto.setFirstName("John");
        memberRequestDto.setLastName("Doe");
        memberRequestDto.setEmail("johndoe@example.com");
        memberRequestDto.setPhoneNumber("1234567890");
        memberRequestDto.setAddress("123 Main St, Anytown, USA");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.writeValueAsString(memberRequestDto);
    }

    private MemberResponseDto initMemberResponseDto(int i) {
        MemberResponseDto memberResponseDto = new MemberResponseDto();
        memberResponseDto.setId(i);
        memberResponseDto.setAccountFirstName("John " + i);
        memberResponseDto.setAccountLastName("Doe " + i);
        memberResponseDto.setAccountEmail("johndoe" + i + "@example.com");
        memberResponseDto.setAccountPhoneNumber("123456789" + i);
        memberResponseDto.setAccountStatus(AccountStatusEnum.ACTIVE);
        memberResponseDto.setMembershipNumber("MEMBER" + i);
        memberResponseDto.setMembershipType(MembershipTypeEnum.BASIC);
        return memberResponseDto;
    }
}
