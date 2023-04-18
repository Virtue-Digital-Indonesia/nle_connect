package com.nle.shared.service.tax;

import com.nle.config.openfeign.TaxMinistryServiceClient;
import com.nle.constant.AppConstant;
import com.nle.constant.enums.TaxMinistryStatusEnum;
import com.nle.ui.controller.depo.InventoryController;
import com.nle.io.entity.GateMove;
import com.nle.io.repository.GateMoveRepository;
import com.nle.shared.dto.taxministry.TaxMinistryRequestDTO;
import com.nle.shared.dto.taxministry.TaxMinistryResponseDTO;
import com.nle.util.NleUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TaxMinistryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryController.class);
    private final TaxMinistryServiceClient taxMinistryServiceClient;
    private final GateMoveRepository gateMoveRepository;

    @Scheduled(cron = "${app.scheduler.tax-ministry-sync-cron}")
    public void syncDataToTaxMinistry() {
        List<GateMove> waitingGateMove = gateMoveRepository.findAllByStatus(AppConstant.Status.WAITING);
        if (!waitingGateMove.isEmpty()) {
            for (GateMove gateMove : waitingGateMove) {

                if (gateMove.getDepoOwnerAccount().getTaxMinistryStatusEnum() == TaxMinistryStatusEnum.DISABLE) {
                    continue;
                }

                TaxMinistryRequestDTO taxMinistryRequestDTO = NleUtil.convertFromGateMove(gateMove);
                try {
                    syncDataToTaxMinistry(taxMinistryRequestDTO);
                } catch (Exception exception) {
                    LOGGER.error("Error while syncing data to tax ministry for id {} with error {}", gateMove.getId(), exception);
                }
            }
        }
    }


    public void syncDataToTaxMinistry(TaxMinistryRequestDTO taxMinistryRequestDTO) {
        System.out.println(taxMinistryRequestDTO);
        TaxMinistryResponseDTO taxMinistryResponseDTO = taxMinistryServiceClient.syncDataToTaxMinistry(taxMinistryRequestDTO);
        System.out.println(taxMinistryResponseDTO.getStatus());
        System.out.println(taxMinistryResponseDTO.getMessage());

        if (taxMinistryResponseDTO.getStatus()) {
            gateMoveRepository.updateGateMoveStatusById(AppConstant.Status.SUBMITTED,
                    LocalDateTime.now(),
                    taxMinistryResponseDTO.getData().getIdTraffic(),
                    taxMinistryRequestDTO.getId());
        } else {
            if (taxMinistryResponseDTO.getStatus() == false && taxMinistryResponseDTO.getData().getMessage().equalsIgnoreCase("Data Sudah Ada")) {
                gateMoveRepository.updateGateMoveStatusById(AppConstant.Status.ALREADY, null, taxMinistryRequestDTO.getId());
            }
            LOGGER.error("Error while syncing data to tax ministry {}", taxMinistryResponseDTO.getData().getMessage());
        }
    }
}
