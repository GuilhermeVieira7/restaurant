package com.restaurant.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserRequest {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotNull
    @Pattern(regexp = "ADMIN|WAITER|KITCHEN", flags = Pattern.Flag.CASE_INSENSITIVE,
             message = "Role must be one of: ADMIN, WAITER, KITCHEN")
    private String role;
}
