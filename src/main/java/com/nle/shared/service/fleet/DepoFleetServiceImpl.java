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
import com.nle.shared.service.item.ItemService;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.DepoFleetRegisterRequest;
import com.nle.ui.model.request.DepoFleetUpdateRequest;
import com.nle.ui.model.request.search.DepoFleetSearchRequest;
import com.nle.ui.model.response.DepoFleetResponse;
import com.nle.util.ConvertResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DepoFleetServiceImpl implements DepoFleetService{

    private final DepoFleetRepository depoFleetRepository;
    private final DepoOwnerAccountRepository depoOwnerAccountRepository;
    private final FleetRepository fleetRepository;
    private final ItemService itemService;

    private final static Map<String,String> mapOfSortField= Map.ofEntries(
            Map.entry("code","fleet.code"),
            Map.entry("fleet_manager_company","fleet.fleet_manager_company"),
            Map.entry("city", "fleet.city"),
            Map.entry("country","fleet.country"),
            Map.entry("id","id"),
            Map.entry("name","name")
    );

    @Override
    public PagingResponseModel<DepoFleetResponse> getAllFleetsDepo (Pageable pageable){
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (!currentUserLogin.isEmpty()) {
            Page<DepoFleet> listFleet = depoFleetRepository.getAllDepoFleet(currentUserLogin.get(), pageable);
            return new PagingResponseModel<>(listFleet.map(ConvertResponseUtil::convertDepoFleetToResponse));
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
            depoFleet.setDeleted(false);
            DepoFleet entity = depoFleetRepository.save(depoFleet);

            DepoFleetResponse depoFleetResponse = ConvertResponseUtil.convertDepoFleetToResponse(entity);
            depoFleetResponse.setItemInfo(itemService.createMultipleItem(depoFleet));

            return depoFleetResponse;
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
        return ConvertResponseUtil.convertDepoFleetToResponse(saved);
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

        depoFleet.get().setDeleted(true);

        depoFleetRepository.save(depoFleet.get());
        return ConvertResponseUtil.convertDepoFleetToResponse(depoFleet.get());
    }

    @Override
    public PagingResponseModel<DepoFleetResponse> searchDepoFleet(DepoFleetSearchRequest depoFleetSearchRequest, Pageable pageable) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (!currentUserLogin.isEmpty()) {
            Pageable customPageable= PageRequest.of(pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(getDirection(pageable),
                            mapOfSortField.get(getSortBy(pageable))));
            Page<DepoFleet> listFleet = depoFleetRepository.searchDepoFleet(currentUserLogin.get(),
                    depoFleetSearchRequest.getName(),
                    depoFleetSearchRequest.getId(),
                    depoFleetSearchRequest.getCode(),
                    depoFleetSearchRequest.getFleetManagerCompany(),
                    depoFleetSearchRequest.getCity(),
                    depoFleetSearchRequest.getCountry(),
                    depoFleetSearchRequest.getGlobalSearch(),
                    customPageable);
            return new PagingResponseModel<>(listFleet.map(ConvertResponseUtil::convertDepoFleetToResponse));
        }

        return new PagingResponseModel<>();
    }

    public Sort.Direction getDirection(Pageable pageable) {
        return pageable.getSort().stream().map(Sort.Order::getDirection).collect(Collectors.toList()).get(0);
    }

    public String getSortBy(Pageable pageable) {
        return pageable.getSort().stream().map(Sort.Order::getProperty).collect(Collectors.toList()).get(0);
    }
}
