package com.sanedge.pointofsale.service.category.stats;

import java.util.List;

import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoriesMonthPriceResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoriesYearPriceResponse;

public interface CategoryPriceService {
    ApiResponse<List<CategoriesMonthPriceResponse>> findMonthPrice(Integer year);

    ApiResponse<List<CategoriesYearPriceResponse>> findYearPrice(Integer year);
}
