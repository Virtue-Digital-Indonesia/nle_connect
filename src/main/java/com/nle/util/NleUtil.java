package com.nle.util;

import com.nle.constant.AppConstant;
import com.nle.constant.GateMoveSource;
import com.nle.entity.GateMove;
import com.nle.service.dto.ftp.MoveDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static com.nle.service.ftp.FTPService.GATE_IN;
import static com.nle.service.ftp.FTPService.GATE_IN_EMPTY;
import static com.nle.service.ftp.FTPService.GATE_OUT;
import static com.nle.service.ftp.FTPService.GATE_OUT_EMPTY;

public class NleUtil {

    private static final Logger log = LoggerFactory.getLogger(NleUtil.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    public static GateMove convertToGateMoveEntity(MoveDTO moveDTO, GateMoveSource gateMoveSource) {
        GateMove gateMove = new GateMove();
        BeanUtils.copyProperties(moveDTO, gateMove);
        if (moveDTO.getTx_date() != null) {
            if (GateMoveSource.MOBILE == gateMoveSource) {
                gateMove.setTxDateFormatted(LocalDateTime.parse(moveDTO.getTx_date(), DATE_TIME_FORMATTER));
            } else if (GateMoveSource.FTP_SERVER == gateMoveSource) {
                gateMove.setTxDateFormatted(formatTxDate(moveDTO.getTx_date()));
            }
        }
        gateMove.setGateMoveType(transformProcessTypeToGateMoveType(moveDTO.getProcess_type()));
        gateMove.setClean("yes".equalsIgnoreCase(moveDTO.getClean()) || "true".equalsIgnoreCase(moveDTO.getClean()));
        gateMove.setTare(moveDTO.getTare());
        gateMove.setPayload(moveDTO.getPayload());
        gateMove.setStatus(AppConstant.Status.WAITING);
        gateMove.setNleId(UUID.randomUUID().toString());
        gateMove.setSource(gateMoveSource);
        return gateMove;
    }

    public static String transformProcessTypeToGateMoveType(String processType) {
        if (processType == null) {
            return null;
        }
        if (GATE_IN.equals(processType) || GATE_IN_EMPTY.equals(processType)) {
            return GATE_IN;
        }
        if (GATE_OUT.equals(processType) || GATE_OUT_EMPTY.equals(processType)) {
            return GATE_OUT;
        }
        return null;
    }

    private static LocalDateTime formatTxDate(String txDate) {
        String replace = txDate.replace(AppConstant.TIME_ZONE, "");
        try {
            return LocalDateTime.parse(replace);
        } catch (Exception exception) {
            log.error("Can not format transaction date {}", txDate);
            return null;
        }
    }
}