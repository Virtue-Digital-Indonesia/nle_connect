package com.nle.util;

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
        response.setBon_no(orderDetailResponseList);
//        response.setBank_code();

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

    public static List<String> getBonListLoading(BookingHeader entity) {
        Set<BookingDetailLoading> loadingList = entity.getBookingDetailLoadings();
        List<String> bon = new ArrayList<>();
        if (loadingList == null || loadingList.isEmpty())
            return bon;

        for (BookingDetailLoading loading : loadingList) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String formattedDate = entity.getCreatedDate().format(dateTimeFormatter);
            String bonNo = ("BON/" + formattedDate + "/" + String.format("%04d", loading.getId()));
            bon.add(bonNo);
        }

        return bon;
    }

    public static BonLoadingResponse getBonLoading(BookingHeader entity,BookingDetailLoading detailLoading){
        BonLoadingResponse bonLoadingResponse = new BonLoadingResponse();
        BeanUtils.copyProperties(detailLoading.getItem(), bonLoadingResponse);
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String formattedDate = entity.getCreatedDate().format(dateTimeFormatter);
            String bonNo = ("BON/" + formattedDate + "/" + String.format("%04d", detailLoading.getId()));
            bonLoadingResponse.setBon_no(bonNo);
        } catch (Exception e){
            bonLoadingResponse.setBon_no(null);
        }
        return bonLoadingResponse;
    }

}
