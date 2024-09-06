package com.khoaly.book_library.service;

import com.khoaly.book_library.dto.MemberDetailResponseDto;
import com.khoaly.book_library.dto.MemberRequestDto;
import com.khoaly.book_library.dto.MemberResponseDto;
import com.khoaly.book_library.dto.PageResponse;

public interface MemberService {
    String createMember(MemberRequestDto memberRequestDto);
    MemberDetailResponseDto getMemberDetailByMembershipNumber(String membershipNumber);
    PageResponse<MemberResponseDto> getMembers(int pageNo);
}
