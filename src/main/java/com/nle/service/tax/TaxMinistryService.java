package com.nle.service.tax;

import com.nle.config.openfeign.TaxMinistryServiceClient;
import com.nle.constant.AppConstant;
import com.nle.controller.depo.InventoryController;
import com.nle.entity.GateMove;
import com.nle.repository.GateMoveRepository;
import com.nle.service.dto.taxministry.TaxMinistryRequestDTO;
import com.nle.service.dto.taxministry.TaxMinistryResponseDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                TaxMinistryRequestDTO taxMinistryRequestDTO = convertFromGateMove(gateMove);
                try {
                    syncDataToTaxMinistry(taxMinistryRequestDTO);
                } catch (Exception exception) {
                    LOGGER.error("Error while syncing data to tax ministry for id {} with error {}", gateMove.getId(), exception);
                }
            }
        }
    }


    public void syncDataToTaxMinistry(TaxMinistryRequestDTO taxMinistryRequestDTO) {
        TaxMinistryResponseDTO taxMinistryResponseDTO = taxMinistryServiceClient.syncDataToTaxMinistry(taxMinistryRequestDTO);
        if (taxMinistryResponseDTO.getStatus()) {
            gateMoveRepository.updateGateMoveStatusById(AppConstant.Status.SUBMITTED, taxMinistryRequestDTO.getId());
        } else {
            LOGGER.error("Error while syncing data to tax ministry {}", taxMinistryResponseDTO.getData().getMessage());
        }
    }

    private TaxMinistryRequestDTO convertFromGateMove(GateMove gateMove) {
        TaxMinistryRequestDTO taxMinistryRequestDTO = new TaxMinistryRequestDTO();
        BeanUtils.copyProperties(gateMove, taxMinistryRequestDTO);
        taxMinistryRequestDTO.setClean(gateMove.getClean() ? "yes" : "no");
        taxMinistryRequestDTO.setDateManufacturing(gateMove.getDateManufacturer());
        return taxMinistryRequestDTO;
    }
}
