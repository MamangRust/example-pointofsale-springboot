package com.sanedge.pointofsale.service.impl.category.statsbymerchant;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.pointofsale.domain.requests.category.MonthPriceMerchant;
import com.sanedge.pointofsale.domain.requests.category.YearPriceMerchant;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoriesMonthPriceResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoriesYearPriceResponse;
import com.sanedge.pointofsale.models.category.CategoryMonthPrice;
import com.sanedge.pointofsale.models.category.CategoryYearPrice;
import com.sanedge.pointofsale.repository.category.statsbymerchant.CategoryPriceByMerchantRepository;
import com.sanedge.pointofsale.service.category.statsbymerchant.CategoryPriceByMerchantService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryPriceByMerchantImplService implements CategoryPriceByMerchantService {

        private final CategoryPriceByMerchantRepository categoryPriceByMerchantRepository;

        @Override
        public ApiResponse<List<CategoriesMonthPriceResponse>> findMonthPriceByMerchant(MonthPriceMerchant req) {
                log.info("📊 Fetching monthly price by merchant | MerchantId: {}, Year: {}", req.getMerchantId(),
                                req.getYear());

                if (req.getMerchantId() == null || req.getYear() == null) {
                        return ApiResponse.<List<CategoriesMonthPriceResponse>>builder()
                                        .status("error")
                                        .message("MerchantId and Year must not be null")
                                        .data(List.of())
                                        .build();
                }

                try {
                        List<CategoryMonthPrice> results = categoryPriceByMerchantRepository
                                        .findMonthlyCategoryPriceByMerchant(req.getMerchantId().longValue(),
                                                        req.getYear());

                        List<CategoriesMonthPriceResponse> response = results.stream()
                                        .map(CategoriesMonthPriceResponse::from)
                                        .toList();

                        log.info("✅ Found {} monthly price stats for merchant {}", response.size(),
                                        req.getMerchantId());

                        return ApiResponse.<List<CategoriesMonthPriceResponse>>builder()
                                        .status("success")
                                        .message("Monthly price by merchant retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Error fetching monthly price by merchant | MerchantId: {}, Year: {}",
                                        req.getMerchantId(), req.getYear(), e);

                        return ApiResponse.<List<CategoriesMonthPriceResponse>>builder()
                                        .status("error")
                                        .message("Unable to fetch monthly price data at the moment")
                                        .data(List.of())
                                        .build();
                }
        }

        @Override
        public ApiResponse<List<CategoriesYearPriceResponse>> findYearPriceByMerchant(YearPriceMerchant req) {
                log.info("📊 Fetching yearly price by merchant | MerchantId: {}, Year: {}", req.getMerchantId(),
                                req.getYear());

                if (req.getMerchantId() == null || req.getYear() == null) {
                        return ApiResponse.<List<CategoriesYearPriceResponse>>builder()
                                        .status("error")
                                        .message("MerchantId and Year must not be null")
                                        .data(List.of())
                                        .build();
                }

                try {
                        List<CategoryYearPrice> results = categoryPriceByMerchantRepository
                                        .findYearlyCategoryPriceByMerchant(req.getMerchantId().longValue(),
                                                        req.getYear());

                        List<CategoriesYearPriceResponse> response = results.stream()
                                        .map(CategoriesYearPriceResponse::from)
                                        .toList();

                        log.info("✅ Found {} yearly price stats for merchant {}", response.size(), req.getMerchantId());

                        return ApiResponse.<List<CategoriesYearPriceResponse>>builder()
                                        .status("success")
                                        .message("Yearly price by merchant retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Error fetching yearly price by merchant | MerchantId: {}, Year: {}",
                                        req.getMerchantId(), req.getYear(), e);

                        return ApiResponse.<List<CategoriesYearPriceResponse>>builder()
                                        .status("error")
                                        .message("Unable to fetch yearly price data at the moment")
                                        .data(List.of())
                                        .build();
                }
        }
}
