package com.sanedge.pointofsale.service.impl.cashier.statsbyid;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.pointofsale.domain.requests.cashier.MonthTotalSalesCashier;
import com.sanedge.pointofsale.domain.requests.cashier.YearTotalSalesCashier;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.cashier.CashierResponseMonthTotalSales;
import com.sanedge.pointofsale.domain.responses.cashier.CashierResponseYearTotalSales;
import com.sanedge.pointofsale.models.cashier.CashierMonthTotalSales;
import com.sanedge.pointofsale.models.cashier.CashierYearTotalSales;
import com.sanedge.pointofsale.repository.cashier.statsbyid.CashierTotalSalesByIdRepository;
import com.sanedge.pointofsale.service.cashier.statsbyid.CashierTotalSalesByIdService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CashierTotalSalesByIdImplService implements CashierTotalSalesByIdService {
    private final CashierTotalSalesByIdRepository cashierTotalSalesByIdRepository;

    @Override
    public ApiResponse<List<CashierResponseMonthTotalSales>> findMonthlyTotalSalesById(MonthTotalSalesCashier req) {
        log.info("📊 Fetching monthly total cashier sales by cashierId | CashierID: {}, Year: {}, Month: {}",
                req.getCashierId(), req.getYear(), req.getMonth());

        if (req.getCashierId() == null || req.getYear() == null || req.getMonth() == null) {
            log.error("❌ CashierId, Year, or Month is null | req: {}", req);
            return ApiResponse.<List<CashierResponseMonthTotalSales>>builder()
                    .status("error")
                    .message("CashierId, Year, and Month must not be null")
                    .data(List.of())
                    .build();
        }

        try {
            LocalDate currentMonth = LocalDate.of(req.getYear(), req.getMonth(), 1);
            LocalDate nextMonth = currentMonth.plusMonths(1);

            List<CashierMonthTotalSales> results = cashierTotalSalesByIdRepository.findMonthTotalSalesById(
                    req.getCashierId().longValue(),
                    req.getYear(),
                    req.getMonth(),
                    nextMonth.getYear(),
                    nextMonth.getMonthValue());

            List<CashierResponseMonthTotalSales> response = results.stream()
                    .map(CashierResponseMonthTotalSales::from)
                    .toList();

            log.info("✅ Found {} monthly total cashier sales for cashier {}", response.size(), req.getCashierId());

            return ApiResponse.<List<CashierResponseMonthTotalSales>>builder()
                    .status("success")
                    .message("Monthly total cashier sales by cashier retrieved successfully")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error(
                    "💥 Failed to fetch monthly total cashier sales by cashier | CashierID: {}, Year: {}, Month: {}",
                    req.getCashierId(), req.getYear(), req.getMonth(), e);
            return ApiResponse.<List<CashierResponseMonthTotalSales>>builder()
                    .status("error")
                    .message("Failed to fetch monthly total cashier sales by cashier")
                    .data(List.of())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<CashierResponseYearTotalSales>> findYearlyTotalSalesById(YearTotalSalesCashier req) {
        log.info("📊 Fetching yearly total cashier sales by cashierId | CashierID: {}, Year: {}",
                req.getCashierId(), req.getYear());

        if (req.getCashierId() == null || req.getYear() == null) {
            log.error("❌ CashierId or Year is null | req: {}", req);
            return ApiResponse.<List<CashierResponseYearTotalSales>>builder()
                    .status("error")
                    .message("CashierId and Year must not be null")
                    .data(List.of())
                    .build();
        }

        try {
            List<CashierYearTotalSales> results = cashierTotalSalesByIdRepository.findYearTotalSalesById(
                    req.getCashierId().longValue(),
                    req.getYear(),
                    req.getYear() - 1);

            List<CashierResponseYearTotalSales> response = results.stream()
                    .map(CashierResponseYearTotalSales::from)
                    .toList();

            log.info("✅ Found {} yearly total cashier sales for cashier {}", response.size(), req.getCashierId());

            return ApiResponse.<List<CashierResponseYearTotalSales>>builder()
                    .status("success")
                    .message("Yearly total cashier sales by cashier retrieved successfully")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch yearly total cashier sales by cashier | CashierID: {}, Year: {}",
                    req.getCashierId(), req.getYear(), e);
            return ApiResponse.<List<CashierResponseYearTotalSales>>builder()
                    .status("error")
                    .message("Failed to fetch yearly total cashier sales by cashier")
                    .data(List.of())
                    .build();
        }
    }
}
