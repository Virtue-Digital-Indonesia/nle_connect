package com.nle.shared.service.inventory;

import com.nle.io.entity.GateMove;
import com.nle.io.entity.Inventory;
import com.nle.io.repository.InventoryRepository;
import com.nle.security.SecurityUtils;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.response.InventoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

    public PagingResponseModel<InventoryResponse> getAllInventory (Pageable pageable){
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isPresent())  {
            String email = currentUserLogin.get();
            Page<Inventory> listResults = inventoryRepository.getAllInventory(email, pageable);
            return new PagingResponseModel<>(listResults.map(this::convertToResponse));
        }
        return new PagingResponseModel<>();
    }

    private InventoryResponse convertToResponse(Inventory inventory){
        InventoryResponse response = new InventoryResponse();
        BeanUtils.copyProperties(inventory, response);
        return response;
    }
};
