package com.nle.ui.model.request;

import com.nle.shared.dto.ftp.MoveDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateGateMoveReqDTO extends MoveDTO {
    @NotNull(message = "Gate Move Id can not be null!")
    private Long id;
}
