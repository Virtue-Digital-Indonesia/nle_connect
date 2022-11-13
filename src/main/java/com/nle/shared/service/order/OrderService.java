package com.nle.shared.service.order;

import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.order.CreateOrderHeaderRequest;
import com.nle.ui.model.response.order.OrderHeaderResponse;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    PagingResponseModel<OrderHeaderResponse> SearchByPhone(String phoneNumber, Pageable pageable);
    OrderHeaderResponse CreateOrder(CreateOrderHeaderRequest request);
}
