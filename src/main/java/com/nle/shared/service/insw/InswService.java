package com.nle.shared.service.insw;

import com.nle.ui.model.response.insw.InswResponse;


public interface InswService {
    InswResponse getBolData(String bolNumber, Long depoId);

}
