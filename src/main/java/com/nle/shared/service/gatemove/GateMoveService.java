package com.nle.shared.service.gatemove;

import com.nle.constant.enums.GateMoveSource;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.CreateGateMoveReqDTO;
import com.nle.ui.model.request.UpdateGateMoveReqDTO;
import com.nle.ui.model.request.search.GateMoveSearchRequest;
import com.nle.ui.model.response.CreatedGateMoveResponseDTO;
import com.nle.ui.model.response.GateMoveResponseDTO;
import com.nle.ui.model.response.UpdatedGateMoveResponseDTO;
import com.nle.io.repository.dto.MoveStatistic;
import com.nle.io.repository.dto.ShippingLineStatistic;
import com.nle.ui.model.response.count.CountResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface GateMoveService {
    CreatedGateMoveResponseDTO createGateMove(CreateGateMoveReqDTO createGateMoveReqDTO, GateMoveSource source);

    UpdatedGateMoveResponseDTO updateGateMove(UpdateGateMoveReqDTO updateGateMoveReqDTO, GateMoveSource source);

    void uploadFile(MultipartFile[] files, Long gateMoveId);

    PagingResponseModel<GateMoveResponseDTO> findAll(Pageable pageable, LocalDateTime from, LocalDateTime to);

    PagingResponseModel<GateMoveResponseDTO> findByType(Pageable pageable);

    List<MoveStatistic> countTotalGateMoveByType();

    List<ShippingLineStatistic> countTotalGateMoveByShippingLine();

    PagingResponseModel<GateMoveResponseDTO> searchByCondition(Pageable pageable, GateMoveSearchRequest request);

    CountResponse countTotalGateMoveByDuration(Long duration);
    CountResponse countTotalGateMoveByDurationWithFleetManager(Long duration);
}
