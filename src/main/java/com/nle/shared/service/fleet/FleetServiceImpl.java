package com.nle.shared.service.fleet;

import com.nle.exception.BadRequestException;
import com.nle.io.entity.Fleet;
import com.nle.io.repository.FleetRepository;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.FleetRequest;
import com.nle.ui.model.response.FleetResponse;
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
public class FleetServiceImpl implements FleetService{

    private final FleetRepository fleetRepository;
    @Override
    public PagingResponseModel<FleetResponse> getAllFleets(Pageable pageable) {
        Page<Fleet> listFleet = fleetRepository.getAllFleet(pageable);
        return new PagingResponseModel<>(listFleet.map(this::convertToResponse));
    }

    @Override
    public FleetResponse createFleet(FleetRequest request) {
        String code = request.getCode();
        Optional<Fleet> check = fleetRepository.getByCode(code);

        if (!check.isEmpty())
            throw new BadRequestException("Code is already in use!");

        Fleet fleet = new Fleet();
        BeanUtils.copyProperties(request, fleet);
        Fleet savedEntity = fleetRepository.save(fleet);
        return this.convertToResponse(savedEntity);
    }

    @Override
    public FleetResponse searchFleetCode (String code) {
        Optional<Fleet> fleet = fleetRepository.getByCode(code);

        if (fleet.isEmpty()) {
            fleet = fleetRepository.getByCode("APLU");
        }

        return this.convertToResponse(fleet.get());
    }

    private FleetResponse convertToResponse (Fleet fleet) {
        FleetResponse fleetResponse = new FleetResponse();
        BeanUtils.copyProperties(fleet, fleetResponse);
        return fleetResponse;
    }
}
