package com.khoaly.book_library.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.io.Serializable;

/**
 * DTO for {@link com.khoaly.book_library.entity.Account}
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MemberRequestDto implements Serializable {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Email must be a valid email address")
    @NotBlank(message = "Email is required")
    private String email;

    @Pattern(message = "Invalid phone format", regexp = "^\\d{10}$")
    @NotBlank(message = "Phone is required")
    private String phoneNumber;

    @NotBlank
    private String address;
}