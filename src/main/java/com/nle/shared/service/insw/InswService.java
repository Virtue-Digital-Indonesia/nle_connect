package com.nle.shared.service.insw;

import com.nle.shared.dto.insw.InswSyncDataDTO;
import com.nle.ui.model.response.insw.InswResponse;

import java.util.List;


public interface InswService {
    InswResponse getBolData(String bolNumber, Long depoId);

    List<InswSyncDataDTO> syncInsw();
}
