package com.nle.shared.service.booking;

import com.nle.io.entity.booking.BookingHeader;
import com.nle.io.repository.booking.BookingHeaderRepository;
import com.nle.security.SecurityUtils;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.response.booking.BookingResponse;
import com.nle.util.ConvertBookingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class OrderServiceImpl implements OrderService{

    private final BookingHeaderRepository bookingHeaderRepository;

    @Override
    public PagingResponseModel<BookingResponse> getOrderDepo(Pageable pageable) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isEmpty()) {
            return new PagingResponseModel<>();
        }

        Page<BookingHeader> listBooking = bookingHeaderRepository.getOrderDepo(currentUserLogin.get(), pageable);
        return new PagingResponseModel<>(listBooking.map(ConvertBookingUtil::convertBookingHeaderToResponse));
    }
}
