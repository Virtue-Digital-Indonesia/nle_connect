package com.nle.shared.service.order;

import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.response.order.OrderHeaderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class OrderServiceImpl implements OrderService{

    @Override
    public PagingResponseModel<OrderHeaderResponse> SearchByPhone(String phoneNumber) {
        return null;
    }

    @Override
    public OrderHeaderResponse CreateOrder() {
        return null;
    }
}
