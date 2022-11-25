package com.nle.util;

import com.nle.io.entity.booking.BookingDetailLoading;
import com.nle.io.entity.booking.BookingDetailUnloading;
import com.nle.io.entity.booking.BookingHeader;
import com.nle.ui.model.response.ApplicantResponse;
import com.nle.ui.model.response.ItemResponse;
import com.nle.ui.model.response.booking.BookingResponse;
import com.nle.ui.model.response.booking.DetailLoadingResponse;
import com.nle.ui.model.response.booking.DetailUnloadingResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class ConvertBookingUtil {

    public static BookingResponse convertBookingHeaderToResponse(BookingHeader entity) {

//      convert Booking Header
        BookingResponse response = new BookingResponse();
        BeanUtils.copyProperties(entity, response);

//      convert depo / applicant
        ApplicantResponse applicantResponse = new ApplicantResponse();
        BeanUtils.copyProperties(entity.getDepoOwnerAccount(), applicantResponse);
        response.setDepo(applicantResponse);

//      convert item
        List<ItemResponse> orderDetailResponseList = new ArrayList<>();
        orderDetailResponseList = convertBookingUnloadingDetail(entity, orderDetailResponseList);
        orderDetailResponseList = convertBookingLoadingDetail(entity, orderDetailResponseList);
        response.setItems(orderDetailResponseList);
        return response;
    }

    public static List<ItemResponse> convertBookingUnloadingDetail(BookingHeader entity, List<ItemResponse> orderDetailResponseList) {

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

    public static DetailUnloadingResponse convertUnloading(BookingDetailUnloading detail){
        //convert item to unloading response
        DetailUnloadingResponse unloadingResponse = new DetailUnloadingResponse();
        BeanUtils.copyProperties(detail.getItem(), unloadingResponse);
        //convert unloading to unloading response
        unloadingResponse.setPrice(detail.getPrice());
        unloadingResponse.setContainer_number(detail.getContainer_number());
        //convert fleet
        if (detail.getItem().getDepoFleet() != null) {
            unloadingResponse.setFleet(ConvertResponseUtil.convertDepoFleetToResponse(detail.getItem().getDepoFleet()));
        }

        return unloadingResponse;
    }

    public static List<ItemResponse> convertBookingLoadingDetail(BookingHeader entity, List<ItemResponse> orderDetailResponseList) {

        Set<BookingDetailLoading> loadingList = entity.getBookingDetailLoadings();
        if(loadingList == null || loadingList.isEmpty())
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

        if (detail.getItem().getDepoFleet() != null)
            loadingResponse.setFleet(ConvertResponseUtil.convertDepoFleetToResponse(detail.getItem().getDepoFleet()));

        return loadingResponse;
    }

}
