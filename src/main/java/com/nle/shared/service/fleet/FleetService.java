package com.nle.shared.service.fleet;

import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.response.FleetResponse;
import org.springframework.data.domain.Pageable;

public interface FleetService {
    PagingResponseModel<FleetResponse> getAllFleets (Pageable pageable);
}
