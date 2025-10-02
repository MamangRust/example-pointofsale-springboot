package com.sanedge.pointofsale.service.category.statsbyid;

import java.util.List;

import com.sanedge.pointofsale.domain.requests.category.MonthTotalPriceCategory;
import com.sanedge.pointofsale.domain.requests.category.YearTotalPriceCategory;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoriesMonthlyTotalPriceResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoriesYearlyTotalPriceResponse;

public interface CategoryTotalPriceByIdService {
    ApiResponse<List<CategoriesMonthlyTotalPriceResponse>> findMonthlyTotalPriceById(MonthTotalPriceCategory req);

    ApiResponse<List<CategoriesYearlyTotalPriceResponse>> findYearlyTotalPriceById(YearTotalPriceCategory req);
}
