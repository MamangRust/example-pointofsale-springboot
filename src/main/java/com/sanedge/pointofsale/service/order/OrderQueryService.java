package com.sanedge.pointofsale.service.order;

import java.util.List;

import com.sanedge.pointofsale.domain.requests.order.FindAllOrderByMerchantRequest;
import com.sanedge.pointofsale.domain.requests.order.FindAllOrderRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.api.ApiResponsePagination;
import com.sanedge.pointofsale.domain.responses.order.OrderResponse;
import com.sanedge.pointofsale.domain.responses.order.OrderResponseDeleteAt;

public interface OrderQueryService {
    ApiResponsePagination<List<OrderResponse>> findAll(FindAllOrderRequest req);

    ApiResponsePagination<List<OrderResponseDeleteAt>> findByActive(FindAllOrderRequest req);

    ApiResponsePagination<List<OrderResponseDeleteAt>> findByTrashed(FindAllOrderRequest req);

    ApiResponse<OrderResponse> findById(Integer id);

    ApiResponsePagination<List<OrderResponse>> findByMerchantId(FindAllOrderByMerchantRequest req);
}
