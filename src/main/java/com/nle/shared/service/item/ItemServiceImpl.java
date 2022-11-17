package com.nle.shared.service.item;

import com.nle.constant.enums.ItemTypeEnum;
import com.nle.exception.BadRequestException;
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
import com.nle.ui.model.request.search.ItemSearchRequest;
import com.nle.ui.model.response.DepoFleetResponse;
import com.nle.ui.model.response.FleetResponse;
import com.nle.ui.model.response.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService{

    private final ItemRepository itemRepository;
    private final DepoOwnerAccountRepository depoOwnerAccountRepository;
    private final DepoFleetRepository depoFleetRepository;

    private final static Map<String,String> mapOfSort= Map.ofEntries(
            Map.entry("fleetCode","f.code"),
            Map.entry("fleetName","dF.name")
    );

    @Override
    public PagingResponseModel<ItemResponse> getListItem(Pageable pageable) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (!currentUserLogin.isEmpty()) {
            Page<Item> listItem = itemRepository.getAllDepoItem(currentUserLogin.get(), pageable);
            return new PagingResponseModel<>(listItem.map(ItemServiceImpl::convertToResponse));
        }

        return new PagingResponseModel<>();
    }

    @Override
    public List<ItemResponse> getItemDepo(Long depo_id, ItemTypeEnum type) {
        List<Item> itemList = itemRepository.getAllByDepoId(depo_id,type);
        List<ItemResponse> responseList = new ArrayList<>();
        for (Item item : itemList) {
            responseList.add(this.convertToResponse(item));
        }
        return responseList;
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

            item.setDepoFleet(fleet.get());
        }

        Item savedItem = itemRepository.save(item);
        return this.convertToResponse(savedItem);
    }

    @Override
    public ItemResponse updateItem (Long id, CreateItemRequest request) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isEmpty()) {
            return new ItemResponse();
        }

        Optional<Item> optionalItem = itemRepository.findById(id);

        if (optionalItem.isEmpty()) throw new CommonException("cannot find item id");
        Item item = optionalItem.get();
        if (!item.getDepoOwnerAccount().getCompanyEmail().equalsIgnoreCase(currentUserLogin.get()))
            throw new BadRequestException("this item is not in this depo ");

        BeanUtils.copyProperties(request, item);

        if (request.getFleetCode() != null && !request.getFleetCode().trim().isEmpty()) {
            Optional<DepoFleet> depoFleet = depoFleetRepository.getFleetInDepo(currentUserLogin.get(), request.getFleetCode());
            if (depoFleet.isEmpty())
                throw new CommonException("cannot find this fleet in this depo");
            item.setDepoFleet(depoFleet.get());
        }
        else if (request.getFleetCode() == null || request.getFleetCode().trim().isEmpty()) {
            item.setDepoFleet(null);
        }

        Item savedItem = itemRepository.save(item);
        return this.convertToResponse(savedItem);
    }

    @Override
    public List<ItemResponse> multipleDeleteItem(List<Long> listID) {
        List<ItemResponse> responseList = new ArrayList<>();
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isEmpty())
            return responseList;

        String email = currentUserLogin.get();
        for (Long id : listID) {
            Optional<Item> optionalItem = itemRepository.findById(id);
            if (optionalItem.isEmpty()) continue;

            Item item = optionalItem.get();
            if (!item.getDepoOwnerAccount().getCompanyEmail().equalsIgnoreCase(email)) continue;
            if (item.getDeleted() == true) continue;

            item.setDeleted(true);
            Item savedItem = itemRepository.save(item);
            responseList.add(this.convertToResponse(savedItem));
        }
        return responseList;
    }

    @Override
    public PagingResponseModel<ItemResponse> search(ItemSearchRequest request,Pageable pageable) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        String sortField=mapOfSort.get(getSortBy(pageable))==null?getSortBy(pageable):mapOfSort.get(getSortBy(pageable));
        Pageable customPageable= PageRequest.of(pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(getDirection(pageable),
                        sortField));
        if (!currentUserLogin.isEmpty()) {
            Page<Item> listItem = itemRepository.searchItem(currentUserLogin.get(),
                    request.getItemName(),
                    request.getSku(),
                    request.getDescription(),
                    request.getFleetName(),
                    request.getPrice(),
                    request.getType(),
                    false,
                    request.getStatus(),
                    request.getFleetCode(),
                    request.getGlobalSearch(),
                    customPageable);
            return new PagingResponseModel<>(listItem.map(ItemServiceImpl::convertToResponse));
        }
        return null;
    }

    public static ItemResponse convertToResponse (Item item) {
        ItemResponse itemResponse = new ItemResponse();
        BeanUtils.copyProperties(item, itemResponse);

        if (item.getDepoFleet() != null) {
            DepoFleet depoFleet = item.getDepoFleet();
            DepoFleetResponse depoFleetResponse = new DepoFleetResponse();
            BeanUtils.copyProperties(depoFleet.getFleet(), depoFleetResponse);
            depoFleetResponse.setDepo_fleet_id(depoFleet.getId());
            depoFleetResponse.setName(depoFleet.getName());
            itemResponse.setFleet(depoFleetResponse);
        }
        return itemResponse;
    }

    public Sort.Direction getDirection(Pageable pageable) {
        return pageable.getSort().stream().map(Sort.Order::getDirection).collect(Collectors.toList()).get(0);
    }

    public String getSortBy(Pageable pageable) {
        return pageable.getSort().stream().map(Sort.Order::getProperty).collect(Collectors.toList()).get(0);
    }

}
