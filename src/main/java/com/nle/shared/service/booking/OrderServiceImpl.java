package com.nle.shared.service.booking;

import com.nle.exception.BadRequestException;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.entity.booking.BookingHeader;
import com.nle.io.repository.DepoOwnerAccountRepository;
import com.nle.io.repository.booking.BookingHeaderRepository;
import com.nle.security.SecurityUtils;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.search.BookingSearchRequest;
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
    private final DepoOwnerAccountRepository depoOwnerAccountRepository;

    @Override
    public PagingResponseModel<BookingResponse> getOrderDepo(Pageable pageable) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isEmpty()) {
            return new PagingResponseModel<>();
        }

        Page<BookingHeader> listBooking = bookingHeaderRepository.getOrderDepo(currentUserLogin.get(), pageable);
        return new PagingResponseModel<>(listBooking.map(ConvertBookingUtil::convertBookingHeaderToResponse));
    }

    @Override
    public PagingResponseModel<BookingResponse> searchOrderDepo(BookingSearchRequest request, Pageable pageable) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isEmpty()) {
            return new PagingResponseModel<>();
        }
        Page<BookingHeader> listBooking = bookingHeaderRepository.searchOrder(request,currentUserLogin.get(), pageable);
        System.out.println(listBooking.getSize());
        return new PagingResponseModel<>(listBooking.map(ConvertBookingUtil::convertBookingHeaderToResponse));
    }

    //Method validate for depo
    @Override
    public DepoOwnerAccount orderValidate(Optional<String> username) {
        if (username.isEmpty())
            throw new BadRequestException("Invalid Token!");

        Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountRepository.findByCompanyEmail(username.get());
        if (depoOwnerAccount.isEmpty())
            throw new BadRequestException("Can't Find Depo!");

        DepoOwnerAccount doa = depoOwnerAccount.get();
        if (doa.getXenditVaId() == null)
            throw new BadRequestException("This depo is not active!");

        return doa;
    }
}
