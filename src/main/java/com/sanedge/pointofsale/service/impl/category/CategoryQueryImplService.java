package com.sanedge.pointofsale.service.impl.category;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sanedge.pointofsale.domain.requests.category.FindAllCategory;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.api.ApiResponsePagination;
import com.sanedge.pointofsale.domain.responses.api.PaginationMeta;
import com.sanedge.pointofsale.domain.responses.category.CategoryResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoryResponseDeleteAt;
import com.sanedge.pointofsale.models.category.Category;
import com.sanedge.pointofsale.repository.category.CategoryQueryRepository;
import com.sanedge.pointofsale.service.category.CategoryQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryQueryImplService implements CategoryQueryService {

    private final CategoryQueryRepository categoryQueryRepository;

    @Override
    public ApiResponsePagination<List<CategoryResponse>> findAll(FindAllCategory req) {
        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
        String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

        log.info("🔍 Searching all categories | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                keyword.isEmpty() ? "None" : keyword);

        try {
            Pageable pageable = PageRequest.of(page, pageSize);
            Page<Category> categoryPage = categoryQueryRepository.findCategories(keyword, pageable);

            List<CategoryResponse> responses = categoryPage.getContent()
                    .stream()
                    .map(CategoryResponse::from)
                    .toList();

            log.info("✅ Found {} categories", responses.size());

            return ApiResponsePagination.<List<CategoryResponse>>builder()
                    .status("success")
                    .message("Categories retrieved successfully")
                    .data(responses)
                    .pagination(PaginationMeta.fromSpringPage(categoryPage))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to fetch categories: {}", e.getMessage(), e);
            return ApiResponsePagination.<List<CategoryResponse>>builder()
                    .status("error")
                    .message("Failed to fetch categories")
                    .data(Collections.emptyList())
                    .pagination(null)
                    .build();
        }
    }

    @Override
    public ApiResponsePagination<List<CategoryResponseDeleteAt>> findByActive(FindAllCategory req) {
        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
        String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

        log.info("🔍 Searching active categories | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                keyword.isEmpty() ? "None" : keyword);

        try {
            Pageable pageable = PageRequest.of(page, pageSize);
            Page<Category> categoryPage = categoryQueryRepository.findActiveCategories(keyword, pageable);

            List<CategoryResponseDeleteAt> responses = categoryPage.getContent()
                    .stream()
                    .map(CategoryResponseDeleteAt::from)
                    .toList();

            log.info("✅ Found {} active categories", responses.size());

            return ApiResponsePagination.<List<CategoryResponseDeleteAt>>builder()
                    .status("success")
                    .message("Active categories retrieved successfully")
                    .data(responses)
                    .pagination(PaginationMeta.fromSpringPage(categoryPage))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to fetch active categories: {}", e.getMessage(), e);
            return ApiResponsePagination.<List<CategoryResponseDeleteAt>>builder()
                    .status("error")
                    .message("Failed to fetch active categories")
                    .data(Collections.emptyList())
                    .pagination(null)
                    .build();
        }
    }

    @Override
    public ApiResponsePagination<List<CategoryResponseDeleteAt>> findByTrashed(FindAllCategory req) {
        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
        String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

        log.info("🗑 Searching trashed categories | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                keyword.isEmpty() ? "None" : keyword);

        try {
            Pageable pageable = PageRequest.of(page, pageSize);
            Page<Category> categoryPage = categoryQueryRepository.findTrashedCategories(keyword, pageable);

            List<CategoryResponseDeleteAt> responses = categoryPage.getContent()
                    .stream()
                    .map(CategoryResponseDeleteAt::from)
                    .toList();

            log.info("✅ Found {} trashed categories", responses.size());

            return ApiResponsePagination.<List<CategoryResponseDeleteAt>>builder()
                    .status("success")
                    .message("Trashed categories retrieved successfully")
                    .data(responses)
                    .pagination(PaginationMeta.fromSpringPage(categoryPage))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to fetch trashed categories: {}", e.getMessage(), e);
            return ApiResponsePagination.<List<CategoryResponseDeleteAt>>builder()
                    .status("error")
                    .message("Failed to fetch trashed categories")
                    .data(Collections.emptyList())
                    .pagination(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<CategoryResponse> findById(Integer categoryId) {
        log.info("🔎 Finding category by ID: {}", categoryId);
        try {
            return categoryQueryRepository.findCategoryById(categoryId.longValue())
                    .map(category -> ApiResponse.<CategoryResponse>builder()
                            .status("success")
                            .message("Category retrieved successfully")
                            .data(CategoryResponse.from(category))
                            .build())
                    .orElseGet(() -> ApiResponse.<CategoryResponse>builder()
                            .status("error")
                            .message("Category not found")
                            .data(null)
                            .build());
        } catch (Exception e) {
            log.error("💥 Failed to fetch category by id={}: {}", categoryId, e.getMessage(), e);
            return ApiResponse.<CategoryResponse>builder()
                    .status("error")
                    .message("Failed to fetch category")
                    .data(null)
                    .build();
        }
    }
}
