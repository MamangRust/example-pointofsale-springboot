package com.sanedge.pointofsale.service.cashier.statsbymerchant;

import java.util.List;

import com.sanedge.pointofsale.domain.requests.cashier.MonthTotalSalesMerchant;
import com.sanedge.pointofsale.domain.requests.cashier.YearTotalSalesMerchant;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.cashier.CashierResponseMonthTotalSales;
import com.sanedge.pointofsale.domain.responses.cashier.CashierResponseYearTotalSales;

public interface CashierTotalSalesByMerchantService {
    ApiResponse<List<CashierResponseMonthTotalSales>> findMonthlyTotalSalesByMerchant(MonthTotalSalesMerchant req);

    ApiResponse<List<CashierResponseYearTotalSales>> findYearlyTotalSalesByMerchant(YearTotalSalesMerchant req);
}
