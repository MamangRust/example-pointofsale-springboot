package com.sanedge.pointofsale.service.merchant;

import com.sanedge.pointofsale.domain.requests.merchant.CreateMerchantRequest;
import com.sanedge.pointofsale.domain.requests.merchant.UpdateMerchantRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.merchant.MerchantResponse;
import com.sanedge.pointofsale.domain.responses.merchant.MerchantResponseDeleteAt;

public interface MerchantCommandService {
    ApiResponse<MerchantResponse> createMerchant(CreateMerchantRequest req);

    ApiResponse<MerchantResponse> updateMerchant(UpdateMerchantRequest req);

    ApiResponse<MerchantResponseDeleteAt> trashedMerchant(Integer merchantId);

    ApiResponse<MerchantResponseDeleteAt> restoreMerchant(Integer merchantId);

    ApiResponse<Boolean> deleteMerchantPermanent(Integer merchantId);

    ApiResponse<Boolean> restoreAllMerchant();

    ApiResponse<Boolean> deleteAllMerchantPermanent();
}
