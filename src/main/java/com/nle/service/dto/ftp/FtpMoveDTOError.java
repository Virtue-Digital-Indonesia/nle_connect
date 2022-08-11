package com.nle.service.dto.ftp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FtpMoveDTOError {
    private FtpMoveDTO ftpMoveDTO;
    private String errorMessage;
}
