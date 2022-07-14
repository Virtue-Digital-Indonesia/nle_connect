package com.nle.service.impl;

import com.nle.constant.VerificationType;
import com.nle.domain.DepoOwnerAccount;
import com.nle.domain.VerificationToken;
import com.nle.repository.VerificationTokenRepository;
import com.nle.service.VerificationTokenService;
import com.nle.service.dto.VerificationTokenDTO;
import com.nle.service.mapper.VerificationTokenMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link VerificationToken}.
 */
@Service
@Transactional
public class VerificationTokenServiceImpl implements VerificationTokenService {

    private final Logger log = LoggerFactory.getLogger(VerificationTokenServiceImpl.class);

    private final VerificationTokenRepository verificationTokenRepository;

    private final VerificationTokenMapper verificationTokenMapper;

    public VerificationTokenServiceImpl(
        VerificationTokenRepository verificationTokenRepository,
        VerificationTokenMapper verificationTokenMapper
    ) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.verificationTokenMapper = verificationTokenMapper;
    }

    @Override
    public VerificationTokenDTO save(VerificationTokenDTO verificationTokenDTO) {
        log.debug("Request to save VerificationToken : {}", verificationTokenDTO);
        VerificationToken verificationToken = verificationTokenMapper.toEntity(verificationTokenDTO);
        verificationToken = verificationTokenRepository.save(verificationToken);
        return verificationTokenMapper.toDto(verificationToken);
    }

    @Override
    public VerificationTokenDTO update(VerificationTokenDTO verificationTokenDTO) {
        log.debug("Request to save VerificationToken : {}", verificationTokenDTO);
        VerificationToken verificationToken = verificationTokenMapper.toEntity(verificationTokenDTO);
        verificationToken = verificationTokenRepository.save(verificationToken);
        return verificationTokenMapper.toDto(verificationToken);
    }

    @Override
    public Optional<VerificationTokenDTO> partialUpdate(VerificationTokenDTO verificationTokenDTO) {
        log.debug("Request to partially update VerificationToken : {}", verificationTokenDTO);

        return verificationTokenRepository
            .findById(verificationTokenDTO.getId())
            .map(existingVerificationToken -> {
                verificationTokenMapper.partialUpdate(existingVerificationToken, verificationTokenDTO);

                return existingVerificationToken;
            })
            .map(verificationTokenRepository::save)
            .map(verificationTokenMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VerificationTokenDTO> findAll() {
        log.debug("Request to get all VerificationTokens");
        return verificationTokenRepository
            .findAll()
            .stream()
            .map(verificationTokenMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VerificationTokenDTO> findOne(Long id) {
        log.debug("Request to get VerificationToken : {}", id);
        return verificationTokenRepository.findById(id).map(verificationTokenMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete VerificationToken : {}", id);
        verificationTokenRepository.deleteById(id);
    }

    @Override
    public VerificationToken createVerificationToken(DepoOwnerAccount depoOwnerAccount, VerificationType type) {
        final String token = UUID.randomUUID().toString();
        // plus 7 days before token expired
        LocalDateTime expiredDate = LocalDateTime.now().plusDays(7);
        // create new VerificationToken
        final VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setExpiryDate(expiredDate);
        verificationToken.tokenType(type);
        verificationToken.setDepoOwnerAccount(depoOwnerAccount);
        // save VerificationToken
        return verificationTokenRepository.save(verificationToken);
    }
}
