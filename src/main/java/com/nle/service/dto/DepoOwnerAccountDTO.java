package com.nle.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Objects;

public class DepoOwnerAccountDTO implements Serializable {

    private Long id;

    @NotNull
    @Pattern(regexp = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@[A-Za-z\\d-]+(.[A-Za-z\\d]+)*(.[A-Za-z]{2,})$")
    private String companyEmail;

    @NotNull
    private String phoneNumber;

    @NotNull
    @JsonIgnore
    private String password;

    private String fullName;

    private String organizationName;

    private String organizationCode;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DepoOwnerAccountDTO)) {
            return false;
        }

        DepoOwnerAccountDTO depoOwnerAccountDTO = (DepoOwnerAccountDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, depoOwnerAccountDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DepoOwnerAccountDTO{" +
            "id=" + getId() +
            ", companyEmail='" + getCompanyEmail() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", password='" + getPassword() + "'" +
            ", fullName='" + getFullName() + "'" +
            ", organizationName='" + getOrganizationName() + "'" +
            "}";
    }
}
