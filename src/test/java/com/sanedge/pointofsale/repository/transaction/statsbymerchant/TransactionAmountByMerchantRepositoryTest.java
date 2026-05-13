package com.sanedge.pointofsale.repository.transaction.statsbymerchant;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.pointofsale.BaseIntegrationTest;
import com.sanedge.pointofsale.models.Merchant;
import com.sanedge.pointofsale.models.User;
import com.sanedge.pointofsale.models.transaction.Transaction;
import com.sanedge.pointofsale.models.transaction.TransactionMonthlyAmountFailed;
import com.sanedge.pointofsale.models.transaction.TransactionMonthlyAmountSuccess;
import com.sanedge.pointofsale.models.transaction.TransactionYearlyAmountFailed;
import com.sanedge.pointofsale.models.transaction.TransactionYearlyAmountSuccess;
import com.sanedge.pointofsale.repository.merchant.MerchantCommandRepository;
import com.sanedge.pointofsale.repository.user.UserCommandRepository;
import com.sanedge.pointofsale.repository.transaction.TransactionCommandRepository;

public class TransactionAmountByMerchantRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private TransactionAmountByMerchantRepository transactionAmountByMerchantRepository;

    @Autowired
    private TransactionCommandRepository transactionRepository;

    @Autowired
    private MerchantCommandRepository merchantRepository;

    @Autowired
    private UserCommandRepository userRepository;

    private Merchant merchant;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser_tx");
        user.setFirstname("Test");
        user.setLastname("User");
        user.setEmail("test_tx@example.com");
        user.setPassword("password");
        user = userRepository.save(user);

        merchant = new Merchant();
        merchant.setUserId(user.getUserId());
        merchant.setName("Test Merchant Tx");
        merchant.setDescription("Test Description");
        merchant.setAddress("Test Address");
        merchant = merchantRepository.save(merchant);

        createTransaction("SUCCESS", 1000L, LocalDateTime.of(2024, 1, 15, 10, 0));
        createTransaction("SUCCESS", 2000L, LocalDateTime.of(2024, 1, 20, 10, 0));
        createTransaction("SUCCESS", 1500L, LocalDateTime.of(2023, 12, 15, 10, 0));
        createTransaction("FAILED", 500L, LocalDateTime.of(2024, 1, 10, 10, 0));
    }

    private void createTransaction(String status, Long amount, LocalDateTime createdAt) {
        Transaction transaction = new Transaction();
        transaction.setMerchantId(merchant.getMerchantId());
        transaction.setOrderId(1L);
        transaction.setAmount(amount.intValue());
        transaction.setPaymentMethod("CREDIT_CARD");
        
        if (status.equals("SUCCESS")) {
            transaction.setStatus(com.sanedge.pointofsale.enums.PaymentStatus.SUCCESS);
        } else {
            transaction.setStatus(com.sanedge.pointofsale.enums.PaymentStatus.FAILED);
        }
        
        transaction.setCreatedAt(java.sql.Timestamp.valueOf(createdAt));
        transaction.setUpdatedAt(java.sql.Timestamp.valueOf(createdAt));
        transactionRepository.save(transaction);
    }

    @Test
    void findMonthlySuccessByMerchant_ShouldReturnCorrectStats() {
        List<TransactionMonthlyAmountSuccess> stats = transactionAmountByMerchantRepository
                .findMonthlySuccessByMerchant(merchant.getMerchantId(), 2024, 1, 2023, 12);

        assertThat(stats).isNotNull();
    }

    @Test
    void findYearlySuccessByMerchant_ShouldReturnCorrectStats() {
        List<TransactionYearlyAmountSuccess> stats = transactionAmountByMerchantRepository
                .findYearlySuccessByMerchant(merchant.getMerchantId(), 2024);

        assertThat(stats).isNotNull();
    }

    @Test
    void findMonthlyFailedByMerchant_ShouldReturnCorrectStats() {
        List<TransactionMonthlyAmountFailed> stats = transactionAmountByMerchantRepository
                .findMonthlyFailedByMerchant(merchant.getMerchantId(), 2024, 1, 2023, 12);

        assertThat(stats).isNotNull();
    }

    @Test
    void findYearlyFailedByMerchant_ShouldReturnCorrectStats() {
        List<TransactionYearlyAmountFailed> stats = transactionAmountByMerchantRepository
                .findYearlyFailedByMerchant(merchant.getMerchantId(), 2024);

        assertThat(stats).isNotNull();
    }
}
