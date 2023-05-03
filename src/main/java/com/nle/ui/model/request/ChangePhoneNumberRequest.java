package com.nle.ui.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ChangePhoneNumberRequest {
    @NotNull(message = "old_phone_number shouldn't be null")
    @NotBlank(message = "old_phone_number shouldn't be blank")
    @JsonProperty("old_phone_number")
    private String oldPhoneNumber;
    @NotNull(message = "new_phone_number shouldn't be null")
    @NotBlank(message = "new_phone_number shouldn't be blank")
    @JsonProperty("new_phone_number")
    private String newPhoneNumber;
    @NotNull(message = "confirm_new_phone_number shouldn't be null")
    @NotBlank(message = "confirm_new_phone_number shouldn't be blank")
    @JsonProperty("confirm_new_phone_number")
    private String confirmPhoneNumber;
}
