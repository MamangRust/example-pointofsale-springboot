package com.sanedge.pointofsale.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.sanedge.pointofsale.BaseIntegrationTest;
import com.sanedge.pointofsale.domain.requests.category.MonthPriceId;
import com.sanedge.pointofsale.domain.requests.category.MonthPriceMerchant;
import com.sanedge.pointofsale.domain.requests.category.MonthTotalPrice;
import com.sanedge.pointofsale.domain.requests.category.MonthTotalPriceCategory;
import com.sanedge.pointofsale.domain.requests.category.MonthTotalPriceMerchant;
import com.sanedge.pointofsale.domain.requests.category.YearPriceId;
import com.sanedge.pointofsale.domain.requests.category.YearPriceMerchant;
import com.sanedge.pointofsale.domain.requests.category.YearTotalPriceCategory;
import com.sanedge.pointofsale.domain.requests.category.YearTotalPriceMerchant;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoriesMonthPriceResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoriesMonthlyTotalPriceResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoriesYearPriceResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoriesYearlyTotalPriceResponse;
import com.sanedge.pointofsale.repository.category.stats.CategoryPriceRepository;
import com.sanedge.pointofsale.repository.category.stats.CategoryTotalPriceRepository;
import com.sanedge.pointofsale.repository.category.statsbyid.CategoryPriceByIdRepository;
import com.sanedge.pointofsale.repository.category.statsbyid.CategoryTotalPriceByIdRepository;
import com.sanedge.pointofsale.repository.category.statsbymerchant.CategoryPriceByMerchantRepository;
import com.sanedge.pointofsale.repository.category.statsbymerchant.CategoryTotalPriceByMerchantRepository;
import com.sanedge.pointofsale.service.category.statsbyid.CategoryPriceByIdService;
import com.sanedge.pointofsale.service.category.statsbyid.CategoryTotalPriceByIdService;
import com.sanedge.pointofsale.service.category.statsbymerchant.CategoryPriceByMerchantService;
import com.sanedge.pointofsale.service.category.statsbymerchant.CategoryTotalPriceByMerchantService;
import com.sanedge.pointofsale.service.category.stats.CategoryPriceService;
import com.sanedge.pointofsale.service.category.stats.CategoryTotalPriceService;

