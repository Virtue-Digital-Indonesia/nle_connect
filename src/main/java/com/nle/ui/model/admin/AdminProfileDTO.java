package com.nle.ui.model.admin;

import lombok.Data;

@Data
public class AdminProfileDTO {
    private Long id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String roles;
}
