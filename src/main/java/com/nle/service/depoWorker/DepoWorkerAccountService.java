package com.nle.service.depoWorker;

import com.nle.controller.dto.DepoWorkerActivationDTO;
import com.nle.controller.dto.DepoWorkerApproveReqDto;
import com.nle.controller.dto.DepoWorkerUpdateGateNameReqDto;
import com.nle.controller.dto.pageable.PagingResponseModel;
import com.nle.controller.dto.response.DepoWorkerListDTO;
import com.nle.entity.DepoWorkerAccount;
import com.nle.service.dto.DepoWorkerAccountDTO;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface DepoWorkerAccountService {
    Optional<DepoWorkerAccount> findByEmail(String email);

    DepoWorkerAccountDTO createAndSendInvitationEmail(String email);

    void depoWorkerJoinRequest(DepoWorkerActivationDTO depoWorkerActivationDTO);

    void approveJoinRequest(DepoWorkerApproveReqDto depoWorkerApproveReqDto);

    void deleteJoinRequest(String email);

    DepoWorkerAccountDTO completeDepoWorkerRegistration(DepoWorkerUpdateGateNameReqDto depoWorkerUpdateGateNameReqDto);

    PagingResponseModel<DepoWorkerListDTO> findAll(Pageable pageable);

}
