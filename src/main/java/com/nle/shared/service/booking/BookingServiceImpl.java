package com.nle.shared.service.booking;

import com.nle.constant.enums.BookingStatusEnum;
import com.nle.constant.enums.ItemTypeEnum;
import com.nle.exception.BadRequestException;
import com.nle.exception.CommonException;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.entity.Item;
import com.nle.io.entity.booking.BookingDetailLoading;
import com.nle.io.entity.booking.BookingDetailUnloading;
import com.nle.io.entity.booking.BookingHeader;
import com.nle.io.repository.DepoOwnerAccountRepository;
import com.nle.io.repository.ItemRepository;
import com.nle.io.repository.booking.BookingDetailUnloadingRepository;
import com.nle.io.repository.booking.BookingHeaderRepository;
import com.nle.io.repository.booking.BookingLoadingRepository;
import com.nle.security.AuthoritiesConstants;
import com.nle.security.SecurityUtils;
import com.nle.security.jwt.TokenProvider;
import com.nle.shared.dto.verihubs.VerihubsResponseDTO;
import com.nle.shared.service.OTPService;
import com.nle.ui.model.JWTToken;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.booking.*;
import com.nle.ui.model.request.search.BookingSearchRequest;
import com.nle.ui.model.response.ItemResponse;
import com.nle.ui.model.response.booking.BookingResponse;
import com.nle.util.ConvertBookingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingHeaderRepository bookingHeaderRepository;
    private final BookingDetailUnloadingRepository bookingDetailUnloadingRepository;
    private final BookingLoadingRepository bookingLoadingRepository;
    private final DepoOwnerAccountRepository depoOwnerAccountRepository;
    private final ItemRepository itemRepository;
    private final OTPService otpService;
    private final TokenProvider tokenProvider;
    private DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    @Override
    public BookingResponse getBookingById(Long booking_id) {
        Optional<String> phone = SecurityUtils.getCurrentUserLogin();
        if (phone.isEmpty())
            throw new BadRequestException("need to log in");

        String phone_number = phone.get();
        if (booking_id == null) throw new BadRequestException("booking_id cannot be nulll");
        if (phone_number == null || phone_number.trim().isEmpty()) throw new BadRequestException("phone number cannot be null");

        Optional<BookingHeader> optional = bookingHeaderRepository.findById(booking_id);
        if (optional.isEmpty()) throw new CommonException("Cannot find booking");

        BookingHeader bookingHeader = optional.get();
        if (!bookingHeader.getPhone_number().equals(phone_number)) throw new BadRequestException("this booking is not belong to phone number: " + phone_number);
        return ConvertBookingUtil.convertBookingHeaderToResponse(bookingHeader);
    }

    @Override
    public PagingResponseModel<BookingResponse> SearchByPhone(Pageable pageable) {
        Optional<String> phoneNumber = SecurityUtils.getCurrentUserLogin();
        Page<BookingHeader> headerPage = bookingHeaderRepository.getOrderByPhoneNumber(phoneNumber.get(), pageable);
        return new PagingResponseModel<>(headerPage.map(ConvertBookingUtil::convertBookingHeaderToResponse));
    }

    @Override
    public VerihubsResponseDTO sendOtpMobile (String phoneNumber) {
        VerihubsResponseDTO response = otpService.sendOTP(phoneNumber);
        return response;
    }

    @Override
    public JWTToken verifOTP(String otp, String phone_number) {
        ResponseEntity<String> verify = otpService.verifOTP(otp, phone_number);

        if (verify.getStatusCodeValue() != 200) return null;

        String token = tokenProvider.generateManualToken(phone_number, AuthoritiesConstants.BOOKING_CUSTOMER);
        return new JWTToken(token);
    }

    @Override
    public BookingResponse createBookingUnloading(CreateBookingUnloading request) {

        BookingHeader savedHeader = saveBookingHeader(request, ItemTypeEnum.UNLOADING);
        String companyEmail = savedHeader.getDepoOwnerAccount().getCompanyEmail();
        List<ItemResponse> detailList = new ArrayList<>();

        for (DetailUnloadingRequest detailRequest : request.getDetailRequests()) {
            BookingDetailUnloading bookingDetailUnloading = new BookingDetailUnloading();

            Optional<Item> item = itemRepository.findById(detailRequest.getItemId());
            if (item.isEmpty()) throw new BadRequestException("Cannot find item");

            if (!item.get().getDepoOwnerAccount().getCompanyEmail().equals(companyEmail))
                throw new BadRequestException("this item is not from depo " + companyEmail);
            if (item.get().getType() != ItemTypeEnum.UNLOADING)
                throw new BadRequestException("this type item is not unloading");

            bookingDetailUnloading.setBookingHeader(savedHeader);
            bookingDetailUnloading.setItem(item.get());
            bookingDetailUnloading.setContainer_number(detailRequest.getContainer_number());

            if (detailRequest.getPrice() != -1)
                bookingDetailUnloading.setPrice(detailRequest.getPrice());
            else
                bookingDetailUnloading.setPrice(item.get().getPrice());

            BookingDetailUnloading savedDetail = bookingDetailUnloadingRepository.save(bookingDetailUnloading);
            detailList.add(ConvertBookingUtil.convertUnloading(savedDetail));
        }

        BookingResponse response = ConvertBookingUtil.convertBookingHeaderToResponse(savedHeader);
        response.setItems(detailList);
        return response;
    }

    public BookingResponse createBookingLoading(CreateBookingLoading request){
        BookingHeader savedHeader = saveBookingHeader(request, ItemTypeEnum.LOADING);
        String companyEmail = savedHeader.getDepoOwnerAccount().getCompanyEmail();
        List<ItemResponse> detailList = new ArrayList<>();

        for (DetailLoadingRequest detailLoadingRequest : request.getDetailRequests()) {
            BookingDetailLoading loading = new BookingDetailLoading();

            Optional<Item> item = itemRepository.findById(detailLoadingRequest.getItemId());
            if (item.isEmpty()) throw new BadRequestException("Cannot find item");

            if (!item.get().getDepoOwnerAccount().getCompanyEmail().equals(companyEmail))
                throw new BadRequestException("this item is not from depo " + companyEmail);
            if (item.get().getType() != ItemTypeEnum.LOADING)
                throw new BadRequestException("this type item is not LOADING");

            if (detailLoadingRequest.getPrice() != -1)
                loading.setPrice(detailLoadingRequest.getPrice());
            else
                loading.setPrice(item.get().getPrice());

            if (detailLoadingRequest.getQuantity() <= 0)
                throw new BadRequestException("quantity cannot less than 1");

            loading.setQuantity(detailLoadingRequest.getQuantity());
            loading.setBookingHeader(savedHeader);
            loading.setItem(item.get());
            BookingDetailLoading savedDetail = bookingLoadingRepository.save(loading);
            detailList.add(ConvertBookingUtil.convertLoading(savedDetail));
        }
        BookingResponse response = ConvertBookingUtil.convertBookingHeaderToResponse(savedHeader);
        response.setItems(detailList);
        return response;
    }

    @Override
    public PagingResponseModel<BookingResponse> searchBooking(BookingSearchRequest request, Pageable pageable) {

        Optional<String> phone = SecurityUtils.getCurrentUserLogin();
        request.setPhone_number(phone.get());
        Page<BookingHeader> headerPage = bookingHeaderRepository.searchBooking(request, pageable);
        return new PagingResponseModel<>(headerPage.map(ConvertBookingUtil::convertBookingHeaderToResponse));
    }

    private BookingHeader saveBookingHeader(BookingHeaderRequest request, ItemTypeEnum booking_type) {
        BookingHeader entity = new BookingHeader();
        BeanUtils.copyProperties(request, entity);
        entity.setTxDateFormatted(LocalDateTime.parse(request.getTx_date(), DATE_TIME_FORMATTER));

        entity.setBooking_status(BookingStatusEnum.WAITING);
        entity.setBooking_type(booking_type);

        Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountRepository.findById(request.getDepo_id());
        if (depoOwnerAccount.isEmpty()) throw new BadRequestException("Depo Id cannot be find");
        entity.setDepoOwnerAccount(depoOwnerAccount.get());
        BookingHeader savedHeader = bookingHeaderRepository.save(entity);

        return savedHeader;
    }

}
