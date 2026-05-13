package com.sanedge.pointofsale.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.sanedge.pointofsale.BaseIntegrationTest;
import com.sanedge.pointofsale.domain.requests.transactions.CreateTransactionRequest;
import com.sanedge.pointofsale.domain.requests.transactions.FindAllTransactionRequest;
import com.sanedge.pointofsale.domain.requests.transactions.UpdateTransactionRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.api.ApiResponsePagination;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionResponse;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionResponseDeleteAt;
import com.sanedge.pointofsale.enums.PaymentStatus;
import com.sanedge.pointofsale.enums.Status;
import com.sanedge.pointofsale.models.order.OrderItem;
import com.sanedge.pointofsale.models.Merchant;
import com.sanedge.pointofsale.models.cashier.Cashier;
import com.sanedge.pointofsale.models.order.Order;
import com.sanedge.pointofsale.models.transaction.Transaction;
import com.sanedge.pointofsale.repository.merchant.MerchantCommandRepository;
import com.sanedge.pointofsale.repository.cashier.CashierCommandRepository;
import com.sanedge.pointofsale.repository.order.OrderCommandRepository;
import com.sanedge.pointofsale.repository.OrderItemRepository;
import com.sanedge.pointofsale.repository.transaction.TransactionCommandRepository;
import com.sanedge.pointofsale.service.transaction.TransactionCommandService;
import com.sanedge.pointofsale.service.transaction.TransactionQueryService;

import jakarta.validation.Validator;

public class TransactionServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TransactionQueryService queryService;

    @Autowired
    private TransactionCommandService commandService;

    @Autowired
    private MerchantCommandRepository merchantCommandRepository;

    @Autowired
    private CashierCommandRepository cashierCommandRepository;

    @Autowired
    private OrderCommandRepository orderCommandRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private TransactionCommandRepository transactionCommandRepository;

    @MockBean
    private Validator validator;

    @Test
    void testAllTransactionServiceMethods() {
        Merchant merchant = new Merchant();
        merchant.setUserId(adminUser.getUserId());
        merchant.setName("Tx Merchant");
        merchant.setDescription("Tx merchant description");
        merchant.setAddress("Some road");
        merchant.setContactEmail("tx@merchant.com");
        merchant.setContactPhone("11223344");
        merchant.setStatus(Status.SUCCESS);
        merchant = merchantCommandRepository.save(merchant);

        Cashier cashier = new Cashier();
        cashier.setMerchantId(merchant.getMerchantId());
        cashier.setUserId(adminUser.getUserId());
        cashier.setName("Tx Cashier");
        cashier = cashierCommandRepository.save(cashier);

        Order order = new Order();
        order.setCashierId(cashier.getCashierId());
        order.setMerchantId(merchant.getMerchantId());
        order.setTotalPrice(35000L);
        order = orderCommandRepository.save(order);

        OrderItem item = new OrderItem();
        item.setOrderId(order.getOrderId());
        item.setProductId(101L);
        item.setQuantity(2);
        item.setPrice(15000);
        item = orderItemRepository.save(item);

        Transaction tx = new Transaction();
        tx.setOrderId(order.getOrderId());
        tx.setMerchantId(merchant.getMerchantId());
        tx.setPaymentMethod("GOPAY");
        tx.setAmount(38500);
        tx.setStatus(PaymentStatus.PENDING);
        tx = transactionCommandRepository.save(tx);

        Long id = tx.getTransactionId();

        entityManager.flush();
        entityManager.clear();

        // 1. Create
        CreateTransactionRequest createReq = new CreateTransactionRequest();
        createReq.setOrderID(order.getOrderId().intValue());
        createReq.setMerchantID(merchant.getMerchantId().intValue());
        createReq.setPaymentMethod("OVO");
        createReq.setAmount(40000);
        createReq.setPaymentStatus("PENDING");

        ApiResponse<TransactionResponse> createResp = commandService.create(createReq);
        assertThat(createResp.getStatus()).isEqualTo("success");

        entityManager.flush();
        entityManager.clear();

        // 2. Find All
        FindAllTransactionRequest findReq = new FindAllTransactionRequest();
        findReq.setSearch("GOPAY");
        findReq.setPage(1);
        findReq.setPageSize(10);

        ApiResponsePagination<List<TransactionResponse>> allResp = queryService.findAllTransactions(findReq);
        assertThat(allResp.getStatus()).isEqualTo("success");

        // 3. Find By ID
        ApiResponse<TransactionResponse> byIdResp = queryService.findById(id.intValue());
        assertThat(byIdResp.getStatus()).isEqualTo("success");

        // 4. Update
        UpdateTransactionRequest updateReq = new UpdateTransactionRequest();
        updateReq.setTransactionID(id.intValue());
        updateReq.setOrderID(order.getOrderId().intValue());
        updateReq.setMerchantID(merchant.getMerchantId().intValue());
        updateReq.setPaymentMethod("CREDIT_CARD");
        updateReq.setAmount(45000);
        updateReq.setPaymentStatus("SUCCESS");

        ApiResponse<TransactionResponse> updateResp = commandService.update(updateReq);
        assertThat(updateResp.getStatus()).isEqualTo("success");

        entityManager.flush();
        entityManager.clear();

        // 5. Find By Active
        ApiResponsePagination<List<TransactionResponseDeleteAt>> activeResp = queryService.findByActive(findReq);
        assertThat(activeResp.getStatus()).isEqualTo("success");

        // 6. Trash
        ApiResponse<TransactionResponseDeleteAt> trashResp = commandService.trash(id.intValue());
        assertThat(trashResp.getStatus()).isEqualTo("success");

        entityManager.flush();
        entityManager.clear();

        // 7. Find By Trashed
        ApiResponsePagination<List<TransactionResponseDeleteAt>> trashedResp = queryService.findByTrashed(findReq);
        assertThat(trashedResp.getStatus()).isEqualTo("success");

        // 8. Restore
        ApiResponse<TransactionResponseDeleteAt> restoreResp = commandService.restore(id.intValue());
        assertThat(restoreResp.getStatus()).isEqualTo("success");

        entityManager.flush();
        entityManager.clear();

        // Trash again before delete permanent
        ApiResponse<TransactionResponseDeleteAt> trashAgainResp = commandService.trash(id.intValue());
        assertThat(trashAgainResp.getStatus()).isEqualTo("success");

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
