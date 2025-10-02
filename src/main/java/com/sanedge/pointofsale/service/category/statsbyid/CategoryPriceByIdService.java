package com.sanedge.pointofsale.service.category.statsbyid;

import java.util.List;

import com.sanedge.pointofsale.domain.requests.category.MonthPriceId;
import com.sanedge.pointofsale.domain.requests.category.YearPriceId;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoriesMonthPriceResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoriesYearPriceResponse;

public interface CategoryPriceByIdService {
    ApiResponse<List<CategoriesMonthPriceResponse>> findMonthPriceById(MonthPriceId req);

    ApiResponse<List<CategoriesYearPriceResponse>> findYearPriceById(YearPriceId req);
}
