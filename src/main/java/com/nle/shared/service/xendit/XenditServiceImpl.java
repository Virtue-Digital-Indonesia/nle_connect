package com.nle.shared.service.xendit;

import com.nle.config.prop.AppProperties;
import com.nle.io.entity.XenditVA;
import com.nle.io.repository.XenditRepository;
import com.nle.ui.model.request.xendit.XenditCallbackPayload;
import com.nle.ui.model.request.xendit.XenditRequest;
import com.nle.ui.model.response.XenditResponse;
import com.nle.util.DateUtil;
import com.xendit.Xendit;
import com.xendit.exception.XenditException;
import com.xendit.model.FixedVirtualAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class XenditServiceImpl implements XenditService{

    private final AppProperties appProperties;
    private final String DATE_PATTERN = "yyyy-MM-dd";
    private final XenditRepository xenditRepository;

    private final String feeRule = "xpfeeru_37136bb4-e471-4d00-a464-a371997d7008";

    @Override
    public XenditResponse CreateVirtualAccount(XenditRequest request) {

        Optional<XenditVA> optionalXendit = xenditRepository.findByPhoneAndBank(request.getPhone_number(), request.getBack_code());

        XenditResponse response = null;
        if (optionalXendit.isEmpty())
            response = CreateNewVirtualAccount(request);
        else
            response = UpdateVirtualAccount(optionalXendit.get(), request);

        return response;
    }

    @Override
    public XenditResponse CreateNewVirtualAccount(XenditRequest request) {
        Xendit.apiKey = appProperties.getXendit().getApiKey();
        Map<String, Object> params = new HashMap<>();
        params.put("external_id", "va-" + request.getPhone_number());
        params.put("bank_code", request.getBack_code());
        params.put("name", request.getName());
//        params.put("virtual_account_number", request.getPhone_number());
        params.put("expected_amount", request.getExpected_amount());
        params.put("description", request.getDescription()); //BRI || BSI
        params.put("expiration_date", DateUtil.getTomorrowString(DATE_PATTERN));
        params.put("is_closed", true);

        Map<String, String> headers = new HashMap<>();
        headers.put("for-user-id", "63a15cccb566b1a4513dd533");
        headers.put("with-fee-rule", feeRule);


        XenditResponse response = new XenditResponse();
        try {
            FixedVirtualAccount closedVA = FixedVirtualAccount.createClosed(headers, params);
            BeanUtils.copyProperties(closedVA, response);
            response.setExpirationDate(String.valueOf(closedVA.getExpirationDate()));

            XenditVA xenditVA = new XenditVA();
            xenditVA.setXendit_id(closedVA.getId());
            xenditVA.setPhone_number(request.getPhone_number());
            xenditVA.setAmount((Integer) request.getExpected_amount());
            xenditVA.setBank_code(closedVA.getBankCode());
            xenditVA.setPayment_status(closedVA.getStatus());
            xenditVA.setExpired_date(String.valueOf(closedVA.getExpirationDate()));
            xenditRepository.save(xenditVA);
        } catch (XenditException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    @Override
    public XenditResponse UpdateVirtualAccount(XenditVA xenditVA, XenditRequest request) {

        int price = (int) request.getExpected_amount();
        if (xenditVA.getPayment_status().equalsIgnoreCase("PENDING"))
            price += xenditVA.getAmount();

        Xendit.apiKey = appProperties.getXendit().getApiKey();
        Map<String, Object> params = new HashMap<>();
        params.put("is_single_use", false);
        params.put("expected_amount", price);
        params.put("expiration_date", DateUtil.getTomorrowString(DATE_PATTERN));
        params.put("external_id", "va-" + request.getPhone_number());
        params.put("description", request.getDescription()); //BRI || BSI

        XenditResponse response = new XenditResponse();
        try {
            FixedVirtualAccount va = FixedVirtualAccount.update(xenditVA.getXendit_id(), params);
            BeanUtils.copyProperties(va, response);
            response.setExpirationDate(String.valueOf(va.getExpirationDate()));

            xenditVA.setAmount(price);
            xenditVA.setPayment_status(va.getStatus());
            xenditVA.setExpired_date(String.valueOf(va.getExpirationDate()));
            xenditRepository.save(xenditVA);
        } catch (XenditException e) {
            throw new RuntimeException(e);
        }

        return response;
    }

    @Override
    public void VirtualAccountPayment(XenditCallbackPayload payload) {
        Optional<XenditVA> optionalXenditVA = xenditRepository.findByXendit_id(payload.getId());
        if (optionalXenditVA.isEmpty())
            System.out.println("id not saved");

        XenditVA xenditVA = optionalXenditVA.get();
        xenditVA.setPayment_status("DONE");

        Xendit.apiKey = appProperties.getXendit().getApiKey();
        Map<String, Object> params = new HashMap<>();
        params.put("is_single_use", false);
        params.put("expiration_date", DateUtil.getYesterdayString(DATE_PATTERN));

        try {
            FixedVirtualAccount va = FixedVirtualAccount.update(payload.getId(), params);
//            xenditVA.setPayment_status(va.getStatus());
            System.out.println(va.getStatus());
            xenditVA.setExpired_date(String.valueOf(va.getExpirationDate()));
            xenditRepository.save(xenditVA);
        } catch (XenditException e) {
            throw new RuntimeException(e);
        }
    }
}
