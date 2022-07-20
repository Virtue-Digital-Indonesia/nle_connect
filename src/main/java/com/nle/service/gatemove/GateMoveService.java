package com.nle.service.gatemove;

import com.nle.controller.dto.GateMoveCreateDTO;
import com.nle.controller.dto.pageable.PagingResponseModel;
import com.nle.service.dto.GateMoveDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface GateMoveService {
    GateMoveDTO createGateMove(GateMoveCreateDTO gateMoveCreateDTO);

    void uploadFile(MultipartFile[] files, Long gateMoveId);

    PagingResponseModel<GateMoveDTO> findAll(Pageable pageable);
}
