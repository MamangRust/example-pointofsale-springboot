package com.sanedge.pointofsale.service.transaction;

import java.util.List;

import com.sanedge.pointofsale.domain.requests.transactions.FindAllTransactionByMerchantRequest;
import com.sanedge.pointofsale.domain.requests.transactions.FindAllTransactionRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.api.ApiResponsePagination;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionResponse;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionResponseDeleteAt;

public interface TransactionQueryService {
    ApiResponsePagination<List<TransactionResponse>> findAllTransactions(FindAllTransactionRequest req);

    ApiResponsePagination<List<TransactionResponseDeleteAt>> findByActive(FindAllTransactionRequest req);

    ApiResponsePagination<List<TransactionResponseDeleteAt>> findByTrashed(FindAllTransactionRequest req);

    ApiResponsePagination<List<TransactionResponse>> findByMerchant(FindAllTransactionByMerchantRequest req);

    ApiResponse<TransactionResponse> findById(Integer id);

    ApiResponse<TransactionResponse> findByOrderId(Integer id);
}
