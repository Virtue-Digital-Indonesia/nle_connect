package com.nle.shared.service.booking;

import com.nle.constant.enums.BookingStatusEnum;
import com.nle.exception.BadRequestException;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.entity.Item;
import com.nle.io.entity.booking.BookingDetail;
import com.nle.io.entity.booking.BookingHeader;
import com.nle.io.repository.DepoOwnerAccountRepository;
import com.nle.io.repository.ItemRepository;
import com.nle.io.repository.booking.BookingDetailRepository;
import com.nle.io.repository.booking.BookingHeaderRepository;
import com.nle.shared.service.item.ItemServiceImpl;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.booking.CreateBookingRequest;
import com.nle.ui.model.request.booking.BookingDetailRequest;
import com.nle.ui.model.request.search.BookingSearchRequest;
import com.nle.ui.model.response.ItemResponse;
import com.nle.ui.model.response.booking.BookingResponse;
import com.nle.util.NleUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingHeaderRepository bookingHeaderRepository;
    private final BookingDetailRepository bookingDetailRepository;
    private final DepoOwnerAccountRepository depoOwnerAccountRepository;
    private final ItemRepository itemRepository;
    private DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    @Override
    public PagingResponseModel<BookingResponse> SearchByPhone(String phoneNumber, Pageable pageable) {

        Page<BookingHeader> headerPage = bookingHeaderRepository.getOrderByPhoneNumber(phoneNumber, pageable);
        System.out.println(headerPage);
        return new PagingResponseModel<>(headerPage.map(this::convertToResponse));
    }

    @Override
    public BookingResponse CreateOrder(CreateBookingRequest request) {
        BookingHeader entity = new BookingHeader();
        BeanUtils.copyProperties(request, entity);
        entity.setTxDateFormatted(LocalDateTime.parse(request.getTx_date(), DATE_TIME_FORMATTER));

        if (entity.getBooking_status() == null) {
            entity.setBooking_status(BookingStatusEnum.WAITING);
        }

        Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountRepository.findById(request.getDepo_id());
        if (depoOwnerAccount.isEmpty()) throw new BadRequestException("Depo Id cannot be find");
        entity.setDepoOwnerAccount(depoOwnerAccount.get());
        BookingHeader savedHeader = bookingHeaderRepository.save(entity);

        for (BookingDetailRequest detailRequest : request.getDetailRequests()) {
            BookingDetail bookingDetail = new BookingDetail();

            Optional<Item> item = itemRepository.findById(detailRequest.getItemId());
            if (item.isEmpty()) throw new BadRequestException("Cannot find item");

            if (!item.get().getDepoOwnerAccount().getCompanyEmail().equals(depoOwnerAccount.get().getCompanyEmail()))
                throw new BadRequestException("this item is not from depo " + depoOwnerAccount.get().getCompanyEmail());

            bookingDetail.setBookingHeader(savedHeader);
            bookingDetail.setItem(item.get());

            if (detailRequest.getPrice() != -1)
                bookingDetail.setPrice(detailRequest.getPrice());
            else
                bookingDetail.setPrice(item.get().getPrice());

            bookingDetailRepository.save(bookingDetail);
        }

        return this.convertToResponse(savedHeader);
    }

    @Override
    public PagingResponseModel<BookingResponse> searchBooking(BookingSearchRequest request, Pageable pageable) {

        if (request.getPhone_number() == null || request.getPhone_number().trim().isEmpty())
            throw new BadRequestException("phone number cannot be null");

        Page<BookingHeader> headerPage = bookingHeaderRepository.searchBooking(request, pageable);
        return new PagingResponseModel<>(headerPage.map(this::convertToResponse));
    }

    private BookingResponse convertToResponse(BookingHeader entity) {
        BookingResponse response = new BookingResponse();
        List<ItemResponse> orderDetailResponseList = new ArrayList<>();

        BeanUtils.copyProperties(entity, response);
        List<BookingDetail> orderDetailList = bookingDetailRepository.getAllByBookingHeaderId(entity.getId());
        for (BookingDetail bookingDetail : orderDetailList){
            Item item = bookingDetail.getItem();
            ItemResponse itemResponse = ItemServiceImpl.convertToResponse(item);
            itemResponse.setPrice(bookingDetail.getPrice());
            orderDetailResponseList.add(itemResponse);
        }

        response.setItems(orderDetailResponseList);
        return response;
    }
}
