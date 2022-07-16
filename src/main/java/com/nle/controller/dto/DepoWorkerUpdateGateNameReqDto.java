package com.nle.controller.dto;

import lombok.Data;

import javax.validation.constraints.Pattern;

import static com.nle.constant.AppConstant.Pattern.EMAIL_PATTERN;

@Data
public class DepoWorkerUpdateGateNameReqDto {
    @Pattern(regexp = EMAIL_PATTERN, message = "Email is not valid!")
    private String email;

    private String gateName;
}
