package com.nle.shared.service.order;

import com.nle.io.entity.Item;
import com.nle.io.entity.order.OrderDetail;
import com.nle.io.entity.order.OrderHeader;
import com.nle.io.repository.OrderRepository;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.response.order.OrderDetailResponse;
import com.nle.ui.model.response.order.OrderHeaderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;

    @Override
    public PagingResponseModel<OrderHeaderResponse> SearchByPhone(String phoneNumber, Pageable pageable) {

        Page<OrderHeader> headerPage = orderRepository.getOrderByPhoneNumber(phoneNumber, pageable);
        return new PagingResponseModel<>(headerPage.map(this::convertToResponse));
    }

    @Override
    public OrderHeaderResponse CreateOrder() {
        return null;
    }

    private OrderHeaderResponse convertToResponse(OrderHeader entity) {
        OrderHeaderResponse response = new OrderHeaderResponse();
        List<OrderDetailResponse> orderDetailResponseList = new ArrayList<>();

        BeanUtils.copyProperties(entity, response);
        for (OrderDetail orderDetail : entity.getDetailList()){
            Item item = orderDetail.getItem();
            OrderDetailResponse detailResponse = new OrderDetailResponse();
            BeanUtils.copyProperties(item, detailResponse);
            detailResponse.setPrice(orderDetail.getPrice());
            orderDetailResponseList.add(detailResponse);
        }

        response.setItems(orderDetailResponseList);
        return response;
    }
}
