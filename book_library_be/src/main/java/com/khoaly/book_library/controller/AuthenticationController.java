package com.khoaly.book_library.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("app/v1/authentication")
@RequiredArgsConstructor
public class AuthenticationController {

    @GetMapping("/hello-admin")
    @PreAuthorize("hasRole('admin')")
    public String hello() {
        return "Hello admin";
    }
}
