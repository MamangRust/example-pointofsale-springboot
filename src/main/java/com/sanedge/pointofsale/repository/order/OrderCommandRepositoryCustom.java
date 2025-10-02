package com.sanedge.pointofsale.repository.order;

import com.sanedge.pointofsale.models.order.Order;

public interface OrderCommandRepositoryCustom {
    Order trashed(Long orderId);

    Order restore(Long orderId);

    Order deletePermanent(Long orderId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}