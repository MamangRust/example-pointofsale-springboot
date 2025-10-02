package com.sanedge.pointofsale.service.impl.category.stats;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoriesMonthPriceResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoriesYearPriceResponse;
import com.sanedge.pointofsale.models.category.CategoryMonthPrice;
import com.sanedge.pointofsale.models.category.CategoryYearPrice;
import com.sanedge.pointofsale.repository.category.stats.CategoryPriceRepository;
import com.sanedge.pointofsale.service.category.stats.CategoryPriceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryPriceImplService implements CategoryPriceService {
    private final CategoryPriceRepository categoryPriceRepository;

    @Override
    public ApiResponse<List<CategoriesMonthPriceResponse>> findMonthPrice(Integer year) {
        log.info("📊 Fetching monthly category price stats | Year: {}", year);
        try {
            List<CategoryMonthPrice> results = categoryPriceRepository.findMonthlyCategoryPrice(year);

            List<CategoriesMonthPriceResponse> response = results.stream()
                    .map(CategoriesMonthPriceResponse::from)
                    .toList();

            log.info("✅ Found {} monthly category price stats", response.size());

            return ApiResponse.<List<CategoriesMonthPriceResponse>>builder()
                    .status("success")
                    .message("Monthly category price stats retrieved successfully")
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to fetch monthly category price stats | Year: {}", year, e);
            return ApiResponse.<List<CategoriesMonthPriceResponse>>builder()
                    .status("error")
                    .message("Failed to fetch monthly category price stats")
                    .data(List.of())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<CategoriesYearPriceResponse>> findYearPrice(Integer year) {
        log.info("📊 Fetching yearly category price stats | Year: {}", year);
        try {
            List<CategoryYearPrice> results = categoryPriceRepository.findYearlyCategoryPrice(year);

            List<CategoriesYearPriceResponse> response = results.stream()
                    .map(CategoriesYearPriceResponse::from)
                    .toList();

            log.info("✅ Found {} yearly category price stats", response.size());

            return ApiResponse.<List<CategoriesYearPriceResponse>>builder()
                    .status("success")
                    .message("Yearly category price stats retrieved successfully")
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to fetch yearly category price stats | Year: {}", year, e);
            return ApiResponse.<List<CategoriesYearPriceResponse>>builder()
                    .status("error")
                    .message("Failed to fetch yearly category price stats")
                    .data(List.of())
                    .build();
        }
    }
}
