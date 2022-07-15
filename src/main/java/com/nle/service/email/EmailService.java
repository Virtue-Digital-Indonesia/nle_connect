package com.nle.service.email;

import com.nle.entity.DepoOwnerAccount;
import com.nle.service.dto.EmailDTO;


public interface EmailService {
    void sendSimpleEmail(EmailDTO emailDTO);

    void sendDepoOwnerActiveEmail(DepoOwnerAccount depoOwnerAccount, String token);
}
