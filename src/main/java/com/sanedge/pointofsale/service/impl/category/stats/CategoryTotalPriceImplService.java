package com.sanedge.pointofsale.service.impl.category.stats;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.pointofsale.domain.requests.category.MonthTotalPrice;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoriesMonthlyTotalPriceResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoriesYearlyTotalPriceResponse;
import com.sanedge.pointofsale.models.category.CategoryMonthTotalPrice;
import com.sanedge.pointofsale.models.category.CategoryYearTotalPrice;
import com.sanedge.pointofsale.repository.category.stats.CategoryTotalPriceRepository;
import com.sanedge.pointofsale.service.category.stats.CategoryTotalPriceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryTotalPriceImplService implements CategoryTotalPriceService {
        private final CategoryTotalPriceRepository categoryTotalPriceRepository;

        @Override
        public ApiResponse<List<CategoriesMonthlyTotalPriceResponse>> findMonthlyTotalPrice(
                        MonthTotalPrice req) {
                log.info("📊 Fetching monthly total category price | Year: {}, Month: {}",
                                req.getYear(), req.getMonth());

                if (req.getYear() == null || req.getMonth() == null) {
                        log.error("❌ Year or Month is null | req: {}", req);
                        return ApiResponse.<List<CategoriesMonthlyTotalPriceResponse>>builder()
                                        .status("error")
                                        .message("Year and Month must not be null")
                                        .data(List.of())
                                        .build();
                }

                try {
                        LocalDate currentMonth = LocalDate.of(req.getYear(), req.getMonth(), 1);
                        LocalDate nextMonth = currentMonth.plusMonths(1);

                        List<CategoryMonthTotalPrice> results = categoryTotalPriceRepository.findMonthlyTotalPrice(
                                        req.getYear(),
                                        req.getMonth(),
                                        nextMonth.getYear(),
                                        nextMonth.getMonthValue());

                        List<CategoriesMonthlyTotalPriceResponse> response = results.stream()
                                        .map(CategoriesMonthlyTotalPriceResponse::from)
                                        .toList();

                        log.info("✅ Found {} monthly total price stats", response.size());

                        return ApiResponse.<List<CategoriesMonthlyTotalPriceResponse>>builder()
                                        .status("success")
                                        .message("Monthly total price stats retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to fetch monthly total category price | Year: {}, Month: {}",
                                        req.getYear(), req.getMonth(), e);
                        return ApiResponse.<List<CategoriesMonthlyTotalPriceResponse>>builder()
                                        .status("error")
                                        .message("Failed to fetch monthly total category price")
                                        .data(List.of())
                                        .build();
                }
        }

        @Override
        public ApiResponse<List<CategoriesYearlyTotalPriceResponse>> findYearlyTotalPrice(Integer year) {
                log.info("📊 Fetching yearly total category price | Year: {}", year);
                try {
                        List<CategoryYearTotalPrice> results = categoryTotalPriceRepository
                                        .findYearlyTotalPrice(year, year - 1);

                        List<CategoriesYearlyTotalPriceResponse> response = results.stream()
                                        .map(CategoriesYearlyTotalPriceResponse::from)
                                        .toList();

                        log.info("✅ Found {} yearly total price stats", response.size());

                        return ApiResponse.<List<CategoriesYearlyTotalPriceResponse>>builder()
                                        .status("success")
                                        .message("Yearly total price stats retrieved successfully")
                                        .data(response)
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch yearly total category price | Year: {}", year, e);
                        return ApiResponse.<List<CategoriesYearlyTotalPriceResponse>>builder()
                                        .status("error")
                                        .message("Failed to fetch yearly total category price")
                                        .data(List.of())
                                        .build();
                }
        }
}
