package com.nle.shared.service.gatemove;


import com.nle.config.prop.AppProperties;
import com.nle.constant.enums.GateMoveSource;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.CreateGateMoveReqDTO;
import com.nle.ui.model.request.UpdateGateMoveReqDTO;
import com.nle.ui.model.request.search.GateMoveSearchRequest;
import com.nle.ui.model.response.CreatedGateMoveResponseDTO;
import com.nle.ui.model.response.GateMoveResponseDTO;
import com.nle.ui.model.response.UpdatedGateMoveResponseDTO;
import com.nle.ui.controller.gavemove.GateMoveController;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.entity.GateMove;
import com.nle.io.entity.Media;
import com.nle.exception.CommonException;
import com.nle.exception.ResourceNotFoundException;
import com.nle.io.repository.GateMoveRepository;
import com.nle.io.repository.MediaRepository;
import com.nle.io.repository.dto.MoveStatistic;
import com.nle.io.repository.dto.ShippingLineStatistic;
import com.nle.security.SecurityUtils;
import com.nle.shared.service.depoOwner.DepoOwnerAccountService;
import com.nle.shared.service.s3.S3StoreService;
import com.nle.ui.model.response.count.CountListResponse;
import com.nle.ui.model.response.count.CountResponse;
import com.nle.util.NleUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.nle.constant.AppConstant.SPLASH;
import static org.apache.http.entity.ContentType.IMAGE_BMP;
import static org.apache.http.entity.ContentType.IMAGE_GIF;
import static org.apache.http.entity.ContentType.IMAGE_JPEG;
import static org.apache.http.entity.ContentType.IMAGE_PNG;

