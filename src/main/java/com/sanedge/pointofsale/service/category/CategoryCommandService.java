package com.sanedge.pointofsale.service.category;

import com.sanedge.pointofsale.domain.requests.category.CreateCategoryRequest;
import com.sanedge.pointofsale.domain.requests.category.UpdateCategoryRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoryResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoryResponseDeleteAt;

public interface CategoryCommandService {
    ApiResponse<CategoryResponse> createCategory(CreateCategoryRequest req);

    ApiResponse<CategoryResponse> updateCategory(UpdateCategoryRequest req);

    ApiResponse<CategoryResponseDeleteAt> trashedCategory(Integer categoryId);

    ApiResponse<CategoryResponseDeleteAt> restoreCategory(Integer categoryId);

    ApiResponse<Boolean> deleteCategoryPermanent(Integer categoryId);

    ApiResponse<Boolean> restoreAllCategories();

    ApiResponse<Boolean> deleteAllCategoriesPermanent();
}
