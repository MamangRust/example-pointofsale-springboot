package com.sanedge.pointofsale.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanedge.pointofsale.BaseIntegrationTest;
import com.sanedge.pointofsale.domain.requests.order.CreateOrderItemRequest;
import com.sanedge.pointofsale.domain.requests.order.CreateOrderRequest;
import com.sanedge.pointofsale.domain.requests.order.UpdateOrderItemRequest;
import com.sanedge.pointofsale.domain.requests.order.UpdateOrderRequest;
import com.sanedge.pointofsale.enums.Status;
import com.sanedge.pointofsale.models.order.OrderItem;
import com.sanedge.pointofsale.models.Product;
import com.sanedge.pointofsale.models.category.Category;
import com.sanedge.pointofsale.models.Merchant;
import com.sanedge.pointofsale.models.cashier.Cashier;
import com.sanedge.pointofsale.models.order.Order;
import com.sanedge.pointofsale.repository.category.CategoryCommandRepository;
import com.sanedge.pointofsale.repository.merchant.MerchantCommandRepository;
import com.sanedge.pointofsale.repository.order.OrderCommandRepository;
import com.sanedge.pointofsale.repository.OrderItemRepository;
import com.sanedge.pointofsale.repository.product.ProductCommandRepository;
import com.sanedge.pointofsale.repository.cashier.CashierCommandRepository;
import com.sanedge.pointofsale.security.JwtProvider;

public class OrderControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MerchantCommandRepository merchantCommandRepository;

    @Autowired
    private CashierCommandRepository cashierCommandRepository;

    @Autowired
    private CategoryCommandRepository categoryCommandRepository;

    @Autowired
    private ProductCommandRepository productCommandRepository;

    @Autowired
    private OrderCommandRepository orderCommandRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String authToken;

    @BeforeEach
    void setupAuth() {
        this.authToken = jwtProvider.generateAccessToken(adminUser.getUsername());
    }

    @Test
    void shouldPerformAllOrderEndpoints() throws Exception {
        Merchant merchant = new Merchant();
        merchant.setUserId(adminUser.getUserId());
        merchant.setName("FullTestMerchant");
        merchant.setDescription("Merchant for comprehensive order test");
        merchant.setAddress("Test Road");
        merchant.setContactEmail("fulltest@merchant.com");
        merchant.setContactPhone("1122334455");
        merchant.setStatus(Status.SUCCESS);
        merchant = merchantCommandRepository.save(merchant);

        Cashier cashier = new Cashier();
        cashier.setMerchantId(merchant.getMerchantId());
        cashier.setUserId(adminUser.getUserId());
        cashier.setName("FullTestCashier");
        cashier = cashierCommandRepository.save(cashier);

        Category category = new Category();
        category.setName("Full Category");
        category.setDescription("Category for comprehensive order test");
        category.setSlugCategory("full-category");
        category = categoryCommandRepository.save(category);

        Product product = new Product();
        product.setMerchantId(merchant.getMerchantId());
        product.setCategoryId(category.getCategoryId());
        product.setName("Full Product");
        product.setDescription("Product for full order test");
        product.setPrice(30000);
        product.setCountInStock(25);
        product.setBrand("Brand F");
        product.setWeight(200);
        product.setSlugProduct("full-product-test");
        product.setImageProduct("prod.jpg");
        product = productCommandRepository.save(product);

        entityManager.flush();
        entityManager.clear();

        // 1. Create order
        CreateOrderRequest req = new CreateOrderRequest();
        req.setMerchantId(merchant.getMerchantId().intValue());
        req.setCashierId(cashier.getCashierId().intValue());

        CreateOrderItemRequest itemReq = new CreateOrderItemRequest();
        itemReq.setProductId(product.getProductId().intValue());
        itemReq.setQuantity(2);
        itemReq.setPrice(30000);

        List<CreateOrderItemRequest> itemReqs = new ArrayList<>();
        itemReqs.add(itemReq);
        req.setItems(itemReqs);

        String createRespStr = mockMvc.perform(post("/api/order/create")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        assertThat(createRespStr).contains("success");
        
        List<Order> orders = orderCommandRepository.findAll();
        assertThat(orders).isNotEmpty();
        Integer orderId = orders.get(orders.size() - 1).getOrderId().intValue();

        // 2. Find All
        mockMvc.perform(get("/api/order")
                .header("Authorization", "Bearer " + authToken)
                .param("page", "1")
                .param("pageSize", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 3. Find By ID
        mockMvc.perform(get("/api/order/" + orderId)
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 4. Find Active
        mockMvc.perform(get("/api/order/active")
                .header("Authorization", "Bearer " + authToken)
                .param("page", "1")
                .param("pageSize", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 5. Find Trashed
        mockMvc.perform(get("/api/order/trashed")
                .header("Authorization", "Bearer " + authToken)
                .param("page", "1")
                .param("pageSize", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // We can safely query orderItemId via repositories
        List<OrderItem> savedItems = orderItemRepository.findOrderItemByOrder(orderId.longValue());
        assertThat(savedItems).isNotEmpty();
        Integer orderItemId = savedItems.get(0).getOrderItemId().intValue();

        // 6. Update order
        UpdateOrderRequest updateReq = new UpdateOrderRequest();
        updateReq.setOrderId(orderId);
        updateReq.setCashierId(cashier.getCashierId().intValue());

        UpdateOrderItemRequest updateItem = new UpdateOrderItemRequest();
        updateItem.setOrderItemId(orderItemId);
        updateItem.setProductId(product.getProductId().intValue());
        updateItem.setQuantity(4);
        updateItem.setPrice(30000);

        List<UpdateOrderItemRequest> updateItems = new ArrayList<>();
        updateItems.add(updateItem);
        updateReq.setItems(updateItems);

        mockMvc.perform(post("/api/order/update/" + orderId)
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 7. Trash order
        mockMvc.perform(post("/api/order/trashed/" + orderId)
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 8. Restore order
        mockMvc.perform(post("/api/order/restore/" + orderId)
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Trash it again for deletion tests
        mockMvc.perform(post("/api/order/trashed/" + orderId)
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 9. Permanent Delete
        mockMvc.perform(delete("/api/order/permanent/" + orderId)
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 10. Restore All
        mockMvc.perform(post("/api/order/restore/all")
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 11. Delete All (permanent)
        mockMvc.perform(post("/api/order/permanent/all")
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
