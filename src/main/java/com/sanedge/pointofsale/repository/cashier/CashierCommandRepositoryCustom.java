package com.sanedge.pointofsale.repository.cashier;

import com.sanedge.pointofsale.models.cashier.Cashier;

public interface CashierCommandRepositoryCustom {
    Cashier trashed(Long cashierId);

    Cashier restore(Long cashierId);

    Cashier deletePermanent(Long cashierId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
