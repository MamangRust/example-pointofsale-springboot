package com.sanedge.pointofsale.service.impl.order.stats;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sanedge.pointofsale.domain.requests.order.MonthTotalRevenue;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.order.OrderMonthlyTotalRevenueResponse;
import com.sanedge.pointofsale.domain.responses.order.OrderYearlyTotalRevenueResponse;
import com.sanedge.pointofsale.models.order.OrderMonthTotalRevenue;
import com.sanedge.pointofsale.models.order.OrderYearTotalRevenue;
import com.sanedge.pointofsale.repository.order.stats.OrderTotalRevenueRepository;
import com.sanedge.pointofsale.service.order.stats.OrderTotalRevenueService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderTotalRevenueImplService implements OrderTotalRevenueService {

    private OrderTotalRevenueRepository orderTotalRevenueRepository;

    @Override
    public ApiResponse<List<OrderMonthlyTotalRevenueResponse>> findMonthlyStats(MonthTotalRevenue req) {
        log.info("📊 Fetching monthly order stats | Year: {}, Month: {}", req.getYear(), req.getMonth());

        if (req.getYear() == null || req.getMonth() == null) {
            log.error("❌ Year or Month is null | req: {}", req);
            return ApiResponse.<List<OrderMonthlyTotalRevenueResponse>>builder()
                    .status("error")
                    .message("Year and Month must not be null")
                    .data(List.of())
                    .build();
        }

        try {
            if (req.getMonth() < 1 || req.getMonth() > 12) {
                return ApiResponse.<List<OrderMonthlyTotalRevenueResponse>>builder()
                        .status("error")
                        .message("Month must be between 1 and 12")
                        .data(List.of())
                        .build();
            }

            LocalDate current = LocalDate.of(req.getYear(), req.getMonth(), 1);
            LocalDate next = current.plusMonths(1);

            List<OrderMonthTotalRevenue> rawData = orderTotalRevenueRepository.findMonthlyTotalRevenue(req.getYear(),
                    req.getMonth(),
                    next.getYear(), next.getMonthValue());

            List<OrderMonthlyTotalRevenueResponse> response = rawData.stream()
                    .map(OrderMonthlyTotalRevenueResponse::from)
                    .collect(Collectors.toList());

            log.info("✅ Found {} monthly order stats", response.size());

            return ApiResponse.<List<OrderMonthlyTotalRevenueResponse>>builder()
                    .status("success")
                    .message("Monthly order stats retrieved successfully")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch monthly order stats | Year: {}, Month: {}",
                    req.getYear(), req.getMonth(), e);
            return ApiResponse.<List<OrderMonthlyTotalRevenueResponse>>builder()
                    .status("error")
                    .message("Failed to fetch monthly order stats. Please try again later.")
                    .data(List.of())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<OrderYearlyTotalRevenueResponse>> findYearlyStats(Integer year) {
        log.info("📈 Fetching yearly order stats | Year: {}", year);

        if (year == null) {
            log.error("❌ Year is null | req: {}", year);
            return ApiResponse.<List<OrderYearlyTotalRevenueResponse>>builder()
                    .status("error")
                    .message("Year must not be null")
                    .data(List.of())
                    .build();
        }

        try {
            List<OrderYearTotalRevenue> rawData = orderTotalRevenueRepository.findYearlyTotalRevenue(year);

            List<OrderYearlyTotalRevenueResponse> response = rawData.stream()
                    .map(OrderYearlyTotalRevenueResponse::from)
                    .collect(Collectors.toList());

            log.info("✅ Found {} yearly order stats", response.size());

            return ApiResponse.<List<OrderYearlyTotalRevenueResponse>>builder()
                    .status("success")
                    .message("Yearly order stats retrieved successfully")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch yearly order stats | Year: {}", year, e);
            return ApiResponse.<List<OrderYearlyTotalRevenueResponse>>builder()
                    .status("error")
                    .message("Failed to fetch yearly order stats. Please try again later.")
                    .data(List.of())
                    .build();
        }
    }
}