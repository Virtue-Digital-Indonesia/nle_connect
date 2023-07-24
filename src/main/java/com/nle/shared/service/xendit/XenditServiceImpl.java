package com.nle.shared.service.xendit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nle.config.prop.AppProperties;
import com.nle.constant.enums.*;
import com.nle.exception.BadRequestException;
import com.nle.exception.CommonException;
import com.nle.io.entity.BankDepo;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.entity.XenditVA;
import com.nle.io.entity.booking.BookingHeader;
import com.nle.io.repository.BankDepoRepository;
import com.nle.io.repository.DepoOwnerAccountRepository;
import com.nle.io.repository.XenditRepository;
import com.nle.io.repository.booking.BookingDetailUnloadingRepository;
import com.nle.io.repository.booking.BookingHeaderRepository;
import com.nle.security.SecurityUtils;
import com.nle.shared.component.ValidateComponent;
import com.nle.shared.component.XenditComponent;
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
    //Initialize
    private final AppProperties appProperties;
    private final String DATE_PATTERN = "yyyy-MM-dd";
    public static String VA_CODE_GENERAL = "90566"; // kalo test 9999
    public static String VA_CODE_MANDIRI = "9056";
    private final String VA_CODE = "9999"; // kalo live 90566
    //    private final String feeRule = "xpfeeru_1cb70def-7bdc-43e4-9495-6b81cd5bdedb";
    private final String feeRule = "xpfeeru_37136bb4-e471-4d00-a464-a371997d7008";

    //Repository
    private final XenditRepository xenditRepository;
    private final BookingHeaderRepository bookingHeaderRepository;
    private final DepoOwnerAccountRepository depoOwnerAccountRepository;
    private final BankDepoRepository bankDepoRepository;
    private final BookingDetailUnloadingRepository bookingDetailUnloadingRepository;

    //Component
    private final ValidateComponent validateComponent;
    private final XenditComponent xenditComponent;
    private final RestTemplate restTemplate;

    @Override
    public XenditResponse ControllerCreateVirtualAccount(XenditRequest request) {

        DepoOwnerAccount doa = validateComponent.ValidateDepoAccount(request.getDepo_id());
        validateComponent.ValidateXenditVA(doa);
        BookingHeader bookingHeader = validateComponent.ValidateBookingHeader(request.getBooking_header_id(),
                request.getDepo_id());

        Optional<XenditVA> optionalXenditPending = xenditRepository
                .getVaWithPhoneAndBankAndPendingPayment(request.getPhone_number(), request.getBank_code());

        XenditResponse response;
        if (!optionalXenditPending.isEmpty()) {
            XenditVA xenditVA = optionalXenditPending.get();
            response = xenditComponent.getXenditStatus(appProperties, doa, xenditVA);
            return response;
        }

        //Create va
        FixedVirtualAccount closedVA = CreateNewVirtualAccount(request,doa);
        Invoice invoice =BindWithInvoice(closedVA,doa.getXenditVaId(), bookingHeader.getEmail());
        XenditVA xenditVA = xenditComponent.FactoryXenditVA(closedVA, bookingHeader, invoice);
        xenditRepository.save(xenditVA);
        response = xenditComponent.createXenditResponse(closedVA,invoice);

        return response;
    }

    @Override
    public FixedVirtualAccount CreateNewVirtualAccount(XenditRequest request, DepoOwnerAccount depo) {
        //For initialization virtual account xendit
//        String va_number = xenditComponent.initialVaNumber(request.getPhone_number(), request.getBank_code());
        int va_index = request.getPhone_number().length();
        String va_number = VA_CODE + request.getPhone_number().substring(va_index - 6, va_index);

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

        FixedVirtualAccount closedVA = null;
        try {
            //Create VA
            closedVA = FixedVirtualAccount.createClosed(headers, params);
        } catch (XenditException e) {
            throw new RuntimeException(e);
        }
        return closedVA;
    }

    private Invoice BindWithInvoice(FixedVirtualAccount closedVA, String depo_Xendit_id, String bookingHeaderEmail) {
        Xendit.apiKey = appProperties.getXendit().getApiKey();

        String[] paymentMethod = {closedVA.getBankCode() };

        Map<String, Object> customerObject = new HashMap<>();
        customerObject.put("email", bookingHeaderEmail);
        Map<String, Object> customerNotificationPreference = new HashMap<>();
        String[] notifications = { "email" };
        customerNotificationPreference.put("invoice_created", notifications);
        customerNotificationPreference.put("invoice_paid", notifications);
        customerNotificationPreference.put("invoice_expired", notifications);

        Map<String, Object> params = new HashMap<>();
        params.put("external_id", closedVA.getExternalId());
        params.put("amount", closedVA.getExpectedAmount());
        params.put("description", "Invoice-" + DateUtil.getNowString(DATE_PATTERN));
        params.put("customer", customerObject);
        params.put("customer_notification_preference", customerNotificationPreference);
        params.put("callback_virtual_account_id", closedVA.getId());
        params.put("payment_methods", paymentMethod);

        Map<String, String> headers = new HashMap<>();
        headers.put("for-user-id", depo_Xendit_id);
        headers.put("with-fee-rule", feeRule);

        Invoice invoice = null;
        try {
            invoice = Invoice.create(headers, params);
        } catch (XenditException e) {
            throw new RuntimeException(e);
        }
        return invoice;
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

                if (bookingHeader.getBooking_type().equals(ItemTypeEnum.UNLOADING)){
                    bookingDetailUnloadingRepository.updatePaymentStatus(bookingHeader.getId(), PaymentStatusEnum.PAID);
                }

                CreateDisbursements(payload.getUser_id(), entity);
            } else if (invoice.getStatus().equalsIgnoreCase("EXPIRED")) {
                entity.setPayment_status(XenditEnum.EXPIRED);
                BookingHeader bookingHeader = xenditVA.get().getBooking_header_id();
                bookingHeader.setBooking_status(BookingStatusEnum.EXPIRED);
                bookingHeaderRepository.save(bookingHeader);

                if (bookingHeader.getBooking_type().equals(ItemTypeEnum.UNLOADING)){
                    bookingDetailUnloadingRepository.updatePaymentStatus(bookingHeader.getId(), PaymentStatusEnum.EXPIRED);
                }
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
        response.setInvoice_url(appProperties.getUrl().getXenditCheckout() + xenditVA.getInvoice_id());
        response.setExternalId("va-" + xenditVA.getBank_code() + "-" + xenditVA.getPhone_number());
        response.setOwnerId(doa.getXenditVaId());
        response.setBankCode(xenditVA.getBank_code());
        response.setAccountNumber(xenditVA.getAccount_number());
        response.setIsClosed(Boolean.TRUE);
        response.setIsSingleUse(Boolean.TRUE);
        response.setExpirationDate(xenditVA.getExpiry_date());

        return response;
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
            xenditListResponse.setInvoiceUrl(appProperties.getUrl().getXenditCheckout() + xenditVA.getInvoice_id());
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

    public XenditResponse cancelOrderXendit(Long bookingId) {
        XenditResponse xenditResponse = new XenditResponse();

        //Validate if any booking
        Optional<BookingHeader> bookingHeaderOptional = bookingHeaderRepository.findById(bookingId);
        if (bookingHeaderOptional.isEmpty())
            throw new BadRequestException("Cannot find booking!");

        if (!bookingHeaderOptional.get().getBooking_status().equals(BookingStatusEnum.WAITING))
            throw new BadRequestException("The booking status is "+bookingHeaderOptional.get().getBooking_status());


        //For set booking status at booking header
        bookingHeaderRepository.cancelStatus(BookingStatusEnum.CANCEL, bookingId);

        //For change expired to booking detail unloading
        if (bookingHeaderOptional.get().getBooking_type().equals(ItemTypeEnum.UNLOADING)) {
            bookingDetailUnloadingRepository.updatePaymentStatus(bookingId, PaymentStatusEnum.CANCEL);
        }

        //Validate if payment no exist
        Optional<XenditVA> xenditVAOptional = xenditRepository.findWithBookingID(bookingId);
        if (xenditVAOptional.isEmpty()){
            return xenditResponse;
        }

        XenditVA xenditVA = xenditVAOptional.get();
        BookingHeader bookingHeader = bookingHeaderOptional.get();

        if (xenditVA.getPayment_status().toString().equalsIgnoreCase("PENDING")){
                xenditResponse.setName(bookingHeader.getFull_name());
                xenditResponse.setCurrency("IDR");
                xenditResponse.setAmount(xenditVA.getAmount());
                xenditResponse.setExternalId("va-" + xenditVA.getBank_code() + "-" + xenditVA.getPhone_number());
                xenditResponse.setOwnerId(bookingHeader.getDepoOwnerAccount().getId().toString());
                xenditResponse.setBankCode(xenditVA.getBank_code());
                xenditResponse.setAccountNumber(xenditVA.getAccount_number());
                xenditResponse.setIsClosed(Boolean.TRUE);
                xenditResponse.setIsSingleUse(Boolean.TRUE);
                //for changed to expired VA
                cancelVirtualAccount(xenditVA.getXendit_id(), bookingHeader.getDepoOwnerAccount().getXenditVaId(), xenditResponse);

                //for changed to expired invoice
                getCancelInvoice(xenditVA.getInvoice_id(), xenditResponse, bookingHeader.getDepoOwnerAccount().getXenditVaId());

                //for changed to expired DB
                xenditRepository.updateCancelOrder(XenditEnum.CANCEL, xenditVA.getId());
                xenditResponse.setStatus("CANCEL");
        }

                return xenditResponse;

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
