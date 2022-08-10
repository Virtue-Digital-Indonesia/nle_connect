package com.nle.service.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DepoOwnerAccountProfileDTO implements Serializable {

    private Long id;

    private String companyEmail;

    private String phoneNumber;

    private String fullName;

    private String organizationName;

    private String organizationCode;
}