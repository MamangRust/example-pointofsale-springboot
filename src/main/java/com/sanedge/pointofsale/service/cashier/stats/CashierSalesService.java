package com.sanedge.pointofsale.service.cashier.stats;

import java.util.List;

import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.cashier.CashierResponseMonthSales;
import com.sanedge.pointofsale.domain.responses.cashier.CashierResponseYearSales;

public interface CashierSalesService {
    ApiResponse<List<CashierResponseMonthSales>> findMonthlySales(Integer year);

    ApiResponse<List<CashierResponseYearSales>> findYearlySales(Integer year);
}
