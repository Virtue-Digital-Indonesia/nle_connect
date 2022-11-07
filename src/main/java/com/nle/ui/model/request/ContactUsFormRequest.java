package com.nle.ui.model.request;

import com.nle.constant.enums.UserFeedbackCategory;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class ContactUsFormRequest {
    private String fullName;
    private String email;
    private String address;
    private UserFeedbackCategory category;
    private String message;
}
