package com.nle.shared.service.item;

import com.nle.exception.BadRequestException;
import com.nle.io.entity.IsoCodeContainer;
import com.nle.io.entity.ItemType;
import com.nle.io.repository.IsoCodeContainerRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemTypeServiceImpl implements ItemTypeService{

    private final ItemTypeRepository itemTypeRepository;
    private final IsoCodeContainerRepository isoCodeContainerRepository;
    @Override
    public PagingResponseModel<ItemTypeResponse> getAllItemType(Pageable pageable) {
        Page<ItemType> listItemType = itemTypeRepository.findAll(pageable);
        return new PagingResponseModel<>(listItemType.map(this::convertItemTypeToResponse));
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

    @Override
    public List<ItemTypeResponse> getFromIsoCode(String size, String type) {
        List<ItemTypeResponse> itemTypeResponseList = new ArrayList<>();
        String code = null;

        //Validate If type 4 character get item
        if (type.length() == 4){
            code = type;
            List<ItemType> itemTypeResponses = itemTypeRepository.findByItemCode(code);
            for (ItemType getItem: itemTypeResponses) {
                itemTypeResponseList.add(this.convertItemTypeToResponse(getItem));
            }
            return itemTypeResponseList;
        }
        code = size+type;

        //Validate if type not contains numeric
        if (type.matches("[a-zA-Z]+")){
            List<ItemType> itemTypeResponses = itemTypeRepository.findByItemCode(code);
            for (ItemType getItem: itemTypeResponses) {
                itemTypeResponseList.add(this.convertItemTypeToResponse(getItem));
            }
            return itemTypeResponseList;
        }

        //Get grup code from iso container base on code
        Optional<IsoCodeContainer> isoCodeContainerOpt = isoCodeContainerRepository.findByCode(code);
        IsoCodeContainer isoCodeContainer = isoCodeContainerOpt.get();
        List<ItemType> itemTypeResponses = itemTypeRepository.findByItemCode(isoCodeContainer.getIso_group());
        for (ItemType getItem: itemTypeResponses) {
            itemTypeResponseList.add(this.convertItemTypeToResponse(getItem));
        }
        return itemTypeResponseList;
    }

    private ItemTypeResponse convertItemTypeToResponse(ItemType itemType) {
        ItemTypeResponse response = new ItemTypeResponse();
        BeanUtils.copyProperties(itemType, response);
        return response;
    }
}
