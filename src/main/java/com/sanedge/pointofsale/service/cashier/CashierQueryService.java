package com.sanedge.pointofsale.service.cashier;

import java.util.List;

import com.sanedge.pointofsale.domain.requests.cashier.FindAllCashierMerchant;
import com.sanedge.pointofsale.domain.requests.cashier.FindAllCashiers;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.api.ApiResponsePagination;
import com.sanedge.pointofsale.domain.responses.cashier.CashierResponse;
import com.sanedge.pointofsale.domain.responses.cashier.CashierResponseDeleteAt;

public interface CashierQueryService {
    ApiResponsePagination<List<CashierResponse>> findAll(FindAllCashiers req);

    ApiResponse<CashierResponse> findById(Integer cashierId);

    ApiResponsePagination<List<CashierResponseDeleteAt>> findByActive(FindAllCashiers req);

    ApiResponsePagination<List<CashierResponseDeleteAt>> findByTrashed(FindAllCashiers req);

    ApiResponsePagination<List<CashierResponse>> findByMerchant(FindAllCashierMerchant req);
}
