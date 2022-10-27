package com.nle.shared.service.item;

import com.nle.exception.CommonException;
import com.nle.io.entity.DepoFleet;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.entity.Fleet;
import com.nle.io.entity.Item;
import com.nle.io.repository.DepoFleetRepository;
import com.nle.io.repository.DepoOwnerAccountRepository;
import com.nle.io.repository.ItemRepository;
import com.nle.security.SecurityUtils;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.CreateItemRequest;
import com.nle.ui.model.response.DepoFleetResponse;
import com.nle.ui.model.response.FleetResponse;
import com.nle.ui.model.response.ItemResponse;
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
public class ItemServiceImpl implements ItemService{

    private final ItemRepository itemRepository;
    private final DepoOwnerAccountRepository depoOwnerAccountRepository;
    private final DepoFleetRepository depoFleetRepository;

    @Override
    public PagingResponseModel<ItemResponse> getListItem(Pageable pageable) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (!currentUserLogin.isEmpty()) {
            Page<Item> listItem = itemRepository.getAllDepoItem(currentUserLogin.get(), pageable);
            return new PagingResponseModel<>(listItem.map(this::convertToResponse));
        }

        return new PagingResponseModel<>();
    }

    @Override
    public ItemResponse createItem (CreateItemRequest request) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isEmpty()) {
            return new ItemResponse();
        }

        Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountRepository.findByCompanyEmail(currentUserLogin.get());
        if (depoOwnerAccount.isEmpty())
            throw new CommonException("Cannot find this depo owner ");

        Item item = new Item();
        BeanUtils.copyProperties(request, item);
        item.setDepoOwnerAccount(depoOwnerAccount.get());

        if (request.getFleetCode() != null && !request.getFleetCode().trim().isEmpty()) {
            Optional<DepoFleet> fleet = depoFleetRepository.getFleetInDepo(currentUserLogin.get(), request.getFleetCode());
            if (fleet.isEmpty())
                throw new CommonException("this fleet code is not register in depo");

            item.setFleet(fleet.get().getFleet());
        }

        Item savedItem = itemRepository.save(item);
        return this.convertToResponse(savedItem);
    }

    private ItemResponse convertToResponse (Item item) {
        ItemResponse itemResponse = new ItemResponse();
        BeanUtils.copyProperties(item, itemResponse);

        if (item.getFleet() != null) {
            Optional<DepoFleet> depoFleet = depoFleetRepository.getFleetInDepo(
                    item.getDepoOwnerAccount().getCompanyEmail(),
                    item.getFleet().getCode());

            DepoFleetResponse depoFleetResponse = new DepoFleetResponse();
            BeanUtils.copyProperties(depoFleet.get().getFleet(), depoFleetResponse);
            depoFleetResponse.setDepo_fleet_id(depoFleet.get().getId());
            depoFleetResponse.setName(depoFleet.get().getName());
            itemResponse.setDepoFleet(depoFleetResponse);
        }
        return itemResponse;
    }

}