@RequiredArgsConstructor
@Service
@Transactional
public class GateMoveServiceImpl implements GateMoveService {
    private final Logger log = LoggerFactory.getLogger(GateMoveController.class);
    private static final LocalDateTime EPOCH_TIME = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);

    private final GateMoveRepository gateMoveRepository;
    private final AppProperties appProperties;
    private final S3StoreService s3StoreService;
    private final MediaRepository mediaRepository;
    private final DepoOwnerAccountService depoOwnerAccountService;

    @Override
    public CreatedGateMoveResponseDTO createGateMove(CreateGateMoveReqDTO createGateMoveReqDTO, GateMoveSource source) {
        GateMove gateMove = NleUtil.convertToGateMoveEntity(createGateMoveReqDTO, source);
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isPresent()) {
            Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountService.findByCompanyEmail(currentUserLogin.get());
            if (depoOwnerAccount.isPresent()) {
                gateMove.setDepoOwnerAccount(depoOwnerAccount.get());
            }
        }
        gateMove = gateMoveRepository.save(gateMove);
        return convertToCreatedGateMoveResponseDTO(gateMove);
    }

    @Override
    public UpdatedGateMoveResponseDTO updateGateMove(UpdateGateMoveReqDTO updateGateMoveReqDTO, GateMoveSource source) {
        Optional<GateMove> gateMoveOptional = gateMoveRepository.findById(updateGateMoveReqDTO.getId());
        if (gateMoveOptional.isEmpty()) {
            throw new ResourceNotFoundException("Gate move with id '" + updateGateMoveReqDTO.getId() + "' doesn't exist");
        }
        GateMove gateMove = NleUtil.convertToGateMoveEntity(updateGateMoveReqDTO, source);
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isPresent()) {
            Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountService.findByCompanyEmail(currentUserLogin.get());
            depoOwnerAccount.ifPresent(gateMove::setDepoOwnerAccount);
        }
        GateMove updatedGateMove = gateMoveRepository.save(gateMove);
        UpdatedGateMoveResponseDTO updatedGateMoveResponseDTO = new UpdatedGateMoveResponseDTO();
        BeanUtils.copyProperties(updatedGateMove, updatedGateMoveResponseDTO);
        return updatedGateMoveResponseDTO;
    }

    @Override
    public void uploadFile(MultipartFile[] files, Long gateMoveId) {
        Optional<GateMove> gateMoveOptional = gateMoveRepository.findById(gateMoveId);
        if (gateMoveOptional.isEmpty()) {
            throw new ResourceNotFoundException("Gate move with id '" + gateMoveId + "' doesn't exist");
        }
        // upload file to S3
        for (MultipartFile file : files) {
            try {
                String s3FilePath = this.uploadFileToS3(file);
                Media media = new Media();
                media.setMediaType(file.getContentType());
                media.setFileName(file.getOriginalFilename());
                media.setFileSize(file.getSize());
                media.setFilePath(s3FilePath);
                media.setGateMove(gateMoveOptional.get());
                mediaRepository.save(media);
            } catch (Exception e) {
                log.error("Error while uploading file {} with error {}", file.getOriginalFilename(), e.getMessage());
            }
        }
    }

    @Override
    public PagingResponseModel<GateMoveResponseDTO> findAll(Pageable pageable, LocalDateTime from, LocalDateTime to) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isPresent()) {
            if (from == null) {
                from = EPOCH_TIME;
            }
            if (to == null) {
                to = LocalDateTime.now();
            }
            Page<GateMove> gateMoves = gateMoveRepository.findAllByDepoOwnerAccount_CompanyEmailAndTxDateFormattedBetween(currentUserLogin.get(), from, to, pageable);
            return new PagingResponseModel<>(gateMoves.map(this::convertToGateMoveResponseDTO));
        }
        return new PagingResponseModel<>();

    }

    @Override
    public PagingResponseModel<GateMoveResponseDTO> findByType(Pageable pageable) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isPresent()) {
            Page<GateMove> gateMoves = gateMoveRepository.findAllByDepoOwnerAccount_CompanyEmailAndGateMoveType(currentUserLogin.get(), "gate_in", pageable);
            return new PagingResponseModel<>(gateMoves.map(this::convertToGateMoveResponseDTO));
        }
        return new PagingResponseModel<>();
    }

    @Override
    public PagingResponseModel<GateMoveResponseDTO> searchByCondition(Pageable pageable, GateMoveSearchRequest request){
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isPresent())  {
            Page<GateMove> listResults = gateMoveRepository.searchByCondition(
                    currentUserLogin.get(),
                    pageable,
                    request);
            return new PagingResponseModel<>(listResults.map(this::convertToGateMoveResponseDTO));
        }
        return new PagingResponseModel<>();
    };

    @Override
    public List<MoveStatistic> countTotalGateMoveByType() {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isPresent()) {
            return gateMoveRepository.countTotalGateMoveByType(currentUserLogin.get());
        }
        return new ArrayList<>();
    }

    @Override
    public List<ShippingLineStatistic> countTotalGateMoveByShippingLine() {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isPresent()) {
            return gateMoveRepository.countTotalGateMoveByShippingLine(currentUserLogin.get());
        }
        return new ArrayList<>();
    }

    private String uploadFileToS3(MultipartFile file) {
        if (file.isEmpty()) {
            throw new CommonException("Cannot upload empty file");
        }
        //Check if the file is an image
        if (!Arrays.asList(IMAGE_PNG.getMimeType(),
            IMAGE_BMP.getMimeType(),
            IMAGE_GIF.getMimeType(),
            IMAGE_JPEG.getMimeType()).contains(file.getContentType())) {
            throw new CommonException("File uploaded is not an image");
        }
        //get file metadata
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));
        String s3Path = generateUniquePath(appProperties.getSecurity().getAws().getS3().getBucketName());
        try {
            s3StoreService.upload(s3Path, file.getOriginalFilename(), Optional.of(metadata), file.getInputStream());
            return s3Path + file.getOriginalFilename();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to upload file", e);
        }
    }

    private String generateUniquePath(String bucketName) {
        DateFormat writeFormat = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss");
        return bucketName + SPLASH + writeFormat.format(new Date());
    }

    private CreatedGateMoveResponseDTO convertToCreatedGateMoveResponseDTO(GateMove gateMove) {
        CreatedGateMoveResponseDTO createdGateMoveResponseDTO = new CreatedGateMoveResponseDTO();
        BeanUtils.copyProperties(gateMove, createdGateMoveResponseDTO);
        if (gateMove.getClean()) {
            createdGateMoveResponseDTO.setClean("yes");
        } else {
            createdGateMoveResponseDTO.setClean("no");
        }
        return createdGateMoveResponseDTO;
    }

    private GateMoveResponseDTO convertToGateMoveResponseDTO(GateMove gateMove) {
        GateMoveResponseDTO gateMoveResponseDTO = new GateMoveResponseDTO();
        BeanUtils.copyProperties(gateMove, gateMoveResponseDTO);
        if (gateMove.getClean()) {
            gateMoveResponseDTO.setClean("yes");
        } else {
            gateMoveResponseDTO.setClean("no");
        }
        return gateMoveResponseDTO;
    }

    public CountResponse countTotalGateMoveByDuration(Long duration) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        Double totalAll = 0.0;
        List<CountListResponse> listResponses = new ArrayList<>();

        if (currentUserLogin.isPresent()) {
            String email = currentUserLogin.get();

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime to = now.plusHours(23 - now.getHour()).plusMinutes(59 - now.getMinute()).plusSeconds(59 - now.getSecond());
            LocalDateTime from = now.minusDays(duration).minusHours(now.getHour()).minusMinutes(now.getMinute()).minusSeconds(now.getSecond());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            System.out.println(from.format(formatter));
            System.out.println(to.format(formatter));

            List<GateMove> list = gateMoveRepository.countTotalGateMoveByDuration(email, from.format(formatter), to.format(formatter));
            if (!list.isEmpty()) {
                totalAll += list.size();
                String m = "flag";
                Double totalGate = 0.0;
                Double totalGateIN = 0.0;
                Double totalGateOUT = 0.0;
                for (GateMove gateMove : list) {
                    String tx_date = gateMove.getTx_date().substring(0, 10);
                    System.out.print(tx_date + " ");
                    if (tx_date.equals(m)) {
                        totalGate++;
                    }
                    else {

                        if (totalGate > 0) {
                            CountListResponse response = CountListResponse.builder()
                                    .tx_date(m)
                                    .total_gateMove(totalGate)
                                    .total_gate_in(totalGateIN)
                                    .total_gate_out(totalGateOUT)
                                    .build();
                            listResponses.add(response);
                        }
                        m = tx_date;
                        totalGate = 1.0;
                        totalGateIN = 0.0;
                        totalGateOUT = 0.0;
                    }

                    System.out.println(gateMove.getGateMoveType());
                    if (gateMove.getGateMoveType().equals("gate_in"))totalGateIN++;
                    else if (gateMove.getGateMoveType().equals("gate_out"))totalGateOUT++;

                }

                CountListResponse response = CountListResponse.builder()
                        .tx_date(m)
                        .total_gateMove(totalGate)
                        .total_gate_in(totalGateIN)
                        .total_gate_out(totalGateOUT)
                        .build();
                listResponses.add(response);
            }
        }

        return CountResponse.builder()
                .total_moves(totalAll)
                .list_moves(listResponses)
                .build();
    };

}
