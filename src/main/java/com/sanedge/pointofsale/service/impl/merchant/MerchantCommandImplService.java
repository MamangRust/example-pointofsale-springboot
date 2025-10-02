package com.sanedge.pointofsale.service.impl.merchant;

import org.springframework.stereotype.Service;

import com.sanedge.pointofsale.domain.requests.merchant.CreateMerchantRequest;
import com.sanedge.pointofsale.domain.requests.merchant.UpdateMerchantRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.merchant.MerchantResponse;
import com.sanedge.pointofsale.domain.responses.merchant.MerchantResponseDeleteAt;
import com.sanedge.pointofsale.exception.ResourceNotFoundException;
import com.sanedge.pointofsale.models.Merchant;
import com.sanedge.pointofsale.repository.merchant.MerchantCommandRepository;
import com.sanedge.pointofsale.repository.merchant.MerchantQueryRepository;
import com.sanedge.pointofsale.repository.user.UserQueryRepository;
import com.sanedge.pointofsale.service.merchant.MerchantCommandService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantCommandImplService implements MerchantCommandService {

    private final MerchantCommandRepository merchantCommandRepository;
    private final MerchantQueryRepository merchantQueryRepository;
    private final UserQueryRepository userQueryRepository;

    @Override
    public ApiResponse<MerchantResponse> createMerchant(CreateMerchantRequest req) {
        log.info("🆕 Creating merchant: {}", req.getName());
        try {
            if (req.getUserId() != null) {
                userQueryRepository.findByUserId(req.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + req.getUserId()));
            }

            Merchant merchant = Merchant.fromCreateRequest(req);
            Merchant saved = merchantCommandRepository.save(merchant);

            return ApiResponse.<MerchantResponse>builder()
                    .status("success")
                    .message("Merchant created successfully")
                    .data(MerchantResponse.from(saved))
                    .build();
        } catch (ResourceNotFoundException ex) {
            log.warn("❌ Resource not found: {}", ex.getMessage());
            return ApiResponse.<MerchantResponse>builder()
                    .status("error")
                    .message("Resource not found")
                    .data(null)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to create merchant: {}", req.getName(), e);
            return ApiResponse.<MerchantResponse>builder()
                    .status("error")
                    .message("Failed to create merchant")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<MerchantResponse> updateMerchant(UpdateMerchantRequest req) {
        log.info("✏️ Updating merchant ID: {}", req.getMerchantId());
        try {
            Merchant merchant = merchantQueryRepository.findById(req.getMerchantId().longValue())
                    .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"));

            merchant.updateFromRequest(req);
            Merchant updated = merchantCommandRepository.save(merchant);

            return ApiResponse.<MerchantResponse>builder()
                    .status("success")
                    .message("Merchant updated successfully")
                    .data(MerchantResponse.from(updated))
                    .build();
        } catch (ResourceNotFoundException ex) {
            log.warn("❌ Resource not found: {}", ex.getMessage());
            return ApiResponse.<MerchantResponse>builder()
                    .status("error")
                    .message("Resource not found")
                    .data(null)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to update merchant ID: {}", req.getMerchantId(), e);
            return ApiResponse.<MerchantResponse>builder()
                    .status("error")
                    .message("Failed to update merchant")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<MerchantResponseDeleteAt> trashedMerchant(Integer merchantId) {
        log.info("🗑️ Soft deleting merchant ID: {}", merchantId);
        try {
            Merchant merchant = merchantCommandRepository.trashed(merchantId.longValue());
            return ApiResponse.<MerchantResponseDeleteAt>builder()
                    .status("success")
                    .message("Merchant trashed successfully")
                    .data(MerchantResponseDeleteAt.from(merchant))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to trash merchant ID: {}", merchantId, e);
            return ApiResponse.<MerchantResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to trash merchant")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<MerchantResponseDeleteAt> restoreMerchant(Integer merchantId) {
        log.info("♻️ Restoring merchant ID: {}", merchantId);
        try {
            Merchant merchant = merchantCommandRepository.restore(merchantId.longValue());
            return ApiResponse.<MerchantResponseDeleteAt>builder()
                    .status("success")
                    .message("Merchant restored successfully")
                    .data(MerchantResponseDeleteAt.from(merchant))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore merchant ID: {}", merchantId, e);
            return ApiResponse.<MerchantResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to restore merchant")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteMerchantPermanent(Integer merchantId) {
        log.warn("❌ Permanently deleting merchant ID: {}", merchantId);
        try {
            boolean deleted = merchantCommandRepository.deletePermanent(merchantId.longValue());
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("Merchant permanently deleted")
                    .data(deleted)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to permanently delete merchant ID: {}", merchantId, e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to permanently delete merchant")
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> restoreAllMerchant() {
        log.info("♻️ Restoring all trashed merchants");
        try {
            boolean restored = merchantCommandRepository.restoreAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("All trashed merchants restored")
                    .data(restored)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore all trashed merchants", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to restore all trashed merchants")
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteAllMerchantPermanent() {
        log.warn("❌ Permanently deleting all trashed merchants");
        try {
            boolean deleted = merchantCommandRepository.deleteAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("All trashed merchants permanently deleted")
                    .data(deleted)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to permanently delete all trashed merchants", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to permanently delete all trashed merchants")
                    .data(false)
                    .build();
        }
    }
}
