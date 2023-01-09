package com.nle.shared.service.xendit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nle.config.prop.AppProperties;
import com.nle.constant.enums.BookingStatusEnum;
import com.nle.constant.enums.PaymentMethodEnum;
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
import com.nle.util.XenditUtil;
import com.xendit.Xendit;
import com.xendit.exception.XenditException;
import com.xendit.model.FixedVirtualAccount;
import com.xendit.model.Invoice;
import lombok.RequiredArgsConstructor;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.util.*;

@RequiredArgsConstructor
@Service
@Transactional
public class XenditServiceImpl implements XenditService {

    private final AppProperties appProperties;
    private final String DATE_PATTERN = "yyyy-MM-dd";
    private final String VA_CODE = "90566"; // kalo test 9999
    private final XenditRepository xenditRepository;
    private final BookingHeaderRepository bookingHeaderRepository;
    private final DepoOwnerAccountRepository depoOwnerAccountRepository;
    private final String feeRule = "xpfeeru_800a5bcb-f007-4547-b4f4-5ae704f839b0";

    @Override
    public XenditResponse CreateVirtualAccount(XenditRequest request) {

        Optional<String> username = SecurityUtils.getCurrentUserLogin();
        if (username.isEmpty())
            throw new BadRequestException("invalid token");

        if (!username.get().startsWith("+62") && !username.get().startsWith("62") && !username.get().startsWith("0"))
            throw new BadRequestException("not token from phone");

        Optional<DepoOwnerAccount> accountOptional = depoOwnerAccountRepository.findById(request.getDepo_id());
        if (accountOptional.isEmpty())
            throw new BadRequestException("can't find depo");

        DepoOwnerAccount doa = accountOptional.get();
        if (doa.getXenditVaId() == null)
            throw new BadRequestException("this depo is not active");

        Optional<XenditVA> optionalXenditPending = xenditRepository
                .getVaWithPhoneAndBankAndPendingPayment(request.getPhone_number(), request.getBank_code());

        XenditResponse response = new XenditResponse();
        if (!optionalXenditPending.isEmpty()) {
            XenditVA xenditVA = optionalXenditPending.get();
            Xendit.apiKey = appProperties.getXendit().getApiKey();
            Invoice invoice = XenditUtil.getInvoice(doa.getXenditVaId(), xenditVA.getInvoice_id());
            if (invoice.getStatus().equalsIgnoreCase("EXPIRED")) {
                xenditVA.setPayment_status(XenditEnum.EXPIRED);
                xenditRepository.save(xenditVA);
            } else if (invoice.getStatus().equalsIgnoreCase("PENDING")) {
                FixedVirtualAccount fvAccount = XenditUtil.getVA(doa.getXenditVaId(), xenditVA.getXendit_id());
                BeanUtils.copyProperties(fvAccount, response);
                response.setExpirationDate(String.valueOf(fvAccount.getExpirationDate()));
                response.setAmount(fvAccount.getExpectedAmount());
                response.setInvoice_url("https://checkout-staging.xendit.co/web/" + xenditVA.getInvoice_id());
                response.setStatus("PENDING");
                return response;
            }
        }

        response = CreateNewVirtualAccount(request, doa);
        return response;
    }

