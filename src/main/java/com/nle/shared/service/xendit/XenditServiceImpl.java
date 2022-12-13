package com.nle.shared.service.xendit;

import com.nle.config.prop.AppProperties;
import com.nle.ui.model.request.xendit.XenditCallbackPayload;
import com.nle.ui.model.request.xendit.XenditRequest;
import com.nle.ui.model.response.XenditResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class XenditServiceImpl implements XenditService{

    private final AppProperties appProperties;
    private final String HEADER_JSON ="application/json";

    @Override
    public XenditResponse CreateVirtualAccount(XenditRequest request) {
        return null;
    }

    @Override
    public void VirtualAccountPayment(XenditCallbackPayload payload) {

    }
}
