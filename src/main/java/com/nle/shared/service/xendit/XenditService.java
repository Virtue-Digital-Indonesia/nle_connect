package com.nle.shared.service.xendit;

import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.entity.XenditVA;
import com.nle.ui.model.request.xendit.XenditCallbackPayload;
import com.nle.ui.model.request.xendit.XenditRequest;
import com.nle.ui.model.response.XenditResponse;

public interface XenditService {
    XenditResponse CreateVirtualAccount(XenditRequest request);

    XenditResponse CreateNewVirtualAccount(XenditRequest request, DepoOwnerAccount depo);

    XenditResponse UpdateVirtualAccount(XenditVA xenditVA, XenditRequest request);

    void VirtualAccountPayment(XenditCallbackPayload payload);

    String createXenditAccount(DepoOwnerAccount depoOwnerAccount);
}
