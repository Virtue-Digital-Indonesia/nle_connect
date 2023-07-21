package com.nle.shared.component;

import com.nle.constant.enums.BookingStatusEnum;
import com.nle.constant.enums.XenditEnum;
import com.nle.exception.BadRequestException;
import com.nle.exception.CommonException;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.entity.XenditVA;
import com.nle.io.entity.booking.BookingHeader;
import com.nle.io.repository.DepoOwnerAccountRepository;
import com.nle.io.repository.booking.BookingHeaderRepository;
import com.nle.security.SecurityUtils;
import com.nle.shared.service.xendit.XenditServiceImpl;
import com.nle.ui.model.response.XenditResponse;
import com.xendit.model.FixedVirtualAccount;
import com.xendit.model.Invoice;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Transactional
@RequiredArgsConstructor
public class XenditComponent {

    private final String VA_CODE_GENERAL = XenditServiceImpl.VA_CODE_GENERAL;
    private final String VA_CODE_MANDIRI = XenditServiceImpl.VA_CODE_MANDIRI;

    public  String initialVaNumber(String phoneNumber, String bankCode){
        if (phoneNumber == null || phoneNumber.isEmpty() )
            throw new BadRequestException("Phone number cannot be null!");

        if (bankCode == null || bankCode.isEmpty())
            throw new BadRequestException("Bank code cannot be null!");

        String va_number;
        int va_index = phoneNumber.length();
        if (bankCode.equalsIgnoreCase("MANDIRI")){
            va_number = VA_CODE_MANDIRI + phoneNumber.substring(va_index - 9, va_index);
        } else if (bankCode.equalsIgnoreCase("BCA") || bankCode.equalsIgnoreCase("SAHABAT_SAMPOERNA")) {
            va_number = VA_CODE_GENERAL + phoneNumber.substring(va_index - 6, va_index);
        } else {
            va_number = VA_CODE_GENERAL + phoneNumber.substring(va_index - 7, va_index);
        }

        return va_number;
    }

    public XenditVA FactoryXenditVA(FixedVirtualAccount closedVA, BookingHeader bookingHeader, Invoice invoice){
        XenditVA xenditVA = new XenditVA();
        xenditVA.setXendit_id(closedVA.getId());
        xenditVA.setBooking_header_id(bookingHeader);
        xenditVA.setInvoice_id(invoice.getId());
        xenditVA.setDisbursement_id(null);
        xenditVA.setPayment_id(null);
        xenditVA.setPhone_number(bookingHeader.getPhone_number());
        xenditVA.setAmount(closedVA.getExpectedAmount());
        xenditVA.setBank_code(closedVA.getBankCode());
        xenditVA.setExpiry_date(invoice.getExpiryDate());
        xenditVA.setAccount_number(closedVA.getAccountNumber());
        xenditVA.setPayment_status(XenditEnum.PENDING);

        return xenditVA;
    }

    public XenditResponse createXenditResponse(FixedVirtualAccount closedVA, Invoice invoice) {
        XenditResponse response = new XenditResponse();
        BeanUtils.copyProperties(closedVA, response);
        response.setExpirationDate(String.valueOf(closedVA.getExpirationDate()));
        response.setAmount(closedVA.getExpectedAmount());
        response.setInvoice_url(invoice.getInvoiceUrl());
        return response;
    }
}
