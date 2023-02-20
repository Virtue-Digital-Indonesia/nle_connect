package com.nle.shared.service.fleet;

import com.nle.exception.BadRequestException;
import com.nle.io.entity.InswShipping;
import com.nle.io.repository.InswShippingRepository;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.InswShippingRequest;
import com.nle.ui.model.response.InswShippingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class InswShippingServiceImpl implements InswShippingService{

    private final InswShippingRepository inswShippingRepository;

    @Override
    public PagingResponseModel<InswShippingResponse> getAllInswShipping(Pageable pageable) {
        Page<InswShipping> inswShippingPage = inswShippingRepository.findAll(pageable);
        return new PagingResponseModel<>(inswShippingPage.map(this::convertToResponse));
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
    public InswShippingResponse searchShippingCode(String code) {
        Optional<InswShipping> inswShipping = inswShippingRepository.findByCode(code);

        if (inswShipping.isEmpty()) {
            inswShipping = inswShippingRepository.findByCode("SSI");
        }

        return convertToResponse(inswShipping.get());
    }

    private InswShippingResponse convertToResponse(InswShipping entity) {
        InswShippingResponse response = new InswShippingResponse();
        BeanUtils.copyProperties(entity, response);
        return response;
    }
}
