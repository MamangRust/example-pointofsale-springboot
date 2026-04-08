package com.sanedge.pointofsale.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sanedge.pointofsale.domain.requests.category.CreateCategoryRequest;
import com.sanedge.pointofsale.domain.requests.category.FindAllCategory;
import com.sanedge.pointofsale.domain.requests.category.MonthPriceId;
import com.sanedge.pointofsale.domain.requests.category.MonthPriceMerchant;
import com.sanedge.pointofsale.domain.requests.category.MonthTotalPrice;
import com.sanedge.pointofsale.domain.requests.category.MonthTotalPriceCategory;
import com.sanedge.pointofsale.domain.requests.category.MonthTotalPriceMerchant;
import com.sanedge.pointofsale.domain.requests.category.UpdateCategoryRequest;
import com.sanedge.pointofsale.domain.requests.category.YearPriceId;
import com.sanedge.pointofsale.domain.requests.category.YearPriceMerchant;
import com.sanedge.pointofsale.domain.requests.category.YearTotalPriceCategory;
import com.sanedge.pointofsale.domain.requests.category.YearTotalPriceMerchant;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.api.ApiResponsePagination;
import com.sanedge.pointofsale.domain.responses.category.CategoriesMonthPriceResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoriesMonthlyTotalPriceResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoriesYearPriceResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoriesYearlyTotalPriceResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoryResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoryResponseDeleteAt;
import com.sanedge.pointofsale.service.category.CategoryCommandService;
import com.sanedge.pointofsale.service.category.CategoryQueryService;
import com.sanedge.pointofsale.service.category.stats.CategoryPriceService;
import com.sanedge.pointofsale.service.category.stats.CategoryTotalPriceService;
import com.sanedge.pointofsale.service.category.statsbyid.CategoryPriceByIdService;
import com.sanedge.pointofsale.service.category.statsbyid.CategoryTotalPriceByIdService;
import com.sanedge.pointofsale.service.category.statsbymerchant.CategoryPriceByMerchantService;
import com.sanedge.pointofsale.service.category.statsbymerchant.CategoryTotalPriceByMerchantService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/category")
public class CategoryController {
    private final CategoryQueryService categoryQueryService;
    private final CategoryCommandService categoryCommandService;
    private final CategoryTotalPriceService categoryTotalPriceService;
    private final CategoryTotalPriceByMerchantService categoryTotalPriceByMerchantService;
    private final CategoryTotalPriceByIdService categoryTotalPriceByIdService;
    private final CategoryPriceService categoryPriceService;
    private final CategoryPriceByMerchantService categoryPriceByMerchantService;
    private final CategoryPriceByIdService categoryPriceByIdService;

