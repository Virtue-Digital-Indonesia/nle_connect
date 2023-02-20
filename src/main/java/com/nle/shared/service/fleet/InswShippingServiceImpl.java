package com.nle.shared.service.fleet;

import com.nle.exception.BadRequestException;
import com.nle.io.entity.Fleet;
import com.nle.io.entity.InswShipping;
import com.nle.io.repository.InswShippingRepository;
import com.nle.ui.model.request.InswShippingRequest;
import com.nle.ui.model.response.InswShippingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class InswShippingServiceImpl implements InswShippingService{

    private final InswShippingRepository inswShippingRepository;

    @Override
    public List<InswShippingResponse> getAllInswShipping() {
        List<InswShipping> inswShippingList =  inswShippingRepository.findAll();
        List<InswShippingResponse> responseList = new ArrayList<>();
        for (InswShipping shipping : inswShippingList) {
            responseList.add(convertToResponse(shipping));
        }
        return responseList;
    }

    @Override
    public InswShippingResponse insertInswShipping(InswShippingRequest request) {

        Optional<InswShipping> check = inswShippingRepository.findByCode(request.getCode());
        if (check.isPresent())
            throw new BadRequestException("code is already registered");

        InswShipping entity = new InswShipping();
        BeanUtils.copyProperties(request, entity);
        InswShipping savedEntity = inswShippingRepository.save(entity);
        return convertToResponse(savedEntity);
    }

    @Override
    public InswShippingResponse searchFleetCode(String code) {
        Optional<InswShipping> fleet = inswShippingRepository.findByCode(code);

        if (fleet.isEmpty()) {
            fleet = null;
        }

        return this.convertToResponse(fleet.get());
    }

    private InswShippingResponse convertToResponse(InswShipping entity) {
        InswShippingResponse response = new InswShippingResponse();
        BeanUtils.copyProperties(entity, response);
        return response;
    }
}
