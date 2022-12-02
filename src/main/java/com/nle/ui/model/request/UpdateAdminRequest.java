package com.nle.ui.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Setter
@Getter
public class UpdateAdminRequest {
    @NotNull(message = "Id can not be null")
    private Long id;

    private String fullName;
    @JsonProperty("phone_number")
    private String phoneNumber;

    @Pattern(regexp = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@[A-Za-z\\d-]+(.[A-Za-z\\d]+)*(.[A-Za-z]{2,})$")
    private String email;
}
