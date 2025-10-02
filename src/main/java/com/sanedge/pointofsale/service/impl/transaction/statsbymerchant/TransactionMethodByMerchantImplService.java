package com.sanedge.pointofsale.service.impl.transaction.statsbymerchant;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sanedge.pointofsale.domain.requests.transactions.MonthMethodTransactionMerchantRequest;
import com.sanedge.pointofsale.domain.requests.transactions.YearMethodTransactionMerchantRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionMonthlyMethodResponse;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionYearlyMethodResponse;
import com.sanedge.pointofsale.models.transaction.TransactionMonthlyMethod;
import com.sanedge.pointofsale.models.transaction.TransactionYearMethod;
import com.sanedge.pointofsale.repository.transaction.statsbymerchant.TransactionMethodByMerchantRepository;
import com.sanedge.pointofsale.service.transaction.statsbymerchant.TransactionMethodByMerchantService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionMethodByMerchantImplService implements TransactionMethodByMerchantService {
    private final TransactionMethodByMerchantRepository transactionMethodByMerchantRepository;

    @Override
    public ApiResponse<List<TransactionMonthlyMethodResponse>> findMonthlyMethodByMerchantSuccess(
            MonthMethodTransactionMerchantRequest req) {
        log.info("📊 Fetching monthly SUCCESS transaction by method & merchant | merchantId={}, year={}, month={}",
                req.getMerchantId(), req.getYear(), req.getMonth());

        if (req.getMerchantId() == null || req.getYear() == null || req.getMonth() == null) {
            log.error("❌ Missing required fields | req: {}", req);
            return ApiResponse.<List<TransactionMonthlyMethodResponse>>builder()
                    .status("error")
                    .message("Merchant ID, Year, and Month must not be null")
                    .data(List.of())
                    .build();
        }

        if (req.getMonth() < 1 || req.getMonth() > 12) {
            return ApiResponse.<List<TransactionMonthlyMethodResponse>>builder()
                    .status("error")
                    .message("Month must be between 1 and 12")
                    .data(List.of())
                    .build();
        }

        try {
            LocalDate current = LocalDate.of(req.getYear(), req.getMonth(), 1);
            LocalDate prev = current.minusMonths(1);

            List<TransactionMonthlyMethod> rawData = transactionMethodByMerchantRepository
                    .findMonthlyTransactionMethodsSuccessByMerchant(
                            req.getMerchantId().longValue(),
                            current.getYear(),
                            current.getMonthValue(),
                            prev.getYear(),
                            prev.getMonthValue());

            List<TransactionMonthlyMethodResponse> response = rawData.stream()
                    .map(TransactionMonthlyMethodResponse::from)
                    .collect(Collectors.toList());

            log.info("✅ Found {} monthly SUCCESS transaction records by method & merchant",
                    response.size());

            return ApiResponse.<List<TransactionMonthlyMethodResponse>>builder()
                    .status("success")
                    .message("Monthly success transaction by method & merchant retrieved successfully")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error(
                    "💥 Failed to fetch monthly SUCCESS transaction by method & merchant | merchantId={}, year={}, month={}",
                    req.getMerchantId(), req.getYear(), req.getMonth(), e);
            return ApiResponse.<List<TransactionMonthlyMethodResponse>>builder()
                    .status("error")
                    .message("Failed to retrieve monthly success transaction data by method & merchant")
                    .data(List.of())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<TransactionMonthlyMethodResponse>> findMonthlyMethodByMerchantFailed(
            MonthMethodTransactionMerchantRequest req) {
        log.info("📊 Fetching monthly FAILED transaction by method & merchant | merchantId={}, year={}, month={}",
                req.getMerchantId(), req.getYear(), req.getMonth());

        if (req.getMerchantId() == null || req.getYear() == null || req.getMonth() == null) {
            log.error("❌ Missing required fields | req: {}", req);
            return ApiResponse.<List<TransactionMonthlyMethodResponse>>builder()
                    .status("error")
                    .message("Merchant ID, Year, and Month must not be null")
                    .data(List.of())
                    .build();
        }

        if (req.getMonth() < 1 || req.getMonth() > 12) {
            return ApiResponse.<List<TransactionMonthlyMethodResponse>>builder()
                    .status("error")
                    .message("Month must be between 1 and 12")
                    .data(List.of())
                    .build();
        }

        try {
            LocalDate current = LocalDate.of(req.getYear(), req.getMonth(), 1);
            LocalDate prev = current.minusMonths(1);

            List<TransactionMonthlyMethod> rawData = transactionMethodByMerchantRepository
                    .findMonthlyTransactionMethodsFailedByMerchant(
                            req.getMerchantId().longValue(),
                            current.getYear(),
                            current.getMonthValue(),
                            prev.getYear(),
                            prev.getMonthValue());

            List<TransactionMonthlyMethodResponse> response = rawData.stream()
                    .map(TransactionMonthlyMethodResponse::from)
                    .collect(Collectors.toList());

            log.info("✅ Found {} monthly FAILED transaction records by method & merchant", response.size());

            return ApiResponse.<List<TransactionMonthlyMethodResponse>>builder()
                    .status("success")
                    .message("Monthly failed transaction by method & merchant retrieved successfully")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error(
                    "💥 Failed to fetch monthly FAILED transaction by method & merchant | merchantId={}, year={}, month={}",
                    req.getMerchantId(), req.getYear(), req.getMonth(), e);
            return ApiResponse.<List<TransactionMonthlyMethodResponse>>builder()
                    .status("error")
                    .message("Failed to retrieve monthly failed transaction data by method & merchant")
                    .data(List.of())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<TransactionYearlyMethodResponse>> findYearlyMethodByMerchantSuccess(
            YearMethodTransactionMerchantRequest req) {
        log.info("📈 Fetching yearly SUCCESS transaction by method & merchant | merchantId={}, year={}",
                req.getMerchantId(), req.getYear());

        if (req.getMerchantId() == null || req.getYear() == null) {
            log.error("❌ Missing required fields | req: {}", req);
            return ApiResponse.<List<TransactionYearlyMethodResponse>>builder()
                    .status("error")
                    .message("Merchant ID and Year must not be null")
                    .data(List.of())
                    .build();
        }

        try {
            List<TransactionYearMethod> rawData = transactionMethodByMerchantRepository
                    .findYearlyTransactionMethodsSuccessByMerchant(
                            req.getMerchantId().longValue(),
                            req.getYear());

            List<TransactionYearlyMethodResponse> response = rawData.stream()
                    .map(TransactionYearlyMethodResponse::from)
                    .collect(Collectors.toList());

            log.info("✅ Found {} yearly SUCCESS transaction records by method & merchant", response.size());

            return ApiResponse.<List<TransactionYearlyMethodResponse>>builder()
                    .status("success")
                    .message("Yearly success transaction by method & merchant retrieved successfully")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch yearly SUCCESS transaction by method & merchant | merchantId={}, year={}",
                    req.getMerchantId(), req.getYear(), e);
            return ApiResponse.<List<TransactionYearlyMethodResponse>>builder()
                    .status("error")
                    .message("Failed to retrieve yearly success transaction data by method & merchant")
                    .data(List.of())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<TransactionYearlyMethodResponse>> findYearlyMethodByMerchantFailed(
            YearMethodTransactionMerchantRequest req) {
        log.info("📈 Fetching yearly FAILED transaction by method & merchant | merchantId={}, year={}",
                req.getMerchantId(), req.getYear());

        if (req.getMerchantId() == null || req.getYear() == null) {
            log.error("❌ Missing required fields | req: {}", req);
            return ApiResponse.<List<TransactionYearlyMethodResponse>>builder()
                    .status("error")
                    .message("Merchant ID and Year must not be null")
                    .data(List.of())
                    .build();
        }

        try {
            List<TransactionYearMethod> rawData = transactionMethodByMerchantRepository
                    .findYearlyTransactionMethodsFailedByMerchant(
                            req.getMerchantId().longValue(),
                            req.getYear());

            List<TransactionYearlyMethodResponse> response = rawData.stream()
                    .map(TransactionYearlyMethodResponse::from)
                    .collect(Collectors.toList());

            log.info("✅ Found {} yearly FAILED transaction records by method & merchant", response.size());

            return ApiResponse.<List<TransactionYearlyMethodResponse>>builder()
                    .status("success")
                    .message("Yearly failed transaction by method & merchant retrieved successfully")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch yearly FAILED transaction by method & merchant | merchantId={}, year={}",
                    req.getMerchantId(), req.getYear(), e);
            return ApiResponse.<List<TransactionYearlyMethodResponse>>builder()
                    .status("error")
                    .message("Failed to retrieve yearly failed transaction data by method & merchant")
                    .data(List.of())
                    .build();
        }
    }
}