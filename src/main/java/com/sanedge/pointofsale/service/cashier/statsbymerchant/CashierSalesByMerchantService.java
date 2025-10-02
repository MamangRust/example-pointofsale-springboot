package com.sanedge.pointofsale.service.cashier.statsbymerchant;

import java.util.List;

import com.sanedge.pointofsale.domain.requests.cashier.MonthCashierMerchantRequest;
import com.sanedge.pointofsale.domain.requests.cashier.YearCashierMerchantRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.cashier.CashierResponseMonthSales;
import com.sanedge.pointofsale.domain.responses.cashier.CashierResponseYearSales;

public interface CashierSalesByMerchantService {
    ApiResponse<List<CashierResponseMonthSales>> findMonthlyCashierByMerchant(MonthCashierMerchantRequest req);

    ApiResponse<List<CashierResponseYearSales>> findYearlyCashierByMerchant(YearCashierMerchantRequest req);
}
