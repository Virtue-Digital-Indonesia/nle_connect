package com.nle.shared.service.inventory;

import com.nle.io.entity.GateMove;
import com.nle.io.entity.Inventory;
import com.nle.io.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService{

    private final InventoryRepository inventoryRepository;
    public void triggerCreateInventory(GateMove gateMove) {
        Inventory inventory = new Inventory();
        BeanUtils.copyProperties(gateMove, inventory);
        inventory.setDepoOwnerAccount(inventory.getDepoOwnerAccount());
        inventory.setGateInId(gateMove);
        inventoryRepository.save(inventory);
    };

    public void triggerGateOutInventory(GateMove gateMove) {
        //TODO search inventory with something, input gateOutId, save in database
    }
};
