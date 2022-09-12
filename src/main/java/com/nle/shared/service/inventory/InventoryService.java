package com.nle.shared.service.inventory;

import com.nle.io.entity.GateMove;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.response.InventoryResponse;
import org.springframework.data.domain.Pageable;

public interface InventoryService {

    void triggerCreateInventory(GateMove gateMove);
    void triggerGateOutInventory(GateMove gateMove);

    PagingResponseModel<InventoryResponse> getAllInventory (Pageable pageable);
}
