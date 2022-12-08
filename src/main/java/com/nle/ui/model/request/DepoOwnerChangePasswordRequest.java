package com.nle.ui.model.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepoOwnerChangePasswordRequest {
    @NotNull(message = "new_password shouldn't be null")
    @NotBlank(message = "new_password shouldn't be blank")
    @JsonProperty("new_password")
    private String newPassword;

    @NotNull(message = "confirm_new_password shouldn't be null")
    @NotBlank(message = "confirm_new_password shouldn't be blank")
    @JsonProperty("confirm_new_password")
    private String confirmNewPassword;
}
