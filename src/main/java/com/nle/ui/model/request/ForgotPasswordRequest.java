package com.nle.ui.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Setter
@Getter
@AllArgsConstructor
@Data
@Builder
public class ForgotPasswordRequest {
    private String password;
    private String confirm_password;
    private String token;
}
