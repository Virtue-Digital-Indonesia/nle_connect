package com.nle.shared.service.fleet;

import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.DepoFleetRegisterRequest;
import com.nle.ui.model.response.DepoFleetResponse;
import com.nle.ui.model.response.FleetResponse;
import org.springframework.data.domain.Pageable;

public interface DepoFleetService {

    PagingResponseModel<DepoFleetResponse> getAllFleetsDepo (Pageable pageable);
    DepoFleetResponse registerFleet(DepoFleetRegisterRequest request);
}
