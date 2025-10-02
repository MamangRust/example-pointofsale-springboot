package com.sanedge.pointofsale.service.cashier.stats;

import java.util.List;

import com.sanedge.pointofsale.domain.requests.cashier.MonthTotalSales;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.cashier.CashierResponseMonthTotalSales;
import com.sanedge.pointofsale.domain.responses.cashier.CashierResponseYearTotalSales;

public interface CashierTotalSalesService {
    ApiResponse<List<CashierResponseMonthTotalSales>> findMonthlyTotalSales(MonthTotalSales req);

    ApiResponse<List<CashierResponseYearTotalSales>> findYearlyTotalSales(Integer year);
}
