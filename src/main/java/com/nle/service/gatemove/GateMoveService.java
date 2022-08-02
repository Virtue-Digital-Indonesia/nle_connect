package com.nle.service.gatemove;

import com.nle.controller.dto.GateMoveCreateDTO;
import com.nle.controller.dto.pageable.PagingResponseModel;
import com.nle.repository.dto.MoveStatistic;
import com.nle.repository.dto.ShippingLineStatistic;
import com.nle.service.dto.GateMoveDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GateMoveService {
    GateMoveDTO createGateMove(GateMoveCreateDTO gateMoveCreateDTO);

    GateMoveDTO updateGateMove(GateMoveDTO gateMoveDTO);

    void uploadFile(MultipartFile[] files, Long gateMoveId);

    PagingResponseModel<GateMoveDTO> findAll(Pageable pageable);

    PagingResponseModel<GateMoveDTO> findByType(Pageable pageable);

    List<MoveStatistic> countTotalGateMoveByType();

    List<ShippingLineStatistic> countTotalGateMoveByShippingLine();
}
