package com.nle.shared.service.applicant;

import com.nle.constant.enums.AccountStatus;
import com.nle.constant.enums.ApprovalStatus;
import com.nle.security.SecurityUtils;
import com.nle.shared.service.xendit.XenditService;
import com.nle.ui.model.ApplicantListReqDTO;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.search.ApplicantSearchRequest;
import com.nle.ui.model.response.ApplicantResponse;
import com.nle.ui.model.response.count.TotalMoves;
import com.nle.ui.model.response.count.CountMovesByDepotResponse;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.exception.BadRequestException;
import com.nle.exception.ResourceNotFoundException;
import com.nle.io.repository.DepoOwnerAccountRepository;
import com.nle.io.repository.GateMoveRepository;
import com.nle.io.repository.dto.GateMovesStatistic;
import com.nle.io.repository.dto.LocationStatistic;
import com.nle.io.repository.dto.ShippingLineStatistic;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class ApplicantServiceImpl implements ApplicantService {
    private final DepoOwnerAccountRepository depoOwnerAccountRepository;
    private final GateMoveRepository gateMoveRepository;
    private final XenditService xenditService;

    private static final LocalDateTime EPOCH_TIME = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);

    @Override
    public PagingResponseModel<ApplicantResponse> findAll(ApplicantListReqDTO applicantListReqDTO, Pageable pageable) {
        List<ApprovalStatus> approvalStatuses = null;
        if (applicantListReqDTO.getFrom() == null) {
            applicantListReqDTO.setFrom(EPOCH_TIME);
        }
        if (applicantListReqDTO.getTo() == null) {
            applicantListReqDTO.setTo(LocalDateTime.now());
        }

        if (applicantListReqDTO.getApprovalStatus() == null) {
            approvalStatuses = List.of(ApprovalStatus.values());
        } else {
            approvalStatuses = List.of(applicantListReqDTO.getApprovalStatus());
        }

        Page<DepoOwnerAccount> depoOwnerAccounts = depoOwnerAccountRepository.filter(applicantListReqDTO.getFrom(),
                applicantListReqDTO.getTo(),
                approvalStatuses, pageable);
        return new PagingResponseModel<>(depoOwnerAccounts.map(this::convertFromEntity));
    }

    @Override
    public ApplicantResponse updateApprovalStatus(Long applicantId, ApprovalStatus approvalStatus) {
        Optional<DepoOwnerAccount> depoOwnerAccountOptional = depoOwnerAccountRepository.findById(applicantId);
        if (depoOwnerAccountOptional.isEmpty()) {
            throw new ResourceNotFoundException("Applicant with id: '" + applicantId + "' doesn't exist");
        }
        DepoOwnerAccount depoOwnerAccount = depoOwnerAccountOptional.get();
        depoOwnerAccount.setApprovalStatus(approvalStatus);
        DepoOwnerAccount updatedDepoOwnerAccount = depoOwnerAccountRepository.save(depoOwnerAccount);
        return convertFromEntity(updatedDepoOwnerAccount);
    }

    @Override
    public ApplicantResponse updateAccountStatus(Long applicantId, AccountStatus accountStatus) {
        Optional<DepoOwnerAccount> depoOwnerAccountOptional = depoOwnerAccountRepository.findById(applicantId);
        if (depoOwnerAccountOptional.isEmpty()) {
            throw new ResourceNotFoundException("Applicant with id: '" + applicantId + "' doesn't exist");
        }
        DepoOwnerAccount depoOwnerAccount = depoOwnerAccountOptional.get();
        depoOwnerAccount.setAccountStatus(accountStatus);
        DepoOwnerAccount updatedDepoOwnerAccount = depoOwnerAccountRepository.save(depoOwnerAccount);
        return convertFromEntity(updatedDepoOwnerAccount);
    }

    public PagingResponseModel<ApplicantResponse> searchByCondition(ApplicantSearchRequest request, Pageable pageable) {
        Optional<String> currentadmin = SecurityUtils.getCurrentUserLogin();
        if (!currentadmin.isEmpty()) {
            Page<DepoOwnerAccount> list = depoOwnerAccountRepository.searchByCondition(request, pageable);
            return new PagingResponseModel<>(list.map(this::convertFromEntity));
        }

        return new PagingResponseModel<>();
    }

    public List<ApplicantResponse> getAllApplicant() {
        List<DepoOwnerAccount> accountList = depoOwnerAccountRepository.findAllByAccountStatus(AccountStatus.ACTIVE);
        List<ApplicantResponse> responseList = new ArrayList<>();

        ArrayList<Long> arrayId = new ArrayList<>();
        arrayId.add(3L);arrayId.add(4L);arrayId.add(5L);arrayId.add(8L);arrayId.add(13L);
        arrayId.add(15L);arrayId.add(20L);arrayId.add(40L);arrayId.add(45L);arrayId.add(48L);
        arrayId.add(54L);arrayId.add(74L);arrayId.add(84L);arrayId.add(85L);arrayId.add(96L);
        arrayId.add(2L);arrayId.add(97L);arrayId.add(98L);

        //error account
        arrayId.add(22L);

        for (DepoOwnerAccount entity : accountList) {

            //sekedar pembatas
            if (entity.getId() > 40)
                break;

            //testing account
            if (arrayId.contains(entity.getId()))
                continue;

            //yang sudah punya
            if (entity.getXenditVaId() != null)
                continue;

            entity.setXenditVaId(xenditService.createXenditAccount(entity));
            System.out.println("entity " + entity.getId() + " " + entity.getXenditVaId());
            DepoOwnerAccount savedEntity = depoOwnerAccountRepository.save(entity);

            responseList.add(this.convertFromEntity(savedEntity));
            responseList.add(this.convertFromEntity(entity));
        }
        return responseList;
    }

    private ApplicantResponse convertFromEntity(DepoOwnerAccount depoOwnerAccount) {
        ApplicantResponse applicantResponse = new ApplicantResponse();
        BeanUtils.copyProperties(depoOwnerAccount, applicantResponse);
        return applicantResponse;
    }

    @Override
    public List<LocationStatistic> countLocation() {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isEmpty())
            throw new BadRequestException("Invalid token");

        return gateMoveRepository.countLocation();
    }

    @Override
    public List<TotalMoves> totalMovesPerDay(int duration) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isEmpty())
            throw new BadRequestException("Invalid token");

        List<TotalMoves> totalMoves = new ArrayList<>();
        DateTimeFormatter formatterWithTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatterWithoutTime = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 0; i < duration; i++) {
            LocalDateTime fromDate = LocalDateTime.now().minus(i, ChronoUnit.DAYS).with(LocalTime.of(0, 0, 0));
            LocalDateTime toDate = LocalDateTime.now().minus(i - 1, ChronoUnit.DAYS).with(LocalTime.of(0, 0, 0));

            List<ShippingLineStatistic> lineStatisticsByDate = countFleetManagerByDate(
                    fromDate.format(formatterWithTime),
                    toDate.format(formatterWithTime));

            Long total = (long) 0;

            if (!lineStatisticsByDate.isEmpty())
                total = countTotalFleetManagerByDate(fromDate.format(formatterWithTime),
                        toDate.format(formatterWithTime));

            TotalMoves tMoves = new TotalMoves(fromDate.format(formatterWithoutTime),
                    total,
                    lineStatisticsByDate);
            totalMoves.add(tMoves);
        }
        return totalMoves;
    }

    @Override
    public List<CountMovesByDepotResponse> countGateMovesByDepotPerDay(int duration) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isEmpty())
            throw new BadRequestException("Invalid token");
        List<CountMovesByDepotResponse> totalGateMoves = new ArrayList<>();

        DateTimeFormatter formatterWithTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatterWithoutTime = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 0; i < duration; i++) {
            LocalDateTime fromDate = LocalDateTime.now().minus(i, ChronoUnit.DAYS).with(LocalTime.of(0, 0, 0));
            LocalDateTime toDate = LocalDateTime.now().minus(i - 1, ChronoUnit.DAYS).with(LocalTime.of(0, 0, 0));

            List<GateMovesStatistic> gateMovesStatistics = countGateMovesByDepot(fromDate.format(formatterWithTime),
                    toDate.format(formatterWithTime));

            CountMovesByDepotResponse countMovesByDepotResponse = new CountMovesByDepotResponse(
                    fromDate.format(formatterWithoutTime), gateMovesStatistics);
            totalGateMoves.add(countMovesByDepotResponse);
        }

        return totalGateMoves;
    }

    @Override
    public List<ShippingLineStatistic> countFleetManager() {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isEmpty())
            throw new BadRequestException("Invalid token");

        return gateMoveRepository.countFleetManager();
    }

    @Override
    public List<GateMovesStatistic> countGateMovesByDepot(String from, String to) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isEmpty())
            throw new BadRequestException("Invalid token");

        return gateMoveRepository.countGateMovesByDepot(from, to);
    }

    @Override
    public List<ShippingLineStatistic> countFleetManagerByDate(String from, String to) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isEmpty())
            throw new BadRequestException("Invalid token");

        return gateMoveRepository.countFleetManagerByDate(from, to);
    }

    @Override
    public Long countTotalFleetManagerByDate(String from, String to) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isEmpty())
            throw new BadRequestException("Invalid token");

        return gateMoveRepository.countTotalFleetManagerByDate(from, to);
    }
}
