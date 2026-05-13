package com.sanedge.pointofsale.repository.category.statsbyid;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.pointofsale.BaseIntegrationTest;
import com.sanedge.pointofsale.models.order.OrderItem;
import com.sanedge.pointofsale.models.Product;
import com.sanedge.pointofsale.models.category.CategoryMonthPrice;
import com.sanedge.pointofsale.models.category.CategoryYearPrice;
import com.sanedge.pointofsale.models.category.Category;
import com.sanedge.pointofsale.models.cashier.Cashier;
import com.sanedge.pointofsale.models.order.Order;
import com.sanedge.pointofsale.repository.category.CategoryCommandRepository;
import com.sanedge.pointofsale.repository.cashier.CashierCommandRepository;
import com.sanedge.pointofsale.repository.order.OrderCommandRepository;
import com.sanedge.pointofsale.repository.OrderItemRepository;
import com.sanedge.pointofsale.repository.product.ProductCommandRepository;

public class CategoryPriceByIdRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private CategoryPriceByIdRepository categoryPriceByIdRepository;

    @Autowired
    private CategoryCommandRepository categoryCommandRepository;

    @Autowired
    private ProductCommandRepository productCommandRepository;

    @Autowired
    private CashierCommandRepository cashierCommandRepository;

    @Autowired
    private OrderCommandRepository orderCommandRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private Cashier testCashier;

    @BeforeEach
    void setupCashier() {
        testCashier = new Cashier();
        testCashier.setMerchantId(adminMerchant.getMerchantId());
        testCashier.setUserId(adminUser.getUserId());
        testCashier.setName("RepoTestCashier");
        testCashier = cashierCommandRepository.save(testCashier);
    }

    @Test
    void shouldFindMonthlyCategoryStatsById() {
        // Setup data
        Category category = new Category();
        category.setName("Electronics Stats Monthly");
        category.setSlugCategory("electronics-monthly");
        category = categoryCommandRepository.save(category);

        Product product = new Product();
        product.setName("Smartphone Stats");
        product.setPrice(1000);
        product.setCategoryId(category.getCategoryId());
        product.setMerchantId(adminMerchant.getMerchantId());
        product.setCountInStock(10);
        product = productCommandRepository.save(product);

        // Order in 2024-01-15
        Order order1 = new Order();
        order1.setCashierId(testCashier.getCashierId());
        order1.setMerchantId(adminMerchant.getMerchantId());
        order1.setTotalPrice(2000L);
        order1.setCreatedAt(java.sql.Timestamp.valueOf(LocalDateTime.of(2024, 1, 15, 10, 0)));
        order1 = orderCommandRepository.save(order1);

        OrderItem item1 = new OrderItem();
        item1.setOrderId(order1.getOrderId());
        item1.setProductId(product.getProductId());
        item1.setQuantity(2);
        item1.setPrice(1000);
        item1.setCreatedAt(order1.getCreatedAt());
        orderItemRepository.save(item1);

        // Order in 2024-02-20
        Order order2 = new Order();
        order2.setCashierId(testCashier.getCashierId());
        order2.setMerchantId(adminMerchant.getMerchantId());
        order2.setTotalPrice(3000L);
        order2.setCreatedAt(java.sql.Timestamp.valueOf(LocalDateTime.of(2024, 2, 20, 15, 0)));
        order2 = orderCommandRepository.save(order2);

        OrderItem item2 = new OrderItem();
        item2.setOrderId(order2.getOrderId());
        item2.setProductId(product.getProductId());
        item2.setQuantity(3);
        item2.setPrice(1000);
        item2.setCreatedAt(order2.getCreatedAt());
        orderItemRepository.save(item2);

        entityManager.flush();
        entityManager.clear();

        // Run query
        List<CategoryMonthPrice> results = categoryPriceByIdRepository.findMonthlyCategoryPriceById(
                category.getCategoryId(), 2024);

        // Verification
        assertThat(results).hasSize(2);
        
        CategoryMonthPrice janStats = results.stream()
                .filter(s -> s.getMonth().equalsIgnoreCase("Jan"))
                .findFirst().orElseThrow();
        
        assertThat(janStats.getCategoryName()).isEqualTo("Electronics Stats Monthly");
        assertThat(janStats.getOrderCount()).isEqualTo(1);
        assertThat(janStats.getItemsSold()).isEqualTo(2);
        assertThat(janStats.getTotalRevenue()).isEqualTo(2000L);

        CategoryMonthPrice febStats = results.stream()
                .filter(s -> s.getMonth().equalsIgnoreCase("Feb"))
                .findFirst().orElseThrow();
        
        assertThat(febStats.getOrderCount()).isEqualTo(1);
        assertThat(febStats.getItemsSold()).isEqualTo(3);
        assertThat(febStats.getTotalRevenue()).isEqualTo(3000L);
    }

    @Test
    void shouldFindYearlyCategoryStatsById() {
        // Setup data
        Category category = new Category();
        category.setName("Books Stats Yearly");
        category.setSlugCategory("books-yearly");
        category = categoryCommandRepository.save(category);

        Product product = new Product();
        product.setName("Java Guide Stats");
        product.setPrice(500);
        product.setCategoryId(category.getCategoryId());
        product.setMerchantId(adminMerchant.getMerchantId());
        product.setCountInStock(50);
        product = productCommandRepository.save(product);

        // Order in 2023
        Order order2023 = new Order();
        order2023.setCashierId(testCashier.getCashierId());
        order2023.setMerchantId(adminMerchant.getMerchantId());
        order2023.setTotalPrice(1000L);
        order2023.setCreatedAt(java.sql.Timestamp.valueOf(LocalDateTime.of(2023, 6, 1, 10, 0)));
        order2023 = orderCommandRepository.save(order2023);

        OrderItem item2023 = new OrderItem();
        item2023.setOrderId(order2023.getOrderId());
        item2023.setProductId(product.getProductId());
        item2023.setQuantity(2);
        item2023.setPrice(500);
        item2023.setCreatedAt(order2023.getCreatedAt());
        orderItemRepository.save(item2023);

        // Order in 2024
        Order order2024 = new Order();
        order2024.setCashierId(testCashier.getCashierId());
        order2024.setMerchantId(adminMerchant.getMerchantId());
        order2024.setTotalPrice(1500L);
        order2024.setCreatedAt(java.sql.Timestamp.valueOf(LocalDateTime.of(2024, 8, 15, 12, 0)));
        order2024 = orderCommandRepository.save(order2024);

        OrderItem item2024 = new OrderItem();
        item2024.setOrderId(order2024.getOrderId());
        item2024.setProductId(product.getProductId());
        item2024.setQuantity(3);
        item2024.setPrice(500);
        item2024.setCreatedAt(order2024.getCreatedAt());
        orderItemRepository.save(item2024);

        entityManager.flush();
        entityManager.clear();

        // Run query for 2024 (should include 2020-2024)
        List<CategoryYearPrice> results = categoryPriceByIdRepository.findYearlyCategoryPriceById(
                category.getCategoryId(), 2024);

        // Verification
        assertThat(results).hasSize(2);
        
        CategoryYearPrice stats2023 = results.stream()
                .filter(s -> s.getYear().equals("2023"))
                .findFirst().orElseThrow();
        assertThat(stats2023.getOrderCount()).isEqualTo(1);
        assertThat(stats2023.getTotalRevenue()).isEqualTo(1000L);

        CategoryYearPrice stats2024 = results.stream()
                .filter(s -> s.getYear().equals("2024"))
                .findFirst().orElseThrow();
        assertThat(stats2024.getOrderCount()).isEqualTo(1);
        assertThat(stats2024.getTotalRevenue()).isEqualTo(1500L);
    }
}
