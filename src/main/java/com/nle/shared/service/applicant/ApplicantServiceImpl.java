package com.nle.shared.service.applicant;

import com.nle.constant.enums.AccountStatus;
import com.nle.constant.enums.ApprovalStatus;
import com.nle.security.SecurityUtils;
import com.nle.ui.model.ApplicantListReqDTO;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.search.ApplicantSearchRequest;
import com.nle.ui.model.response.ApplicantResponse;
import com.nle.ui.model.response.count.MovesDownload;
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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
        for (DepoOwnerAccount entity : accountList) {
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
    public List<TotalMoves> totalMovesPerDay(int duration, String location) {
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
                    toDate.format(formatterWithTime), location);

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
    public List<CountMovesByDepotResponse> countGateMovesByDepotPerDay(int duration, String loc) {
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
                    toDate.format(formatterWithTime), loc);

            CountMovesByDepotResponse countMovesByDepotResponse = new CountMovesByDepotResponse(
                    fromDate.format(formatterWithoutTime), gateMovesStatistics);

            totalGateMoves.add(countMovesByDepotResponse);
        }

        return totalGateMoves;
    }

    @Override
    public ByteArrayInputStream downloadCountGateMovesByDepot(int duration, String location) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isEmpty())
            throw new BadRequestException("Invalid token");
        List<MovesDownload> movesDownloads = new ArrayList<>();

        DateTimeFormatter formatterWithTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatterWithoutTime = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Long totalGateMove = 0L;
        Long totalGateIn = 0L;
        Long totalGateOut = 0L;

        for (int i = 0; i < duration; i++) {
            LocalDateTime fromDate = LocalDateTime.now().minus(i, ChronoUnit.DAYS).with(LocalTime.of(0, 0, 0));
            LocalDateTime toDate = LocalDateTime.now().minus(i - 1, ChronoUnit.DAYS).with(LocalTime.of(0, 0, 0));

            List<GateMovesStatistic> gateMovesStatistics = countGateMovesByDepot(fromDate.format(formatterWithTime),
                    toDate.format(formatterWithTime), location);

            MovesDownload movesDownload = new MovesDownload();
            //Set data to send to excell
            if (!gateMovesStatistics.isEmpty()){
                for (GateMovesStatistic gateMovesStatitic : gateMovesStatistics) {
                            totalGateMove = totalGateMove + gateMovesStatitic.getGate_moves();
                            totalGateIn = totalGateIn + gateMovesStatitic.getGate_in();
                            totalGateOut = totalGateOut + gateMovesStatitic.getGate_out();
                            movesDownload.setGate_in(totalGateIn);
                            movesDownload.setGate_out(totalGateOut);
                            movesDownload.setTotal(totalGateMove);
                            movesDownload.setTx_date(fromDate.format(formatterWithoutTime));
                }
            } else {
                movesDownload.setGate_in(0L);
                movesDownload.setGate_out(0L);
                movesDownload.setTotal(0L);
                movesDownload.setTx_date(fromDate.format(formatterWithoutTime));
            }

                movesDownloads.add(movesDownload);
        }

        //Method for input data to excell
        ByteArrayInputStream inputDataToExcell = this.inputDataToExcell(movesDownloads);
        return inputDataToExcell;
    }

    private ByteArrayInputStream inputDataToExcell(List<MovesDownload> movesDownloads) {
        String type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        String[] headers = { "Date", "Total Gate Move", "Total Gate In", "Total Gate Out" };
        String sheet = "Gatemove";

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheetCreate = workbook.createSheet(sheet);
            Row headerRow = sheetCreate.createRow(0);

            for (int col = 0; col < headers.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(headers[col]);
            }

            int rowIdx = 1;
            for (MovesDownload movesDownload : movesDownloads ) {
                Row row = sheetCreate.createRow(rowIdx++);
                row.createCell(0).setCellValue(movesDownload.getTx_date());
                row.createCell(1).setCellValue(movesDownload.getTotal());
                row.createCell(2).setCellValue(movesDownload.getGate_in());
                row.createCell(3).setCellValue(movesDownload.getGate_out());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }

    @Override
    public List<ShippingLineStatistic> countFleetManager() {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isEmpty())
            throw new BadRequestException("Invalid token");

        return gateMoveRepository.countFleetManager();
    }

    @Override
    public List<GateMovesStatistic> countGateMovesByDepot(String from, String to, String loc) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isEmpty())
            throw new BadRequestException("Invalid token");
        String location = null;
        if (loc.equalsIgnoreCase("all")){
            location = null;
        } else {
            location = loc;
        }

        return gateMoveRepository.countGateMovesByDepot(from, to, location);
    }

    @Override
    public List<ShippingLineStatistic> countFleetManagerByDate(String from, String to, String loc) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isEmpty())
            throw new BadRequestException("Invalid token");
        String location = null;
        if (loc.equalsIgnoreCase("all")){
            location = null;
        } else {
            location = loc;
        }

        return gateMoveRepository.countFleetManagerByDate(from, to, location);
    }

    @Override
    public Long countTotalFleetManagerByDate(String from, String to) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isEmpty())
            throw new BadRequestException("Invalid token");

        return gateMoveRepository.countTotalFleetManagerByDate(from, to);
    }
}
