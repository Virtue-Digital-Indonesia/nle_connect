package com.nle.shared.service.fleet;

import com.nle.ui.model.request.InswShippingRequest;
import com.nle.ui.model.response.FleetResponse;
import com.nle.ui.model.response.InswShippingResponse;

import java.util.List;

public interface InswShippingService {

    List<InswShippingResponse> getAllInswShipping();

    InswShippingResponse insertInswShipping (InswShippingRequest request);
    InswShippingResponse searchFleetCode (String code);
}
