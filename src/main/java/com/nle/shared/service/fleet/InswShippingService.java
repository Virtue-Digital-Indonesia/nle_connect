package com.nle.shared.service.fleet;

import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.InswShippingRequest;
import com.nle.ui.model.response.InswShippingResponse;
import org.springframework.data.domain.Pageable;


public interface InswShippingService {

    PagingResponseModel<InswShippingResponse> getAllInswShipping(Pageable pageable);

    InswShippingResponse insertInswShipping (InswShippingRequest request);

    InswShippingResponse searchShippingCode(String code);
}
