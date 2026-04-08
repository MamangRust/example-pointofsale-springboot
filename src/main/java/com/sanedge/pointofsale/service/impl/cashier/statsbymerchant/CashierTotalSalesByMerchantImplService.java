package com.sanedge.pointofsale.service.impl.cashier.statsbymerchant;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.pointofsale.domain.requests.cashier.MonthTotalSalesMerchant;
import com.sanedge.pointofsale.domain.requests.cashier.YearTotalSalesMerchant;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.cashier.CashierResponseMonthTotalSales;
import com.sanedge.pointofsale.domain.responses.cashier.CashierResponseYearTotalSales;
import com.sanedge.pointofsale.models.cashier.CashierMonthTotalSales;
import com.sanedge.pointofsale.models.cashier.CashierYearTotalSales;
import com.sanedge.pointofsale.repository.cashier.statsbymerchant.CashierTotalSalesByMerchantRepository;
import com.sanedge.pointofsale.service.cashier.statsbymerchant.CashierTotalSalesByMerchantService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CashierTotalSalesByMerchantImplService implements CashierTotalSalesByMerchantService {
        private final CashierTotalSalesByMerchantRepository cashierTotalSalesByMerchantRepository;

        @Override
        public ApiResponse<List<CashierResponseMonthTotalSales>> findMonthlyTotalSalesByMerchant(
                        MonthTotalSalesMerchant req) {
                log.info("📊 Fetching monthly total cashier sales by merchant | MerchantID: {}, Year: {}, Month: {}",
                                req.getMerchantId(), req.getYear(), req.getMonth());

                if (req.getMerchantId() == null || req.getYear() == null || req.getMonth() == null) {
                        log.error("❌ MerchantId, Year, or Month is null | req: {}", req);
                        return ApiResponse.<List<CashierResponseMonthTotalSales>>builder()
                                        .status("error")
                                        .message("MerchantId, Year, and Month must not be null")
                                        .data(List.of())
                                        .build();
                }

                try {
                        LocalDate currentMonth = LocalDate.of(req.getYear(), req.getMonth(), 1);
                        LocalDate nextMonth = currentMonth.plusMonths(1);

                        List<CashierMonthTotalSales> results = cashierTotalSalesByMerchantRepository
                                        .findMonthTotalSalesByMerchant(
                                                        req.getMerchantId().longValue(),
                                                        req.getYear(),
                                                        req.getMonth(),
                                                        nextMonth.getYear(),
                                                        nextMonth.getMonthValue());

                        List<CashierResponseMonthTotalSales> response = results.stream()
                                        .map(CashierResponseMonthTotalSales::from)
                                        .toList();

                        log.info("✅ Found {} monthly total cashier sales for merchant {}", response.size(),
                                        req.getMerchantId());

                        return ApiResponse.<List<CashierResponseMonthTotalSales>>builder()
                                        .status("success")
                                        .message("Monthly total cashier sales by merchant retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error(
                                        "💥 Failed to fetch monthly total cashier sales by merchant | MerchantID: {}, Year: {}, Month: {}",
                                        req.getMerchantId(), req.getYear(), req.getMonth(), e);
                        return ApiResponse.<List<CashierResponseMonthTotalSales>>builder()
                                        .status("error")
                                        .message("Failed to fetch monthly total cashier sales by merchant")
                                        .data(List.of())
                                        .build();
                }
        }

        @Override
        public ApiResponse<List<CashierResponseYearTotalSales>> findYearlyTotalSalesByMerchant(
                        YearTotalSalesMerchant req) {
                log.info("📊 Fetching yearly total cashier sales by merchant | MerchantID: {}, Year: {}",
                                req.getMerchantId(), req.getYear());

                if (req.getMerchantId() == null || req.getYear() == null) {
                        log.error("❌ MerchantId or Year is null | req: {}", req);
                        return ApiResponse.<List<CashierResponseYearTotalSales>>builder()
                                        .status("error")
                                        .message("MerchantId and Year must not be null")
                                        .data(List.of())
                                        .build();
                }

                try {
                        List<CashierYearTotalSales> results = cashierTotalSalesByMerchantRepository
                                        .findYearTotalSalesByMerchant(
                                                        req.getMerchantId().longValue(),
                                                        req.getYear(),
                                                        req.getYear() - 1);

                        List<CashierResponseYearTotalSales> response = results.stream()
                                        .map(CashierResponseYearTotalSales::from)
                                        .toList();

                        log.info("✅ Found {} yearly total cashier sales for merchant {}", response.size(),
                                        req.getMerchantId());

                        return ApiResponse.<List<CashierResponseYearTotalSales>>builder()
                                        .status("success")
                                        .message("Yearly total cashier sales by merchant retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to fetch yearly total cashier sales by merchant | MerchantID: {}, Year: {}",
                                        req.getMerchantId(), req.getYear(), e);
                        return ApiResponse.<List<CashierResponseYearTotalSales>>builder()
                                        .status("error")
                                        .message("Failed to fetch yearly total cashier sales by merchant")
                                        .data(List.of())
                                        .build();
                }
        }
}
