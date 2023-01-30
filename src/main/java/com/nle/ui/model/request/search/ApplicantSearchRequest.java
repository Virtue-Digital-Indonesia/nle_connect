package com.nle.ui.model.request.search;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ApplicantSearchRequest {

    private String companyEmail;
    private String phoneNumber;
    private String fullName;
    private String organizationName;
    private String organizationCode;
    private String globalSearch;
}
