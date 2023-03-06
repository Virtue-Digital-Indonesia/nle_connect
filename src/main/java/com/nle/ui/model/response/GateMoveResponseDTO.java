package com.nle.ui.model.response;

import com.nle.shared.dto.ftp.MoveDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GateMoveResponseDTO extends MoveDTO {
    private Long id;
    private LocalDateTime txDateFormatted;
    private String gateMoveType;
}
