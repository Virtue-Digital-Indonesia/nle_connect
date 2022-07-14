package com.nle.service.impl;

import com.nle.domain.DepoOwnerAccount;
import com.nle.repository.DepoOwnerAccountRepository;
import com.nle.service.DepoOwnerAccountService;
import com.nle.service.dto.DepoOwnerAccountDTO;
import com.nle.service.mapper.DepoOwnerAccountMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link DepoOwnerAccount}.
 */
@Service
@Transactional
public class DepoOwnerAccountServiceImpl implements DepoOwnerAccountService {

    private final Logger log = LoggerFactory.getLogger(DepoOwnerAccountServiceImpl.class);

    private final DepoOwnerAccountRepository depoOwnerAccountRepository;

    private final DepoOwnerAccountMapper depoOwnerAccountMapper;

    public DepoOwnerAccountServiceImpl(
        DepoOwnerAccountRepository depoOwnerAccountRepository,
        DepoOwnerAccountMapper depoOwnerAccountMapper
    ) {
        this.depoOwnerAccountRepository = depoOwnerAccountRepository;
        this.depoOwnerAccountMapper = depoOwnerAccountMapper;
    }

    @Override
    public DepoOwnerAccountDTO save(DepoOwnerAccountDTO depoOwnerAccountDTO) {
        log.debug("Request to save DepoOwnerAccount : {}", depoOwnerAccountDTO);
        DepoOwnerAccount depoOwnerAccount = depoOwnerAccountMapper.toEntity(depoOwnerAccountDTO);
        depoOwnerAccount = depoOwnerAccountRepository.save(depoOwnerAccount);
        return depoOwnerAccountMapper.toDto(depoOwnerAccount);
    }

    @Override
    public DepoOwnerAccountDTO update(DepoOwnerAccountDTO depoOwnerAccountDTO) {
        log.debug("Request to save DepoOwnerAccount : {}", depoOwnerAccountDTO);
        DepoOwnerAccount depoOwnerAccount = depoOwnerAccountMapper.toEntity(depoOwnerAccountDTO);
        depoOwnerAccount = depoOwnerAccountRepository.save(depoOwnerAccount);
        return depoOwnerAccountMapper.toDto(depoOwnerAccount);
    }

    @Override
    public Optional<DepoOwnerAccountDTO> partialUpdate(DepoOwnerAccountDTO depoOwnerAccountDTO) {
        log.debug("Request to partially update DepoOwnerAccount : {}", depoOwnerAccountDTO);

        return depoOwnerAccountRepository
            .findById(depoOwnerAccountDTO.getId())
            .map(existingDepoOwnerAccount -> {
                depoOwnerAccountMapper.partialUpdate(existingDepoOwnerAccount, depoOwnerAccountDTO);

                return existingDepoOwnerAccount;
            })
            .map(depoOwnerAccountRepository::save)
            .map(depoOwnerAccountMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DepoOwnerAccountDTO> findAll(Pageable pageable) {
        log.debug("Request to get all DepoOwnerAccounts");
        return depoOwnerAccountRepository.findAll(pageable).map(depoOwnerAccountMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DepoOwnerAccountDTO> findOne(Long id) {
        log.debug("Request to get DepoOwnerAccount : {}", id);
        return depoOwnerAccountRepository.findById(id).map(depoOwnerAccountMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete DepoOwnerAccount : {}", id);
        depoOwnerAccountRepository.deleteById(id);
    }
}
