package com.sanedge.pointofsale.service.cashier.statsbyid;

import java.util.List;

import com.sanedge.pointofsale.domain.requests.cashier.MonthCashierIdRequest;
import com.sanedge.pointofsale.domain.requests.cashier.YearCashierIdRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.cashier.CashierResponseMonthSales;
import com.sanedge.pointofsale.domain.responses.cashier.CashierResponseYearSales;

public interface CashierSalesByIdService {
    ApiResponse<List<CashierResponseMonthSales>> findMonthlyCashierById(MonthCashierIdRequest req);

    ApiResponse<List<CashierResponseYearSales>> findYearlyCashierById(YearCashierIdRequest req);
}
