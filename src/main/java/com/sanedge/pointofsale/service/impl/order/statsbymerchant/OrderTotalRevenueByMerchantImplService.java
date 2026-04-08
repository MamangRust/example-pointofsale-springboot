package com.sanedge.pointofsale.service.impl.order.statsbymerchant;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sanedge.pointofsale.domain.requests.order.MonthTotalRevenueMerchantRequest;
import com.sanedge.pointofsale.domain.requests.order.YearTotalRevenueMerchantRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.order.OrderMonthlyTotalRevenueResponse;
import com.sanedge.pointofsale.domain.responses.order.OrderYearlyTotalRevenueResponse;
import com.sanedge.pointofsale.models.order.OrderMonthTotalRevenue;
import com.sanedge.pointofsale.models.order.OrderYearTotalRevenue;
import com.sanedge.pointofsale.repository.order.statsbymerchant.OrderTotalRevenueByMerchantRepository;
import com.sanedge.pointofsale.service.order.statsbymerchant.OrderTotalRevenueByMerchantService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderTotalRevenueByMerchantImplService implements OrderTotalRevenueByMerchantService {

        private final OrderTotalRevenueByMerchantRepository repository;

        @Override
        public ApiResponse<List<OrderMonthlyTotalRevenueResponse>> findMonthlyStatsByMerchant(
                        MonthTotalRevenueMerchantRequest req) {

                log.info("📊 Fetching monthly order stats for merchantId={} | Year: {}, Month: {}",
                                req.getMerchantId(), req.getYear(), req.getMonth());

                if (req.getMerchantId() == null || req.getYear() == null || req.getMonth() == null) {
                        log.error("❌ MerchantId, Year, or Month is null | req: {}", req);
                        return ApiResponse.<List<OrderMonthlyTotalRevenueResponse>>builder()
                                        .status("error")
                                        .message("MerchantId, Year, and Month must not be null")
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

                        List<OrderMonthTotalRevenue> rawData = repository.findMonthlyTotalRevenueByMerchant(
                                        req.getMerchantId().longValue(),
                                        req.getYear(), req.getMonth(),
                                        next.getYear(), next.getMonthValue());

                        List<OrderMonthlyTotalRevenueResponse> response = rawData.stream()
                                        .map(OrderMonthlyTotalRevenueResponse::from)
                                        .collect(Collectors.toList());

                        log.info("✅ Found {} monthly order stats for merchantId={}", response.size(),
                                        req.getMerchantId());

                        return ApiResponse.<List<OrderMonthlyTotalRevenueResponse>>builder()
                                        .status("success")
                                        .message("Monthly order stats for merchant retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to fetch monthly order stats for merchantId={}", req.getMerchantId(), e);
                        return ApiResponse.<List<OrderMonthlyTotalRevenueResponse>>builder()
                                        .status("error")
                                        .message("Failed to fetch monthly order stats. Please try again later.")
                                        .data(List.of())
                                        .build();
                }
        }

        @Override
        public ApiResponse<List<OrderYearlyTotalRevenueResponse>> findYearlyStatsByMerchant(
                        YearTotalRevenueMerchantRequest req) {

                log.info("📈 Fetching yearly order stats for merchantId={} | Year: {}", req.getMerchantId(),
                                req.getYear());

                if (req.getMerchantId() == null || req.getYear() == null) {
                        log.error("❌ MerchantId or Year is null | req: {}", req);
                        return ApiResponse.<List<OrderYearlyTotalRevenueResponse>>builder()
                                        .status("error")
                                        .message("MerchantId and Year must not be null")
                                        .data(List.of())
                                        .build();
                }

                try {
                        List<OrderYearTotalRevenue> rawData = repository.findYearlyTotalRevenueByMerchant(
                                        req.getMerchantId().longValue(),
                                        req.getYear());

                        List<OrderYearlyTotalRevenueResponse> response = rawData.stream()
                                        .map(OrderYearlyTotalRevenueResponse::from)
                                        .collect(Collectors.toList());

                        log.info("✅ Found {} yearly order stats for merchantId={}", response.size(),
                                        req.getMerchantId());

                        return ApiResponse.<List<OrderYearlyTotalRevenueResponse>>builder()
                                        .status("success")
                                        .message("Yearly order stats for merchant retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to fetch yearly order stats for merchantId={}", req.getMerchantId(), e);
                        return ApiResponse.<List<OrderYearlyTotalRevenueResponse>>builder()
                                        .status("error")
                                        .message("Failed to fetch yearly order stats. Please try again later.")
                                        .data(List.of())
                                        .build();
                }
        }
}