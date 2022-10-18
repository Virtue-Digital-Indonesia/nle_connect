package com.nle.shared.service.fleet;

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
        Fleet fleet = new Fleet();
        BeanUtils.copyProperties(request, fleet);
        Fleet savedEntity = fleetRepository.save(fleet);
        return this.convertToResponse(savedEntity);
    }

    private FleetResponse convertToResponse (Fleet fleet) {
        FleetResponse fleetResponse = new FleetResponse();
        BeanUtils.copyProperties(fleet, fleetResponse);
        return fleetResponse;
    }
}
