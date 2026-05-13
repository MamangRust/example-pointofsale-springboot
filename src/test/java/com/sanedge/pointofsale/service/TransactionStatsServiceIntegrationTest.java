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
import com.sanedge.pointofsale.domain.requests.transactions.MonthAmountTransactionMerchant;
import com.sanedge.pointofsale.domain.requests.transactions.MonthAmountTransactionRequest;
import com.sanedge.pointofsale.domain.requests.transactions.MonthMethodTransactionMerchantRequest;
import com.sanedge.pointofsale.domain.requests.transactions.MonthMethodTransactionRequest;
import com.sanedge.pointofsale.domain.requests.transactions.YearAmountTransactionMerchant;
import com.sanedge.pointofsale.domain.requests.transactions.YearMethodTransactionMerchantRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionMonthlyAmountFailedResponse;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionMonthlyAmountSuccessResponse;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionMonthlyMethodResponse;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionYearlyAmountFailedResponse;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionYearlyAmountSuccessResponse;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionYearlyMethodResponse;
import com.sanedge.pointofsale.repository.transaction.stats.TransactionAmountStatusRepository;
import com.sanedge.pointofsale.repository.transaction.stats.TransactionMethodRepository;
import com.sanedge.pointofsale.repository.transaction.statsbymerchant.TransactionAmountByMerchantRepository;
import com.sanedge.pointofsale.repository.transaction.statsbymerchant.TransactionMethodByMerchantRepository;
import com.sanedge.pointofsale.service.transaction.stats.TransactionAmountService;
import com.sanedge.pointofsale.service.transaction.stats.TransactionMethodService;
import com.sanedge.pointofsale.service.transaction.statsbymerchant.TransactionAmountByMerchantService;
import com.sanedge.pointofsale.service.transaction.statsbymerchant.TransactionMethodByMerchantService;

