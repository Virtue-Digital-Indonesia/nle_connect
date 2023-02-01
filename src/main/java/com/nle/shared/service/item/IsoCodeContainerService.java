package com.nle.shared.service.item;

import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.IsoCodeContainerRequest;
import com.nle.ui.model.response.IsoCodeContainerResponse;
import org.springframework.data.domain.Pageable;

public interface IsoCodeContainerService {

    PagingResponseModel<IsoCodeContainerResponse> getAllIsoCode(Pageable pageable);
    IsoCodeContainerResponse insertIsoCode(IsoCodeContainerRequest request);
}
