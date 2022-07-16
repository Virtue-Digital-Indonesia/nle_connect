package com.nle.service.depoWorker;

import com.nle.controller.dto.DepoWorkerActivationDTO;
import com.nle.entity.DepoWorkerAccount;
import com.nle.service.dto.DepoWorkerAccountDTO;

import java.util.Optional;

public interface DepoWorkerAccountService {
    Optional<DepoWorkerAccount> findByEmail(String email);

    DepoWorkerAccountDTO createAndSendInvitationEmail(String email);

    void depoWorkerJoinRequest(DepoWorkerActivationDTO depoWorkerActivationDTO);

    void approveJoinRequest(String email);
}
