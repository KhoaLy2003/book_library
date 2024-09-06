package com.khoaly.book_library.service.impl;

import com.khoaly.book_library.constant.CommonConstant;
import com.khoaly.book_library.dto.MemberDetailResponseDto;
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
import com.khoaly.book_library.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final AccountRepository accountRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;
    Random random = new Random();

    @Override
    public String createMember(MemberRequestDto memberRequestDto) {
        if (accountRepository.existsByEmail(memberRequestDto.getEmail())) {
            throw new DuplicateEmailException("Email is duplicate, can not create new account");
        }

        Account newAccount = modelMapper.map(memberRequestDto, Account.class);
        newAccount.setStatus(AccountStatusEnum.ACTIVE);
        newAccount.setNotes(StringUtils.EMPTY);
        newAccount.setRole(AccountRoleEnum.MEMBER);

        Member newMember = Member
                .builder()
                .account(newAccount)
                .membershipNumber(generateMembershipNumber(memberRequestDto.getFirstName()))
                .membershipType(MembershipTypeEnum.BASIC)
                .membershipStartDate(LocalDate.now())
                .membershipEndDate(LocalDate.now().plusYears(CommonConstant.DEFAULT_MEMBERSHIP_DURATION))
                .totalBooksBorrowed(NumberUtils.INTEGER_ZERO)
                .totalLateReturns(NumberUtils.INTEGER_ZERO)
                .currentBorrowedBooks(NumberUtils.INTEGER_ZERO)
                .build();

        newAccount.setMember(newMember);

        accountRepository.save(newAccount);

        return newMember.getMembershipNumber();
    }

    @Override
    public MemberDetailResponseDto getMemberDetailByMembershipNumber(String membershipNumber) {
        Member member = memberRepository.findMemberByMembershipNumber(membershipNumber)
                .orElseThrow(() -> new NotFoundException("Not found member with membership number " + membershipNumber));

        return modelMapper.map(member, MemberDetailResponseDto.class);
    }

    @Override
    public PageResponse<MemberResponseDto> getMembers(int pageNo) {
        Pageable pageable = PageRequest
                .of(pageNo, CommonConstant.PAGE_SIZE, Sort.by(CommonConstant.MEMBER_SORT_BY).descending());
        Page<Member> pageData = memberRepository.findAll(pageable);

        return PageResponse.<MemberResponseDto>builder()
                .currentPage(pageNo)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent().stream().map(element -> modelMapper.map(element, MemberResponseDto.class)).toList())
                .build();
    }

    private String generateMembershipNumber(String firstName) {
        String formattedDate = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        return CommonConstant.PREFIX_MEMBERSHIP_NUMBER + firstName + formattedDate + generateMembershipCode();
    }

    private String generateMembershipCode() {
        StringBuilder otp = new StringBuilder(CommonConstant.MAX_CODE_LENGTH);
        for (int i = 0; i < CommonConstant.MAX_CODE_LENGTH; i++) {
            otp.append(random.nextInt(CommonConstant.RANDOM_BOUND_VALUE));
        }
        return otp.toString();
    }
}
