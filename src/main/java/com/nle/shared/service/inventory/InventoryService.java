package com.nle.shared.service.inventory;

import com.nle.io.entity.GateMove;

public interface InventoryService {

    void triggerCreateInventory(GateMove gateMove);
    void triggerGateOutInventory(GateMove gateMove);

}
