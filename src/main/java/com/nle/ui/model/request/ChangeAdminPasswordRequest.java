package com.nle.ui.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ChangeAdminPasswordRequest {
    @JsonProperty("old_password")
    private String oldPassword;
    @NotNull(message = "new_password shouldn't be null")
    @NotBlank(message = "new_password shouldn't be blank")
    @JsonProperty("new_password")
    private String newPassword;
    @NotNull(message = "confirm_new_password shouldn't be null")
    @NotBlank(message = "confirm_new_password shouldn't be blank")
    @JsonProperty("confirm_new_password")
    private String confirmNewPassword;
}
