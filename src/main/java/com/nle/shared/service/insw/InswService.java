package com.nle.shared.service.insw;

import com.nle.shared.dto.insw.InswSyncDataDTO;
import com.nle.ui.model.response.insw.InswResponse;

import java.util.List;


public interface InswService {
    InswResponse getBolData(String bolNumber, Long depoId);

    List<InswSyncDataDTO> syncInsw();

    //Todo : Hanya untuk tes hapus jika sudah fix ke insw
    String tesSendCon(Long id);
}
