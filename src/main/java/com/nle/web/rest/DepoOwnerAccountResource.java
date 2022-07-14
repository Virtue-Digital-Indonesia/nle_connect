package com.nle.web.rest;

import com.nle.constant.AccountStatus;
import com.nle.domain.DepoOwnerAccount;
import com.nle.domain.VerificationToken;
import com.nle.repository.DepoOwnerAccountRepository;
import com.nle.repository.VerificationTokenRepository;
import com.nle.service.DepoOwnerAccountService;
import com.nle.service.VerificationTokenService;
import com.nle.service.dto.DepoOwnerAccountDTO;
import com.nle.web.rest.vm.ActiveDto;
import com.nle.web.rest.vm.CheckExistDto;
import com.nle.web.rest.vm.DepoOwnerAccountCreateDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.jhipster.web.util.HeaderUtil;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * REST controller for managing {@link com.nle.domain.DepoOwnerAccount}.
 */
@RestController
@RequestMapping("/api")
public class DepoOwnerAccountResource {

    private final Logger log = LoggerFactory.getLogger(DepoOwnerAccountResource.class);

    private static final String ENTITY_NAME = "depoOwnerAccount";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DepoOwnerAccountService depoOwnerAccountService;

    private final DepoOwnerAccountRepository depoOwnerAccountRepository;

    private final VerificationTokenRepository verificationTokenRepository;

    private final VerificationTokenService verificationTokenService;

    public DepoOwnerAccountResource(
        DepoOwnerAccountService depoOwnerAccountService,
        DepoOwnerAccountRepository depoOwnerAccountRepository, VerificationTokenRepository verificationTokenRepository, VerificationTokenService verificationTokenService) {
        this.depoOwnerAccountService = depoOwnerAccountService;
        this.depoOwnerAccountRepository = depoOwnerAccountRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.verificationTokenService = verificationTokenService;
    }

    /**
     * {@code POST  /depo-owner-accounts} : Create a new depoOwnerAccount.
     *
     * @param depoOwnerAccountDTO the depoOwnerAccountDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new depoOwnerAccountDTO, or with status {@code 400 (Bad Request)} if the depoOwnerAccount has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Operation(description = "Register new Depo owner account", operationId = "createDepoOwnerAccount", summary = "Register new Depo owner account")
    @PostMapping("/register/depo-owner-accounts")
    public ResponseEntity<DepoOwnerAccountDTO> createDepoOwnerAccount(@Valid @RequestBody DepoOwnerAccountCreateDTO depoOwnerAccountDTO)
        throws URISyntaxException {
        log.debug("REST request to save DepoOwnerAccount : {}", depoOwnerAccountDTO);
        DepoOwnerAccountDTO ownerAccountDTO = new DepoOwnerAccountDTO();
        BeanUtils.copyProperties(depoOwnerAccountDTO, ownerAccountDTO);
        DepoOwnerAccountDTO result = depoOwnerAccountService.save(ownerAccountDTO);
        return ResponseEntity
            .created(new URI("/api/depo-owner-accounts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @Operation(description = "Check depo owner email exist or not", operationId = "checkEmailExist", summary = "Check depo owner email exist or not")
    @GetMapping("/register/depo-owner-accounts/check-email/{email}")
    public ResponseEntity<CheckExistDto> checkEmailExist(@PathVariable String email) {
        log.debug("REST request to check email exist : {}", email);
        Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountService.findByCompanyEmail(email);
        if (depoOwnerAccount.isPresent()) {
            return ResponseEntity.ok().body(new CheckExistDto(true));
        }
        return ResponseEntity.ok().body(new CheckExistDto(false));
    }

    @Operation(description = "Check depo owner phone number exist or not", operationId = "checkPhoneNumberExist", summary = "Check depo owner phone number exist or not")
    @GetMapping("/register/depo-owner-accounts/check-phone/{phoneNumber}")
    public ResponseEntity<CheckExistDto> checkPhoneNumberExist(@PathVariable String phoneNumber) {
        log.debug("REST request to check phone number exist : {}", phoneNumber);
        Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountService.findByPhoneNumber(phoneNumber);
        if (depoOwnerAccount.isPresent()) {
            return ResponseEntity.ok().body(new CheckExistDto(true));
        }
        return ResponseEntity.ok().body(new CheckExistDto(false));
    }

    // active depo owner account API
    @GetMapping(value = "/activate/{token}")
    public ActiveDto activeCustomer(@PathVariable String token) {
        VerificationToken verificationToken = verificationTokenService.checkVerificationToken(token);
        // active user
        DepoOwnerAccount depoOwnerAccount = verificationToken.getDepoOwnerAccount();
        depoOwnerAccount.setAccountStatus(AccountStatus.ACTIVE);
        depoOwnerAccountRepository.save(depoOwnerAccount);
        log.info("Customer " + depoOwnerAccount.getFullName() + " has been active.");
        // remove verification token
        verificationTokenRepository.delete(verificationToken);
        return new ActiveDto(AccountStatus.ACTIVE.name());
    }
}
