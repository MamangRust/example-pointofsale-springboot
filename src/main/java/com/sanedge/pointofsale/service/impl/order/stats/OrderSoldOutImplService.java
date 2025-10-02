package com.sanedge.pointofsale.service.impl.order.stats;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.order.OrderMonthlyResponse;
import com.sanedge.pointofsale.domain.responses.order.OrderYearlyResponse;
import com.sanedge.pointofsale.models.order.OrderMonth;
import com.sanedge.pointofsale.models.order.OrderYear;
import com.sanedge.pointofsale.repository.order.stats.OrderSoldOutRepository;
import com.sanedge.pointofsale.service.order.stats.OrderSoldoutService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderSoldOutImplService implements OrderSoldoutService {

    private OrderSoldOutRepository orderSoldOutRepository;

    @Override
    public ApiResponse<List<OrderMonthlyResponse>> findMonthlyOrders(Integer yearMonth) {
        log.info("📊 Fetching monthly orders for yearMonth={}", yearMonth);
        try {
            List<OrderMonth> rawData = orderSoldOutRepository.findMonthlyOrdersByYear(yearMonth);
            List<OrderMonthlyResponse> response = rawData.stream()
                    .map(OrderMonthlyResponse::from)
                    .toList();

            return ApiResponse.<List<OrderMonthlyResponse>>builder()
                    .status("success")
                    .message("Monthly order data retrieved successfully")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch monthly orders for yearMonth={}", yearMonth, e);
            return ApiResponse.<List<OrderMonthlyResponse>>builder()
                    .status("error")
                    .message("Failed to retrieve monthly order data. Please try again later.")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<List<OrderYearlyResponse>> findYearlyOrders(Integer yearMonth) {
        log.info("📈 Fetching yearly orders for yearMonth={}", yearMonth);
        try {
            List<OrderYear> rawData = orderSoldOutRepository.findYearlyOrders(yearMonth);
            List<OrderYearlyResponse> response = rawData.stream()
                    .map(OrderYearlyResponse::from)
                    .toList();

            return ApiResponse.<List<OrderYearlyResponse>>builder()
                    .status("success")
                    .message("Yearly order data retrieved successfully")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch yearly orders for yearMonth={}", yearMonth, e);
            return ApiResponse.<List<OrderYearlyResponse>>builder()
                    .status("error")
                    .message("Failed to retrieve yearly order data. Please try again later.")
                    .data(null)
                    .build();
        }
    }
}