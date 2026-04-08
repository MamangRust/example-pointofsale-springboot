package com.sanedge.pointofsale.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sanedge.pointofsale.domain.requests.transactions.CreateTransactionRequest;
import com.sanedge.pointofsale.domain.requests.transactions.FindAllTransactionByMerchantRequest;
import com.sanedge.pointofsale.domain.requests.transactions.FindAllTransactionRequest;
import com.sanedge.pointofsale.domain.requests.transactions.MonthAmountTransactionMerchant;
import com.sanedge.pointofsale.domain.requests.transactions.MonthAmountTransactionRequest;
import com.sanedge.pointofsale.domain.requests.transactions.MonthMethodTransactionMerchantRequest;
import com.sanedge.pointofsale.domain.requests.transactions.MonthMethodTransactionRequest;
import com.sanedge.pointofsale.domain.requests.transactions.UpdateTransactionRequest;
import com.sanedge.pointofsale.domain.requests.transactions.YearAmountTransactionMerchant;
import com.sanedge.pointofsale.domain.requests.transactions.YearMethodTransactionMerchantRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.api.ApiResponsePagination;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionMonthlyAmountFailedResponse;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionMonthlyAmountSuccessResponse;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionMonthlyMethodResponse;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionResponse;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionResponseDeleteAt;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionYearlyAmountFailedResponse;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionYearlyAmountSuccessResponse;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionYearlyMethodResponse;
import com.sanedge.pointofsale.service.transaction.TransactionCommandService;
import com.sanedge.pointofsale.service.transaction.TransactionQueryService;
import com.sanedge.pointofsale.service.transaction.stats.TransactionAmountService;
import com.sanedge.pointofsale.service.transaction.stats.TransactionMethodService;
import com.sanedge.pointofsale.service.transaction.statsbymerchant.TransactionAmountByMerchantService;
import com.sanedge.pointofsale.service.transaction.statsbymerchant.TransactionMethodByMerchantService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transaction")
public class TransactionController {
    private final TransactionQueryService transactionQueryService;
    private final TransactionCommandService transactionCommandService;
    private final TransactionAmountService transactionAmountService;
    private final TransactionMethodService transactionMethodService;
    private final TransactionAmountByMerchantService transactionAmountByMerchantService;
    private final TransactionMethodByMerchantService transactionMethodByMerchantService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<TransactionResponse>>> findAllTransactions(
            @ModelAttribute FindAllTransactionRequest req) {

        return ResponseEntity.ok(transactionQueryService.findAllTransactions(req));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<TransactionResponseDeleteAt>>> findActiveTransactions(
            @ModelAttribute FindAllTransactionRequest req) {

        return ResponseEntity.ok(transactionQueryService.findByActive(req));
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<TransactionResponseDeleteAt>>> findTrashedTransactions(
            @ModelAttribute FindAllTransactionRequest req) {
        return ResponseEntity.ok(transactionQueryService.findByTrashed(req));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<TransactionResponse>> findTransactionById(@PathVariable Integer id) {
        return ResponseEntity.ok(transactionQueryService.findById(id));
    }

    @GetMapping("/merchant/{merchantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<TransactionResponse>>> findByMerchant(
            @PathVariable Integer merchantId,
            @ModelAttribute FindAllTransactionByMerchantRequest req) {
        req.setMerchantId(merchantId);
        return ResponseEntity.ok(transactionQueryService.findByMerchant(req));
    }

    @GetMapping("/monthly-success")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransactionMonthlyAmountSuccessResponse>>> getMonthlyAmountSuccess(
            @ModelAttribute MonthAmountTransactionRequest req) {
        return ResponseEntity.ok(transactionAmountService.findMonthlyAmountSuccess(req));
    }

    @GetMapping("/yearly-success")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransactionYearlyAmountSuccessResponse>>> getYearlyAmountSuccess(
            @RequestParam Integer year) {
        return ResponseEntity.ok(transactionAmountService.findYearlyAmountSuccess(year));
    }

    @GetMapping("/monthly-failed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransactionMonthlyAmountFailedResponse>>> getMonthlyAmountFailed(
            @ModelAttribute MonthAmountTransactionRequest req) {
        return ResponseEntity.ok(transactionAmountService.findMonthlyAmountFailed(req));
    }

    @GetMapping("/yearly-failed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransactionYearlyAmountFailedResponse>>> getYearlyAmountFailed(
            @RequestParam Integer year) {
        return ResponseEntity.ok(transactionAmountService.findYearlyAmountFailed(year));
    }

    @GetMapping("/merchant/monthly-success")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransactionMonthlyAmountSuccessResponse>>> getMonthlyAmountSuccessByMerchant(
            @ModelAttribute MonthAmountTransactionMerchant req) {
        return ResponseEntity.ok(transactionAmountByMerchantService.findMonthlyAmountSuccessByMerchant(req));
    }

    @GetMapping("/merchant/yearly-success")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransactionYearlyAmountSuccessResponse>>> getYearlyAmountSuccessByMerchant(
            @ModelAttribute YearAmountTransactionMerchant req) {
        return ResponseEntity.ok(transactionAmountByMerchantService.findYearlyAmountSuccessByMerchant(req));
    }

    @GetMapping("/merchant/monthly-failed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransactionMonthlyAmountFailedResponse>>> getMonthlyAmountFailedByMerchant(
            @ModelAttribute MonthAmountTransactionMerchant req) {
        return ResponseEntity.ok(transactionAmountByMerchantService.findMonthlyAmountFailedByMerchant(req));
    }

    @GetMapping("/merchant/yearly-failed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransactionYearlyAmountFailedResponse>>> getYearlyAmountFailedByMerchant(
            @ModelAttribute YearAmountTransactionMerchant req) {
        return ResponseEntity.ok(transactionAmountByMerchantService.findYearlyAmountFailedByMerchant(req));
    }

    @GetMapping("/monthly-method-success")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransactionMonthlyMethodResponse>>> getMonthlyMethodSuccess(
            @ModelAttribute MonthMethodTransactionRequest req) {
        return ResponseEntity.ok(transactionMethodService.findMonthlyMethodSuccess(req));
    }

    @GetMapping("/yearly-method-success")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransactionYearlyMethodResponse>>> getYearlyMethodSuccess(
            @RequestParam Integer year) {
        return ResponseEntity.ok(transactionMethodService.findYearlyMethodSuccess(year));
    }

    @GetMapping("/monthly-method-failed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransactionMonthlyMethodResponse>>> getMonthlyMethodFailed(
            @ModelAttribute MonthMethodTransactionRequest req) {
        return ResponseEntity.ok(transactionMethodService.findMonthlyMethodFailed(req));
    }

    @GetMapping("/yearly-method-failed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransactionYearlyMethodResponse>>> getYearlyMethodFailed(
            @RequestParam Integer year) {
        return ResponseEntity.ok(transactionMethodService.findYearlyMethodFailed(year));
    }

    @GetMapping("/merchant/monthly-method-success")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransactionMonthlyMethodResponse>>> getMonthlyMethodByMerchantSuccess(
            @ModelAttribute MonthMethodTransactionMerchantRequest req) {
        return ResponseEntity.ok(transactionMethodByMerchantService.findMonthlyMethodByMerchantSuccess(req));
    }

    @GetMapping("/merchant/yearly-method-success")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransactionYearlyMethodResponse>>> getYearlyMethodByMerchantSuccess(
            @ModelAttribute YearMethodTransactionMerchantRequest req) {
        return ResponseEntity.ok(transactionMethodByMerchantService.findYearlyMethodByMerchantSuccess(req));
    }

    @GetMapping("/merchant/monthly-method-failed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransactionMonthlyMethodResponse>>> getMonthlyMethodByMerchantFailed(
            @ModelAttribute MonthMethodTransactionMerchantRequest req) {
        return ResponseEntity.ok(transactionMethodByMerchantService.findMonthlyMethodByMerchantFailed(req));
    }

    @GetMapping("/merchant/yearly-method-failed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransactionYearlyMethodResponse>>> getYearlyMethodByMerchantFailed(
            @ModelAttribute YearMethodTransactionMerchantRequest req) {
        return ResponseEntity.ok(transactionMethodByMerchantService.findYearlyMethodByMerchantFailed(req));
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<TransactionResponse>> createTransaction(
            @Valid @RequestBody CreateTransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionCommandService.create(request));
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<TransactionResponse>> updateTransaction(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateTransactionRequest request) {
        request.setTransactionID(id);
        return ResponseEntity.ok(transactionCommandService.update(request));
    }

    @PostMapping("/trashed/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<TransactionResponseDeleteAt>> trashTransaction(@PathVariable Integer id) {
        return ResponseEntity.ok(transactionCommandService.trash(id));
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<TransactionResponseDeleteAt>> restoreTransaction(@PathVariable Integer id) {
        return ResponseEntity.ok(transactionCommandService.restore(id));
    }

    @DeleteMapping("/permanent/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteTransaction(@PathVariable Integer id) {
        return ResponseEntity.ok(transactionCommandService.delete(id));
    }

    @PostMapping("/restore/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAllTrashedTransactions() {
        return ResponseEntity.ok(transactionCommandService.restoreAll());
    }

    @PostMapping("/permanent/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllTrashedTransactions() {
        return ResponseEntity.ok(transactionCommandService.deleteAll());
    }
}