    @GetMapping
    public ResponseEntity<ApiResponsePagination<List<CategoryResponse>>> findAll(
            @ModelAttribute FindAllCategory req) {

        return ResponseEntity.ok(categoryQueryService.findAll(req));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponsePagination<List<CategoryResponseDeleteAt>>> findByActive(
            @ModelAttribute FindAllCategory req) {
        return ResponseEntity.ok(categoryQueryService.findByActive(req));
    }

    @GetMapping("/trashed")
    public ResponseEntity<ApiResponsePagination<List<CategoryResponseDeleteAt>>> findByTrashed(
            @ModelAttribute FindAllCategory req) {

        return ResponseEntity.ok(categoryQueryService.findByTrashed(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(categoryQueryService.findById(id));
    }

    @GetMapping("/monthly-total-pricing")
    public ResponseEntity<ApiResponse<List<CategoriesMonthlyTotalPriceResponse>>> findMonthTotalPrice(
            @ModelAttribute MonthTotalPrice req) {
        return ResponseEntity.ok(categoryTotalPriceService.findMonthlyTotalPrice(req));
    }

    @GetMapping("/yearly-total-pricing")
    public ResponseEntity<ApiResponse<List<CategoriesYearlyTotalPriceResponse>>> findYearTotalPrice(
            @RequestParam Integer year) {
        return ResponseEntity.ok(categoryTotalPriceService.findYearlyTotalPrice(year));
    }

    @GetMapping("/merchant/monthly-total-pricing")
    public ResponseEntity<ApiResponse<List<CategoriesMonthlyTotalPriceResponse>>> findMonthTotalPriceByMerchant(
            @ModelAttribute MonthTotalPriceMerchant req) {
        return ResponseEntity.ok(categoryTotalPriceByMerchantService.findMonthlyTotalPriceByMerchant(req));
    }

    @GetMapping("/merchant/yearly-total-pricing")
    public ResponseEntity<ApiResponse<List<CategoriesYearlyTotalPriceResponse>>> findYearTotalPriceByMerchant(
            @ModelAttribute YearTotalPriceMerchant req) {
        return ResponseEntity.ok(categoryTotalPriceByMerchantService.findYearlyTotalPriceByMerchant(req));
    }

    @GetMapping("/mycategory/monthly-total-pricing")
    public ResponseEntity<ApiResponse<List<CategoriesMonthlyTotalPriceResponse>>> findMonthTotalPriceById(
            @ModelAttribute MonthTotalPriceCategory req) {
        return ResponseEntity.ok(categoryTotalPriceByIdService.findMonthlyTotalPriceById(req));
    }

    @GetMapping("/mycategory/yearly-total-pricing")
    public ResponseEntity<ApiResponse<List<CategoriesYearlyTotalPriceResponse>>> findYearTotalPriceById(
            @ModelAttribute YearTotalPriceCategory req) {
        return ResponseEntity.ok(categoryTotalPriceByIdService.findYearlyTotalPriceById(req));
    }

    @GetMapping("/monthly-pricing")
    public ResponseEntity<ApiResponse<List<CategoriesMonthPriceResponse>>> findMonthPrice(
            @RequestParam Integer year) {
        return ResponseEntity.ok(categoryPriceService.findMonthPrice(year));
    }

    @GetMapping("/yearly-pricing")
    public ResponseEntity<ApiResponse<List<CategoriesYearPriceResponse>>> findYearPrice(
            @RequestParam Integer year) {
        return ResponseEntity.ok(categoryPriceService.findYearPrice(year));
    }

    @GetMapping("/merchant/monthly-pricing")
    public ResponseEntity<ApiResponse<List<CategoriesMonthPriceResponse>>> findMonthPriceByMerchant(
            @ModelAttribute MonthPriceMerchant req) {

        return ResponseEntity.ok(categoryPriceByMerchantService.findMonthPriceByMerchant(req));
    }

    @GetMapping("/merchant/yearly-pricing")
    public ResponseEntity<ApiResponse<List<CategoriesYearPriceResponse>>> findYearPriceByMerchant(
            @ModelAttribute YearPriceMerchant req) {
        return ResponseEntity.ok(categoryPriceByMerchantService.findYearPriceByMerchant(req));
    }

    @GetMapping("/mycategory/monthly-pricing")
    public ResponseEntity<ApiResponse<List<CategoriesMonthPriceResponse>>> findMonthPriceById(
            @ModelAttribute MonthPriceId req) {
        return ResponseEntity.ok(categoryPriceByIdService.findMonthPriceById(req));
    }

    @GetMapping("/mycategory/yearly-pricing")
    public ResponseEntity<ApiResponse<List<CategoriesYearPriceResponse>>> findYearPriceById(
            @ModelAttribute YearPriceId req) {
        return ResponseEntity.ok(categoryPriceByIdService.findYearPriceById(req));
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @ModelAttribute CreateCategoryRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryCommandService.createCategory(req));
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Integer id,
            @Valid @ModelAttribute UpdateCategoryRequest req) {
        req.setCategoryId(id);
        return ResponseEntity.ok(categoryCommandService.updateCategory(req));
    }

    @PostMapping("/trashed/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponseDeleteAt>> trashedCategory(@PathVariable Integer id) {
        return ResponseEntity.ok(categoryCommandService.trashedCategory(id));
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponseDeleteAt>> restoreCategory(@PathVariable Integer id) {
        return ResponseEntity.ok(categoryCommandService.restoreCategory(id));
    }

    @DeleteMapping("/permanent/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteCategoryPermanent(@PathVariable Integer id) {
        return ResponseEntity.ok(categoryCommandService.deleteCategoryPermanent(id));
    }

    @PostMapping("/restore/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAllCategories() {
        return ResponseEntity.ok(categoryCommandService.restoreAllCategories());
    }

    @PostMapping("/permanent/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllCategoriesPermanent() {
        return ResponseEntity.ok(categoryCommandService.deleteAllCategoriesPermanent());
    }
}