package com.nle.util;

import com.nle.io.entity.Item;
import com.nle.io.entity.booking.BookingDetailLoading;
import com.nle.io.entity.booking.BookingDetailUnloading;
import com.nle.io.entity.booking.BookingHeader;
import com.nle.shared.service.fleet.DepoFleetServiceImpl;
import com.nle.ui.model.response.ApplicantResponse;
import com.nle.ui.model.response.ItemResponse;
import com.nle.ui.model.response.booking.BookingResponse;
import com.nle.ui.model.response.booking.DetailLoadingResponse;
import com.nle.ui.model.response.booking.DetailUnloadingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ConvertResponse {

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

    private static List<ItemResponse> convertBookingUnloadingDetail(BookingHeader entity, List<ItemResponse> orderDetailResponseList) {

        Set<BookingDetailUnloading> unloadingList = entity.getBookingDetailUnloadings();
        if (unloadingList == null || unloadingList.isEmpty()) {
            return orderDetailResponseList;
        }

        for (BookingDetailUnloading unloading : unloadingList) {
//              convert item to unloading response
            Item item = unloading.getItem();
            DetailUnloadingResponse unloadingResponse = new DetailUnloadingResponse();
            BeanUtils.copyProperties(item, unloadingResponse);
//              convert unloading to unloading response
            unloadingResponse.setPrice(unloading.getPrice());
            unloadingResponse.setContainer_number(unloading.getContainer_number());
//              convert fleet
            if (item.getDepoFleet() != null) {
                unloadingResponse.setFleet(DepoFleetServiceImpl.convertFleetToResponse(item.getDepoFleet()));
            }
            orderDetailResponseList.add(unloadingResponse);
        }

        return orderDetailResponseList;
    }

    private static List<ItemResponse> convertBookingLoadingDetail(BookingHeader entity, List<ItemResponse> orderDetailResponseList) {

        Set<BookingDetailLoading> loadingList = entity.getBookingDetailLoadings();
        if(loadingList == null || loadingList.isEmpty())
            return orderDetailResponseList;

        for (BookingDetailLoading loading : loadingList) {
            DetailLoadingResponse loadingResponse = new DetailLoadingResponse();
            Item item = loading.getItem();
            BeanUtils.copyProperties(item, loadingResponse);

            loadingResponse.setPrice(loading.getPrice());
            loadingResponse.setQuantity(loading.getQuantity());

            if (item.getDepoFleet() != null) {
                loadingResponse.setFleet(DepoFleetServiceImpl.convertFleetToResponse(item.getDepoFleet()));
            }
            orderDetailResponseList.add(loadingResponse);
        }

        return orderDetailResponseList;
    }

}
