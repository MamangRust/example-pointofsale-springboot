package com.sanedge.pointofsale.service.transaction.stats;

import java.util.List;

import com.sanedge.pointofsale.domain.requests.transactions.MonthMethodTransactionRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionMonthlyMethodResponse;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionYearlyMethodResponse;

public interface TransactionMethodService {
    ApiResponse<List<TransactionMonthlyMethodResponse>> findMonthlyMethodSuccess(MonthMethodTransactionRequest req);

    ApiResponse<List<TransactionYearlyMethodResponse>> findYearlyMethodSuccess(Integer year);

    ApiResponse<List<TransactionMonthlyMethodResponse>> findMonthlyMethodFailed(MonthMethodTransactionRequest req);

    ApiResponse<List<TransactionYearlyMethodResponse>> findYearlyMethodFailed(Integer year);
}