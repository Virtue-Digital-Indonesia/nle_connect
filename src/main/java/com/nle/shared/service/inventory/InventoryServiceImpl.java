package com.nle.shared.service.inventory;

import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.entity.GateMove;
import com.nle.io.entity.Inventory;
import com.nle.io.repository.InventoryRepository;
import com.nle.security.SecurityUtils;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.search.InventorySearchRequest;
import com.nle.ui.model.response.InventoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.nle.util.NleUtil.GATE_IN;
import static com.nle.util.NleUtil.GATE_OUT;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService{

    private final InventoryRepository inventoryRepository;

    public void triggerInventory(GateMove gateMove) {

        if (gateMove.getGateMoveType().equalsIgnoreCase(GATE_IN)) {
            triggerCreateInventory(gateMove);
        }
        else if (gateMove.getGateMoveType().equalsIgnoreCase(GATE_OUT)) {
            triggerGateOutInventory(gateMove);
        }
    }

    public void triggerCreateInventory(GateMove gateMove) {
        Inventory inventory = new Inventory();
        BeanUtils.copyProperties(gateMove, inventory);
        inventory.setDepoOwnerAccount(inventory.getDepoOwnerAccount());
        inventory.setGateInId(gateMove);
        inventoryRepository.save(inventory);
    };

    public void triggerGateOutInventory(GateMove gateMove) {
        DepoOwnerAccount depoOwnerAccount = gateMove.getDepoOwnerAccount();
        Long depoId = depoOwnerAccount.getId();
        List<Inventory> optionalInventory = inventoryRepository.findTopByContainerNumber(depoId, gateMove.getContainer_number());

        if (!optionalInventory.isEmpty()) {
            for (Inventory inventory : optionalInventory) {
                inventory.setGateOutId(gateMove);
                inventoryRepository.save(inventory);
            }
        }
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

    public PagingResponseModel<InventoryResponse> searchByCondition (InventorySearchRequest request, Pageable pageable) {

        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isPresent())  {
            String email = currentUserLogin.get();
            Page<Inventory> listResults = inventoryRepository.searchByCondition(email, request, pageable);
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
