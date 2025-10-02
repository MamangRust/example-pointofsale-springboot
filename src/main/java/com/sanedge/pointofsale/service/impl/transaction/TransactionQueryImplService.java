package com.sanedge.pointofsale.service.impl.transaction;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sanedge.pointofsale.domain.requests.transactions.FindAllTransactionByMerchantRequest;
import com.sanedge.pointofsale.domain.requests.transactions.FindAllTransactionRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.api.ApiResponsePagination;
import com.sanedge.pointofsale.domain.responses.api.PaginationMeta;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionResponse;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionResponseDeleteAt;
import com.sanedge.pointofsale.models.transaction.Transaction;
import com.sanedge.pointofsale.repository.transaction.TransactionQueryRepository;
import com.sanedge.pointofsale.service.transaction.TransactionQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionQueryImplService implements TransactionQueryService {

    private final TransactionQueryRepository transactionQueryRepository;

    @Override
    public ApiResponsePagination<List<TransactionResponse>> findAllTransactions(FindAllTransactionRequest req) {
        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
        String keyword = (req.getSearch() != null) ? req.getSearch() : "";

        log.info("🔍 Searching all transactions | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                keyword.isEmpty() ? "None" : keyword);

        try {
            Pageable pageable = PageRequest.of(page, pageSize);
            Page<Transaction> transactionPage = transactionQueryRepository.findTransactions(keyword,
                    pageable);

            List<TransactionResponse> responses = transactionPage.getContent()
                    .stream()
                    .map(TransactionResponse::from)
                    .toList();

            log.info("✅ Found {} transactions", responses.size());

            return ApiResponsePagination.<List<TransactionResponse>>builder()
                    .status("success")
                    .message("Transactions retrieved successfully")
                    .data(responses)
                    .pagination(PaginationMeta.fromSpringPage(transactionPage))
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch transactions", e);
            return ApiResponsePagination.<List<TransactionResponse>>builder()
                    .status("error")
                    .message("Failed to fetch transactions")
                    .data(Collections.emptyList())
                    .pagination(null)
                    .build();
        }
    }

    @Override
    public ApiResponsePagination<List<TransactionResponseDeleteAt>> findByActive(FindAllTransactionRequest req) {
        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
        String keyword = (req.getSearch() != null) ? req.getSearch() : "";

        log.info("🔍 Searching active transactions | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                keyword.isEmpty() ? "None" : keyword);

        try {
            Pageable pageable = PageRequest.of(page, pageSize);
            Page<Transaction> transactionPage = transactionQueryRepository.findActiveTransactions(keyword,
                    pageable);

            List<TransactionResponseDeleteAt> responses = transactionPage.getContent()
                    .stream()
                    .map(TransactionResponseDeleteAt::from)
                    .toList();

            log.info("✅ Found {} active transactions", responses.size());

            return ApiResponsePagination.<List<TransactionResponseDeleteAt>>builder()
                    .status("success")
                    .message("Active transactions retrieved successfully")
                    .data(responses)
                    .pagination(PaginationMeta.fromSpringPage(transactionPage))
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch active transactions", e);
            return ApiResponsePagination.<List<TransactionResponseDeleteAt>>builder()
                    .status("error")
                    .message("Failed to fetch active transactions")
                    .data(Collections.emptyList())
                    .pagination(null)
                    .build();
        }
    }

    @Override
    public ApiResponsePagination<List<TransactionResponseDeleteAt>> findByTrashed(FindAllTransactionRequest req) {
        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
        String keyword = (req.getSearch() != null) ? req.getSearch() : "";

        log.info("🔍 Searching trashed transactions | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                keyword.isEmpty() ? "None" : keyword);

        try {
            Pageable pageable = PageRequest.of(page, pageSize);
            Page<Transaction> transactionPage = transactionQueryRepository.findTrashedTransactions(keyword,
                    pageable);

            List<TransactionResponseDeleteAt> responses = transactionPage.getContent()
                    .stream()
                    .map(TransactionResponseDeleteAt::from)
                    .toList();

            log.info("✅ Found {} trashed transactions", responses.size());

            return ApiResponsePagination.<List<TransactionResponseDeleteAt>>builder()
                    .status("success")
                    .message("Trashed transactions retrieved successfully")
                    .data(responses)
                    .pagination(PaginationMeta.fromSpringPage(transactionPage))
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch trashed transactions", e);
            return ApiResponsePagination.<List<TransactionResponseDeleteAt>>builder()
                    .status("error")
                    .message("Failed to fetch trashed transactions")
                    .data(Collections.emptyList())
                    .pagination(null)
                    .build();
        }
    }

    @Override
    public ApiResponsePagination<List<TransactionResponse>> findByMerchant(
            FindAllTransactionByMerchantRequest req) {
        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
        String keyword = (req.getSearch() != null) ? req.getSearch() : "";

        log.info("🔍 Searching transactions by merchant_id={} | Page: {}, Size: {}, Search: {}",
                req.getMerchantId(),
                page + 1, pageSize, keyword.isEmpty() ? "None" : keyword);

        try {
            Pageable pageable = PageRequest.of(page, pageSize);
            Page<Transaction> transactionPage = transactionQueryRepository.findTransactionsByMerchant(
                    keyword,
                    req.getMerchantId(), pageable);

            List<TransactionResponse> responses = transactionPage.getContent()
                    .stream()
                    .map(TransactionResponse::from)
                    .toList();

            log.info("✅ Found {} transactions for merchant_id={}", responses.size(), req.getMerchantId());

            return ApiResponsePagination.<List<TransactionResponse>>builder()
                    .status("success")
                    .message("Transactions by merchant retrieved successfully")
                    .data(responses)
                    .pagination(PaginationMeta.fromSpringPage(transactionPage))
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch transactions by merchant_id={}", req.getMerchantId(), e);
            return ApiResponsePagination.<List<TransactionResponse>>builder()
                    .status("error")
                    .message("Failed to fetch transactions by merchant")
                    .data(Collections.emptyList())
                    .pagination(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<TransactionResponse> findById(Integer id) {
        log.info("🔍 Finding transaction by id={}", id);
        try {
            return transactionQueryRepository.findById(id.longValue())
                    .map(tx -> ApiResponse.<TransactionResponse>builder()
                            .status("success")
                            .message("Transaction retrieved successfully")
                            .data(TransactionResponse.from(tx))
                            .build())
                    .orElseGet(() -> {
                        log.warn("❌ Transaction not found with id={}", id);
                        return ApiResponse.<TransactionResponse>builder()
                                .status("error")
                                .message("Transaction not found")
                                .data(null)
                                .build();
                    });
        } catch (Exception e) {
            log.error("💥 Failed to fetch transaction by id={}", id, e);
            return ApiResponse.<TransactionResponse>builder()
                    .status("error")
                    .message("Failed to fetch transaction")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<TransactionResponse> findByOrderId(Integer id) {
        log.info("🔍 Finding transaction by order_id={}", id);
        try {
            return transactionQueryRepository.findByOrderId(id.longValue())
                    .map(tx -> ApiResponse.<TransactionResponse>builder()
                            .status("success")
                            .message("Transaction retrieved successfully")
                            .data(TransactionResponse.from(tx))
                            .build())
                    .orElseGet(() -> {
                        log.warn("❌ Transaction not found with order_id={}", id);
                        return ApiResponse.<TransactionResponse>builder()
                                .status("error")
                                .message("Transaction not found for order")
                                .data(null)
                                .build();
                    });
        } catch (Exception e) {
            log.error("💥 Failed to fetch transaction by order_id={}", id, e);
            return ApiResponse.<TransactionResponse>builder()
                    .status("error")
                    .message("Failed to fetch transaction by order")
                    .data(null)
                    .build();
        }
    }
}
