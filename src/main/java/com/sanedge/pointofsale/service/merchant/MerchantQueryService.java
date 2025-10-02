package com.sanedge.pointofsale.service.merchant;

import java.util.List;

import com.sanedge.pointofsale.domain.requests.merchant.FindAllMerchants;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.api.ApiResponsePagination;
import com.sanedge.pointofsale.domain.responses.merchant.MerchantResponse;
import com.sanedge.pointofsale.domain.responses.merchant.MerchantResponseDeleteAt;

public interface MerchantQueryService {
    ApiResponsePagination<List<MerchantResponse>> findAll(FindAllMerchants req);

    ApiResponsePagination<List<MerchantResponseDeleteAt>> findByActive(FindAllMerchants req);

    ApiResponsePagination<List<MerchantResponseDeleteAt>> findByTrashed(FindAllMerchants req);

    ApiResponse<MerchantResponse> findById(Integer merchantId);

}
