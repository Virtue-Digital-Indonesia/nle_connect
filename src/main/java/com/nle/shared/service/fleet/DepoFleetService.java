package com.nle.shared.service.fleet;

import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.response.FleetResponse;
import org.springframework.data.domain.Pageable;

public interface DepoFleetService {

    PagingResponseModel<FleetResponse> getAllFleetsDepo (Pageable pageable);
    FleetResponse registerFleet(String fleetCode);
}
