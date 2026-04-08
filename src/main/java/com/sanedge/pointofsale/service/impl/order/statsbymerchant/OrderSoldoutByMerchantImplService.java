package com.sanedge.pointofsale.service.impl.order.statsbymerchant;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.pointofsale.domain.requests.order.MonthOrderMerchantRequest;
import com.sanedge.pointofsale.domain.requests.order.YearOrderMerchantRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.order.OrderMonthlyResponse;
import com.sanedge.pointofsale.domain.responses.order.OrderYearlyResponse;
import com.sanedge.pointofsale.models.order.OrderMonth;
import com.sanedge.pointofsale.models.order.OrderYear;
import com.sanedge.pointofsale.repository.order.statsbymerchant.OrderSoldOutByMerchantRepository;
import com.sanedge.pointofsale.service.order.statsbymerchant.OrderSoldOutByMerchantService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderSoldoutByMerchantImplService implements OrderSoldOutByMerchantService {

        private final OrderSoldOutByMerchantRepository orderSoldOutByMerchantRepository;

        @Override
        public ApiResponse<List<OrderMonthlyResponse>> findMonthlyOrdersByMerchant(MonthOrderMerchantRequest req) {
                log.info("📊 Fetching monthly orders for merchant | merchantId={}, year={}", req.getMerchantId(),
                                req.getYear());

                if (req.getMerchantId() == null || req.getYear() == null) {
                        log.error("❌ Missing required fields | req: {}", req);
                        return ApiResponse.<List<OrderMonthlyResponse>>builder()
                                        .status("error")
                                        .message("Merchant ID and Year are required")
                                        .data(List.of())
                                        .build();
                }

                try {
                        List<OrderMonth> rawData = orderSoldOutByMerchantRepository
                                        .findMonthlyOrdersByMerchant(req.getMerchantId(), req.getYear());

                        List<OrderMonthlyResponse> response = rawData.stream()
                                        .map(OrderMonthlyResponse::from)
                                        .toList();

                        log.info("✅ Found {} monthly order records for merchant", response.size());

                        return ApiResponse.<List<OrderMonthlyResponse>>builder()
                                        .status("success")
                                        .message("Monthly order stats by merchant retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to fetch monthly orders for merchant | merchantId={}, year={}",
                                        req.getMerchantId(), req.getYear(), e);
                        return ApiResponse.<List<OrderMonthlyResponse>>builder()
                                        .status("error")
                                        .message("Failed to retrieve monthly order data. Please try again later.")
                                        .data(List.of())
                                        .build();
                }
        }

        @Override
        public ApiResponse<List<OrderYearlyResponse>> findYearlyOrdersByMerchant(YearOrderMerchantRequest req) {
                log.info("📈 Fetching yearly orders for merchant | merchantId={}, year={}", req.getMerchantId(),
                                req.getYear());

                if (req.getMerchantId() == null || req.getYear() == null) {
                        log.error("❌ Missing required fields | req: {}", req);
                        return ApiResponse.<List<OrderYearlyResponse>>builder()
                                        .status("error")
                                        .message("Merchant ID and Year are required")
                                        .data(List.of())
                                        .build();
                }

                try {
                        List<OrderYear> rawData = orderSoldOutByMerchantRepository
                                        .findYearlyOrdersByMerchant(req.getMerchantId(), req.getYear());

                        List<OrderYearlyResponse> response = rawData.stream()
                                        .map(OrderYearlyResponse::from)
                                        .toList();

                        log.info("✅ Found {} yearly order records for merchant", response.size());

                        return ApiResponse.<List<OrderYearlyResponse>>builder()
                                        .status("success")
                                        .message("Yearly order stats by merchant retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to fetch yearly orders for merchant | merchantId={}, year={}",
                                        req.getMerchantId(), req.getYear(), e);
                        return ApiResponse.<List<OrderYearlyResponse>>builder()
                                        .status("error")
                                        .message("Failed to retrieve yearly order data. Please try again later.")
                                        .data(List.of())
                                        .build();
                }
        }
}