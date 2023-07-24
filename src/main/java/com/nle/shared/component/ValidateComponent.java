package com.nle.shared.component;

import com.nle.constant.enums.BookingStatusEnum;
import com.nle.exception.BadRequestException;
import com.nle.exception.CommonException;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.entity.booking.BookingHeader;
import com.nle.io.repository.DepoOwnerAccountRepository;
import com.nle.io.repository.booking.BookingHeaderRepository;
import com.nle.ui.model.response.XenditResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Transactional
@RequiredArgsConstructor
public class ValidateComponent {
    private final BookingHeaderRepository bookingHeaderRepository;
    private final DepoOwnerAccountRepository depoOwnerAccountRepository;
    public DepoOwnerAccount ValidateDepoAccount (Long request_depo_id){
        Optional<DepoOwnerAccount> accountOptional = depoOwnerAccountRepository.findById(request_depo_id);
        if (accountOptional.isEmpty())
            throw new BadRequestException("can't find depo");

        return accountOptional.get();
    }
    public ValidateComponent ValidateXenditVA (DepoOwnerAccount doa){
        if (doa.getXenditVaId() == null)
            throw new BadRequestException("this depo is not active");

        return this;
    }

    public BookingHeader ValidateBookingHeader(Long booking_header_id, long depo_id){
        Optional<BookingHeader> optionalBookingHeader = bookingHeaderRepository.findById(booking_header_id);
        if (optionalBookingHeader.isEmpty())
            throw new CommonException("not found booking id");
        if (optionalBookingHeader.get().getBooking_status() != BookingStatusEnum.WAITING)
            throw new BadRequestException("this booking status is "+optionalBookingHeader.get().getBooking_status());
        if (optionalBookingHeader.get().getDepoOwnerAccount().getId() != depo_id)
            throw new BadRequestException("this booking not for this depo");

        return optionalBookingHeader.get();
    }
}
