package com.sanedge.pointofsale.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanedge.pointofsale.BaseIntegrationTest;
import com.sanedge.pointofsale.domain.requests.transactions.CreateTransactionRequest;
import com.sanedge.pointofsale.domain.requests.transactions.UpdateTransactionRequest;
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
import com.sanedge.pointofsale.security.JwtProvider;

public class TransactionControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

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

    @Autowired
    private ObjectMapper objectMapper;

    private String authToken;

    @BeforeEach
    void setupAuth() {
        this.authToken = jwtProvider.generateAccessToken(adminUser.getUsername());
    }

    @Test
    void shouldPerformAllTransactionEndpoints() throws Exception {
        Merchant merchant = new Merchant();
        merchant.setUserId(adminUser.getUserId());
        merchant.setName("ControllerTxMerchant");
        merchant.setDescription("Merchant for controller tx test");
        merchant.setAddress("Test Address");
        merchant.setContactEmail("controller@tx-merchant.com");
        merchant.setContactPhone("0812345678");
        merchant.setStatus(Status.SUCCESS);
        merchant = merchantCommandRepository.save(merchant);

        Cashier cashier = new Cashier();
        cashier.setMerchantId(merchant.getMerchantId());
        cashier.setUserId(adminUser.getUserId());
        cashier.setName("ControllerTxCashier");
        cashier = cashierCommandRepository.save(cashier);

        Order order = new Order();
        order.setCashierId(cashier.getCashierId());
        order.setMerchantId(merchant.getMerchantId());
        order.setTotalPrice(25000L);
        order = orderCommandRepository.save(order);

        OrderItem item = new OrderItem();
        item.setOrderId(order.getOrderId());
        item.setProductId(1234L);
        item.setQuantity(2);
        item.setPrice(10000);
        item = orderItemRepository.save(item);

        entityManager.flush();
        entityManager.clear();

        // 1. Create Transaction
        CreateTransactionRequest req = new CreateTransactionRequest();
        req.setOrderID(order.getOrderId().intValue());
        req.setMerchantID(merchant.getMerchantId().intValue());
        req.setPaymentMethod("OVO");
        req.setAmount(30000);
        req.setPaymentStatus("pending");

        String createRespStr = mockMvc.perform(post("/api/transaction/create")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        assertThat(createRespStr).contains("success");

        List<Transaction> transactions = transactionCommandRepository.findAll();
        assertThat(transactions).isNotEmpty();
        Integer txId = transactions.get(transactions.size() - 1).getTransactionId().intValue();

        // 2. Find All Transactions
        mockMvc.perform(get("/api/transaction")
                .header("Authorization", "Bearer " + authToken)
                .param("search", "OVO")
                .param("page", "1")
                .param("pageSize", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 3. Find Transaction By ID
        mockMvc.perform(get("/api/transaction/" + txId)
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 4. Find Active Transactions
        mockMvc.perform(get("/api/transaction/active")
                .header("Authorization", "Bearer " + authToken)
                .param("page", "1")
                .param("pageSize", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 5. Find Trashed Transactions
        mockMvc.perform(get("/api/transaction/trashed")
                .header("Authorization", "Bearer " + authToken)
                .param("page", "1")
                .param("pageSize", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 6. Update Transaction
        UpdateTransactionRequest updateReq = new UpdateTransactionRequest();
        updateReq.setOrderID(order.getOrderId().intValue());
        updateReq.setMerchantID(merchant.getMerchantId().intValue());
        updateReq.setPaymentMethod("GOPAY");
        updateReq.setAmount(35000);
        updateReq.setPaymentStatus("pending");

        mockMvc.perform(post("/api/transaction/update/" + txId)
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 7. Trash Transaction
        mockMvc.perform(post("/api/transaction/trashed/" + txId)
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 8. Restore Transaction
        mockMvc.perform(post("/api/transaction/restore/" + txId)
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Trash it again for deletion tests
        mockMvc.perform(post("/api/transaction/trashed/" + txId)
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 9. Permanent Delete
        mockMvc.perform(delete("/api/transaction/permanent/" + txId)
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 10. Restore All
        mockMvc.perform(post("/api/transaction/restore/all")
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 11. Delete All (permanent)
        mockMvc.perform(post("/api/transaction/permanent/all")
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
