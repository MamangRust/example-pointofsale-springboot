package com.sanedge.pointofsale.service.impl.cashier.statsbymerchant;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.pointofsale.domain.requests.cashier.MonthCashierMerchantRequest;
import com.sanedge.pointofsale.domain.requests.cashier.YearCashierMerchantRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.cashier.CashierResponseMonthSales;
import com.sanedge.pointofsale.domain.responses.cashier.CashierResponseYearSales;
import com.sanedge.pointofsale.models.cashier.CashierMonthSales;
import com.sanedge.pointofsale.models.cashier.CashierYearSales;
import com.sanedge.pointofsale.repository.cashier.statsbymerchant.CashierSalesByMerchantRepository;
import com.sanedge.pointofsale.service.cashier.statsbymerchant.CashierSalesByMerchantService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CashierSalesByMerchantImplService implements CashierSalesByMerchantService {
    private final CashierSalesByMerchantRepository cashierSalesByMerchantRepository;

    @Override
    public ApiResponse<List<CashierResponseMonthSales>> findMonthlyCashierByMerchant(MonthCashierMerchantRequest req) {
        log.info("📊 Fetching monthly cashier sales by merchant | MerchantID: {}, Year: {}",
                req.getMerchantId(), req.getYear());

        if (req.getMerchantId() == null || req.getYear() == null) {
            log.error("❌ MerchantId or Year is null | req: {}", req);
            return ApiResponse.<List<CashierResponseMonthSales>>builder()
                    .status("error")
                    .message("MerchantId and Year must not be null")
                    .data(List.of())
                    .build();
        }

        try {
            LocalDate startMonth = LocalDate.of(req.getYear(), 1, 1);
            LocalDate nextYear = startMonth.plusYears(1);

            List<CashierMonthSales> results = cashierSalesByMerchantRepository.findMonthSalesByMerchant(
                    req.getMerchantId().longValue(),
                    req.getYear(),
                    startMonth.getMonthValue(),
                    nextYear.getMonthValue());

            List<CashierResponseMonthSales> response = results.stream()
                    .map(CashierResponseMonthSales::from)
                    .toList();

            log.info("✅ Found {} monthly cashier sales for merchant {}", response.size(), req.getMerchantId());

            return ApiResponse.<List<CashierResponseMonthSales>>builder()
                    .status("success")
                    .message("Monthly cashier sales by merchant retrieved successfully")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch monthly cashier sales by merchant | MerchantID: {}, Year: {}",
                    req.getMerchantId(), req.getYear(), e);
            return ApiResponse.<List<CashierResponseMonthSales>>builder()
                    .status("error")
                    .message("Failed to fetch monthly cashier sales by merchant")
                    .data(List.of())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<CashierResponseYearSales>> findYearlyCashierByMerchant(YearCashierMerchantRequest req) {
        log.info("📊 Fetching yearly cashier sales by merchant | MerchantID: {}, Year: {}",
                req.getMerchantId(), req.getYear());

        if (req.getMerchantId() == null || req.getYear() == null) {
            log.error("❌ MerchantId or Year is null | req: {}", req);
            return ApiResponse.<List<CashierResponseYearSales>>builder()
                    .status("error")
                    .message("MerchantId and Year must not be null")
                    .data(List.of())
                    .build();
        }

        try {
            List<CashierYearSales> results = cashierSalesByMerchantRepository.findYearSalesByMerchant(
                    req.getMerchantId().longValue(),
                    req.getYear());

            List<CashierResponseYearSales> response = results.stream()
                    .map(CashierResponseYearSales::from)
                    .toList();

            log.info("✅ Found {} yearly cashier sales for merchant {}", response.size(), req.getMerchantId());

            return ApiResponse.<List<CashierResponseYearSales>>builder()
                    .status("success")
                    .message("Yearly cashier sales by merchant retrieved successfully")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch yearly cashier sales by merchant | MerchantID: {}, Year: {}",
                    req.getMerchantId(), req.getYear(), e);
            return ApiResponse.<List<CashierResponseYearSales>>builder()
                    .status("error")
                    .message("Failed to fetch yearly cashier sales by merchant")
                    .data(List.of())
                    .build();
        }
    }
}
