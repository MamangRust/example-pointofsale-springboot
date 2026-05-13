package com.sanedge.pointofsale.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.sanedge.pointofsale.BaseIntegrationTest;
import com.sanedge.pointofsale.domain.requests.order.CreateOrderItemRequest;
import com.sanedge.pointofsale.domain.requests.order.CreateOrderRequest;
import com.sanedge.pointofsale.domain.requests.order.FindAllOrderRequest;
import com.sanedge.pointofsale.domain.requests.order.UpdateOrderItemRequest;
import com.sanedge.pointofsale.domain.requests.order.UpdateOrderRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.api.ApiResponsePagination;
import com.sanedge.pointofsale.domain.responses.order.OrderResponse;
import com.sanedge.pointofsale.domain.responses.order.OrderResponseDeleteAt;
import com.sanedge.pointofsale.enums.Status;
import com.sanedge.pointofsale.models.Product;
import com.sanedge.pointofsale.models.category.Category;
import com.sanedge.pointofsale.models.Merchant;
import com.sanedge.pointofsale.models.cashier.Cashier;
import com.sanedge.pointofsale.repository.category.CategoryCommandRepository;
import com.sanedge.pointofsale.repository.merchant.MerchantCommandRepository;
import com.sanedge.pointofsale.repository.cashier.CashierCommandRepository;
import com.sanedge.pointofsale.repository.product.ProductCommandRepository;
import com.sanedge.pointofsale.service.order.OrderCommandService;
import com.sanedge.pointofsale.service.order.OrderQueryService;

import jakarta.validation.Validator;

public class OrderServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private OrderCommandService commandService;

    @Autowired
    private OrderQueryService queryService;

    @Autowired
    private MerchantCommandRepository merchantCommandRepository;

    @Autowired
    private CashierCommandRepository cashierCommandRepository;

    @Autowired
    private CategoryCommandRepository categoryCommandRepository;

    @Autowired
    private ProductCommandRepository productCommandRepository;

    @MockBean
    private Validator validator;

    @Test
    void testAllOrderServiceMethods() {
        Merchant merchant = new Merchant();
        merchant.setUserId(adminUser.getUserId());
        merchant.setName("OrderMerchant");
        merchant.setDescription("Merchant for order test");
        merchant.setAddress("Test Address");
        merchant.setContactEmail("ordertest@merchant.com");
        merchant.setContactPhone("0812345678");
        merchant.setStatus(Status.SUCCESS);
        merchant = merchantCommandRepository.save(merchant);

        Cashier cashier = new Cashier();
        cashier.setMerchantId(merchant.getMerchantId());
        cashier.setUserId(adminUser.getUserId());
        cashier.setName("OrderCashier");
        cashier = cashierCommandRepository.save(cashier);

        Category category = new Category();
        category.setName("Order Category");
        category.setDescription("Test category for order");
        category.setSlugCategory("order-category");
        category = categoryCommandRepository.save(category);

        Product product = new Product();
        product.setMerchantId(merchant.getMerchantId());
        product.setCategoryId(category.getCategoryId());
        product.setName("Order Product");
        product.setDescription("Product for order");
        product.setPrice(15000);
        product.setCountInStock(10);
        product.setBrand("Brand A");
        product.setWeight(100);
        product.setSlugProduct("order-product-test");
        product.setImageProduct("product.jpg");
        product = productCommandRepository.save(product);

        entityManager.flush();
        entityManager.clear();

        // 1. Create
        CreateOrderRequest req = new CreateOrderRequest();
        req.setMerchantId(merchant.getMerchantId().intValue());
        req.setCashierId(cashier.getCashierId().intValue());

        CreateOrderItemRequest item = new CreateOrderItemRequest();
        item.setProductId(product.getProductId().intValue());
        item.setQuantity(2);
        item.setPrice(15000);

        List<CreateOrderItemRequest> items = new ArrayList<>();
        items.add(item);
        req.setItems(items);

        ApiResponse<OrderResponse> createResp = commandService.create(req);
        assertThat(createResp.getStatus()).isEqualTo("success");
        Long id = createResp.getData().getId();

        entityManager.flush();
        entityManager.clear();

        // 2. Find All
        FindAllOrderRequest findReq = new FindAllOrderRequest();
        findReq.setSearch(null);
        findReq.setPage(1);
        findReq.setPageSize(10);

        ApiResponsePagination<List<OrderResponse>> listResp = queryService.findAll(findReq);
        assertThat(listResp.getStatus()).isEqualTo("success");

        // 3. Find By ID
        ApiResponse<OrderResponse> idResp = queryService.findById(id.intValue());
        assertThat(idResp.getStatus()).isEqualTo("success");

        // 4. Update
        UpdateOrderRequest updateReq = new UpdateOrderRequest();
        updateReq.setOrderId(id.intValue());
        updateReq.setCashierId(cashier.getCashierId().intValue());

        UpdateOrderItemRequest upItem = new UpdateOrderItemRequest();
        upItem.setProductId(product.getProductId().intValue());
        upItem.setQuantity(3);
        upItem.setPrice(15000);
        updateReq.setItems(List.of(upItem));

        ApiResponse<OrderResponse> updateResp = commandService.update(updateReq);
        assertThat(updateResp.getStatus()).isEqualTo("success");

        entityManager.flush();
        entityManager.clear();

        // 5. Find By Active
        ApiResponsePagination<List<OrderResponseDeleteAt>> activeResp = queryService.findByActive(findReq);
        assertThat(activeResp.getStatus()).isEqualTo("success");

        // 6. Trash
        ApiResponse<OrderResponseDeleteAt> trashResp = commandService.trash(id.intValue());
        assertThat(trashResp.getStatus()).isEqualTo("success");

        entityManager.flush();
        entityManager.clear();

        // 7. Find By Trashed
        ApiResponsePagination<List<OrderResponseDeleteAt>> trashedResp = queryService.findByTrashed(findReq);
        assertThat(trashedResp.getStatus()).isEqualTo("success");

        // 8. Restore
        ApiResponse<OrderResponseDeleteAt> restoreResp = commandService.restore(id.intValue());
        assertThat(restoreResp.getStatus()).isEqualTo("success");

        entityManager.flush();
        entityManager.clear();

        // Trash again before delete permanent
        ApiResponse<OrderResponseDeleteAt> trashAgainResp = commandService.trash(id.intValue());
        assertThat(trashAgainResp.getStatus()).isEqualTo("success");

        entityManager.flush();
        entityManager.clear();

        // Delete related dependent tables to avoid foreign key violations
        entityManager.createNativeQuery("DELETE FROM order_items WHERE order_id = :orderId").setParameter("orderId", id.intValue()).executeUpdate();

        entityManager.flush();
        entityManager.clear();

        // 9. Delete Permanent
        ApiResponse<Boolean> delPermResp = commandService.delete(id.intValue());
        assertThat(delPermResp.getStatus()).isEqualTo("success");

        // 10. Restore All
        ApiResponse<Boolean> restoreAllResp = commandService.restoreAll();
        assertThat(restoreAllResp.getStatus()).isEqualTo("success");

        // 11. Delete All Permanent
        ApiResponse<Boolean> delAllResp = commandService.deleteAll();
        assertThat(delAllResp.getStatus()).isEqualTo("success");
    }
}
