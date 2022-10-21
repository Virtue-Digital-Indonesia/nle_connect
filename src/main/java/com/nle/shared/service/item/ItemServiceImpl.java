package com.nle.shared.service.item;

import com.nle.io.entity.Item;
import com.nle.io.repository.ItemRepository;
import com.nle.security.SecurityUtils;
import com.nle.ui.model.pageable.PagingResponseModel;
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

    @Override
    public PagingResponseModel<ItemResponse> getListItem(Pageable pageable) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (!currentUserLogin.isEmpty()) {
            Page<Item> listItem = itemRepository.getAllDepoItem(currentUserLogin.get(), pageable);
            return new PagingResponseModel<>(listItem.map(this::convertToResponse));
        }

        return new PagingResponseModel<>();
    }

    private ItemResponse convertToResponse (Item item) {
        ItemResponse itemResponse = new ItemResponse();
        BeanUtils.copyProperties(item, itemResponse);
        return itemResponse;
    }

}
