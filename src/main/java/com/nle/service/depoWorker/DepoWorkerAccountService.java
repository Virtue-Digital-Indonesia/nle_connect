package com.nle.service.depoWorker;

import com.nle.constant.enums.AccountStatus;
import com.nle.ui.model.DepoWorkerActivationDTO;
import com.nle.ui.model.DepoWorkerLoginDto;
import com.nle.ui.model.JWTToken;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.DepoWorkerApproveReqDto;
import com.nle.ui.model.request.DepoWorkerUpdateGateNameReqDto;
import com.nle.ui.model.response.DepoWorkerListDTO;
import com.nle.service.dto.DepoWorkerAccountDTO;
import org.springframework.data.domain.Pageable;

public interface DepoWorkerAccountService {
    void sendInvitationEmail(String email);

    void depoWorkerJoinRequest(DepoWorkerActivationDTO depoWorkerActivationDTO);

    void approveJoinRequest(DepoWorkerApproveReqDto depoWorkerApproveReqDto);

    void deleteJoinRequest(String email);

    DepoWorkerAccountDTO completeDepoWorkerRegistration(DepoWorkerUpdateGateNameReqDto depoWorkerUpdateGateNameReqDto);

    PagingResponseModel<DepoWorkerListDTO> findAll(Pageable pageable);

    AccountStatus checkDepoWorkerRegistrationStatus(String androidId);

    JWTToken authenticateDepoWorker(DepoWorkerLoginDto androidId);

    DepoWorkerAccountDTO getDepoWorkerAccountDetails();

}
