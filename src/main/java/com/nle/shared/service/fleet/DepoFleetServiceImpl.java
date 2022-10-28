package com.nle.shared.service.fleet;

import com.nle.exception.BadRequestException;
import com.nle.exception.CommonException;
import com.nle.io.entity.DepoFleet;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.entity.Fleet;
import com.nle.io.repository.DepoFleetRepository;
import com.nle.io.repository.DepoOwnerAccountRepository;
import com.nle.io.repository.FleetRepository;
import com.nle.security.SecurityUtils;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.DepoFleetRegisterRequest;
import com.nle.ui.model.request.DepoFleetUpdateRequest;
import com.nle.ui.model.response.DepoFleetResponse;
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
    public PagingResponseModel<DepoFleetResponse> getAllFleetsDepo (Pageable pageable){
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (!currentUserLogin.isEmpty()) {
            Page<DepoFleet> listFleet = depoFleetRepository.getAllDepoFleet(currentUserLogin.get(), pageable);
            return new PagingResponseModel<>(listFleet.map(this::convertFleetToResponse));
        }

        return new PagingResponseModel<>();
    }

    @Override
    public DepoFleetResponse registerFleet(DepoFleetRegisterRequest request) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (!currentUserLogin.isEmpty()) {
            Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountRepository.findByCompanyEmail(currentUserLogin.get());
            if (depoOwnerAccount.isEmpty())
                throw new CommonException("Cannot find Depo Owner Account!");

            Optional<Fleet> fleet = fleetRepository.getByCode(request.getFleet_code());
            if (fleet.isEmpty())
                throw new CommonException("Cannot find fleet code");

            Optional<DepoFleet> flagFleet = depoFleetRepository.getFleetInDepo(currentUserLogin.get(), request.getFleet_code());
            if (!flagFleet.isEmpty())
                throw new BadRequestException("Fleet is already registered in depo!");

            DepoFleet depoFleet = new DepoFleet();
            depoFleet.setDepoOwnerAccount(depoOwnerAccount.get());
            depoFleet.setFleet(fleet.get());

            if(request.getName() == null || request.getName().trim().isEmpty()) {
                depoFleet.setName(fleet.get().getFleet_manager_company());
            }
            else {
                depoFleet.setName(request.getName());
            }

            DepoFleet entity = depoFleetRepository.save(depoFleet);
            return this.convertFleetToResponse(entity);
        }

        return null;
    }

    @Override
    public DepoFleetResponse updateRegisterFleet(DepoFleetUpdateRequest request) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isEmpty()) throw new BadRequestException("You are not log in");
        else {
            Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountRepository.findByCompanyEmail(currentUserLogin.get());
            if (depoOwnerAccount.isEmpty()) throw new CommonException("Cannot find Depo Owner Account!");
        }

        Optional<DepoFleet> optionalDepoFleet = depoFleetRepository.findById(request.getId());
        if (optionalDepoFleet.isEmpty())
            throw new CommonException("Cannot find depo-fleet in this depo");

        Optional<Fleet> fleet = fleetRepository.getByCode(request.getFleet_code());
        if (fleet.isEmpty())
            throw new CommonException("Cannot find fleet code");

        Optional<DepoFleet> flagSame = depoFleetRepository.getFleetInDepo(currentUserLogin.get(), request.getFleet_code());
        if (!flagSame.isEmpty() && flagSame.get().getId() != request.getId())
            throw new BadRequestException("Fleet is already registered in different name");

        DepoFleet depoFleet = optionalDepoFleet.get();
        depoFleet.setFleet(fleet.get());

        if(request.getName() == null || request.getName().trim().isEmpty()) {
            depoFleet.setName(fleet.get().getFleet_manager_company());
        }
        else {
            depoFleet.setName(request.getName());
        }

        DepoFleet saved = depoFleetRepository.save(depoFleet);
        return this.convertFleetToResponse(saved);
    }

    @Override
    public DepoFleetResponse deleteDepoFleet(String fleet_code) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isEmpty()) throw new BadRequestException("You are not log in");
        else {
            Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountRepository.findByCompanyEmail(currentUserLogin.get());
            if (depoOwnerAccount.isEmpty()) throw new CommonException("Cannot find Depo Owner Account!");
        }

        Optional<DepoFleet> depoFleet = depoFleetRepository.getFleetInDepo(currentUserLogin.get(), fleet_code);
        if (depoFleet.isEmpty()) throw new CommonException("Cannot find depo-fleet in this depo");

        depoFleetRepository.delete(depoFleet.get());
        return this.convertFleetToResponse(depoFleet.get());
    }

    private DepoFleetResponse convertFleetToResponse (DepoFleet depoFleet) {
        DepoFleetResponse depoFleetResponse = new DepoFleetResponse();
        BeanUtils.copyProperties(depoFleet.getFleet(), depoFleetResponse);
        depoFleetResponse.setId(depoFleet.getId());
        depoFleetResponse.setName(depoFleet.getName());
        return depoFleetResponse;
    }
}