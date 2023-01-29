package com.nle.shared.service.item;

import com.nle.exception.BadRequestException;
import com.nle.io.entity.IsoCodeContainer;
import com.nle.io.repository.IsoCodeContainerRepository;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.IsoCodeContainerRequest;
import com.nle.ui.model.response.IsoCodeContainerResponse;
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
public class IsoCodeContainerServiceImpl implements IsoCodeContainerService{

    private final IsoCodeContainerRepository isoCodeContainerRepository;


    @Override
    public PagingResponseModel<IsoCodeContainerResponse> getAllIsoCode(Pageable pageable) {
        Page<IsoCodeContainer> listIsoCode = isoCodeContainerRepository.findAll(pageable);
        return new PagingResponseModel<>(listIsoCode.map(this::convertToResponse));
    }

    @Override
    public IsoCodeContainerResponse insertIsoCode(IsoCodeContainerRequest request) {

        Optional<IsoCodeContainer> optional = isoCodeContainerRepository.findByCode(request.getIso_code());
        if (optional.isPresent())
            throw new BadRequestException("this code is already registered");

        IsoCodeContainer entity = new IsoCodeContainer();
        BeanUtils.copyProperties(request, entity);
        IsoCodeContainer saved = isoCodeContainerRepository.save(entity);
        return convertToResponse(saved);
    }

    private IsoCodeContainerResponse convertToResponse(IsoCodeContainer entity) {
        IsoCodeContainerResponse response = new IsoCodeContainerResponse();
        BeanUtils.copyProperties(entity, response);
        return response;
    }
}
