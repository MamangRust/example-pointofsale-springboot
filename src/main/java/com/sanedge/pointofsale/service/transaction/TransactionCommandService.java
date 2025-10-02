package com.sanedge.pointofsale.service.transaction;

import com.sanedge.pointofsale.domain.requests.transactions.CreateTransactionRequest;
import com.sanedge.pointofsale.domain.requests.transactions.UpdateTransactionRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionResponse;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionResponseDeleteAt;

public interface TransactionCommandService {
    ApiResponse<TransactionResponse> create(CreateTransactionRequest request);

    ApiResponse<TransactionResponse> update(UpdateTransactionRequest request);

    ApiResponse<TransactionResponseDeleteAt> trash(Integer id);

    ApiResponse<TransactionResponseDeleteAt> restore(Integer id);

    ApiResponse<Boolean> delete(Integer id);

    ApiResponse<Boolean> restoreAll();

    ApiResponse<Boolean> deleteAll();
}
