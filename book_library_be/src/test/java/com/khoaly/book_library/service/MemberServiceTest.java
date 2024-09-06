package com.khoaly.book_library.service;

import com.khoaly.book_library.constant.CommonConstant;
import com.khoaly.book_library.dto.MemberRequestDto;
import com.khoaly.book_library.dto.MemberResponseDto;
import com.khoaly.book_library.dto.PageResponse;
import com.khoaly.book_library.entity.Account;
import com.khoaly.book_library.entity.Member;
import com.khoaly.book_library.enumeration.AccountRoleEnum;
import com.khoaly.book_library.enumeration.AccountStatusEnum;
import com.khoaly.book_library.enumeration.MembershipTypeEnum;
import com.khoaly.book_library.exception.DuplicateEmailException;
import com.khoaly.book_library.exception.NotFoundException;
import com.khoaly.book_library.repository.AccountRepository;
import com.khoaly.book_library.repository.MemberRepository;
import com.khoaly.book_library.service.impl.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource("/test.properties")
class MemberServiceTest {
    private final String membershipNumber = "MBUrsola202408197190";

    @Autowired
    private MemberServiceImpl memberService;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private AccountRepository accountRepository;
    private Account account;
    private Member member;
    private MemberRequestDto memberRequestDto;

    @BeforeEach
    void initData() {
        LocalDate membershipStartDate = LocalDate.of(2024, 8, 19);
        LocalDate membershipEndDate = LocalDate.of(2025, 8, 19);

        member = Member
                .builder()
                .currentBorrowedBooks(0)
                .membershipEndDate(membershipEndDate)
                .membershipNumber(membershipNumber)
                .membershipStartDate(membershipStartDate)
                .membershipType(MembershipTypeEnum.BASIC)
                .totalBooksBorrowed(0)
                .totalLateReturns(0)
                .build();

        account = Account.builder()
                .address("123 Main St, Anytown, USA")
                .email("upurdy0@cdbaby.com")
                .firstName("Ursola")
                .lastName("Purdy")
                .notes(null)
                .phoneNumber("1234567890")
                .status(AccountStatusEnum.ACTIVE)
                .role(AccountRoleEnum.MEMBER)
                .member(member)
                .build();

        memberRequestDto = MemberRequestDto
                .builder()
                .firstName("Ursola")
                .lastName("Purdy")
                .email("upurdy0@cdbaby.com")
                .address("123 Main St, Anytown, USA")
                .phoneNumber("1234567890")
                .build();
    }


    @Test
    void createMember_ShouldReturnSuccess() {
        // GIVEN
        when(accountRepository.existsByEmail(anyString())).thenReturn(false);
        when(accountRepository.save(any())).thenReturn(account);

        // WHEN
        var response = memberService.createMember(memberRequestDto);

        // THEN
        assertNotNull(memberRepository.findMemberByMembershipNumber(response));
    }

    @Test
    void createMember_ShouldReturnFail() {
        when(accountRepository.existsByEmail(anyString())).thenReturn(true);

        var exception = assertThrows(DuplicateEmailException.class, () -> memberService.createMember(memberRequestDto));

        assertEquals("Email is duplicate, can not create new account", exception.getMessage());
    }

    @Test
    void getMemberDetailByMembershipNumber_ShouldReturnSuccess() {
        when(memberRepository.findMemberByMembershipNumber(anyString())).thenReturn(Optional.of(member));

        var response = memberService.getMemberDetailByMembershipNumber(membershipNumber);

        assertNotNull(response);
    }

    @Test
    void getMemberDetailByMembershipNumber_ShouldReturnFail() {
        when(memberRepository.findMemberByMembershipNumber(anyString())).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class, () -> memberService.getMemberDetailByMembershipNumber(membershipNumber));

        assertEquals("Not found member with membership number " + membershipNumber, exception.getMessage());
    }

    @Test
    void getAccounts_ShouldReturnSuccess() {
        int pageNo = 0;
        int pageSize = CommonConstant.PAGE_SIZE;
        List<Member> accounts = List.of(member, member);
        Page<Member> pageData = new PageImpl<>(accounts, PageRequest.of(pageNo, pageSize, Sort.by(CommonConstant.MEMBER_SORT_BY).descending()), accounts.size());

        when(memberRepository.findAll(any(Pageable.class))).thenReturn(pageData);

        PageResponse<MemberResponseDto> result = memberService.getMembers(pageNo);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }
}
