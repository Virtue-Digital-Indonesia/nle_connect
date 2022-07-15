package com.nle.controller;

import com.nle.config.AppConfig;
import com.nle.constant.AccountStatus;
import com.nle.constant.VerificationType;
import com.nle.controller.dto.ActiveDto;
import com.nle.controller.dto.CheckExistDto;
import com.nle.controller.dto.DepoOwnerAccountCreateDTO;
import com.nle.controller.dto.JWTToken;
import com.nle.controller.dto.LoginDto;
import com.nle.entity.DepoOwnerAccount;
import com.nle.entity.VerificationToken;
import com.nle.exception.ApiResponse;
import com.nle.exception.ResourceNotFoundException;
import com.nle.repository.DepoOwnerAccountRepository;
import com.nle.repository.VerificationTokenRepository;
import com.nle.security.jwt.JWTFilter;
import com.nle.security.jwt.TokenProvider;
import com.nle.service.DepoOwnerAccountService;
import com.nle.service.VerificationTokenService;
import com.nle.service.dto.DepoOwnerAccountDTO;
import com.nle.service.email.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DepoOwnerController {


    private final Logger log = LoggerFactory.getLogger(DepoOwnerController.class);

    private final DepoOwnerAccountService depoOwnerAccountService;

    private final DepoOwnerAccountRepository depoOwnerAccountRepository;

    private final VerificationTokenRepository verificationTokenRepository;

    private final VerificationTokenService verificationTokenService;

    private final TokenProvider tokenProvider;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final AppConfig appConfig;

    private final EmailService emailService;

    @Operation(description = "Register new Depo owner account", operationId = "createDepoOwnerAccount", summary = "Register new Depo owner account")
    @PostMapping("/register/depo-owner-accounts")
    public ResponseEntity<DepoOwnerAccountDTO> createDepoOwnerAccount(@Valid @RequestBody DepoOwnerAccountCreateDTO depoOwnerAccountDTO)
        throws URISyntaxException {
        log.debug("REST request to save DepoOwnerAccount : {}", depoOwnerAccountDTO);
        DepoOwnerAccountDTO ownerAccountDTO = new DepoOwnerAccountDTO();
        BeanUtils.copyProperties(depoOwnerAccountDTO, ownerAccountDTO);
        DepoOwnerAccountDTO result = depoOwnerAccountService.createDepoOwnerAccount(ownerAccountDTO);
        return ResponseEntity
            .created(new URI("/api/depo-owner-accounts/" + result.getId()))
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

    @Operation(description = "Active Depo owner user by verification token", operationId = "activeDepoOwner", summary = "Active Depo owner user by verification token")
    @GetMapping(value = "/activate/{token}")
    public ResponseEntity<Void> activeDepoOwner(@PathVariable String token) {
        VerificationToken verificationToken = verificationTokenService.checkVerificationToken(token);
        // active user
        DepoOwnerAccount depoOwnerAccount = verificationToken.getDepoOwnerAccount();
        depoOwnerAccount.setAccountStatus(AccountStatus.ACTIVE);
        depoOwnerAccountRepository.save(depoOwnerAccount);
        log.info("Customer " + depoOwnerAccount.getFullName() + " has been active.");
        // remove verification token
        verificationTokenRepository.delete(verificationToken);
        // redirect to login page
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(appConfig.getUrl().getSuccessRedirectUrl())).build();
    }

    @Operation(description = "Authenticate Depo owner user by company email and password", operationId = "authorize", summary = "Authenticate Depo owner user by company email and password")
    @PostMapping("/authenticate")
    public ResponseEntity<JWTToken> authorize(@Valid @RequestBody LoginDto loginDto) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            loginDto.getCompanyEmail(),
            loginDto.getPassword()
        );
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
        return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
    }

    @Operation(description = "Resend activation code via registered email", operationId = "resendActivationCode", summary = "Resend activation code via registered email")
    @GetMapping(value = "/register/resend/{email}")
    public ApiResponse resendActivationCode(@PathVariable String email) {
        Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountService.findByCompanyEmail(email);
        if (depoOwnerAccount.isEmpty()) {
            throw new ResourceNotFoundException("Depo account with email : " + email + " doesn't exist");
        }
        // find all old active token then remove them
        Optional<VerificationToken> oldToken = verificationTokenService.findByEmailAndType(email, VerificationType.ACTIVE_ACCOUNT);
        oldToken.ifPresent(verificationToken -> verificationTokenService.delete(verificationToken.getId()));
        // create new token then send email
        VerificationToken verificationToken = verificationTokenService.createVerificationToken(depoOwnerAccount.get(), VerificationType.ACTIVE_ACCOUNT);
        // send email
        emailService.sendDepoOwnerActiveEmail(depoOwnerAccount.get(), verificationToken.getToken());
        return new ApiResponse(HttpStatus.CREATED, "Resend activation code successfully", "");

    }
}
