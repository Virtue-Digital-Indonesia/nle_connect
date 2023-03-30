package com.nle.shared.service.email;

import com.nle.io.entity.BookingCustomer;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.shared.dto.EmailDTO;
import com.nle.shared.dto.ftp.FtpMoveDTOError;
import com.nle.ui.model.request.ContactUsFormRequest;

import java.util.List;


public interface EmailService {
    void sendSimpleEmail(EmailDTO emailDTO);

    void sendDepoOwnerActiveEmail(DepoOwnerAccount depoOwnerAccount, String token);

    void sendDepoWorkerInvitationEmail(String workerEmail, String activationCode);
    // TODO disable because there is no email for worker invitation process
    // void sendDepoWorkerApproveEmail(String workerFullName, String depoOwnerFullName, String email);

    void sendFTPSynErrorEmail(DepoOwnerAccount depoOwnerAccount, List<FtpMoveDTOError> errors);

    void sendResetPassword(DepoOwnerAccount depoOwnerAccount, String token);
    void sendResetPasswordCustomer(BookingCustomer bookingCustomer, String token);

    void sendMessageForContactUs(ContactUsFormRequest contactUsFormRequest);
}
