package com.sanedge.pointofsale.service.impl.cashier;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.sanedge.pointofsale.domain.requests.cashier.CreateCashierRequest;
import com.sanedge.pointofsale.domain.requests.cashier.UpdateCashierRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.cashier.CashierResponse;
import com.sanedge.pointofsale.domain.responses.cashier.CashierResponseDeleteAt;
import com.sanedge.pointofsale.exception.ResourceNotFoundException;
import com.sanedge.pointofsale.models.cashier.Cashier;
import com.sanedge.pointofsale.repository.cashier.CashierCommandRepository;
import com.sanedge.pointofsale.repository.cashier.CashierQueryRepository;
import com.sanedge.pointofsale.repository.merchant.MerchantQueryRepository;
import com.sanedge.pointofsale.repository.user.UserQueryRepository;
import com.sanedge.pointofsale.service.cashier.CashierCommandService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CashierCommandImplService implements CashierCommandService {
    private final UserQueryRepository userQueryRepository;
    private final MerchantQueryRepository merchantQueryRepository;
    private final CashierCommandRepository cashierCommandRepository;
    private final CashierQueryRepository cashierQueryRepository;

    @Override
    public ApiResponse<CashierResponse> createCashier(CreateCashierRequest req) {
        try {
            log.info("🆕 Creating cashier for merchantId={}, userId={}, name={}", req.getMerchantId(), req.getUserId(),
                    req.getName());

            merchantQueryRepository.findById(req.getMerchantId().longValue())
                    .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"));

            userQueryRepository.findById(req.getUserId().longValue())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            cashierQueryRepository.findByNameAndMerchantId(req.getName(), req.getMerchantId().longValue())
                    .ifPresent(c -> {
                        throw new IllegalArgumentException(
                                "Cashier with name '" + req.getName() + "' already exists for this merchant");
                    });

            Cashier cashier = new Cashier();
            cashier.setMerchantId(req.getMerchantId().longValue());
            cashier.setUserId(req.getUserId().longValue());
            cashier.setName(req.getName());
            cashier.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
            cashier.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

            Cashier saved = cashierCommandRepository.save(cashier);

            log.info("✅ Cashier created successfully with id={}", saved.getCashierId());

            return ApiResponse.<CashierResponse>builder()
                    .status("success")
                    .message("✅ Cashier created successfully!")
                    .data(CashierResponse.from(saved))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to create cashier", e);
            return ApiResponse.<CashierResponse>builder()
                    .status("error")
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<CashierResponse> updateCashier(UpdateCashierRequest req) {
        try {
            log.info("🔄 Updating cashier id={}", req.getCashierId());

            Cashier cashier = cashierCommandRepository.findById(req.getCashierId().longValue())
                    .orElseThrow(() -> new ResourceNotFoundException("Cashier not found"));

            cashier.setName(req.getName());
            cashier.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

            Cashier updated = cashierCommandRepository.save(cashier);

            log.info("✅ Cashier updated successfully id={}", updated.getCashierId());

            return ApiResponse.<CashierResponse>builder()
                    .status("success")
                    .message("✅ Cashier updated successfully!")
                    .data(CashierResponse.from(updated))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to update cashier id={}", req.getCashierId(), e);
            return ApiResponse.<CashierResponse>builder()
                    .status("error")
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<CashierResponseDeleteAt> trashedCashier(Integer cashierId) {
        log.info("🗑️ Trashing cashier id={}", cashierId);
        try {
            Cashier cashier = cashierCommandRepository.trashed(cashierId.longValue());
            return ApiResponse.<CashierResponseDeleteAt>builder()
                    .status("success")
                    .message("🗑️ Cashier trashed successfully!")
                    .data(CashierResponseDeleteAt.from(cashier))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to trash cashier id={}", cashierId, e);
            return ApiResponse.<CashierResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to trash cashier: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<CashierResponseDeleteAt> restoreCashier(Integer cashierId) {
        log.info("♻️ Restoring cashier id={}", cashierId);
        try {
            Cashier cashier = cashierCommandRepository.restore(cashierId.longValue());
            return ApiResponse.<CashierResponseDeleteAt>builder()
                    .status("success")
                    .message("♻️ Cashier restored successfully!")
                    .data(CashierResponseDeleteAt.from(cashier))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore cashier id={}", cashierId, e);
            return ApiResponse.<CashierResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to restore cashier: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteCashierPermanent(Integer cashierId) {
        log.info("🧨 Permanently deleting cashier id={}", cashierId);
        try {
            cashierCommandRepository.deletePermanent(cashierId.longValue());
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🧨 Cashier permanently deleted!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to permanently delete cashier id={}", cashierId, e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to permanently delete cashier: " + e.getMessage())
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> restoreAllCashier() {
        log.info("🔄 Restoring ALL trashed cashiers");
        try {
            cashierCommandRepository.restoreAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🔄 All cashiers restored successfully!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore all cashiers", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to restore all cashiers: " + e.getMessage())
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteAllCashierPermanent() {
        log.info("💣 Permanently deleting ALL trashed cashiers");
        try {
            cashierCommandRepository.deleteAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("💣 All cashiers permanently deleted!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to delete all cashiers", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to delete all cashiers: " + e.getMessage())
                    .data(false)
                    .build();
        }
    }
}
