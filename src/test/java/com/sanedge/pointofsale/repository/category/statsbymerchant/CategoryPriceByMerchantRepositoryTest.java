package com.sanedge.pointofsale.repository.category.statsbymerchant;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.pointofsale.BaseIntegrationTest;
import com.sanedge.pointofsale.models.category.CategoryMonthPrice;
import com.sanedge.pointofsale.models.category.CategoryYearPrice;
import com.sanedge.pointofsale.models.category.Category;
import com.sanedge.pointofsale.models.Merchant;
import com.sanedge.pointofsale.models.cashier.Cashier;
import com.sanedge.pointofsale.models.order.Order;
import com.sanedge.pointofsale.models.order.OrderItem;
import com.sanedge.pointofsale.models.Product;
import com.sanedge.pointofsale.models.User;
import com.sanedge.pointofsale.repository.category.CategoryCommandRepository;
import com.sanedge.pointofsale.repository.merchant.MerchantCommandRepository;
import com.sanedge.pointofsale.repository.cashier.CashierCommandRepository;
import com.sanedge.pointofsale.repository.order.OrderCommandRepository;
import com.sanedge.pointofsale.repository.OrderItemRepository;
import com.sanedge.pointofsale.repository.product.ProductCommandRepository;
import com.sanedge.pointofsale.repository.user.UserCommandRepository;

public class CategoryPriceByMerchantRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private CategoryPriceByMerchantRepository categoryPriceByMerchantRepository;

    @Autowired
    private CategoryCommandRepository categoryRepository;

    @Autowired
    private ProductCommandRepository productRepository;

    @Autowired
    private OrderCommandRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private MerchantCommandRepository merchantRepository;

    @Autowired
    private CashierCommandRepository cashierRepository;

    @Autowired
    private UserCommandRepository userRepository;

    private Merchant merchant;
    private Category category;
    private User user;
    private Cashier cashier;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser_category");
        user.setFirstname("Test");
        user.setLastname("User");
        user.setEmail("test_category@example.com");
        user.setPassword("password");
        user = userRepository.save(user);

        merchant = new Merchant();
        merchant.setUserId(user.getUserId());
        merchant.setName("Test Merchant");
        merchant.setDescription("Test Description");
        merchant.setAddress("Test Address");
        merchant = merchantRepository.save(merchant);

        cashier = new Cashier();
        cashier.setMerchantId(merchant.getMerchantId());
        cashier.setUserId(user.getUserId());
        cashier.setName("Test Cashier");
        cashier = cashierRepository.save(cashier);

        category = new Category();
        category.setName("Test Category");
        category.setSlugCategory("test-category");
        category.setDescription("Test Category Description");
        category = categoryRepository.save(category);

        Product product = new Product();
        product.setName("Test Product");
        product.setSlugProduct("test-product");
        product.setPrice(100);
        product.setCategoryId(category.getCategoryId());
        product.setMerchantId(merchant.getMerchantId());
        product.setCountInStock(10);
        product = productRepository.save(product);

        Order order = new Order();
        order.setCashierId(cashier.getCashierId());
        order.setMerchantId(merchant.getMerchantId());
        order.setTotalPrice(100L);
        order.setCreatedAt(java.sql.Timestamp.valueOf(LocalDateTime.of(2024, 1, 15, 12, 0)));
        order.setUpdatedAt(java.sql.Timestamp.valueOf(LocalDateTime.of(2024, 1, 15, 12, 0)));
        order = orderRepository.save(order);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(order.getOrderId());
        orderItem.setProductId(product.getProductId());
        orderItem.setQuantity(1);
        orderItem.setPrice(100);
        orderItemRepository.save(orderItem);
    }

    @Test
    void findMonthlyCategoryStatsByMerchant_ShouldReturnCorrectStats() {
        List<CategoryMonthPrice> stats = categoryPriceByMerchantRepository.findMonthlyCategoryPriceByMerchant(
                merchant.getMerchantId(), 2024);

        assertThat(stats).isNotEmpty();
        assertThat(stats.get(0).getMonth()).isEqualTo("Jan");
        assertThat(stats.get(0).getCategoryName()).isEqualTo("Test Category");
        assertThat(stats.get(0).getTotalRevenue()).isEqualTo(100L);
    }

    @Test
    void findYearlyCategoryStatsByMerchant_ShouldReturnCorrectStats() {
        List<CategoryYearPrice> stats = categoryPriceByMerchantRepository.findYearlyCategoryPriceByMerchant(
                merchant.getMerchantId(), 2024);

        assertThat(stats).isNotEmpty();
        assertThat(stats.get(0).getYear()).isEqualTo("2024");
        assertThat(stats.get(0).getCategoryName()).isEqualTo("Test Category");
        assertThat(stats.get(0).getTotalRevenue()).isEqualTo(100L);
    }
}