public class TransactionStatsServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TransactionAmountService transactionAmountService;

    @Autowired
    private TransactionMethodService transactionMethodService;

    @Autowired
    private TransactionAmountByMerchantService transactionAmountByMerchantService;

    @Autowired
    private TransactionMethodByMerchantService transactionMethodByMerchantService;

    @MockitoBean
    private TransactionAmountStatusRepository transactionAmountStatusRepository;

    @MockitoBean
    private TransactionMethodRepository transactionMethodRepository;

    @MockitoBean
    private TransactionAmountByMerchantRepository transactionAmountByMerchantRepository;

    @MockitoBean
    private TransactionMethodByMerchantRepository transactionMethodByMerchantRepository;

    @BeforeEach
    void setupMocks() {
        when(transactionAmountStatusRepository.findMonthlyTransactionSuccess(any(), any(), any(), any())).thenReturn(List.of());
        when(transactionAmountStatusRepository.findYearlyTransactionSuccess(any())).thenReturn(List.of());
        when(transactionAmountStatusRepository.findMonthlyTransactionFailed(any(), any(), any(), any())).thenReturn(List.of());
        when(transactionAmountStatusRepository.findYearlyTransactionFailed(any())).thenReturn(List.of());

        when(transactionMethodRepository.findMonthlyMethodsSuccess(any(), any(), any(), any())).thenReturn(List.of());
        when(transactionMethodRepository.findYearlyMethodsSuccess(any())).thenReturn(List.of());
        when(transactionMethodRepository.findMonthlyMethodsFailed(any(), any(), any(), any())).thenReturn(List.of());
        when(transactionMethodRepository.findYearlyMethodsFailed(any())).thenReturn(List.of());

        when(transactionAmountByMerchantRepository.findMonthlySuccessByMerchant(any(), any(), any(), any(), any())).thenReturn(List.of());
        when(transactionAmountByMerchantRepository.findYearlySuccessByMerchant(any(), any())).thenReturn(List.of());
        when(transactionAmountByMerchantRepository.findMonthlyFailedByMerchant(any(), any(), any(), any(), any())).thenReturn(List.of());
        when(transactionAmountByMerchantRepository.findYearlyFailedByMerchant(any(), any())).thenReturn(List.of());

        when(transactionMethodByMerchantRepository.findMonthlyTransactionMethodsSuccessByMerchant(any(), any(), any(), any(), any())).thenReturn(List.of());
        when(transactionMethodByMerchantRepository.findMonthlyTransactionMethodsFailedByMerchant(any(), any(), any(), any(), any())).thenReturn(List.of());
        when(transactionMethodByMerchantRepository.findYearlyTransactionMethodsSuccessByMerchant(any(), any())).thenReturn(List.of());
        when(transactionMethodByMerchantRepository.findYearlyTransactionMethodsFailedByMerchant(any(), any())).thenReturn(List.of());
    }

    @Test
    void testAllTransactionStatsServices() {
        MonthAmountTransactionRequest maReq = new MonthAmountTransactionRequest();
        maReq.setMonth(5);
        maReq.setYear(2026);
        ApiResponse<List<TransactionMonthlyAmountSuccessResponse>> mAmountSuccessResp = transactionAmountService.findMonthlyAmountSuccess(maReq);
        assertThat(mAmountSuccessResp).isNotNull();
        assertThat(mAmountSuccessResp.getData()).isNotNull();

        ApiResponse<List<TransactionYearlyAmountSuccessResponse>> yAmountSuccessResp = transactionAmountService.findYearlyAmountSuccess(2026);
        assertThat(yAmountSuccessResp).isNotNull();
        assertThat(yAmountSuccessResp.getData()).isNotNull();

        ApiResponse<List<TransactionMonthlyAmountFailedResponse>> mAmountFailedResp = transactionAmountService.findMonthlyAmountFailed(maReq);
        assertThat(mAmountFailedResp).isNotNull();
        assertThat(mAmountFailedResp.getData()).isNotNull();

        ApiResponse<List<TransactionYearlyAmountFailedResponse>> yAmountFailedResp = transactionAmountService.findYearlyAmountFailed(2026);
        assertThat(yAmountFailedResp).isNotNull();
        assertThat(yAmountFailedResp.getData()).isNotNull();

        MonthMethodTransactionRequest mmReq = new MonthMethodTransactionRequest();
        mmReq.setMonth(5);
        mmReq.setYear(2026);
        ApiResponse<List<TransactionMonthlyMethodResponse>> mMethodSuccessResp = transactionMethodService.findMonthlyMethodSuccess(mmReq);
        assertThat(mMethodSuccessResp).isNotNull();
        assertThat(mMethodSuccessResp.getData()).isNotNull();

        ApiResponse<List<TransactionYearlyMethodResponse>> yMethodSuccessResp = transactionMethodService.findYearlyMethodSuccess(2026);
        assertThat(yMethodSuccessResp).isNotNull();
        assertThat(yMethodSuccessResp.getData()).isNotNull();

        ApiResponse<List<TransactionMonthlyMethodResponse>> mMethodFailedResp = transactionMethodService.findMonthlyMethodFailed(mmReq);
        assertThat(mMethodFailedResp).isNotNull();
        assertThat(mMethodFailedResp.getData()).isNotNull();

        ApiResponse<List<TransactionYearlyMethodResponse>> yMethodFailedResp = transactionMethodService.findYearlyMethodFailed(2026);
        assertThat(yMethodFailedResp).isNotNull();
        assertThat(yMethodFailedResp.getData()).isNotNull();

        MonthAmountTransactionMerchant mamReq = new MonthAmountTransactionMerchant();
        mamReq.setMerchantId(1);
        mamReq.setYear(2026);
        ApiResponse<List<TransactionMonthlyAmountSuccessResponse>> mamResp = transactionAmountByMerchantService.findMonthlyAmountSuccessByMerchant(mamReq);
        assertThat(mamResp).isNotNull();
        assertThat(mamResp.getData()).isNotNull();

        YearAmountTransactionMerchant yamReq = new YearAmountTransactionMerchant();
        yamReq.setMerchantId(1);
        yamReq.setYear(2026);
        ApiResponse<List<TransactionYearlyAmountSuccessResponse>> yamResp = transactionAmountByMerchantService.findYearlyAmountSuccessByMerchant(yamReq);
        assertThat(yamResp).isNotNull();
        assertThat(yamResp.getData()).isNotNull();

        ApiResponse<List<TransactionMonthlyAmountFailedResponse>> mafmResp = transactionAmountByMerchantService.findMonthlyAmountFailedByMerchant(mamReq);
        assertThat(mafmResp).isNotNull();
        assertThat(mafmResp.getData()).isNotNull();

        ApiResponse<List<TransactionYearlyAmountFailedResponse>> yafmResp = transactionAmountByMerchantService.findYearlyAmountFailedByMerchant(yamReq);
        assertThat(yafmResp).isNotNull();
        assertThat(yafmResp.getData()).isNotNull();

        MonthMethodTransactionMerchantRequest mmReq2 = new MonthMethodTransactionMerchantRequest();
        mmReq2.setMerchantId(1);
        mmReq2.setYear(2026);
        ApiResponse<List<TransactionMonthlyMethodResponse>> mmmSuccessResp = transactionMethodByMerchantService.findMonthlyMethodByMerchantSuccess(mmReq2);
        assertThat(mmmSuccessResp).isNotNull();
        assertThat(mmmSuccessResp.getData()).isNotNull();

        ApiResponse<List<TransactionMonthlyMethodResponse>> mmmFailedResp = transactionMethodByMerchantService.findMonthlyMethodByMerchantFailed(mmReq2);
        assertThat(mmmFailedResp).isNotNull();
        assertThat(mmmFailedResp.getData()).isNotNull();

        YearMethodTransactionMerchantRequest ymReq2 = new YearMethodTransactionMerchantRequest();
        ymReq2.setMerchantId(1);
        ymReq2.setYear(2026);
        ApiResponse<List<TransactionYearlyMethodResponse>> ymmSuccessResp = transactionMethodByMerchantService.findYearlyMethodByMerchantSuccess(ymReq2);
        assertThat(ymmSuccessResp).isNotNull();
        assertThat(ymmSuccessResp.getData()).isNotNull();

        ApiResponse<List<TransactionYearlyMethodResponse>> ymmFailedResp = transactionMethodByMerchantService.findYearlyMethodByMerchantFailed(ymReq2);
        assertThat(ymmFailedResp).isNotNull();
        assertThat(ymmFailedResp.getData()).isNotNull();
    }
}
