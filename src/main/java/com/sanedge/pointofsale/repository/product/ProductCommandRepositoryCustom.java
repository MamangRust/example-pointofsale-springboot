package com.sanedge.pointofsale.repository.product;

import com.sanedge.pointofsale.models.Product;

public interface ProductCommandRepositoryCustom {
    Product trashed(Long productId);

    Product restore(Long productId);

    Product deletePermanent(Long productId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}