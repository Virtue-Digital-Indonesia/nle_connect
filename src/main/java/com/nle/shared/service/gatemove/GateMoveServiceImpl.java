package com.nle.shared.service.gatemove;


import com.nle.config.prop.AppProperties;
import com.nle.constant.enums.GateMoveSource;
import com.nle.shared.service.inventory.InventoryService;
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
import com.nle.ui.model.response.count.CountWithFleetManagerResponse;
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
import java.util.*;

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
    private final InventoryService inventoryService;

    @Override
    public CreatedGateMoveResponseDTO createGateMove(CreateGateMoveReqDTO createGateMoveReqDTO, GateMoveSource source) {
        GateMove gateMove = NleUtil.convertToGateMoveEntity(createGateMoveReqDTO, source);
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isPresent()) {
            Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountService.findByCompanyEmail(currentUserLogin.get());
            if (depoOwnerAccount.isPresent()) {
                gateMove.setDepoOwnerAccount(depoOwnerAccount.get());
                GateMove savedEntity = gateMoveRepository.save(gateMove);
                inventoryService.triggerInventory(savedEntity);
                return convertToCreatedGateMoveResponseDTO(savedEntity);
            }
        }
        return new CreatedGateMoveResponseDTO();
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
            List<GateMove> list = getListGateMoveByDuration(duration, email);

            if (!list.isEmpty()) {
                totalAll += list.size();
                String m = "flag";
                Double totalGate = 0.0;
                Double totalGateIN = 0.0;
                Double totalGateOUT = 0.0;
                for (GateMove gateMove : list) {
                    String tx_date = gateMove.getTx_date().substring(0, 10);
                    if (tx_date.equals(m)) {
                        totalGate++;
                    }
                    else {
                        if (totalGate > 0) {
                            listResponses.add(CountListResponse.factory(m, totalGate, totalGateIN, totalGateOUT));
                        }
                        m = tx_date;
                        totalGate = 1.0;
                        totalGateIN = 0.0;
                        totalGateOUT = 0.0;
                    }

                    if (gateMove.getGateMoveType().equals("gate_in"))totalGateIN++;
                    else if (gateMove.getGateMoveType().equals("gate_out"))totalGateOUT++;

                }
                listResponses.add(CountListResponse.factory(m, totalGate, totalGateIN, totalGateOUT));
            }
        }

        return CountResponse.builder()
                .total_moves(totalAll)
                .list_moves(listResponses)
                .build();
    };

    public CountResponse countTotalGateMoveByDurationWithFleetManager(Long duration) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        TreeMap<String, List<CountWithFleetManagerResponse>> hashList = new TreeMap<>();
        Double totalAll = 0.0;
        List<CountListResponse> lists = new ArrayList<>();

        if (currentUserLogin.isPresent()) {
            String email = currentUserLogin.get();
            List<GateMove> listGateMove = getListGateMoveByDuration(duration, email);

            if (!listGateMove.isEmpty()) {
                totalAll += listGateMove.size();
                String m = "flag"; //tx_date
                String m2 = ""; //fleet_manager
                Double totalGate = 0.0;
                Double totalGateIN = 0.0;
                Double totalGateOUT = 0.0;

                for (GateMove gateMove : listGateMove) {
                    String tx_date = gateMove.getTx_date().substring(0, 10);
                    String fleetManager = gateMove.getFleet_manager();
                    if (tx_date.equals(m) && fleetManager.equals(m2)) {
                        totalGate++;
                    }
                    else {
                        if (totalGate > 0) {
                            insertHash(hashList, m, m2, totalGate, totalGateIN, totalGateOUT);
                        }
                        m = tx_date;
                        m2 = gateMove.getFleet_manager();
                        totalGate = 1.0;
                        totalGateIN = 0.0;
                        totalGateOUT = 0.0;
                    }

                    if (gateMove.getGateMoveType().equals("gate_in"))totalGateIN++;
                    else if (gateMove.getGateMoveType().equals("gate_out"))totalGateOUT++;

                }
                insertHash(hashList, m, m2, totalGate, totalGateIN, totalGateOUT);
                hashList.forEach((key, value) -> {
                    for (CountListResponse temp : value) {
                        lists.add(temp);
                    }
                });
            }
        }

//        return hashList;
        return CountResponse
                .builder()
                .total_moves(totalAll)
                .list_moves(lists)
                .build();
    };

    private List<GateMove> getListGateMoveByDuration (Long duration, String email) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime to = now.plusHours(23 - now.getHour()).plusMinutes(59 - now.getMinute()).plusSeconds(59 - now.getSecond());
        LocalDateTime from = now.minusDays(duration).minusHours(now.getHour()).minusMinutes(now.getMinute()).minusSeconds(now.getSecond());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return gateMoveRepository.countTotalGateMoveByDuration(email, from.format(formatter), to.format(formatter));
    }

    private void insertHash(TreeMap<String, List<CountWithFleetManagerResponse>> hashList, String m, String m2, Double totalGate, Double totalGateIN, Double totalGateOUT) {
        //hashing logic with array 2 dimesions
        if (hashList.containsKey(m)) {
            List<CountWithFleetManagerResponse> tempList = hashList.get(m);

            boolean flag = false;
            for (CountWithFleetManagerResponse data : tempList) {
                if (data.getFleet_manager().equals(m2)) {
                    data.setTotal_gateMove(data.getTotal_gateMove() + totalGate);
                    data.setTotal_gate_in(data.getTotal_gate_in() + totalGateIN);
                    data.setTotal_gate_out(data.getTotal_gate_out() + totalGateOUT);
                    flag = true;
                }

                if (flag == true) break;
            }

            if (flag == false)
                tempList.add(CountWithFleetManagerResponse.factory(m2, m, totalGate, totalGateIN, totalGateOUT));

        }
        else if (!hashList.containsKey(m)) {
            List<CountWithFleetManagerResponse> tempList = new ArrayList<>();
            tempList.add(CountWithFleetManagerResponse.factory(m2, m, totalGate, totalGateIN, totalGateOUT));
            hashList.put(m, tempList);
        }
    }



}
