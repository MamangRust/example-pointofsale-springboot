package com.sanedge.pointofsale.service.impl.category.statsbymerchant;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.pointofsale.domain.requests.category.MonthTotalPriceMerchant;
import com.sanedge.pointofsale.domain.requests.category.YearTotalPriceMerchant;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoriesMonthlyTotalPriceResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoriesYearlyTotalPriceResponse;
import com.sanedge.pointofsale.models.category.CategoryMonthTotalPrice;
import com.sanedge.pointofsale.models.category.CategoryYearTotalPrice;
import com.sanedge.pointofsale.repository.category.statsbymerchant.CategoryTotalPriceByMerchantRepository;
import com.sanedge.pointofsale.service.category.statsbymerchant.CategoryTotalPriceByMerchantService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryTotalPriceByMerchantImpService implements CategoryTotalPriceByMerchantService {

        private final CategoryTotalPriceByMerchantRepository categoryTotalPriceByMerchantRepository;

        @Override
        public ApiResponse<List<CategoriesMonthlyTotalPriceResponse>> findMonthlyTotalPriceByMerchant(
                        MonthTotalPriceMerchant req) {

                log.info("📊 Fetching monthly total price by merchant | MerchantId: {}, Year: {}, Month: {}",
                                req.getMerchantId(), req.getYear(), req.getMonth());

                if (req.getMerchantId() == null || req.getYear() == null || req.getMonth() == null) {
                        return ApiResponse.<List<CategoriesMonthlyTotalPriceResponse>>builder()
                                        .status("error")
                                        .message("MerchantId, Year and Month must not be null")
                                        .data(List.of())
                                        .build();
                }

                try {
                        LocalDate currentMonth = LocalDate.of(req.getYear(), req.getMonth(), 1);
                        LocalDate nextMonth = currentMonth.plusMonths(1);

                        List<CategoryMonthTotalPrice> results = categoryTotalPriceByMerchantRepository
                                        .findMonthlyTotalPriceByMerchant(
                                                        req.getMerchantId().longValue(),
                                                        req.getYear(),
                                                        req.getMonth(),
                                                        nextMonth.getYear(),
                                                        nextMonth.getMonthValue());

                        List<CategoriesMonthlyTotalPriceResponse> response = results.stream()
                                        .map(CategoriesMonthlyTotalPriceResponse::from)
                                        .toList();

                        log.info("✅ Found {} monthly total price stats for merchant {}", response.size(),
                                        req.getMerchantId());

                        return ApiResponse.<List<CategoriesMonthlyTotalPriceResponse>>builder()
                                        .status("success")
                                        .message("Monthly total price by merchant retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to fetch monthly total price by merchant | MerchantId: {}, Year: {}, Month: {}",
                                        req.getMerchantId(), req.getYear(), req.getMonth(), e);

                        return ApiResponse.<List<CategoriesMonthlyTotalPriceResponse>>builder()
                                        .status("error")
                                        .message("Unable to fetch monthly total price by merchant at the moment")
                                        .data(List.of())
                                        .build();
                }
        }

        @Override
        public ApiResponse<List<CategoriesYearlyTotalPriceResponse>> findYearlyTotalPriceByMerchant(
                        YearTotalPriceMerchant req) {

                log.info("📊 Fetching yearly total price by merchant | MerchantId: {}, Year: {}",
                                req.getMerchantId(), req.getYear());

                if (req.getMerchantId() == null || req.getYear() == null) {
                        return ApiResponse.<List<CategoriesYearlyTotalPriceResponse>>builder()
                                        .status("error")
                                        .message("MerchantId and Year must not be null")
                                        .data(List.of())
                                        .build();
                }

                try {
                        List<CategoryYearTotalPrice> results = categoryTotalPriceByMerchantRepository
                                        .findYearlyTotalPriceByMerchant(req.getMerchantId().longValue(), req.getYear(),
                                                        req.getYear() - 1);

                        List<CategoriesYearlyTotalPriceResponse> response = results.stream()
                                        .map(CategoriesYearlyTotalPriceResponse::from)
                                        .toList();

                        log.info("✅ Found {} yearly total price stats for merchant {}", response.size(),
                                        req.getMerchantId());

                        return ApiResponse.<List<CategoriesYearlyTotalPriceResponse>>builder()
                                        .status("success")
                                        .message("Yearly total price by merchant retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to fetch yearly total price by merchant | MerchantId: {}, Year: {}",
                                        req.getMerchantId(), req.getYear(), e);

                        return ApiResponse.<List<CategoriesYearlyTotalPriceResponse>>builder()
                                        .status("error")
                                        .message("Unable to fetch yearly total price by merchant at the moment")
                                        .data(List.of())
                                        .build();
                }
        }
}
