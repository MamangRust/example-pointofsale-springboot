package com.sanedge.pointofsale.service.order.statsbymerchant;

import java.util.List;

import com.sanedge.pointofsale.domain.requests.order.MonthOrderMerchantRequest;
import com.sanedge.pointofsale.domain.requests.order.YearOrderMerchantRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.order.OrderMonthlyResponse;
import com.sanedge.pointofsale.domain.responses.order.OrderYearlyResponse;

public interface OrderSoldOutByMerchantService {
    ApiResponse<List<OrderMonthlyResponse>> findMonthlyOrdersByMerchant(MonthOrderMerchantRequest req);

    ApiResponse<List<OrderYearlyResponse>> findYearlyOrdersByMerchant(YearOrderMerchantRequest req);
}