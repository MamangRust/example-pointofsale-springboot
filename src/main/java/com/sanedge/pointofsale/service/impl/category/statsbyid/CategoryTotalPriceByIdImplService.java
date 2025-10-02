package com.sanedge.pointofsale.service.impl.category.statsbyid;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.pointofsale.domain.requests.category.MonthTotalPriceCategory;
import com.sanedge.pointofsale.domain.requests.category.YearTotalPriceCategory;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoriesMonthlyTotalPriceResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoriesYearlyTotalPriceResponse;
import com.sanedge.pointofsale.models.category.CategoryMonthTotalPrice;
import com.sanedge.pointofsale.models.category.CategoryYearTotalPrice;
import com.sanedge.pointofsale.repository.category.statsbyid.CategoryTotalPriceByIdRepository;
import com.sanedge.pointofsale.service.category.statsbyid.CategoryTotalPriceByIdService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryTotalPriceByIdImplService implements CategoryTotalPriceByIdService {
    private final CategoryTotalPriceByIdRepository categoryTotalPriceByIdRepository;

    @Override
    public ApiResponse<List<CategoriesMonthlyTotalPriceResponse>> findMonthlyTotalPriceById(
            MonthTotalPriceCategory req) {
        log.info("📊 Fetching monthly total price by category | CategoryId: {}, Year: {}, Month: {}",
                req.getCategoryId(), req.getYear(), req.getMonth());

        if (req.getCategoryId() == null || req.getYear() == null || req.getMonth() == null) {
            log.error("❌ CategoryId, Year or Month is null | req: {}", req);
            return ApiResponse.<List<CategoriesMonthlyTotalPriceResponse>>builder()
                    .status("error")
                    .message("CategoryId, Year and Month must not be null")
                    .data(List.of())
                    .build();
        }

        try {
            LocalDate currentMonth = LocalDate.of(req.getYear(), req.getMonth(), 1);
            LocalDate nextMonth = currentMonth.plusMonths(1);

            List<CategoryMonthTotalPrice> results = categoryTotalPriceByIdRepository
                    .findMonthlyTotalPriceByCategoryId(
                            req.getCategoryId().longValue(),
                            req.getYear(),
                            req.getMonth(),
                            nextMonth.getYear(),
                            nextMonth.getMonthValue());

            List<CategoriesMonthlyTotalPriceResponse> response = results.stream()
                    .map(CategoriesMonthlyTotalPriceResponse::from)
                    .toList();

            log.info("✅ Found {} monthly total price stats for categoryId {}", response.size(),
                    req.getCategoryId());

            return ApiResponse.<List<CategoriesMonthlyTotalPriceResponse>>builder()
                    .status("success")
                    .message("Monthly total price by category retrieved successfully")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch monthly total price by category | CategoryId: {}, Year: {}, Month: {}",
                    req.getCategoryId(), req.getYear(), req.getMonth(), e);
            return ApiResponse.<List<CategoriesMonthlyTotalPriceResponse>>builder()
                    .status("error")
                    .message("Failed to fetch monthly total price by category")
                    .data(List.of())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<CategoriesYearlyTotalPriceResponse>> findYearlyTotalPriceById(
            YearTotalPriceCategory req) {
        log.info("📊 Fetching yearly total price by category | CategoryId: {}, Year: {}",
                req.getCategoryId(), req.getYear());

        if (req.getCategoryId() == null || req.getYear() == null) {
            log.error("❌ CategoryId or Year is null | req: {}", req);
            return ApiResponse.<List<CategoriesYearlyTotalPriceResponse>>builder()
                    .status("error")
                    .message("CategoryId and Year must not be null")
                    .data(List.of())
                    .build();
        }

        try {
            List<CategoryYearTotalPrice> results = categoryTotalPriceByIdRepository
                    .findYearlyTotalPriceByCategoryId(req.getCategoryId().longValue(), req.getYear(),
                            req.getYear() - 1);

            List<CategoriesYearlyTotalPriceResponse> response = results.stream()
                    .map(CategoriesYearlyTotalPriceResponse::from)
                    .toList();

            log.info("✅ Found {} yearly total price stats for categoryId {}", response.size(),
                    req.getCategoryId());

            return ApiResponse.<List<CategoriesYearlyTotalPriceResponse>>builder()
                    .status("success")
                    .message("Yearly total price by category retrieved successfully")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch yearly total price by category | CategoryId: {}, Year: {}",
                    req.getCategoryId(), req.getYear(), e);
            return ApiResponse.<List<CategoriesYearlyTotalPriceResponse>>builder()
                    .status("error")
                    .message("Failed to fetch yearly total price by category")
                    .data(List.of())
                    .build();
        }
    }
}
