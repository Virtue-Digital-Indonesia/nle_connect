package com.nle.service.email;

import com.nle.entity.DepoOwnerAccount;
import com.nle.service.dto.EmailDTO;


public interface EmailService {
    void sendSimpleEmail(EmailDTO emailDTO);

    void sendDepoOwnerActiveEmail(DepoOwnerAccount depoOwnerAccount, String token);

    void sendDepoWorkerInvitationEmail(String workerEmail, String activationCode);
    // TODO disable because there is no email for worker invitation process
    // void sendDepoWorkerApproveEmail(String workerFullName, String depoOwnerFullName, String email);

}
