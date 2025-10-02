package com.sanedge.pointofsale.repository.merchant;

import com.sanedge.pointofsale.models.Merchant;

public interface MerchantCommandRepositoryCustom {
    Merchant trashed(Long merchantId);

    Merchant restore(Long merchantId);

    boolean deletePermanent(Long merchantId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}