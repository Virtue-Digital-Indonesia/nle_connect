package com.nle.shared.service.order;

import com.nle.constant.enums.BookingStatusEnum;
import com.nle.exception.BadRequestException;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.entity.Item;
import com.nle.io.entity.order.OrderDetail;
import com.nle.io.entity.order.OrderHeader;
import com.nle.io.repository.DepoFleetRepository;
import com.nle.io.repository.DepoOwnerAccountRepository;
import com.nle.io.repository.ItemRepository;
import com.nle.io.repository.OrderRepository;
import com.nle.io.repository.order.OrderDetailRepository;
import com.nle.shared.service.item.ItemServiceImpl;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.order.CreateOrderHeaderRequest;
import com.nle.ui.model.request.order.OrderDetailRequest;
import com.nle.ui.model.response.ItemResponse;
import com.nle.ui.model.response.order.OrderHeaderResponse;
import com.nle.util.NleUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final DepoOwnerAccountRepository depoOwnerAccountRepository;
    private final ItemRepository itemRepository;
    private final DepoFleetRepository depoFleetRepository;

    @Override
    public PagingResponseModel<OrderHeaderResponse> SearchByPhone(String phoneNumber, Pageable pageable) {

        Page<OrderHeader> headerPage = orderRepository.getOrderByPhoneNumber(phoneNumber, pageable);
        System.out.println(headerPage);
        return new PagingResponseModel<>(headerPage.map(this::convertToResponse));
    }

    @Override
    public OrderHeaderResponse CreateOrder(CreateOrderHeaderRequest request) {
        OrderHeader entity = new OrderHeader();
        BeanUtils.copyProperties(request, entity);
        entity.setTxDateFormatted(NleUtil.formatTxDate(entity.getTx_date()));

        if (entity.getOrder_status() == null) {
            entity.setOrder_status(BookingStatusEnum.WAITING);
        }

        Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountRepository.findById(request.getDepo_id());
        if (depoOwnerAccount.isEmpty()) throw new BadRequestException("Depo Id cannot be find");
        entity.setDepoOwnerAccount(depoOwnerAccount.get());
        OrderHeader savedHeader = orderRepository.save(entity);

        for (OrderDetailRequest detailRequest : request.getDetailRequests()) {
            OrderDetail orderDetail = new OrderDetail();

            Optional<Item> item = itemRepository.findById(detailRequest.getItemId());
            if (item.isEmpty()) throw new BadRequestException("Cannot find item");

            if (!item.get().getDepoOwnerAccount().getCompanyEmail().equals(depoOwnerAccount.get().getCompanyEmail()))
                throw new BadRequestException("this item is not from depo " + depoOwnerAccount.get().getCompanyEmail());

            orderDetail.setOrderHeader(savedHeader);
            orderDetail.setItem(item.get());

            if (detailRequest.getPrice() != -1)
                orderDetail.setPrice(detailRequest.getPrice());
            else
                orderDetail.setPrice(item.get().getPrice());

            orderDetailRepository.save(orderDetail);
        }

        return this.convertToResponse(savedHeader);
    }

    private OrderHeaderResponse convertToResponse(OrderHeader entity) {
        OrderHeaderResponse response = new OrderHeaderResponse();
        List<ItemResponse> orderDetailResponseList = new ArrayList<>();

        BeanUtils.copyProperties(entity, response);
//        List<OrderDetail> orderDetailList = orderDetailRepository.getAllByOrderHeaderId(entity.getId());
        for (OrderDetail orderDetail : entity.getOrderDetails()){
            Item item = orderDetail.getItem();
            ItemResponse itemResponse = ItemServiceImpl.convertToResponse(item);
            itemResponse.setPrice(orderDetail.getPrice());
            orderDetailResponseList.add(itemResponse);
        }

        response.setItems(orderDetailResponseList);
        return response;
    }
}
