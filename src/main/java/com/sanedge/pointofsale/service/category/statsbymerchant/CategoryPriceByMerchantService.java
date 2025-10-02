package com.sanedge.pointofsale.service.category.statsbymerchant;

import java.util.List;

import com.sanedge.pointofsale.domain.requests.category.MonthPriceMerchant;
import com.sanedge.pointofsale.domain.requests.category.YearPriceMerchant;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoriesMonthPriceResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoriesYearPriceResponse;

public interface CategoryPriceByMerchantService {
    ApiResponse<List<CategoriesMonthPriceResponse>> findMonthPriceByMerchant(MonthPriceMerchant req);

    ApiResponse<List<CategoriesYearPriceResponse>> findYearPriceByMerchant(YearPriceMerchant req);
}
