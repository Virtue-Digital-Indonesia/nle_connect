package com.nle.shared.service.insw;

import com.nle.io.entity.GateMove;
import com.nle.io.repository.GateMoveRepository;
import com.nle.shared.dto.insw.InswSyncDataDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SyncToInsw {
    private final InswService inswService;
    private final GateMoveRepository gateMoveRepository;

    public void SyncDataToInsw(){
        List<GateMove> gateMoveList = gateMoveRepository.findAllBySyncToInswNull();
        List<InswSyncDataDTO> listResponse = new ArrayList<>();
        for (GateMove gateMove : gateMoveList) {

            //Get data from method convertToInswSyncDataDto
            InswSyncDataDTO inswDTO = inswService.convertToInswSyncDataDto(gateMove);

            //method for Send data to insw
            try {
                inswDTO.setStatusFeedback(inswService.sendToInsw(inswDTO));
                listResponse.add(inswDTO);
                log.info("Success send data to INSW with ID : "+ gateMove.getId());
            } catch (Exception e){
                e.printStackTrace();
            }

            //gate move yang berhasil dikirim ke insw akan dicatat tanggal kirimnya
            if (inswDTO.getStatusFeedback() != null && inswDTO.getStatusFeedback().equalsIgnoreCase("Success!")){
                gateMoveRepository.updateGateMoveStatusByInsw(gateMove.getId(), LocalDateTime.now());
                log.info("Success update field Sync_to_insw with ID : "+ gateMove.getId());
            }
        }
    }
}
