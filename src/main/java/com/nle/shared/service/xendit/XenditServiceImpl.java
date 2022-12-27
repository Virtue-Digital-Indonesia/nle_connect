package com.nle.shared.service.xendit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nle.config.prop.AppProperties;
import com.nle.constant.enums.BookingStatusEnum;
import com.nle.constant.enums.XenditEnum;
import com.nle.exception.BadRequestException;
import com.nle.exception.CommonException;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.entity.XenditVA;
import com.nle.io.entity.booking.BookingHeader;
import com.nle.io.repository.DepoOwnerAccountRepository;
import com.nle.io.repository.XenditRepository;
import com.nle.io.repository.booking.BookingHeaderRepository;
import com.nle.security.SecurityUtils;
import com.nle.ui.model.request.xendit.XenditCallbackPayload;
import com.nle.ui.model.request.xendit.XenditRequest;
import com.nle.ui.model.response.XenditResponse;
import com.nle.util.DateUtil;
import com.xendit.Xendit;
import com.xendit.exception.XenditException;
import com.xendit.model.FixedVirtualAccount;
import com.xendit.model.Invoice;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Base64;

@RequiredArgsConstructor
@Service
@Transactional
public class XenditServiceImpl implements XenditService {

    private final AppProperties appProperties;
    private final String DATE_PATTERN = "yyyy-MM-dd";
    private final String VA_CODE = "9999"; //kalo live 90566
    private final XenditRepository xenditRepository;
    private final BookingHeaderRepository bookingHeaderRepository;
    private final DepoOwnerAccountRepository depoOwnerAccountRepository;
    private final String feeRule = "xpfeeru_37136bb4-e471-4d00-a464-a371997d7008";

    @Override
    public XenditResponse CreateVirtualAccount(XenditRequest request) {

        Optional<String> username = SecurityUtils.getCurrentUserLogin();
        if (username.isEmpty() || !username.get().startsWith("+62") || !username.get().startsWith("62") || !username.get().startsWith("0"))
            throw new BadRequestException("invalid token");

        Optional<XenditVA> optionalXendit = xenditRepository.getVaWithPhoneAndBank(request.getPhone_number(),
                request.getBank_code());

        Optional<DepoOwnerAccount> accountOptional = depoOwnerAccountRepository.findById(request.getDepo_id());
        if (accountOptional.isEmpty())
            throw new BadRequestException("can't find depo");

        DepoOwnerAccount doa = accountOptional.get();
        if (doa.getXenditVaId() == null)
            throw new BadRequestException("this depo is not active");

        XenditResponse response = CreateNewVirtualAccount(request, doa);
        return response;
    }

