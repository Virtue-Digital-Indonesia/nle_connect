package com.nle.ui.model.request;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@Data
@Builder
public class ForgotPhoneNumberRequest {
    private String phone_number;
    private String confirm_phone_number;
    private String token;
}
