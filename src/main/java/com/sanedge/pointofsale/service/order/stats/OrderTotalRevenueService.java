package com.sanedge.pointofsale.service.order.stats;

import java.util.List;

import com.sanedge.pointofsale.domain.requests.order.MonthTotalRevenue;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.order.OrderMonthlyTotalRevenueResponse;
import com.sanedge.pointofsale.domain.responses.order.OrderYearlyTotalRevenueResponse;

public interface OrderTotalRevenueService {
    ApiResponse<List<OrderMonthlyTotalRevenueResponse>> findMonthlyStats(MonthTotalRevenue req);

    ApiResponse<List<OrderYearlyTotalRevenueResponse>> findYearlyStats(Integer year);
}