    @Override
    public XenditResponse CreateNewVirtualAccount(XenditRequest request, DepoOwnerAccount depo) {

        int va_index = request.getPhone_number().length();
        String va_number = VA_CODE + request.getPhone_number().substring(va_index - 8, va_index);

        Optional<BookingHeader> optionalBookingHeader = bookingHeaderRepository.findById(request.getBooking_header_id());
        if (optionalBookingHeader.isEmpty())
            throw new CommonException("not found booking id");
        if (optionalBookingHeader.get().getBooking_status() != BookingStatusEnum.WAITING)
            throw new BadRequestException("this booking already paid");
        if (optionalBookingHeader.get().getDepoOwnerAccount().getId() != depo.getId())
            throw new BadRequestException("this booking not for this depo");


        Xendit.apiKey = appProperties.getXendit().getApiKey();
        Map<String, Object> params = new HashMap<>();
        params.put("external_id", "va-" + request.getBank_code() + "-" + request.getPhone_number());
        params.put("bank_code", request.getBank_code());
        params.put("name", request.getName());
        params.put("virtual_account_number", va_number);
        params.put("expected_amount", request.getExpected_amount());
        params.put("expiration_date", DateUtil.getTomorrowString(DATE_PATTERN) + "T23:59:00");
        params.put("is_closed", true);
        params.put("is_single_use", true);

        Map<String, String> headers = new HashMap<>();
        headers.put("for-user-id", depo.getXenditVaId());
        headers.put("with-fee-rule", feeRule);

        XenditResponse response = new XenditResponse();
        try {
            FixedVirtualAccount closedVA = FixedVirtualAccount.createClosed(headers, params);
            BeanUtils.copyProperties(closedVA, response);
            response.setExpirationDate(String.valueOf(closedVA.getExpirationDate()));
            response.setAmount(closedVA.getExpectedAmount());

            XenditVA xenditVA = new XenditVA();
            xenditVA.setXendit_id(closedVA.getId());
            xenditVA.setPhone_number(request.getPhone_number());
            xenditVA.setAmount(closedVA.getExpectedAmount());
            xenditVA.setBank_code(closedVA.getBankCode());
            xenditVA.setPayment_status(XenditEnum.PENDING);
            xenditVA.setBooking_header_id(optionalBookingHeader.get());
            BindWithInvoice(response, depo.getXenditVaId(), xenditVA);
            xenditRepository.save(xenditVA);
        } catch (XenditException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    @Override
    public XenditResponse UpdateVirtualAccount(XenditVA xenditVA, XenditRequest request) {

        Xendit.apiKey = appProperties.getXendit().getApiKey();
        Map<String, Object> params = new HashMap<>();
        params.put("is_single_use", false);
        params.put("expected_amount", request.getExpected_amount());
        params.put("expiration_date", DateUtil.getTomorrowString(DATE_PATTERN));
        params.put("external_id", "va-" + request.getBank_code() + "-" + request.getPhone_number());

        XenditResponse response = new XenditResponse();
        try {
            FixedVirtualAccount va = FixedVirtualAccount.update(xenditVA.getXendit_id(), params);
            BeanUtils.copyProperties(va, response);
            response.setExpirationDate(String.valueOf(va.getExpirationDate()));

            xenditVA.setAmount(va.getExpectedAmount());
            xenditVA.setPayment_status(XenditEnum.PENDING);
            xenditRepository.save(xenditVA);
        } catch (XenditException e) {
            throw new RuntimeException(e);
        }

        return response;
    }

    private void BindWithInvoice(XenditResponse xenditResponse, String depo_Xendit_id, XenditVA xenditVA) {
        Xendit.apiKey = appProperties.getXendit().getApiKey();

        String [] paymentMethod = {xenditResponse.getBankCode()};

        Map<String, Object> params = new HashMap<>();
        params.put("external_id", xenditResponse.getExternalId());
        params.put("amount", xenditResponse.getAmount());
        params.put("description", "Invoice-" + DateUtil.getNowString(DATE_PATTERN));
        params.put("callback_virtual_account_id", xenditResponse.getId());
        params.put("payment_methods", paymentMethod);

        Map<String, String> headers = new HashMap<>();
        headers.put("for-user-id", depo_Xendit_id);
        headers.put("with-fee-rule", feeRule);

        try {
            Invoice invoice = Invoice.create(headers, params);
            xenditVA.setInvoice_id(invoice.getId());
            xenditResponse.setInvoice_url(invoice.getInvoiceUrl());
        } catch (XenditException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void VirtualAccountPayment(XenditCallbackPayload payload) {
        Optional<XenditVA> optionalXenditVA = xenditRepository.getVaWithXenditId(payload.getId());
        if (optionalXenditVA.isEmpty())
            System.out.println("id not saved");

        XenditVA xenditVA = optionalXenditVA.get();
        xenditVA.setPayment_status(XenditEnum.PAID);
    }

    @Override
    public String createXenditAccount(DepoOwnerAccount depoOwnerAccount) {
        String createAccountUrl = "https://api.xendit.co/v2/accounts";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        String username = appProperties.getXendit().getApiKey();
        String auth = username + ":";
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        httpHeaders.add("Authorization", "Basic " + encodedAuth);
        httpHeaders.add("Content-Type", "application/json");

        JSONObject publicProfile = new JSONObject();
        try {
            publicProfile.put("business_name", depoOwnerAccount.getOrganizationName());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject accountProfile = new JSONObject();
        try {
            accountProfile.put("email", depoOwnerAccount.getCompanyEmail());
            accountProfile.put("type", "OWNED");
            accountProfile.put("public_profile", publicProfile);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ObjectMapper objectMapper = new ObjectMapper();

        HttpEntity<String> request = new HttpEntity<String>(accountProfile.toString(), httpHeaders);
        String result = restTemplate.postForObject(createAccountUrl, request,
                String.class);
        try {
            JsonNode root = objectMapper.readTree(result);
            return root.path("id").asText();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "failed";
    }
}
