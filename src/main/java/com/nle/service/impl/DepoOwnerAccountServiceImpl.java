package com.nle.service.impl;

import com.nle.constant.AccountStatus;
import com.nle.constant.VerificationType;
import com.nle.domain.DepoOwnerAccount;
import com.nle.domain.VerificationToken;
import com.nle.exception.EmailAlreadyUsedException;
import com.nle.exception.PhoneNumberAlreadyUsedException;
import com.nle.repository.DepoOwnerAccountRepository;
import com.nle.service.DepoOwnerAccountService;
import com.nle.service.VerificationTokenService;
import com.nle.service.dto.DepoOwnerAccountDTO;
import com.nle.service.mapper.DepoOwnerAccountMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final PasswordEncoder passwordEncoder;

    private final VerificationTokenService verificationTokenService;

    public DepoOwnerAccountServiceImpl(
        DepoOwnerAccountRepository depoOwnerAccountRepository,
        DepoOwnerAccountMapper depoOwnerAccountMapper,
        PasswordEncoder passwordEncoder, VerificationTokenService verificationTokenService) {
        this.depoOwnerAccountRepository = depoOwnerAccountRepository;
        this.depoOwnerAccountMapper = depoOwnerAccountMapper;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenService = verificationTokenService;
    }

    @Override
    public DepoOwnerAccountDTO save(DepoOwnerAccountDTO depoOwnerAccountDTO) {
        log.debug("Request to save DepoOwnerAccount : {}", depoOwnerAccountDTO);
        // check email exist or not
        Optional<DepoOwnerAccount> companyEmail = this.findByCompanyEmail(depoOwnerAccountDTO.getCompanyEmail());
        if (companyEmail.isPresent()) {
            throw new EmailAlreadyUsedException();
        }
        // check phone number
        Optional<DepoOwnerAccount> phoneNumber = this.findByPhoneNumber(depoOwnerAccountDTO.getPhoneNumber());
        if (phoneNumber.isPresent()) {
            throw new PhoneNumberAlreadyUsedException();
        }
        // encoded password
        depoOwnerAccountDTO.setPassword(passwordEncoder.encode(depoOwnerAccountDTO.getPassword()));
        // map to entity
        DepoOwnerAccount depoOwnerAccount = depoOwnerAccountMapper.toEntity(depoOwnerAccountDTO);
        depoOwnerAccount.setAccountStatus(AccountStatus.INACTIVE);
        // save to db
        depoOwnerAccount = depoOwnerAccountRepository.save(depoOwnerAccount);
        VerificationToken verificationToken = verificationTokenService.createVerificationToken(depoOwnerAccount, VerificationType.ACTIVE_ACCOUNT);
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

    @Override
    public Optional<DepoOwnerAccount> findByCompanyEmail(String companyEmail) {
        return depoOwnerAccountRepository.findByCompanyEmail(companyEmail);
    }

    @Override
    public Optional<DepoOwnerAccount> findByPhoneNumber(String phoneNumber) {
        return depoOwnerAccountRepository.findByPhoneNumber(phoneNumber);
    }
}
