package com.nle.controller.dto.response;

import com.nle.service.dto.ftp.MoveDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdatedGateMoveResponseDTO extends MoveDTO {
    private Long id;
    private LocalDateTime txDateFormatted;
    private String gateMoveType;
}
