package com.nle.controller.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DepoWorkerActivationDTO {
    @NotNull(message = "Android id can not be null!")
    private String androidId;

    @NotNull(message = "Full name can not be null!")
    private String fullName;

    @NotNull(message = "Activation Code can not be null!")
    private String organizationCode;
}
