package com.sanedge.pointofsale.service.category.statsbymerchant;

import java.util.List;

import com.sanedge.pointofsale.domain.requests.category.MonthTotalPriceMerchant;
import com.sanedge.pointofsale.domain.requests.category.YearTotalPriceMerchant;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoriesMonthlyTotalPriceResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoriesYearlyTotalPriceResponse;

public interface CategoryTotalPriceByMerchantService {
    ApiResponse<List<CategoriesMonthlyTotalPriceResponse>> findMonthlyTotalPriceByMerchant(MonthTotalPriceMerchant req);

    ApiResponse<List<CategoriesYearlyTotalPriceResponse>> findYearlyTotalPriceByMerchant(YearTotalPriceMerchant req);
}
