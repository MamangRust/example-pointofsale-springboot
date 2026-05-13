package com.sanedge.pointofsale.repository.category.statsbymerchant;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.pointofsale.BaseIntegrationTest;
import com.sanedge.pointofsale.models.category.CategoryMonthTotalPrice;
import com.sanedge.pointofsale.models.category.CategoryYearTotalPrice;

public class CategoryTotalPriceByMerchantRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private CategoryTotalPriceByMerchantRepository categoryTotalPriceByMerchantRepository;

    @Test
    void findMonthlyTotalPriceByMerchant_ShouldReturnCorrectStats() {
        List<CategoryMonthTotalPrice> stats = categoryTotalPriceByMerchantRepository.findMonthlyTotalPriceByMerchant(
                1L, 2024, 1, 2024, 2);

        assertThat(stats).isNotNull();
    }

    @Test
    void findYearlyTotalPriceByMerchant_ShouldReturnCorrectStats() {
        List<CategoryYearTotalPrice> stats = categoryTotalPriceByMerchantRepository.findYearlyTotalPriceByMerchant(
                1L, 2024, 2023);

        assertThat(stats).isNotNull();
    }
}
