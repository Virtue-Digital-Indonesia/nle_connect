package com.nle.service.gatemove;


import com.nle.config.prop.AppProperties;
import com.nle.constant.AppConstant;
import com.nle.constant.GateMoveSource;
import com.nle.controller.dto.GateMoveCreateDTO;
import com.nle.controller.dto.pageable.PagingResponseModel;
import com.nle.controller.gavemove.GateMoveController;
import com.nle.entity.DepoOwnerAccount;
import com.nle.entity.GateMove;
import com.nle.entity.Media;
import com.nle.exception.CommonException;
import com.nle.exception.ResourceNotFoundException;
import com.nle.mapper.GateMoveMapper;
import com.nle.repository.GateMoveRepository;
import com.nle.repository.MediaRepository;
import com.nle.repository.dto.MoveStatistic;
import com.nle.repository.dto.ShippingLineStatistic;
import com.nle.security.SecurityUtils;
import com.nle.service.depoOwner.DepoOwnerAccountService;
import com.nle.service.dto.GateMoveDTO;
import com.nle.service.s3.S3StoreService;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
    private final GateMoveMapper gateMoveMapper;
    private final AppProperties appProperties;
    private final S3StoreService s3StoreService;
    private final MediaRepository mediaRepository;
    private final DepoOwnerAccountService depoOwnerAccountService;

    @Override
    public GateMoveDTO createGateMove(GateMoveCreateDTO gateMoveCreateDTO) {
        GateMoveDTO gateMoveDTO = new GateMoveDTO();
        BeanUtils.copyProperties(gateMoveCreateDTO, gateMoveDTO);
        GateMove gateMove = gateMoveMapper.toEntity(gateMoveDTO);
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isPresent()) {
            Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountService.findByCompanyEmail(currentUserLogin.get());
            if (depoOwnerAccount.isPresent()) {
                gateMove.setDepoOwnerAccount(depoOwnerAccount.get());
            }
        }
        gateMove.setSource(GateMoveSource.MOBILE);
        gateMove.setStatus(AppConstant.Status.WAITING);
        gateMove.setNleId(UUID.randomUUID().toString());
        gateMove = gateMoveRepository.save(gateMove);
        return gateMoveMapper.toDto(gateMove);
    }

    @Override
    public GateMoveDTO updateGateMove(GateMoveDTO gateMoveDTO) {
        Optional<GateMove> gateMoveOptional = gateMoveRepository.findById(gateMoveDTO.getId());
        if (gateMoveOptional.isEmpty()) {
            throw new ResourceNotFoundException("Gate move with id '" + gateMoveDTO.getId() + "' doesn't exist");
        }
        GateMove gateMove = gateMoveOptional.get();
        BeanUtils.copyProperties(gateMoveDTO, gateMove);
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isPresent()) {
            Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountService.findByCompanyEmail(currentUserLogin.get());
            if (depoOwnerAccount.isPresent()) {
                gateMove.setDepoOwnerAccount(depoOwnerAccount.get());
            }
        }
        GateMove updatedGateMove = gateMoveRepository.save(gateMove);
        return gateMoveMapper.toDto(updatedGateMove);
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
    public PagingResponseModel<GateMoveDTO> findAll(Pageable pageable, LocalDateTime from, LocalDateTime to) {

        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isPresent()) {
            if (from == null) {
                from = EPOCH_TIME;
            }
            if (to == null) {
                to = LocalDateTime.now();
            }
            Page<GateMove> gateMoves = gateMoveRepository.findAllByDepoOwnerAccount_CompanyEmailAndTxDateFormattedBetween(currentUserLogin.get(), from, to, pageable);
            return new PagingResponseModel<>(gateMoves.map(gateMoveMapper::toDto));
        }
        return new PagingResponseModel<>();

    }

    @Override
    public PagingResponseModel<GateMoveDTO> findByType(Pageable pageable) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isPresent()) {
            Page<GateMove> gateMoves = gateMoveRepository.findAllByDepoOwnerAccount_CompanyEmailAndGateMoveType(currentUserLogin.get(), "gate_in", pageable);
            return new PagingResponseModel<>(gateMoves.map(gateMoveMapper::toDto));
        }
        return new PagingResponseModel<>();
    }

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
}
