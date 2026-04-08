package com.sanedge.pointofsale.service.impl.transaction.statsbymerchant;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sanedge.pointofsale.domain.requests.transactions.MonthAmountTransactionMerchant;
import com.sanedge.pointofsale.domain.requests.transactions.YearAmountTransactionMerchant;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionMonthlyAmountFailedResponse;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionMonthlyAmountSuccessResponse;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionYearlyAmountFailedResponse;
import com.sanedge.pointofsale.domain.responses.transaction.TransactionYearlyAmountSuccessResponse;
import com.sanedge.pointofsale.models.transaction.TransactionMonthlyAmountFailed;
import com.sanedge.pointofsale.models.transaction.TransactionMonthlyAmountSuccess;
import com.sanedge.pointofsale.models.transaction.TransactionYearlyAmountFailed;
import com.sanedge.pointofsale.models.transaction.TransactionYearlyAmountSuccess;
import com.sanedge.pointofsale.repository.transaction.statsbymerchant.TransactionAmountByMerchantRepository;
import com.sanedge.pointofsale.service.transaction.statsbymerchant.TransactionAmountByMerchantService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionAmountByMerchantImplService implements TransactionAmountByMerchantService {

        private final TransactionAmountByMerchantRepository transactionAmountByMerchantRepository;

        @Override
        public ApiResponse<List<TransactionMonthlyAmountSuccessResponse>> findMonthlyAmountSuccessByMerchant(
                        MonthAmountTransactionMerchant req) {
                log.info("📊 Fetching monthly SUCCESS transaction amount by merchant | merchantId={}, year={}, month={}",
                                req.getMerchantId(), req.getYear(), req.getMonth());

                if (req.getMerchantId() == null || req.getYear() == null || req.getMonth() == null) {
                        log.error("❌ Missing required fields | req: {}", req);
                        return ApiResponse.<List<TransactionMonthlyAmountSuccessResponse>>builder()
                                        .status("error")
                                        .message("Merchant ID, Year, and Month must not be null")
                                        .data(List.of())
                                        .build();
                }

                if (req.getMonth() < 1 || req.getMonth() > 12) {
                        return ApiResponse.<List<TransactionMonthlyAmountSuccessResponse>>builder()
                                        .status("error")
                                        .message("Month must be between 1 and 12")
                                        .data(List.of())
                                        .build();
                }

                try {
                        LocalDate current = LocalDate.of(req.getYear(), req.getMonth(), 1);
                        LocalDate prev = current.minusMonths(1);

                        List<TransactionMonthlyAmountSuccess> rawData = transactionAmountByMerchantRepository
                                        .findMonthlySuccessByMerchant(
                                                        req.getMerchantId().longValue(),
                                                        req.getYear(),
                                                        req.getMonth(),
                                                        prev.getYear(),
                                                        prev.getMonthValue());

                        List<TransactionMonthlyAmountSuccessResponse> response = rawData.stream()
                                        .map(TransactionMonthlyAmountSuccessResponse::from)
                                        .collect(Collectors.toList());

                        log.info("✅ Found {} monthly SUCCESS transaction records for merchant", response.size());

                        return ApiResponse.<List<TransactionMonthlyAmountSuccessResponse>>builder()
                                        .status("success")
                                        .message("Monthly success transaction amount by merchant retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error(
                                        "💥 Failed to fetch monthly SUCCESS transaction amount by merchant | merchantId={}, year={}, month={}",
                                        req.getMerchantId(), req.getYear(), req.getMonth(), e);
                        return ApiResponse.<List<TransactionMonthlyAmountSuccessResponse>>builder()
                                        .status("error")
                                        .message("Failed to retrieve monthly success transaction data by merchant")
                                        .data(List.of())
                                        .build();
                }
        }

        @Override
        public ApiResponse<List<TransactionYearlyAmountSuccessResponse>> findYearlyAmountSuccessByMerchant(
                        YearAmountTransactionMerchant req) {
                log.info("📈 Fetching yearly SUCCESS transaction amount by merchant | merchantId={}, year={}",
                                req.getMerchantId(), req.getYear());

                if (req.getMerchantId() == null || req.getYear() == null) {
                        log.error("❌ Missing required fields | req: {}", req);
                        return ApiResponse.<List<TransactionYearlyAmountSuccessResponse>>builder()
                                        .status("error")
                                        .message("Merchant ID and Year must not be null")
                                        .data(List.of())
                                        .build();
                }

                try {
                        List<TransactionYearlyAmountSuccess> rawData = transactionAmountByMerchantRepository
                                        .findYearlySuccessByMerchant(
                                                        req.getMerchantId().longValue(),
                                                        req.getYear());

                        List<TransactionYearlyAmountSuccessResponse> response = rawData.stream()
                                        .map(TransactionYearlyAmountSuccessResponse::from)
                                        .collect(Collectors.toList());

                        log.info("✅ Found {} yearly SUCCESS transaction records for merchant", response.size());

                        return ApiResponse.<List<TransactionYearlyAmountSuccessResponse>>builder()
                                        .status("success")
                                        .message("Yearly success transaction amount by merchant retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to fetch yearly SUCCESS transaction amount by merchant | merchantId={}, year={}",
                                        req.getMerchantId(), req.getYear(), e);
                        return ApiResponse.<List<TransactionYearlyAmountSuccessResponse>>builder()
                                        .status("error")
                                        .message("Failed to retrieve yearly success transaction data by merchant")
                                        .data(List.of())
                                        .build();
                }
        }

        @Override
        public ApiResponse<List<TransactionMonthlyAmountFailedResponse>> findMonthlyAmountFailedByMerchant(
                        MonthAmountTransactionMerchant req) {
                log.info("📊 Fetching monthly FAILED transaction amount by merchant | merchantId={}, year={}, month={}",
                                req.getMerchantId(), req.getYear(), req.getMonth());

                if (req.getMerchantId() == null || req.getYear() == null || req.getMonth() == null) {
                        log.error("❌ Missing required fields | req: {}", req);
                        return ApiResponse.<List<TransactionMonthlyAmountFailedResponse>>builder()
                                        .status("error")
                                        .message("Merchant ID, Year, and Month must not be null")
                                        .data(List.of())
                                        .build();
                }

                if (req.getMonth() < 1 || req.getMonth() > 12) {
                        return ApiResponse.<List<TransactionMonthlyAmountFailedResponse>>builder()
                                        .status("error")
                                        .message("Month must be between 1 and 12")
                                        .data(List.of())
                                        .build();
                }

                try {
                        LocalDate current = LocalDate.of(req.getYear(), req.getMonth(), 1);
                        LocalDate prev = current.minusMonths(1);

                        List<TransactionMonthlyAmountFailed> rawData = transactionAmountByMerchantRepository
                                        .findMonthlyFailedByMerchant(
                                                        req.getMerchantId().longValue(),
                                                        req.getYear(),
                                                        req.getMonth(),
                                                        prev.getYear(),
                                                        prev.getMonthValue());

                        List<TransactionMonthlyAmountFailedResponse> response = rawData.stream()
                                        .map(TransactionMonthlyAmountFailedResponse::from)
                                        .collect(Collectors.toList());

                        log.info("✅ Found {} monthly FAILED transaction records for merchant", response.size());

                        return ApiResponse.<List<TransactionMonthlyAmountFailedResponse>>builder()
                                        .status("success")
                                        .message("Monthly failed transaction amount by merchant retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error(
                                        "💥 Failed to fetch monthly FAILED transaction amount by merchant | merchantId={}, year={}, month={}",
                                        req.getMerchantId(), req.getYear(), req.getMonth(), e);
                        return ApiResponse.<List<TransactionMonthlyAmountFailedResponse>>builder()
                                        .status("error")
                                        .message("Failed to retrieve monthly failed transaction data by merchant")
                                        .data(List.of())
                                        .build();
                }
        }

        @Override
        public ApiResponse<List<TransactionYearlyAmountFailedResponse>> findYearlyAmountFailedByMerchant(
                        YearAmountTransactionMerchant req) {
                log.info("📈 Fetching yearly FAILED transaction amount by merchant | merchantId={}, year={}",
                                req.getMerchantId(), req.getYear());

                if (req.getMerchantId() == null || req.getYear() == null) {
                        log.error("❌ Missing required fields | req: {}", req);
                        return ApiResponse.<List<TransactionYearlyAmountFailedResponse>>builder()
                                        .status("error")
                                        .message("Merchant ID and Year must not be null")
                                        .data(List.of())
                                        .build();
                }

                try {
                        List<TransactionYearlyAmountFailed> rawData = transactionAmountByMerchantRepository
                                        .findYearlyFailedByMerchant(
                                                        req.getMerchantId().longValue(),
                                                        req.getYear());

                        List<TransactionYearlyAmountFailedResponse> response = rawData.stream()
                                        .map(TransactionYearlyAmountFailedResponse::from)
                                        .collect(Collectors.toList());

                        log.info("✅ Found {} yearly FAILED transaction records for merchant", response.size());

                        return ApiResponse.<List<TransactionYearlyAmountFailedResponse>>builder()
                                        .status("success")
                                        .message("Yearly failed transaction amount by merchant retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to fetch yearly FAILED transaction amount by merchant | merchantId={}, year={}",
                                        req.getMerchantId(), req.getYear(), e);
                        return ApiResponse.<List<TransactionYearlyAmountFailedResponse>>builder()
                                        .status("error")
                                        .message("Failed to retrieve yearly failed transaction data by merchant")
                                        .data(List.of())
                                        .build();
                }
        }
}