package com.nle.service.gatemove;

import com.nle.controller.dto.pageable.PagingResponseModel;
import com.nle.controller.dto.request.CreateGateMoveReqDTO;
import com.nle.controller.dto.request.UpdateGateMoveReqDTO;
import com.nle.controller.dto.response.CreatedGateMoveResponseDTO;
import com.nle.controller.dto.response.GateMoveResponseDTO;
import com.nle.controller.dto.response.UpdatedGateMoveResponseDTO;
import com.nle.repository.dto.MoveStatistic;
import com.nle.repository.dto.ShippingLineStatistic;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface GateMoveService {
    CreatedGateMoveResponseDTO createGateMove(CreateGateMoveReqDTO createGateMoveReqDTO);

    UpdatedGateMoveResponseDTO updateGateMove(UpdateGateMoveReqDTO updateGateMoveReqDTO);

    void uploadFile(MultipartFile[] files, Long gateMoveId);

    PagingResponseModel<GateMoveResponseDTO> findAll(Pageable pageable, LocalDateTime from, LocalDateTime to);

    PagingResponseModel<GateMoveResponseDTO> findByType(Pageable pageable);

    List<MoveStatistic> countTotalGateMoveByType();

    List<ShippingLineStatistic> countTotalGateMoveByShippingLine();
}