    @Override
    public XenditResponse CreateNewVirtualAccount(XenditRequest request, DepoOwnerAccount depo) {

        int va_index = request.getPhone_number().length();
        String va_number = VA_CODE + request.getPhone_number().substring(va_index - 7, va_index);

        Optional<BookingHeader> optionalBookingHeader = bookingHeaderRepository
                .findById(request.getBooking_header_id());
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
            BindWithInvoice(response, depo.getXenditVaId(), xenditVA, optionalBookingHeader.get().getEmail());
            xenditRepository.save(xenditVA);
        } catch (XenditException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    private void BindWithInvoice(XenditResponse xenditResponse, String depo_Xendit_id, XenditVA xenditVA,
            String bookingHeaderEmail) {
        Xendit.apiKey = appProperties.getXendit().getApiKey();

        String[] paymentMethod = { xenditResponse.getBankCode() };

        Map<String, Object> customerObject = new HashMap<>();
        customerObject.put("email", bookingHeaderEmail);
        Map<String, Object> customerNotificationPreference = new HashMap<>();
        String[] notifications = { "email" };
        customerNotificationPreference.put("invoice_created", notifications);
        customerNotificationPreference.put("invoice_paid", notifications);
        customerNotificationPreference.put("invoice_expired", notifications);

        Map<String, Object> params = new HashMap<>();
        params.put("external_id", xenditResponse.getExternalId());
        params.put("amount", xenditResponse.getAmount());
        params.put("description", "Invoice-" + DateUtil.getNowString(DATE_PATTERN));
        params.put("customer", customerObject);
        params.put("customer_notification_preference", customerNotificationPreference);
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
    public void CallbackInvoice(XenditCallbackPayload payload) {
        Optional<XenditVA> xenditVA = xenditRepository.findWithInvoiceId(payload.getId());
        Xendit.apiKey = appProperties.getXendit().getApiKey();

        if (xenditVA.isEmpty())
            throw new BadRequestException("not found invoice id");

        Map<String, String> headers = new HashMap<>();
        headers.put("for-user-id", payload.getUser_id());

        try {
            Invoice invoice = Invoice.getById(headers, payload.getId());
            XenditVA entity = xenditVA.get();

            if (invoice.getStatus().equalsIgnoreCase("PENDING")) {
                return;
            } else if (invoice.getStatus().equalsIgnoreCase("SETTLED")) {
                entity.setPayment_id(payload.getPaid_at());
                entity.setPayment_status(XenditEnum.PAID);
                BookingHeader bookingHeader = xenditVA.get().getBooking_header_id();
                bookingHeader.setPayment_method(PaymentMethodEnum.BANK);
                bookingHeader.setBooking_status(BookingStatusEnum.SUCCESS);
                bookingHeaderRepository.save(bookingHeader);
            } else if (invoice.getStatus().equalsIgnoreCase("EXPIRED")) {
                entity.setPayment_status(XenditEnum.EXPIRED);
            }

            xenditRepository.save(entity);
        } catch (XenditException e) {
            throw new RuntimeException(e);
        }
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

    public XenditResponse getXenditByBookingId(Long booking_id) {
        Optional<String> username = SecurityUtils.getCurrentUserLogin();
        if (username.isEmpty())
            throw new BadRequestException("invalid token");

        Optional<XenditVA> optionalXenditVA = xenditRepository.findWithBookingID(booking_id);
        XenditResponse response = new XenditResponse();
        if (optionalXenditVA.isEmpty())
            return response;

        XenditVA xenditVA = optionalXenditVA.get();
        BookingHeader bookingHeader = xenditVA.getBooking_header_id();
        DepoOwnerAccount doa = bookingHeader.getDepoOwnerAccount();

        Xendit.apiKey = appProperties.getXendit().getApiKey();
        Invoice invoice = XenditUtil.getInvoice(doa.getXenditVaId(), xenditVA.getInvoice_id());
        BeanUtils.copyProperties(invoice, response);
        response.setInvoice_url(invoice.getInvoiceUrl());
        response.setExpirationDate(invoice.getExpiryDate());
        response.setOwnerId(doa.getXenditVaId());
        response.setName(bookingHeader.getFull_name());
        response.setAmount(invoice.getAmount().longValue());

        int va_index = xenditVA.getPhone_number().length();
        String va_number = VA_CODE + xenditVA.getPhone_number().substring(va_index - 8, va_index);
        response.setAccountNumber(va_number);
        response.setIsClosed(Boolean.TRUE);
        response.setIsSingleUse(Boolean.TRUE);
        return response;
    }

    public List<XenditResponse> getMultipleXenditByBookingId(List<Long> list_booking_header) {
        List<XenditResponse> list = new ArrayList<>();

        for (Long booking_id : list_booking_header) {
            list.add(getXenditByBookingId(booking_id));
        }

        return list;
    }

}
