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
import com.nle.io.entity.BankDepo;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.entity.XenditVA;
import com.nle.io.entity.booking.BookingHeader;
import com.nle.io.repository.BankDepoRepository;
import com.nle.io.repository.DepoOwnerAccountRepository;
import com.nle.io.repository.XenditRepository;
import com.nle.io.repository.booking.BookingHeaderRepository;
import com.nle.security.SecurityUtils;
import com.nle.ui.model.request.xendit.XenditCallbackPayload;
import com.nle.ui.model.request.xendit.XenditDisCallbackPayload;
import com.nle.ui.model.request.xendit.XenditRequest;
import com.nle.ui.model.response.XenditListResponse;
import com.nle.ui.model.response.XenditResponse;
import com.nle.util.DateUtil;
import com.nle.util.XenditUtil;
import com.xendit.Xendit;
import com.xendit.exception.XenditException;
import com.xendit.model.Balance;
import com.xendit.model.Disbursement;
import com.xendit.model.FixedVirtualAccount;
import com.xendit.model.Invoice;
import lombok.RequiredArgsConstructor;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private final BankDepoRepository bankDepoRepository;
    private final String feeRule = "xpfeeru_1cb70def-7bdc-43e4-9495-6b81cd5bdedb";
    @Autowired
    private RestTemplate restTemplate;
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
            xenditVA.setAccount_number(closedVA.getAccountNumber());
            xenditVA.setPayment_status(XenditEnum.PENDING);
            xenditVA.setBooking_header_id(optionalBookingHeader.get());
            BindWithInvoice(response, depo.getXenditVaId(), xenditVA, optionalBookingHeader.get().getEmail());
            xenditVA.setDisbursement_id(null);
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
            xenditVA.setExpiry_date(invoice.getExpiryDate());
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
                CreateDisbursements(payload.getUser_id(), entity);
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

        response.setId(xenditVA.getXendit_id());
        response.setName(bookingHeader.getFull_name());
        response.setCurrency("IDR");
        response.setAmount(xenditVA.getAmount());
        response.setStatus(xenditVA.getPayment_status().toString());
        response.setInvoice_url("https://checkout-staging.xendit.co/web/" + xenditVA.getInvoice_id());
        response.setExternalId("va-" + xenditVA.getBank_code() + "-" + xenditVA.getPhone_number());
        response.setOwnerId(doa.getXenditVaId());
        response.setBankCode(xenditVA.getBank_code());
        response.setAccountNumber(xenditVA.getAccount_number());
        response.setIsClosed(Boolean.TRUE);
        response.setIsSingleUse(Boolean.TRUE);
        response.setExpirationDate(xenditVA.getExpiry_date());

        return response;
    }

    @Override
    public XenditResponse CreatePaymentOrder(XenditRequest request) {

        Optional<String> username = SecurityUtils.getCurrentUserLogin();

        if (username.isEmpty())
            throw new BadRequestException("Invalid Token!");

        Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountRepository.findByCompanyEmail(username.get());
        if (depoOwnerAccount.isEmpty())
            throw new BadRequestException("Can't Find Depo!");

        DepoOwnerAccount doa = depoOwnerAccount.get();
        if (doa.getXenditVaId() == null)
            throw new BadRequestException("This Depo is Not Active!");

        Optional<XenditVA> optionalXenditPending = xenditRepository
                .getVaWithPhoneAndBankAndPendingPayment(request.getPhone_number(), request.getBank_code());

        XenditResponse xenditResponse = new XenditResponse();
        if (!optionalXenditPending.isEmpty()) {
            XenditVA xenditVA = optionalXenditPending.get();
            Xendit.apiKey = appProperties.getXendit().getApiKey();
            Invoice invoice = XenditUtil.getInvoice(doa.getXenditVaId(), xenditVA.getInvoice_id());
            if (invoice.getStatus().equalsIgnoreCase("EXPIRED")) {
                xenditVA.setPayment_status(XenditEnum.EXPIRED);
                xenditRepository.save(xenditVA);
            } else if (invoice.getStatus().equalsIgnoreCase("PENDING")) {
                FixedVirtualAccount fixedVirtualAccount = XenditUtil.getVA(doa.getXenditVaId(), xenditVA.getXendit_id());
                BeanUtils.copyProperties(fixedVirtualAccount, xenditResponse);
                xenditResponse.setExpirationDate(String.valueOf(fixedVirtualAccount.getExpirationDate()));
                xenditResponse.setAmount(fixedVirtualAccount.getExpectedAmount());
                xenditResponse.setInvoice_url("https://checkout-staging.xendit.co/web/" + xenditVA.getInvoice_id());
                xenditResponse.setStatus("PENDING");
                return xenditResponse;
            }
        }

        xenditResponse = CreateNewVirtualAccount(request, doa);
        return xenditResponse;

    }
    public List<XenditListResponse> getMultipleXenditByPhone() {
        Optional<String> username = SecurityUtils.getCurrentUserLogin();
        if (username.isEmpty())
            throw new BadRequestException("invalid token");

        if (!username.get().startsWith("+62") && !username.get().startsWith("62") &&
                !username.get().startsWith("0"))
            throw new BadRequestException("not token from phone");
        String phone = username.get();

        List<XenditVA> listXenditVA = xenditRepository.findWithPhone(phone);
        List<XenditListResponse> listResponse = new ArrayList<>();

        for (XenditVA xenditVA : listXenditVA) {
            XenditListResponse xenditListResponse = new XenditListResponse();
            xenditListResponse.setBookingId(xenditVA.getBooking_header_id().getId());
            xenditListResponse.setBookingType(xenditVA.getBooking_header_id().getBooking_type().toString());
            xenditListResponse.setBankCode(xenditVA.getBank_code());
            xenditListResponse.setVa(xenditVA.getAccount_number());
            xenditListResponse.setExpiryDate(xenditVA.getExpiry_date());
            xenditListResponse.setAmount(xenditVA.getAmount());
            xenditListResponse.setInvoiceUrl("https://checkout-staging.xendit.co/web/" + xenditVA.getInvoice_id());
            xenditListResponse.setStatus(xenditVA.getPayment_status().toString());
            listResponse.add(xenditListResponse);
        }

        return listResponse;
    }

    @Override
    public void CreateDisbursements(String xendit_id, XenditVA xenditVA){
        //get balance
        Xendit.apiKey = appProperties.getXendit().getApiKey();
        Map<String, String> header = new HashMap<>();
        header.put("for-user-id", xendit_id);

        int balance_amount = 0;
        try {
            Balance balance = Balance.get(header, Balance.AccountType.CASH);
            balance_amount = Integer.parseInt(balance.getBalance().toString()) - 5550;
        } catch (XenditException e) {
            e.printStackTrace();
        }

        if (balance_amount < 1)
            return;

        //get Bank Depo
        Optional<DepoOwnerAccount> optional = depoOwnerAccountRepository.findByXenditVaId(xendit_id);
        if (optional.isEmpty())
            return;

        Optional<BankDepo> bankDepoOptional = bankDepoRepository.findDefaultDepoByCompanyEmail(optional.get().getCompanyEmail());
        if (bankDepoOptional.isEmpty())
            return;

        BankDepo bankDepo = bankDepoOptional.get();

        //create disbursement
        Map<String, Object> params = new HashMap<>();
        params.put("external_id", "disb-nle-" + DateUtil.getNowString(DATE_PATTERN));
        params.put("amount", balance_amount);
        params.put("bank_code", bankDepo.getBank_code());
        params.put("account_holder_name", bankDepo.getAccount_holder_name());
        params.put("account_number", bankDepo.getAccount_number());
        params.put("description", bankDepo.getDescription_bank());

        try {
            Disbursement disbursement = Disbursement.create(header, params);
            xenditVA.setDisbursement_id(disbursement.getId());
        } catch (XenditException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String CallbackDisbursements(XenditDisCallbackPayload payload){
        Optional<XenditVA> xenditVAOptional = xenditRepository.findByDisbursement_id(payload.getId());
        if (xenditVAOptional.isEmpty())
            throw new CommonException("Not found disbursement id");

        Xendit.apiKey = appProperties.getXendit().getApiKey();
        Map<String, String> header = new HashMap<>();
        header.put("for-user-id", payload.getUser_id());

        if (!payload.getStatus().equalsIgnoreCase("COMPLETED"))
            return "status is " + payload.getStatus();


        try {
            Disbursement disbursement = Disbursement.getById(header, payload.getId());
            if (!disbursement.getStatus().equalsIgnoreCase("COMPLETED"))
                return "status in xendit is " + payload.getStatus();

            XenditVA xenditVA = xenditVAOptional.get();
            BookingHeader bookingHeader = xenditVA.getBooking_header_id();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            LocalDateTime UTC = LocalDateTime.parse(payload.getUpdated(), formatter);
            LocalDateTime GMT = DateUtil.convertLocalDateWithTimeZone(UTC, "GMT+7");
            bookingHeader.setDisbursement_status(true);
            bookingHeader.setDisbursement_date(GMT);
            bookingHeaderRepository.save(bookingHeader);
        } catch (XenditException e) {
            e.printStackTrace();
            throw new BadRequestException("something wrong with xendit");
        }

        return "SUCCESS disbursement";

    }

    public XenditResponse cancelOrderXendit(Long bookingId, DepoOwnerAccount doa) {
        XenditResponse xenditResponse = new XenditResponse();

        //Validate if any booking but there are no payment
        Optional<BookingHeader> bookingHeaderOptional = bookingHeaderRepository.findById(bookingId);
        Optional<XenditVA> xenditVAOptional = xenditRepository.findWithBookingID(bookingId);
        if (!bookingHeaderOptional.isEmpty() && xenditVAOptional.isEmpty()){
            //For set booking status at booking header
            bookingHeaderRepository.cancelStatus(BookingStatusEnum.CANCEL, bookingId);
            return xenditResponse;
        }

        XenditVA xenditVA = xenditVAOptional.get();
        BookingHeader bookingHeader = xenditVA.getBooking_header_id();

        if (xenditVA.getPayment_status().toString().equalsIgnoreCase("PENDING")){
                xenditResponse.setName(bookingHeader.getFull_name());
                xenditResponse.setCurrency("IDR");
                xenditResponse.setAmount(xenditVA.getAmount());
                xenditResponse.setExternalId("va-" + xenditVA.getBank_code() + "-" + xenditVA.getPhone_number());
                xenditResponse.setOwnerId(doa.getXenditVaId());
                xenditResponse.setBankCode(xenditVA.getBank_code());
                xenditResponse.setAccountNumber(xenditVA.getAccount_number());
                xenditResponse.setIsClosed(Boolean.TRUE);
                xenditResponse.setIsSingleUse(Boolean.TRUE);
                //for changed to expired VA
                cancelVirtualAccount(xenditVA.getXendit_id(), doa.getXenditVaId(), xenditResponse);

                //for changed to expired invoice
                getCancelInvoice(xenditVA.getInvoice_id(), xenditResponse, doa.getXenditVaId());

                //for changed to expired DB
                xenditRepository.updateCancelOrder(XenditEnum.CANCEL, xenditVA.getId());
                xenditResponse.setStatus("CANCEL");

                //For set booking status at booking header
                bookingHeaderRepository.cancelStatus(BookingStatusEnum.CANCEL, bookingId);
                return xenditResponse;
        }
                return xenditResponse;

    }

    //Method validate for customer
    @Override
    public DepoOwnerAccount bookingValidate(Optional<String> phone, Long booking_id) {
            if (phone.isEmpty())
                throw new BadRequestException("You must login!");

            Optional<XenditVA> xenditVAOptional = xenditRepository.findWithBookingID(booking_id);
            if (xenditVAOptional.isEmpty())
                throw new BadRequestException("Not found booking!");
            XenditVA xenditVA = xenditVAOptional.get();
            BookingHeader bookingHeader = xenditVA.getBooking_header_id();

            String phone_number = phone.get();
            if (!bookingHeader.getPhone_number().equals(phone_number))
                throw new BadRequestException("this booking is not belong to phone number: "
                        + phone_number);

            Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountRepository.findByPhoneNumber(xenditVA.getPhone_number());
            if (depoOwnerAccount.isEmpty())
                throw new BadRequestException("Can't Find Depo!");

            DepoOwnerAccount doa = depoOwnerAccount.get();
            if (doa.getXenditVaId() == null)
                throw new BadRequestException("This depo is not active!");

            return doa;
    }

    //Method validate for depo
    @Override
    public DepoOwnerAccount orderValidate(Optional<String> username, Long booking_id) {
        if (username.isEmpty())
            throw new BadRequestException("Invalid Token!");

        if (username.get().startsWith("+62") && username.get().startsWith("62") &&
                username.get().startsWith("0"))
            throw new BadRequestException("Please login!");

        Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountRepository.findByCompanyEmail(username.get());
        if (depoOwnerAccount.isEmpty())
            throw new BadRequestException("Can't Find Depo!");

        DepoOwnerAccount doa = depoOwnerAccount.get();
        if (doa.getXenditVaId() == null)
            throw new BadRequestException("This depo is not active!");

        return doa;
    }

    private void getCancelInvoice(String invoiceId, XenditResponse xenditResponse, String depo_xendit_id) {
        Xendit.apiKey = appProperties.getXendit().getApiKey();
        Map<String, String> headers = new HashMap<>();
        headers.put("for-user-id", depo_xendit_id);
        try {
            Invoice invoice = Invoice.expire(headers, invoiceId);
            xenditResponse.setInvoice_url(invoice.getInvoiceUrl());
        } catch (XenditException e){
            e.printStackTrace();
        }
    }

    public void cancelVirtualAccount(String xenditId, String depo_xendit_id, XenditResponse xenditResponse){
        String updateVa = "https://api.xendit.co/callback_virtual_accounts/"+xenditId;

        HttpHeaders httpHeaders = new HttpHeaders();
        String username = appProperties.getXendit().getApiKey();
        String auth = username + ":";
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        httpHeaders.add("Authorization", "Basic " + encodedAuth);
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("for-user-id", depo_xendit_id);

        JSONObject paramBody = new JSONObject();
        try {
            paramBody.put("expiration_date", DateUtil.getCancelExpiration(DATE_PATTERN));
        } catch (JSONException e){
            e.printStackTrace();
        }

        final ObjectMapper objectMapper = new ObjectMapper();

        HttpEntity<String> request = new HttpEntity<String>(paramBody.toString(), httpHeaders);
        String result = restTemplate.patchForObject(updateVa, request, String.class);

        try {
            JsonNode root = objectMapper.readTree(result);
            xenditResponse.setId(root.path("id").asText());
            xenditResponse.setMerchantCode(root.path("merchant_code").asText());
            xenditResponse.setExpirationDate(root.path("expiration_date").asText());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
