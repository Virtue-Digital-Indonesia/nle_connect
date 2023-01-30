package com.nle.shared.service.item;

import com.nle.constant.enums.ItemTypeEnum;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.CreateItemRequest;
import com.nle.ui.model.request.search.ItemSearchRequest;
import com.nle.ui.model.response.ItemResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemService {
    PagingResponseModel<ItemResponse> getListItem(Pageable pageable);
    ItemResponse createItem (CreateItemRequest request);
    ItemResponse updateItem (Long id, CreateItemRequest request);
    List<ItemResponse> multipleDeleteItem(List<Long> listID);
    PagingResponseModel<ItemResponse> search(ItemSearchRequest request,Pageable pageable);
    List<ItemResponse> getItemDepo(Long depo_id, ItemTypeEnum type);
}
