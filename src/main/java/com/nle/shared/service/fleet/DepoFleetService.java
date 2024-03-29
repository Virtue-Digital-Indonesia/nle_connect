package com.nle.shared.service.fleet;

import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.DepoFleetRegisterRequest;
import com.nle.ui.model.request.DepoFleetUpdateRequest;
import com.nle.ui.model.request.search.DepoFleetSearchRequest;
import com.nle.ui.model.response.DepoFleetResponse;
import org.springframework.data.domain.Pageable;

public interface DepoFleetService {

    PagingResponseModel<DepoFleetResponse> getAllFleetsDepo (Pageable pageable);
    DepoFleetResponse registerFleet(DepoFleetRegisterRequest request);
    DepoFleetResponse updateRegisterFleet(DepoFleetUpdateRequest request);
    DepoFleetResponse deleteDepoFleet(String fleet_code);
    PagingResponseModel<DepoFleetResponse> searchDepoFleet(DepoFleetSearchRequest depoFleetSearchRequest, Pageable pageable);
}
