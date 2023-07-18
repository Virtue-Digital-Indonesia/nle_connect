package com.nle.shared.service.xendit;

import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.entity.XenditVA;
import com.nle.io.entity.booking.BookingHeader;
import com.nle.ui.model.request.xendit.XenditCallbackPayload;
import com.nle.ui.model.request.xendit.XenditDisCallbackPayload;
import com.nle.ui.model.request.xendit.XenditRequest;
import com.nle.ui.model.response.XenditListResponse;
import com.nle.ui.model.response.XenditResponse;

import java.util.List;

public interface XenditService {
    XenditResponse ControllerCreateVirtualAccount(XenditRequest request);

    XenditResponse CreateNewVirtualAccount(XenditRequest request, DepoOwnerAccount depo, BookingHeader bookingHeader);

    void CallbackInvoice(XenditCallbackPayload payload);

    String createXenditAccount(DepoOwnerAccount depoOwnerAccount);

    XenditResponse getXenditByBookingId(Long booking_id);

    XenditResponse CreatePaymentOrder(XenditRequest request);

    List<XenditListResponse> getMultipleXenditByPhone();

    void CreateDisbursements(String xendit_id, XenditVA xenditVA);

    String CallbackDisbursements(XenditDisCallbackPayload payload);

    XenditResponse cancelOrderXendit(Long bookingId);

}
