package com.sanedge.pointofsale.service.order.statsbymerchant;

import java.util.List;

import com.sanedge.pointofsale.domain.requests.order.MonthTotalRevenueMerchantRequest;
import com.sanedge.pointofsale.domain.requests.order.YearTotalRevenueMerchantRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.order.OrderMonthlyTotalRevenueResponse;
import com.sanedge.pointofsale.domain.responses.order.OrderYearlyTotalRevenueResponse;

public interface OrderTotalRevenueByMerchantService {
    ApiResponse<List<OrderMonthlyTotalRevenueResponse>> findMonthlyStatsByMerchant(
            MonthTotalRevenueMerchantRequest req);

    ApiResponse<List<OrderYearlyTotalRevenueResponse>> findYearlyStatsByMerchant(YearTotalRevenueMerchantRequest req);
}