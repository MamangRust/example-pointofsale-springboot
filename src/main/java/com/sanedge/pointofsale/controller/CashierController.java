package com.sanedge.pointofsale.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sanedge.pointofsale.domain.requests.cashier.CreateCashierRequest;
import com.sanedge.pointofsale.domain.requests.cashier.FindAllCashierMerchant;
import com.sanedge.pointofsale.domain.requests.cashier.FindAllCashiers;
import com.sanedge.pointofsale.domain.requests.cashier.MonthCashierIdRequest;
import com.sanedge.pointofsale.domain.requests.cashier.MonthCashierMerchantRequest;
import com.sanedge.pointofsale.domain.requests.cashier.MonthTotalSales;
import com.sanedge.pointofsale.domain.requests.cashier.MonthTotalSalesCashier;
import com.sanedge.pointofsale.domain.requests.cashier.MonthTotalSalesMerchant;
import com.sanedge.pointofsale.domain.requests.cashier.UpdateCashierRequest;
import com.sanedge.pointofsale.domain.requests.cashier.YearCashierIdRequest;
import com.sanedge.pointofsale.domain.requests.cashier.YearCashierMerchantRequest;
import com.sanedge.pointofsale.domain.requests.cashier.YearTotalSalesCashier;
import com.sanedge.pointofsale.domain.requests.cashier.YearTotalSalesMerchant;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.api.ApiResponsePagination;
import com.sanedge.pointofsale.domain.responses.cashier.CashierResponse;
import com.sanedge.pointofsale.domain.responses.cashier.CashierResponseDeleteAt;
import com.sanedge.pointofsale.domain.responses.cashier.CashierResponseMonthSales;
import com.sanedge.pointofsale.domain.responses.cashier.CashierResponseMonthTotalSales;
import com.sanedge.pointofsale.domain.responses.cashier.CashierResponseYearSales;
import com.sanedge.pointofsale.domain.responses.cashier.CashierResponseYearTotalSales;
import com.sanedge.pointofsale.service.cashier.CashierCommandService;
import com.sanedge.pointofsale.service.cashier.CashierQueryService;
import com.sanedge.pointofsale.service.cashier.stats.CashierSalesService;
import com.sanedge.pointofsale.service.cashier.stats.CashierTotalSalesService;
import com.sanedge.pointofsale.service.cashier.statsbyid.CashierSalesByIdService;
import com.sanedge.pointofsale.service.cashier.statsbyid.CashierTotalSalesByIdService;
import com.sanedge.pointofsale.service.cashier.statsbymerchant.CashierSalesByMerchantService;
import com.sanedge.pointofsale.service.cashier.statsbymerchant.CashierTotalSalesByMerchantService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/cashier")
@RequiredArgsConstructor
@Slf4j
public class CashierController {
    private final CashierQueryService cashierQueryService;
    private final CashierCommandService cashierCommandService;
    private final CashierSalesService cashierSalesService;
    private final CashierTotalSalesService cashierTotalSalesService;
    private final CashierSalesByIdService cashierSalesByIdService;
    private final CashierTotalSalesByIdService cashierTotalSalesByIdService;
    private final CashierSalesByMerchantService cashierSalesByMerchantService;
    private final CashierTotalSalesByMerchantService cashierTotalSalesByMerchantService;

