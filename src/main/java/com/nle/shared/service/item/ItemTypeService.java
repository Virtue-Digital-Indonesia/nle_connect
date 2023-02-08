package com.nle.shared.service.item;

import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.ItemTypeRequest;
import com.nle.ui.model.response.ItemTypeResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemTypeService {
    PagingResponseModel<ItemTypeResponse> getAllItemType(Pageable pageable);
    ItemTypeResponse createItemType(ItemTypeRequest request);

    List<ItemTypeResponse> getFromIsoCode(String size, String type);

}
