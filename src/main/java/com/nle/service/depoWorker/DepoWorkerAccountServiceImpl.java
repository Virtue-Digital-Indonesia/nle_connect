package com.nle.service.depoWorker;

import com.nle.constant.AccountStatus;
import com.nle.entity.DepoWorkerAccount;
import com.nle.entity.VerificationToken;
import com.nle.mapper.DepoWorkerAccountMapper;
import com.nle.repository.DepoWorkerAccountRepository;
import com.nle.service.VerificationTokenService;
import com.nle.service.dto.DepoWorkerAccountDTO;
import com.nle.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.nle.constant.VerificationType.ACTIVE_ACCOUNT;

@RequiredArgsConstructor
@Service
@Transactional
public class DepoWorkerAccountServiceImpl implements DepoWorkerAccountService {
    private final DepoWorkerAccountRepository depoWorkerAccountRepository;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;
    private final DepoWorkerAccountMapper depoWorkerAccountMapper;

    @Override
    public Optional<DepoWorkerAccount> findByEmail(String email) {
        return depoWorkerAccountRepository.findByEmail(email);
    }

    @Override
    public DepoWorkerAccountDTO createAndSendInvitationEmail(String email) {
        // create new record in depo worker table
        DepoWorkerAccount depoWorkerAccount = new DepoWorkerAccount();
        depoWorkerAccount.setEmail(email);
        depoWorkerAccount.setAccountStatus(AccountStatus.INACTIVE);
        depoWorkerAccount = depoWorkerAccountRepository.save(depoWorkerAccount);
        // create verification token
        VerificationToken invitationToken = verificationTokenService.createInvitationToken(depoWorkerAccount, ACTIVE_ACCOUNT);
        // send email
        emailService.sendDepoWorkerActiveEmail(email, invitationToken.getToken());
        return depoWorkerAccountMapper.toDto(depoWorkerAccount);
    }

}
