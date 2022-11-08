package com.nle.shared.service.order;

import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.response.order.OrderHeaderResponse;

public interface OrderService {

    public PagingResponseModel<OrderHeaderResponse> SearchByPhone(String phoneNumber);
    public OrderHeaderResponse CreateOrder();
}
