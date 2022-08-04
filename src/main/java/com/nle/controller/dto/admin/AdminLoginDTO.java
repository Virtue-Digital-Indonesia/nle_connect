package com.nle.controller.dto.admin;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminLoginDTO {
    @NotNull(message = "Email can not be null")
    private String email;

    @NotNull(message = "Password can not be null")
    private String password;
}
