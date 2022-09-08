package com.nle.ui.controller.depo;

import com.nle.config.prop.AppProperties;
import com.nle.constant.AppConstant;
import com.nle.constant.enums.VerificationType;
import com.nle.ui.model.ActiveDto;
import com.nle.ui.model.CheckExistDto;
import com.nle.ui.model.JWTToken;
import com.nle.ui.model.LoginDto;
import com.nle.ui.model.request.DepoOwnerAccountCreateDTO;
import com.nle.ui.model.request.DepoWorkerApproveReqDto;
import com.nle.ui.model.request.DepoWorkerInvitationReqDto;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.entity.VerificationToken;
import com.nle.exception.ApiResponse;
import com.nle.exception.ResourceNotFoundException;
import com.nle.security.jwt.JWTFilter;
import com.nle.security.jwt.TokenProvider;
import com.nle.shared.service.VerificationTokenService;
import com.nle.shared.service.depoOwner.DepoOwnerAccountService;
import com.nle.shared.service.depoWorker.DepoWorkerAccountService;
import com.nle.shared.dto.DepoOwnerAccountDTO;
import com.nle.shared.dto.DepoOwnerAccountProfileDTO;
import com.nle.shared.service.email.EmailService;
import com.nle.ui.model.request.ForgotPasswordRequest;
import com.nle.util.DecodeUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.nle.constant.AppConstant.VerificationStatus.ALREADY_ACTIVE;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DepoOwnerController {

    private final Logger log = LoggerFactory.getLogger(DepoOwnerController.class);

    private final DepoOwnerAccountService depoOwnerAccountService;

    private final VerificationTokenService verificationTokenService;

    private final TokenProvider tokenProvider;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final AppProperties appProperties;

    private final EmailService emailService;

    private final DepoWorkerAccountService depoWorkerAccountService;

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
    public ResponseEntity<ActiveDto> activeDepoOwner(@PathVariable String token) {
        ActiveDto activeDto = depoOwnerAccountService.activeDepoOwnerAccount(token);
        if (ALREADY_ACTIVE.equals(activeDto.getActiveStatus())) {
            return ResponseEntity.ok(activeDto);
        }
        // redirect to login page
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(appProperties.getUrl().getSuccessRedirectUrl())).build();
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
            throw new ResourceNotFoundException("Depo owner account with email: '" + email + "' doesn't exist");
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

    @Operation(description = "Send invitation email to worker", operationId = "sendInvitation", summary = "Send invitation email to worker")
    @PostMapping(value = "/send-invitation")
    @SecurityRequirement(name = "nleapi")
    public ResponseEntity<Void> sendInvitation(@RequestBody @Valid DepoWorkerInvitationReqDto depoWorkerInvitationReqDto) {
        depoWorkerAccountService.sendInvitationEmail(depoWorkerInvitationReqDto.getEmail());
        return ResponseEntity.ok().build();
    }

    @Operation(description = "Approve depo worker join request", operationId = "approveDepoWorkerJoinRequest", summary = "Approve depo worker join request")
    @PostMapping(value = "/approve-join-request")
    @SecurityRequirement(name = "nleapi")
    public ResponseEntity<ActiveDto> approveDepoWorkerJoinRequest(@RequestBody @Valid DepoWorkerApproveReqDto depoWorkerApproveReqDto) {
        depoWorkerAccountService.approveJoinRequest(depoWorkerApproveReqDto);
        return ResponseEntity.ok(new ActiveDto(AppConstant.VerificationStatus.ACTIVE, "Depo Worker account is active."));
    }

    @Operation(description = "Delete depo worker join request", operationId = "deleteDepoWorkerJoinRequest", summary = "Delete depo worker join request")
    @DeleteMapping(value = "/approve-join-request/{androidId}")
    @SecurityRequirement(name = "nleapi")
    public ResponseEntity<Void> deleteDepoWorkerJoinRequest(@PathVariable String androidId) {
        depoWorkerAccountService.deleteJoinRequest(androidId);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Get Depo Owner Profile", operationId = "getDepoOwnerAccountProfile", summary = "Get Depo Owner Profile")
    @GetMapping(value = "/profile")
    @SecurityRequirement(name = "nleapi")
    public ResponseEntity<DepoOwnerAccountProfileDTO> getDepoOwnerAccountProfile() {
        return ResponseEntity.ok(depoOwnerAccountService.getProfileDetails());
    }

    @Operation(description = "forgot password, send token to email", operationId = "forgotPassword", summary = "forgot password, send token to email")
    @PostMapping(value = "/forgot-password")
    public ResponseEntity<JWTToken> generateResetToken (@RequestParam String email) {
        return ResponseEntity.ok(depoOwnerAccountService.resetPasswordToken(email));
    }

    @Operation(description = "reset password for forgot password", operationId = "resetPassword", summary = "reset password for forgot password")
    @PostMapping(value = "/reset-password")
    public ResponseEntity<String> forgotPassword (@RequestBody ForgotPasswordRequest request) {
        Map<String, String> authBody = DecodeUtil.decodeToken(request.getToken());
        return ResponseEntity.ok(depoOwnerAccountService.changeForgotPassword(request, authBody));
    }

}
