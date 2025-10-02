package com.sanedge.pointofsale.service.cashier;

import com.sanedge.pointofsale.domain.requests.cashier.CreateCashierRequest;
import com.sanedge.pointofsale.domain.requests.cashier.UpdateCashierRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.cashier.CashierResponse;
import com.sanedge.pointofsale.domain.responses.cashier.CashierResponseDeleteAt;

public interface CashierCommandService {
    ApiResponse<CashierResponse> createCashier(CreateCashierRequest req);

    ApiResponse<CashierResponse> updateCashier(UpdateCashierRequest req);

    ApiResponse<CashierResponseDeleteAt> trashedCashier(Integer cashierId);

    ApiResponse<CashierResponseDeleteAt> restoreCashier(Integer cashierId);

    ApiResponse<Boolean> deleteCashierPermanent(Integer cashierId);

    ApiResponse<Boolean> restoreAllCashier();

    ApiResponse<Boolean> deleteAllCashierPermanent();
}
