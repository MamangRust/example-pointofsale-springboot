package com.sanedge.pointofsale.service.category;

import java.util.List;

import com.sanedge.pointofsale.domain.requests.category.FindAllCategory;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.api.ApiResponsePagination;
import com.sanedge.pointofsale.domain.responses.category.CategoryResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoryResponseDeleteAt;

public interface CategoryQueryService {
    ApiResponsePagination<List<CategoryResponse>> findAll(FindAllCategory req);

    ApiResponse<CategoryResponse> findById(Integer categoryId);

    ApiResponsePagination<List<CategoryResponseDeleteAt>> findByActive(FindAllCategory req);

    ApiResponsePagination<List<CategoryResponseDeleteAt>> findByTrashed(FindAllCategory req);
}