public class CategoryStatsServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CategoryPriceByIdService categoryPriceByIdService;

    @Autowired
    private CategoryPriceByMerchantService categoryPriceByMerchantService;

    @Autowired
    private CategoryPriceService categoryPriceService;

    @Autowired
    private CategoryTotalPriceByIdService categoryTotalPriceByIdService;

    @Autowired
    private CategoryTotalPriceByMerchantService categoryTotalPriceByMerchantService;

    @Autowired
    private CategoryTotalPriceService categoryTotalPriceService;

    @MockitoBean
    private CategoryPriceRepository categoryPriceRepository;

    @MockitoBean
    private CategoryPriceByMerchantRepository categoryPriceByMerchantRepository;

    @MockitoBean
    private CategoryPriceByIdRepository categoryPriceByIdRepository;

    @MockitoBean
    private CategoryTotalPriceRepository categoryTotalPriceRepository;

    @MockitoBean
    private CategoryTotalPriceByMerchantRepository categoryTotalPriceByMerchantRepository;

    @MockitoBean
    private CategoryTotalPriceByIdRepository categoryTotalPriceByIdRepository;

    @BeforeEach
    void setupMocks() {
        when(categoryPriceRepository.findMonthlyCategoryPrice(any())).thenReturn(List.of());
        when(categoryPriceRepository.findYearlyCategoryPrice(any())).thenReturn(List.of());

        when(categoryPriceByMerchantRepository.findMonthlyCategoryPriceByMerchant(any(), any())).thenReturn(List.of());
        when(categoryPriceByMerchantRepository.findYearlyCategoryPriceByMerchant(any(), any())).thenReturn(List.of());

        when(categoryPriceByIdRepository.findMonthlyCategoryPriceById(any(), any())).thenReturn(List.of());
        when(categoryPriceByIdRepository.findYearlyCategoryPriceById(any(), any())).thenReturn(List.of());

        when(categoryTotalPriceRepository.findMonthlyTotalPrice(any(), any(), any(), any())).thenReturn(List.of());
        when(categoryTotalPriceRepository.findYearlyTotalPrice(any(), any())).thenReturn(List.of());

        when(categoryTotalPriceByMerchantRepository.findMonthlyTotalPriceByMerchant(any(), any(), any(), any(), any())).thenReturn(List.of());
        when(categoryTotalPriceByMerchantRepository.findYearlyTotalPriceByMerchant(any(), any(), any())).thenReturn(List.of());

        when(categoryTotalPriceByIdRepository.findMonthlyTotalPriceByCategoryId(any(), any(), any(), any(), any())).thenReturn(List.of());
        when(categoryTotalPriceByIdRepository.findYearlyTotalPriceByCategoryId(any(), any(), any())).thenReturn(List.of());
    }

    @Test
    void testAllCategoryStatsServices() {
        ApiResponse<List<CategoriesMonthPriceResponse>> mPriceResp = categoryPriceService.findMonthPrice(2026);
        assertThat(mPriceResp).isNotNull();
        assertThat(mPriceResp.getData()).isNotNull();

        ApiResponse<List<CategoriesYearPriceResponse>> yPriceResp = categoryPriceService.findYearPrice(2026);
        assertThat(yPriceResp).isNotNull();
        assertThat(yPriceResp.getData()).isNotNull();

        MonthPriceMerchant mpMerchantReq = new MonthPriceMerchant();
        mpMerchantReq.setMerchantId(1);
        mpMerchantReq.setYear(2026);
        ApiResponse<List<CategoriesMonthPriceResponse>> mpmResp = categoryPriceByMerchantService.findMonthPriceByMerchant(mpMerchantReq);
        assertThat(mpmResp).isNotNull();
        assertThat(mpmResp.getData()).isNotNull();

        YearPriceMerchant ypMerchantReq = new YearPriceMerchant();
        ypMerchantReq.setMerchantId(1);
        ypMerchantReq.setYear(2026);
        ApiResponse<List<CategoriesYearPriceResponse>> ypmResp = categoryPriceByMerchantService.findYearPriceByMerchant(ypMerchantReq);
        assertThat(ypmResp).isNotNull();
        assertThat(ypmResp.getData()).isNotNull();

        MonthPriceId mpiReq = new MonthPriceId();
        mpiReq.setCategoryId(1);
        mpiReq.setYear(2026);
        ApiResponse<List<CategoriesMonthPriceResponse>> mpiResp = categoryPriceByIdService.findMonthPriceById(mpiReq);
        assertThat(mpiResp).isNotNull();
        assertThat(mpiResp.getData()).isNotNull();

        YearPriceId ypiReq = new YearPriceId();
        ypiReq.setCategoryId(1);
        ypiReq.setYear(2026);
        ApiResponse<List<CategoriesYearPriceResponse>> ypiResp = categoryPriceByIdService.findYearPriceById(ypiReq);
        assertThat(ypiResp).isNotNull();
        assertThat(ypiResp.getData()).isNotNull();

        MonthTotalPrice mtpReq = new MonthTotalPrice();
        mtpReq.setMonth(5);
        mtpReq.setYear(2026);
        ApiResponse<List<CategoriesMonthlyTotalPriceResponse>> mtpResp = categoryTotalPriceService.findMonthlyTotalPrice(mtpReq);
        assertThat(mtpResp).isNotNull();
        assertThat(mtpResp.getData()).isNotNull();

        ApiResponse<List<CategoriesYearlyTotalPriceResponse>> ytpResp = categoryTotalPriceService.findYearlyTotalPrice(2026);
        assertThat(ytpResp).isNotNull();
        assertThat(ytpResp.getData()).isNotNull();

        MonthTotalPriceMerchant mtpmReq = new MonthTotalPriceMerchant();
        mtpmReq.setMerchantId(1);
        mtpmReq.setYear(2026);
        ApiResponse<List<CategoriesMonthlyTotalPriceResponse>> mtpmResp = categoryTotalPriceByMerchantService.findMonthlyTotalPriceByMerchant(mtpmReq);
        assertThat(mtpmResp).isNotNull();
        assertThat(mtpmResp.getData()).isNotNull();

        YearTotalPriceMerchant ytpmReq = new YearTotalPriceMerchant();
        ytpmReq.setMerchantId(1);
        ytpmReq.setYear(2026);
        ApiResponse<List<CategoriesYearlyTotalPriceResponse>> ytpmResp = categoryTotalPriceByMerchantService.findYearlyTotalPriceByMerchant(ytpmReq);
        assertThat(ytpmResp).isNotNull();
        assertThat(ytpmResp.getData()).isNotNull();

        MonthTotalPriceCategory mtpiReq = new MonthTotalPriceCategory();
        mtpiReq.setCategoryId(1);
        mtpiReq.setMonth(5);
        mtpiReq.setYear(2026);
        ApiResponse<List<CategoriesMonthlyTotalPriceResponse>> mtpiResp = categoryTotalPriceByIdService.findMonthlyTotalPriceById(mtpiReq);
        assertThat(mtpiResp).isNotNull();
        assertThat(mtpiResp.getData()).isNotNull();

        YearTotalPriceCategory ytpiReq = new YearTotalPriceCategory();
        ytpiReq.setCategoryId(1);
        ytpiReq.setYear(2026);
        ApiResponse<List<CategoriesYearlyTotalPriceResponse>> ytpiResp = categoryTotalPriceByIdService.findYearlyTotalPriceById(ytpiReq);
        assertThat(ytpiResp).isNotNull();
        assertThat(ytpiResp.getData()).isNotNull();
    }
}
