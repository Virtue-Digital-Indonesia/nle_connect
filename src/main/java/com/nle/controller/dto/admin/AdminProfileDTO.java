package com.nle.controller.dto.admin;

import lombok.Data;

@Data
public class AdminProfileDTO {
    private Long id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String roles;
}
