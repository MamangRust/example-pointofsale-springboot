package com.sanedge.pointofsale.service.order.stats;

import java.util.List;

import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.order.OrderMonthlyResponse;
import com.sanedge.pointofsale.domain.responses.order.OrderYearlyResponse;

public interface OrderSoldoutService {
    ApiResponse<List<OrderMonthlyResponse>> findMonthlyOrders(Integer yearMonth);

    ApiResponse<List<OrderYearlyResponse>> findYearlyOrders(Integer year);
}
