package com.nle.controller.dto.request;

import com.nle.service.dto.ftp.MoveDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateGateMoveReqDTO extends MoveDTO {
    @NotNull(message = "Gate Move Id can not be null!")
    private Long id;
}
