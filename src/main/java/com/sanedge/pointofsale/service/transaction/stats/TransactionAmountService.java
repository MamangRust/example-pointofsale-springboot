package com.sanedge.pointofsale.service.transaction.stats;

import java.util.List;

import com.sanedge.pointofsale.domain.requests.transactions.MonthAmountTransactionRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionMonthlyAmountFailedResponse;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionMonthlyAmountSuccessResponse;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionYearlyAmountFailedResponse;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionYearlyAmountSuccessResponse;

public interface TransactionAmountService {
    ApiResponse<List<TransactionMonthlyAmountSuccessResponse>> findMonthlyAmountSuccess(
            MonthAmountTransactionRequest req);

    ApiResponse<List<TransactionYearlyAmountSuccessResponse>> findYearlyAmountSuccess(Integer year);

    ApiResponse<List<TransactionMonthlyAmountFailedResponse>> findMonthlyAmountFailed(
            MonthAmountTransactionRequest req);

    ApiResponse<List<TransactionYearlyAmountFailedResponse>> findYearlyAmountFailed(Integer year);
}