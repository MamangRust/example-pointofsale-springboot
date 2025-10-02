package com.sanedge.pointofsale.service.order;

import com.sanedge.pointofsale.domain.requests.order.CreateOrderRequest;
import com.sanedge.pointofsale.domain.requests.order.UpdateOrderRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.order.OrderResponse;
import com.sanedge.pointofsale.domain.responses.order.OrderResponseDeleteAt;

public interface OrderCommandService {
    ApiResponse<OrderResponse> create(CreateOrderRequest request);

    ApiResponse<OrderResponse> update(UpdateOrderRequest request);

    ApiResponse<OrderResponseDeleteAt> trash(Integer id);

    ApiResponse<OrderResponseDeleteAt> restore(Integer id);

    ApiResponse<Boolean> delete(Integer id);

    ApiResponse<Boolean> restoreAll();

    ApiResponse<Boolean> deleteAll();
}
