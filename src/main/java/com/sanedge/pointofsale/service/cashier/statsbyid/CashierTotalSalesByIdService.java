package com.sanedge.pointofsale.service.cashier.statsbyid;

import java.util.List;

import com.sanedge.pointofsale.domain.requests.cashier.MonthTotalSalesCashier;
import com.sanedge.pointofsale.domain.requests.cashier.YearTotalSalesCashier;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.cashier.CashierResponseMonthTotalSales;
import com.sanedge.pointofsale.domain.responses.cashier.CashierResponseYearTotalSales;

public interface CashierTotalSalesByIdService {
    ApiResponse<List<CashierResponseMonthTotalSales>> findMonthlyTotalSalesById(MonthTotalSalesCashier req);

    ApiResponse<List<CashierResponseYearTotalSales>> findYearlyTotalSalesById(YearTotalSalesCashier req);
}
