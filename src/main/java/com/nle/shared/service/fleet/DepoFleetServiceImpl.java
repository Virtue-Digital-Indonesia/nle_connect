package com.nle.shared.service.fleet;

import com.nle.exception.CommonException;
import com.nle.io.entity.DepoFleet;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.entity.Fleet;
import com.nle.io.repository.DepoFleetRepository;
import com.nle.io.repository.DepoOwnerAccountRepository;
import com.nle.io.repository.FleetRepository;
import com.nle.security.SecurityUtils;
import com.nle.ui.model.pageable.PagingResponseModel;
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
public class DepoFleetServiceImpl implements DepoFleetService{

    private final DepoFleetRepository depoFleetRepository;
    private final DepoOwnerAccountRepository depoOwnerAccountRepository;
    private final FleetRepository fleetRepository;

    @Override
    public PagingResponseModel<FleetResponse> getAllFleetsDepo (Pageable pageable){
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (!currentUserLogin.isEmpty()) {
            Page<Fleet> listFleet = depoFleetRepository.getAllDepoFleet(currentUserLogin.get(), pageable);
            return new PagingResponseModel<>(listFleet.map(this::convertFleetToResponse));
        }

        return new PagingResponseModel<>();
    }

    @Override
    public FleetResponse registerFleet(String fleetCode) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (!currentUserLogin.isEmpty()) {
            Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountRepository.findByCompanyEmail(currentUserLogin.get());
            if (depoOwnerAccount.isEmpty())
                throw new CommonException("Cannot find Depo Owner Account!");

            Optional<Fleet> fleet = fleetRepository.getByCode(fleetCode);
            if (fleet.isEmpty())
                throw new CommonException("Cannot find fleet code");

            DepoFleet depoFleet = new DepoFleet();
            depoFleet.setDepoOwnerAccount(depoOwnerAccount.get());
            depoFleet.setFleet(fleet.get());
            DepoFleet entity = depoFleetRepository.save(depoFleet);
            return this.convertFleetToResponse(entity.getFleet());
        }

        return null;
    }

    private FleetResponse convertFleetToResponse (Fleet fleet) {
        FleetResponse fleetResponse = new FleetResponse();
        BeanUtils.copyProperties(fleet, fleetResponse);
        return fleetResponse;
    }
}
