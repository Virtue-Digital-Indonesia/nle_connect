package com.nle.web.rest.vm;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * A DTO for the {@link com.nle.domain.DepoOwnerAccount} entity.
 */
@Data
@ToString
public class DepoOwnerAccountCreateDTO implements Serializable {

    @NotNull
    @Pattern(regexp = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$")
    private String companyEmail;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String password;

    private String fullName;

    private String organizationName;
}
