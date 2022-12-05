package com.nle.ui.model.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateDepoOwnerRequest {
    private String phoneNumber;
    private String fullName;
    private String organizationName;
    private String address;
}
