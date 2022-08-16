package com.nle.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Pattern;

import static com.nle.constant.AppConstant.Pattern.EMAIL_PATTERN;

@Data
public class DepoWorkerInvitationReqDto {
    @Pattern(regexp = EMAIL_PATTERN, message = "Email is not valid!")
    @Schema(example = "admin@gmail.com", required = true, description = "Email to invite")
    private String email;
}
