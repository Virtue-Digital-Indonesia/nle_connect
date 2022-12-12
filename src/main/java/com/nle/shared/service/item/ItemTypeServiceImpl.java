package com.nle.shared.service.item;

import com.nle.exception.BadRequestException;
import com.nle.io.entity.ItemType;
import com.nle.io.repository.ItemTypeRepository;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.ItemTypeRequest;
import com.nle.ui.model.response.ItemTypeResponse;
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
public class ItemTypeServiceImpl implements ItemTypeService{

    private final ItemTypeRepository itemTypeRepository;
    @Override
    public PagingResponseModel<ItemTypeResponse> getAllItemType(Pageable pageable) {
        Page<ItemType> listItemType = itemTypeRepository.findAll(pageable);
        return new PagingResponseModel<>(listItemType.map(ItemTypeServiceImpl::convertItemTypeToResponse));
    }

    @Override
    public ItemTypeResponse createItemType(ItemTypeRequest request) {

        Optional<ItemType> itemTypeOptional = itemTypeRepository.findByCode(request.getItemCode());
        if (!itemTypeOptional.isEmpty())
            throw new BadRequestException("this code is already used");

        ItemType itemType = new ItemType();
        BeanUtils.copyProperties(request, itemType);
        ItemType savedEntity = itemTypeRepository.save(itemType);
        return this.convertItemTypeToResponse(savedEntity);
    }

    public static ItemTypeResponse convertItemTypeToResponse(ItemType itemType) {
        ItemTypeResponse response = new ItemTypeResponse();
        BeanUtils.copyProperties(itemType, response);
        return response;
    }
}
