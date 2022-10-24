package com.nle.ui.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

import static com.nle.constant.AppConstant.Pattern.EMAIL_PATTERN;

@Setter
@Getter
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

    private String address;

    @Override
    public String toString() {
        return "DepoOwnerAccountCreateDTO{" +
            "companyEmail='" + companyEmail + '\'' +
            ", phoneNumber='" + phoneNumber + '\'' +
            ", password='" + password + '\'' +
            ", fullName='" + fullName + '\'' +
            ", organizationName='" + organizationName + '\'' +
            ", address='" + address + '\'' +
            '}';
    }
}
