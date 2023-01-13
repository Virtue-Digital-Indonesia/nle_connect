package com.nle.util;

import com.nle.io.entity.XenditVA;
import com.nle.io.entity.booking.BookingDetailLoading;
import com.nle.io.entity.booking.BookingDetailUnloading;
import com.nle.io.entity.booking.BookingHeader;
import com.nle.ui.model.response.*;
import com.nle.ui.model.response.booking.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ConvertBookingUtil {

    public static BookingResponse convertBookingHeaderToResponse(BookingHeader entity) {

        // convert Booking Header
        BookingResponse response = new BookingResponse();
        BeanUtils.copyProperties(entity, response);

        // convert depo / applicant
        ApplicantResponse applicantResponse = new ApplicantResponse();
        BeanUtils.copyProperties(entity.getDepoOwnerAccount(), applicantResponse);
        response.setDepo(applicantResponse);

        // convert item
        List<ItemResponse> orderDetailResponseList = new ArrayList<>();
        orderDetailResponseList = convertBookingUnloadingDetail(entity, orderDetailResponseList);
        orderDetailResponseList = convertBookingLoadingDetail(entity, orderDetailResponseList);
        response.setItems(orderDetailResponseList);

        // Add invoice_no,bon_no,bank_code
        response.setInvoice_no(getInvoice(entity).getInvoice_no());
        response.setBon_no(getBonList(entity));
        response.setBank_code(getBankCode(entity));

        return response;
    }

    public static List<ItemResponse> convertBookingUnloadingDetail(BookingHeader entity,
            List<ItemResponse> orderDetailResponseList) {

        Set<BookingDetailUnloading> unloadingList = entity.getBookingDetailUnloadings();
        if (unloadingList == null || unloadingList.isEmpty()) {
            return orderDetailResponseList;
        }

        for (BookingDetailUnloading unloading : unloadingList) {
            DetailUnloadingResponse unloadingResponse = convertUnloading(unloading);
            orderDetailResponseList.add(unloadingResponse);
        }

        return orderDetailResponseList;
    }

    public static DetailUnloadingResponse convertUnloading(BookingDetailUnloading detail) {
        // convert item to unloading response
        DetailUnloadingResponse unloadingResponse = new DetailUnloadingResponse();
        BeanUtils.copyProperties(detail.getItem(), unloadingResponse);
        // convert unloading to unloading response
        unloadingResponse.setPrice(detail.getPrice());
        unloadingResponse.setContainer_number(detail.getContainer_number());

        // convert item type
        ItemTypeResponse itemTypeResponse = new ItemTypeResponse();
        BeanUtils.copyProperties(detail.getItem().getItem_name(), itemTypeResponse);
        unloadingResponse.setItem_name(itemTypeResponse);

        // convert fleet
        if (detail.getItem().getDepoFleet() != null) {
            unloadingResponse.setFleet(ConvertResponseUtil.convertDepoFleetToResponse(detail.getItem().getDepoFleet()));
        }

        return unloadingResponse;
    }

    public static List<ItemResponse> convertBookingLoadingDetail(BookingHeader entity,
            List<ItemResponse> orderDetailResponseList) {

        Set<BookingDetailLoading> loadingList = entity.getBookingDetailLoadings();
        if (loadingList == null || loadingList.isEmpty())
            return orderDetailResponseList;

        for (BookingDetailLoading loading : loadingList) {
            DetailLoadingResponse loadingResponse = convertLoading(loading);
            orderDetailResponseList.add(loadingResponse);
        }

        return orderDetailResponseList;
    }

    public static DetailLoadingResponse convertLoading(BookingDetailLoading detail) {
        DetailLoadingResponse loadingResponse = new DetailLoadingResponse();
        BeanUtils.copyProperties(detail.getItem(), loadingResponse);

        loadingResponse.setPrice(detail.getPrice());
        loadingResponse.setQuantity(detail.getQuantity());

        // convert item type
        ItemTypeResponse itemTypeResponse = new ItemTypeResponse();
        BeanUtils.copyProperties(detail.getItem().getItem_name(), itemTypeResponse);
        loadingResponse.setItem_name(itemTypeResponse);

        if (detail.getItem().getDepoFleet() != null)
            loadingResponse.setFleet(ConvertResponseUtil.convertDepoFleetToResponse(detail.getItem().getDepoFleet()));

        return loadingResponse;
    }

    public static String getBankCode(BookingHeader entity){
        List<String> bankCodeResponses = new ArrayList<>();
        List<XenditVA> xenditVAS = entity.getXenditVAS();

        for (XenditVA xenditVA : xenditVAS) {
//            BankCodeResponse bankCodeResponse = new BankCodeResponse();
            if (xenditVA.getPayment_status().toString().equals("PENDING")){
//            bankCodeResponse.setBank_code(xenditVA.getBank_code());
            bankCodeResponses.add(xenditVA.getBank_code());
            }
        }
        String bank_code;
        if (bankCodeResponses.isEmpty()){
            bank_code = null;
        } else {
            bank_code = bankCodeResponses.get(0);
        }
        return bank_code;
    }

//    public static List<BankCodeResponse> getBankCode(BookingHeader entity){
//        List<BankCodeResponse> bankCodeResponses = new ArrayList<>();
//        List<XenditVA> xenditVAS = entity.getXenditVAS();
//
//        for (XenditVA xenditVA : xenditVAS) {
//            BankCodeResponse bankCodeResponse = new BankCodeResponse();
//            if (xenditVA.getPayment_status().toString().equals("PENDING")){
//                bankCodeResponse.setBank_code(xenditVA.getBank_code());
//                bankCodeResponses.add(bankCodeResponse);
//            }
//        }
//
//        return bankCodeResponses;
//    }

    public static BankCodeResponse convertBankCode(XenditVA xenditVA) {
        BankCodeResponse bankCodeResponse = new BankCodeResponse();
        BeanUtils.copyProperties(xenditVA.getBank_code(), bankCodeResponse);

        if (xenditVA.getPayment_status().equals("PENDING")){
        bankCodeResponse.setBank_code(xenditVA.getBank_code());
        } else {
            bankCodeResponse.setBank_code(null);
        }

        return bankCodeResponse;
    }


    public static InvoiceResponse getInvoice(BookingHeader entity) {
        InvoiceResponse invoiceResponse = new InvoiceResponse();
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String formattedDate = entity.getCreatedDate().format(dateTimeFormatter);
            invoiceResponse.setInvoice_no("INV/" + formattedDate + "/" + String.format("%04d", entity.getId()));
        } catch (Exception e){
            invoiceResponse.setInvoice_no(null);
        }
        return invoiceResponse;
    }

    public static List<BonResponse> getBonList(BookingHeader entity) {
        List<BonResponse> bon = new ArrayList<>();
        try {
            if (entity.getBooking_type().equals("LOADING")){
            Set<BookingDetailLoading> loadingList = entity.getBookingDetailLoadings();
            for (BookingDetailLoading loading : loadingList) {
                BonResponse bonResponse = new BonResponse();
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                String formattedDate = entity.getCreatedDate().format(dateTimeFormatter);
                String bonNo = ("BON/" + formattedDate + "/"+ String.format("%04d", loading.getId()));
                bonResponse.setBon_no(bonNo);
                bon.add(bonResponse);
            }
            } else {
                Set<BookingDetailUnloading> unloadingsList = entity.getBookingDetailUnloadings();
                for (BookingDetailUnloading unloading : unloadingsList) {
                    BonResponse bonResponse = new BonResponse();
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                    String formattedDate = entity.getCreatedDate().format(dateTimeFormatter);
                    String bonNo = ("BON/" + formattedDate + "/"+ String.format("%04d", unloading.getId()));
                    bonResponse.setBon_no(bonNo);
                    bon.add(bonResponse);
                }
            }
        } catch (Exception e){
            bon = null;
        }
        return bon;
    }



}
