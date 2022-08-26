package com.nle.ui.model.response;

import com.nle.constant.AccountStatus;
import com.nle.constant.ApprovalStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;


@Getter
@Setter
@ToString
public class ApplicantResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String companyEmail;

    private String phoneNumber;

    private String fullName;

    private String organizationName;

    private String organizationCode;

    private AccountStatus accountStatus;

    private ApprovalStatus approvalStatus;

    private LocalDateTime createdDate;
}
