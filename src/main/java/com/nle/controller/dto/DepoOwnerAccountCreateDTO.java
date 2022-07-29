package com.nle.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

import static com.nle.constant.AppConstant.Pattern.EMAIL_PATTERN;

public class DepoOwnerAccountCreateDTO implements Serializable {

    @Pattern(regexp = EMAIL_PATTERN, message = "Email is not valid!")
    @Schema(example = "admin@gmail.com", required = true, description = "Depo owner email")
    private String companyEmail;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String password;

    @NotNull
    private String fullName;

    private String organizationName;

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    @Override
    public String toString() {
        return "DepoOwnerAccountCreateDTO{" +
            "companyEmail='" + companyEmail + '\'' +
            ", phoneNumber='" + phoneNumber + '\'' +
            ", password='" + password + '\'' +
            ", fullName='" + fullName + '\'' +
            ", organizationName='" + organizationName + '\'' +
            '}';
    }
}
