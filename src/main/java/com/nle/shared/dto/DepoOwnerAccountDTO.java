package com.nle.shared.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
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

    private String address;

    private String fullName;

    private String organizationName;

    private String organizationCode;

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
            ", address='" + getAddress() + "'" +
            ", password='" + getPassword() + "'" +
            ", fullName='" + getFullName() + "'" +
            ", organizationName='" + getOrganizationName() + "'" +
            "}";
    }
}
