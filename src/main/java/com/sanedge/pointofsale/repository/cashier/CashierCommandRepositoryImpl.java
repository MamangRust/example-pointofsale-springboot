package com.sanedge.pointofsale.repository.cashier;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.models.cashier.Cashier;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class CashierCommandRepositoryImpl implements CashierCommandRepositoryCustom {

        @PersistenceContext
        private EntityManager em;

        @Override
        @Transactional
        public Cashier trashed(Long cashierId) {
                return (Cashier) em.createNativeQuery(
                                "UPDATE cashiers SET deleted_at = CURRENT_TIMESTAMP " +
                                                "WHERE cashier_id = :cashierId AND deleted_at IS NULL " +
                                                "RETURNING *",
                                Cashier.class)
                                .setParameter("cashierId", cashierId)
                                .getSingleResult();
        }

        @Override
        @Transactional
        public Cashier restore(Long cashierId) {
                return (Cashier) em.createNativeQuery(
                                "UPDATE cashiers SET deleted_at = NULL " +
                                                "WHERE cashier_id = :cashierId AND deleted_at IS NOT NULL " +
                                                "RETURNING *",
                                Cashier.class)
                                .setParameter("cashierId", cashierId)
                                .getSingleResult();
        }

        @Override
        @Transactional
        public Cashier deletePermanent(Long cashierId) {
                return (Cashier) em.createNativeQuery(
                                "DELETE FROM cashiers " +
                                                "WHERE cashier_id = :cashierId AND deleted_at IS NOT NULL " +
                                                "RETURNING *",
                                Cashier.class)
                                .setParameter("cashierId", cashierId)
                                .getSingleResult();
        }

        @Override
        @Transactional
        public boolean restoreAllDeleted() {
                int updated = em.createNativeQuery(
                                "UPDATE cashiers SET deleted_at = NULL WHERE deleted_at IS NOT NULL")
                                .executeUpdate();
                return updated > 0;
        }

        @Override
        @Transactional
        public boolean deleteAllDeleted() {
                int deleted = em.createNativeQuery(
                                "DELETE FROM cashiers WHERE deleted_at IS NOT NULL")
                                .executeUpdate();
                return deleted > 0;
        }
}
