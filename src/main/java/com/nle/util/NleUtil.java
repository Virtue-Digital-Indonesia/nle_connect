package com.nle.util;

import com.nle.constant.AppConstant;
import com.nle.constant.enums.GateMoveSource;
import com.nle.io.entity.GateMove;
import com.nle.shared.dto.ftp.MoveDTO;
import com.nle.shared.dto.taxministry.TaxMinistryRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NleUtil {

    private static final Logger log = LoggerFactory.getLogger(NleUtil.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    public static final String GATE_IN = "gate_in";
    public static final String GATE_IN_EMPTY = "gate_in_empty";
    public static final String GATE_OUT = "gate_out";
    public static final String GATE_OUT_EMPTY = "gate_out_empty";
    public static final String GATE_IN_REPO = "gate_in_repo";
    public static final String GATE_OUT_REPO = "gate_out_repo";


    public static GateMove convertToGateMoveEntity(MoveDTO moveDTO, GateMoveSource gateMoveSource) {
        GateMove gateMove = new GateMove();
        BeanUtils.copyProperties(moveDTO, gateMove);
        if (moveDTO.getTx_date() != null) {
            if (GateMoveSource.MOBILE == gateMoveSource) {
                gateMove.setTxDateFormatted(LocalDateTime.parse(moveDTO.getTx_date(), DATE_TIME_FORMATTER));
                gateMove.setLatitude(moveDTO.getLatitude());
                gateMove.setLongitude(moveDTO.getLongitude());
            } else if (GateMoveSource.API == gateMoveSource) {
                gateMove.setTxDateFormatted(LocalDateTime.parse(moveDTO.getTx_date(), DATE_TIME_FORMATTER));
            } else if (GateMoveSource.FTP_SERVER == gateMoveSource) {
                gateMove.setTxDateFormatted(formatTxDate(moveDTO.getTx_date()));
            }
        }
        //copy variable
        gateMove.setTx_date(moveDTO.getTx_date());
        gateMove.setProcess_type(moveDTO.getProcess_type());
        gateMove.setDepot(moveDTO.getDepot());
        gateMove.setFleet_manager(moveDTO.getFleet_manager());
        gateMove.setContainer_number(moveDTO.getContainer_number());
        gateMove.setIso_code(moveDTO.getIso_code());
        gateMove.setCondition(moveDTO.getCondition());
        gateMove.setClean("yes".equalsIgnoreCase(moveDTO.getClean()) || "true".equalsIgnoreCase(moveDTO.getClean()));
        gateMove.setGrade(moveDTO.getGrade());
        gateMove.setDamage_by(moveDTO.getDamage_by());
        gateMove.setAmount(moveDTO.getAmount());
        gateMove.setOrder_number(moveDTO.getOrder_number());
        gateMove.setCustomer(moveDTO.getCustomer());
        gateMove.setVessel(moveDTO.getVessel());
        gateMove.setVoyage(moveDTO.getVoyage());
        gateMove.setDischarge_port(moveDTO.getDischarge_port());
        gateMove.setCarrier(moveDTO.getCarrier());
        gateMove.setTransport_number(moveDTO.getTransport_number());
        gateMove.setTare(moveDTO.getTare());
        gateMove.setPayload(moveDTO.getPayload());
        gateMove.setDate_manufacturer(moveDTO.getDate_manufacturer());
        gateMove.setRemarks(moveDTO.getRemarks());
        gateMove.setDelivery_port(moveDTO.getDelivery_port());
        gateMove.setDriver_name(moveDTO.getDriver_name());
        gateMove.setMax_gross(moveDTO.getMax_gross());
//        gateMove.setLinkToGoogleMap(moveDTO.getLink);
        gateMove.setGateMoveType(transformProcessTypeToGateMoveType(moveDTO.getProcess_type()));
        gateMove.setStatus(AppConstant.Status.WAITING);
//        gateMove.setNleId(UUID.randomUUID().toString());
        gateMove.setSource(gateMoveSource);


        return gateMove;
    }

    public static String transformProcessTypeToGateMoveType(String processType) {
        if (processType == null) {
            return null;
        }
        if (GATE_IN.equalsIgnoreCase(processType) || GATE_IN_EMPTY.equalsIgnoreCase(processType) || GATE_IN_REPO.equalsIgnoreCase(processType)) {
            return GATE_IN;
        }
        if (GATE_OUT.equalsIgnoreCase(processType) || GATE_OUT_EMPTY.equalsIgnoreCase(processType) || GATE_OUT_REPO.equalsIgnoreCase(processType)) {
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

    public static TaxMinistryRequestDTO convertFromGateMove(GateMove gateMove) {
        TaxMinistryRequestDTO taxMinistryRequestDTO = new TaxMinistryRequestDTO();
        BeanUtils.copyProperties(gateMove, taxMinistryRequestDTO);
        taxMinistryRequestDTO.setClean(gateMove.getClean() ? "yes" : "no");
        taxMinistryRequestDTO.setDateManufacturing(gateMove.getDate_manufacturer());
        taxMinistryRequestDTO.setContainerNumber(gateMove.getContainer_number());
        taxMinistryRequestDTO.setDeliveryPort(gateMove.getDelivery_port());
        taxMinistryRequestDTO.setDiscargePort(gateMove.getDischarge_port());
        taxMinistryRequestDTO.setDriveName(gateMove.getDriver_name());
        taxMinistryRequestDTO.setFleetManager(gateMove.getFleet_manager());
        taxMinistryRequestDTO.setIsoCode(gateMove.getIso_code());
        taxMinistryRequestDTO.setMaxGross(gateMove.getMax_gross());
        taxMinistryRequestDTO.setOrderNumber(gateMove.getOrder_number());
        taxMinistryRequestDTO.setProcessType(gateMove.getProcess_type().toLowerCase());
        taxMinistryRequestDTO.setRemark(gateMove.getRemarks());
        taxMinistryRequestDTO.setTransportNumber(gateMove.getTransport_number());
        taxMinistryRequestDTO.setTxDate(gateMove.getTx_date());
        taxMinistryRequestDTO.setVessel(gateMove.getVessel());
        taxMinistryRequestDTO.setAmount(gateMove.getAmount());
        return taxMinistryRequestDTO;
    }

    public static String convertProcessType(String processType){
        String getProcessNo = null;
        if (processType.equalsIgnoreCase("GATE_IN")) {
            getProcessNo = "Gate in";
        } else if (processType.equalsIgnoreCase("GATE_OUT")) {
            getProcessNo = "Gate out";
        } else if (processType.equalsIgnoreCase("GATE_IN_EMPTY")) {
            getProcessNo = "Gate in empty";
        } else if (processType.equalsIgnoreCase("GATE_OUT_EMPTY")) {
            getProcessNo = "Gate out empty";
        } else if (processType.equalsIgnoreCase("GATE_IN_REPO")) {
            getProcessNo = "Gate in repo";
        } else if (processType.equalsIgnoreCase("GATE_OUT_REPO")) {
            getProcessNo = "Gate out repo";
        } else {
            getProcessNo = null;
        }

        return getProcessNo;
    }
}