    @GetMapping("")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")    
    public ApiResponsePagination<List<CashierResponse>> findAll(FindAllCashiers req) {
        return cashierQueryService.findAll(req);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<CashierResponse> findById(@PathVariable Integer id) {
        return cashierQueryService.findById(id);
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponsePagination<List<CashierResponseDeleteAt>> findByActive(FindAllCashiers req) {
        return cashierQueryService.findByActive(req);
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponsePagination<List<CashierResponseDeleteAt>> findByTrashed(FindAllCashiers req) {
        return cashierQueryService.findByTrashed(req);
    }

    @GetMapping("/merchant")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponsePagination<List<CashierResponse>> findByMerchant(FindAllCashierMerchant req) {
        return cashierQueryService.findByMerchant(req);
    }

    @GetMapping("/monthly-sales")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<CashierResponseMonthSales>> findMonthSales(@RequestParam Integer year) {
        return cashierSalesService.findMonthlySales(year);
    }

    @GetMapping("/yearly-sales")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<CashierResponseYearSales>> findYearSales(@RequestParam Integer year) {
        return cashierSalesService.findYearlySales(year);
    }

    @GetMapping("/monthly-total-sales")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<CashierResponseMonthTotalSales>> findMonthlyTotalSales(MonthTotalSales req) {
        return cashierTotalSalesService.findMonthlyTotalSales(req);
    }

    @GetMapping("/yearly-total-sales")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<CashierResponseYearTotalSales>> findYearTotalSales(@RequestParam Integer year) {
        return cashierTotalSalesService.findYearlyTotalSales(year);
    }

    @GetMapping("/mycashier/monthly-sales")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<CashierResponseMonthSales>> findMonthSalesById(MonthCashierIdRequest req) {
        return cashierSalesByIdService.findMonthlyCashierById(req);
    }

    @GetMapping("/mycashier/yearly-sales")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<CashierResponseYearSales>> findYearSalesById(YearCashierIdRequest req) {
        return cashierSalesByIdService.findYearlyCashierById(req);
    }

    @GetMapping("/mycashier/monthly-total-sales")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<CashierResponseMonthTotalSales>> findMonthlyTotalSalesById(MonthTotalSalesCashier req) {
        return cashierTotalSalesByIdService.findMonthlyTotalSalesById(req);
    }

    @GetMapping("/mycashier/yearly-total-sales")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<CashierResponseYearTotalSales>> findYearlyTotalSalesById(YearTotalSalesCashier req) {
        return cashierTotalSalesByIdService.findYearlyTotalSalesById(req);
    }

    @GetMapping("/merchant/monthly-sales")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<CashierResponseMonthSales>> findMonthSalesByMerchant(MonthCashierMerchantRequest req) {
        return cashierSalesByMerchantService.findMonthlyCashierByMerchant(req);
    }

    @GetMapping("/merchant/yearly-sales")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<CashierResponseYearSales>> findYearSalesByMerchant(YearCashierMerchantRequest req) {
        return cashierSalesByMerchantService.findYearlyCashierByMerchant(req);
    }

    @GetMapping("/merchant/monthly-total-sales")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<CashierResponseMonthTotalSales>> findMonthlyTotalSalesByMerchant(
            MonthTotalSalesMerchant req) {
        return cashierTotalSalesByMerchantService.findMonthlyTotalSalesByMerchant(req);
    }

    @GetMapping("/merchant/yearly-total-sales")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<CashierResponseYearTotalSales>> findYearlyTotalSalesByMerchant(YearTotalSalesMerchant req) {
        return cashierTotalSalesByMerchantService.findYearlyTotalSalesByMerchant(req);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<CashierResponse> createCashier(@RequestBody CreateCashierRequest req) {
        return cashierCommandService.createCashier(req);
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<CashierResponse> updateCashier(@PathVariable Integer id,
            @RequestBody UpdateCashierRequest req) {
        req.setCashierId(id);
        return cashierCommandService.updateCashier(req);
    }

    @PostMapping("/trashed/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<CashierResponseDeleteAt> trashedCashier(@PathVariable Integer id) {
        return cashierCommandService.trashedCashier(id);
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<CashierResponseDeleteAt> restoreCashier(@PathVariable Integer id) {
        return cashierCommandService.restoreCashier(id);
    }

    @DeleteMapping("/permanent/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Boolean> deleteCashierPermanent(@PathVariable Integer id) {
        return cashierCommandService.deleteCashierPermanent(id);
    }

    @PostMapping("/restore/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Boolean> restoreAllCashier() {
        return cashierCommandService.restoreAllCashier();
    }

    @PostMapping("/permanent/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Boolean> deleteAllCashierPermanent() {
        return cashierCommandService.deleteAllCashierPermanent();
    }
}
