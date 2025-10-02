package com.sanedge.pointofsale.service.category.stats;

import java.util.List;

import com.sanedge.pointofsale.domain.requests.category.MonthTotalPrice;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoriesMonthlyTotalPriceResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoriesYearlyTotalPriceResponse;

public interface CategoryTotalPriceService {
    ApiResponse<List<CategoriesMonthlyTotalPriceResponse>> findMonthlyTotalPrice(MonthTotalPrice req);

    ApiResponse<List<CategoriesYearlyTotalPriceResponse>> findYearlyTotalPrice(Integer year);
}
