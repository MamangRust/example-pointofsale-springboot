package com.sanedge.pointofsale.repository.order;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.models.order.Order;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class OrderCommandRepositoryImpl implements OrderCommandRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public Order trashed(Long orderId) {
        return (Order) em.createNativeQuery(
                "UPDATE orders SET deleted_at = CURRENT_TIMESTAMP " +
                        "WHERE order_id = :orderId AND deleted_at IS NULL " +
                        "RETURNING *",
                Order.class)
                .setParameter("orderId", orderId)
                .getSingleResult();
    }

    @Override
    @Transactional
    public Order restore(Long orderId) {
        return (Order) em.createNativeQuery(
                "UPDATE orders SET deleted_at = NULL " +
                        "WHERE order_id = :orderId AND deleted_at IS NOT NULL " +
                        "RETURNING *",
                Order.class)
                .setParameter("orderId", orderId)
                .getSingleResult();
    }

    @Override
    @Transactional
    public Order deletePermanent(Long orderId) {
        return (Order) em.createNativeQuery(
                "DELETE FROM orders " +
                        "WHERE order_id = :orderId AND deleted_at IS NOT NULL " +
                        "RETURNING *",
                Order.class)
                .setParameter("orderId", orderId)
                .getSingleResult();
    }

    @Override
    @Transactional
    public boolean restoreAllDeleted() {
        int updated = em.createNativeQuery(
                "UPDATE orders SET deleted_at = NULL WHERE deleted_at IS NOT NULL")
                .executeUpdate();
        return updated > 0;
    }

    @Override
    @Transactional
    public boolean deleteAllDeleted() {
        int deleted = em.createNativeQuery(
                "DELETE FROM orders WHERE deleted_at IS NOT NULL")
                .executeUpdate();
        return deleted > 0;
    }
}
