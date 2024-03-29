package com.nle.shared.dto.ftp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FtpMoveDTOError {
    private MoveDTO moveDTO;
    private String errorMessage;
}
