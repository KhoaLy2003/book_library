package com.khoaly.book_library.controller;

import com.khoaly.book_library.constant.MessageConstant;
import com.khoaly.book_library.constant.UriConstant;
import com.khoaly.book_library.dto.BaseResponse;
import com.khoaly.book_library.dto.MemberRequestDto;
import com.khoaly.book_library.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(UriConstant.MEMBER_BASE_URI)
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<BaseResponse> createNewAccount(@Valid @RequestBody MemberRequestDto memberRequestDto) {
        String membershipNumber = memberService.createMember(memberRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BaseResponse(HttpStatus.CREATED.value(), MessageConstant.CREATE_MEMBER_SUCCESSFULLY, membershipNumber));
    }

    @GetMapping(UriConstant.MEMBER_DETAIL_URI)
    public ResponseEntity<BaseResponse> getMemberDetail(@RequestParam(name = "membershipNumber") String membershipNumber) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), MessageConstant.GET_MEMBER_DETAIL_SUCCESSFULLY, memberService.getMemberDetailByMembershipNumber(membershipNumber)));
    }

    @GetMapping
    public ResponseEntity<BaseResponse> getMembers(@RequestParam(name = "pageNo") int pageNo) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), MessageConstant.GET_MEMBER_LIST_SUCCESSFULLY, memberService.getMembers(pageNo)));
    }
}
