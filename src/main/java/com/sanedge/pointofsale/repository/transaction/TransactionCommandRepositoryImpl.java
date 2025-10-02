package com.sanedge.pointofsale.repository.transaction;

import com.sanedge.pointofsale.models.transaction.Transaction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionCommandRepositoryImpl implements TransactionCommandRepositoryCustom {
        @PersistenceContext
        private EntityManager em;

        @Override
        @Transactional
        public Transaction trashed(Long transactionId) {
                return (Transaction) em.createNativeQuery(
                                "UPDATE transactions SET deleted_at = CURRENT_TIMESTAMP " +
                                                "WHERE transaction_id = :transactionId AND deleted_at IS NULL " +
                                                "RETURNING *",
                                Transaction.class)
                                .setParameter("transactionId", transactionId)
                                .getSingleResult();
        }

        @Override
        @Transactional
        public Transaction restore(Long transactionId) {
                return (Transaction) em.createNativeQuery(
                                "UPDATE transactions SET deleted_at = NULL " +
                                                "WHERE transaction_id = :transactionId AND deleted_at IS NOT NULL " +
                                                "RETURNING *",
                                Transaction.class)
                                .setParameter("transactionId", transactionId)
                                .getSingleResult();
        }

        @Override
        @Transactional
        public Transaction deletePermanent(Long transactionId) {
                return (Transaction) em.createNativeQuery(
                                "DELETE FROM transactions " +
                                                "WHERE transaction_id = :transactionId AND deleted_at IS NOT NULL " +
                                                "RETURNING *",
                                Transaction.class)
                                .setParameter("transactionId", transactionId)
                                .getSingleResult();
        }

        @Override
        @Transactional
        public boolean restoreAllDeleted() {
                int updated = em.createNativeQuery(
                                "UPDATE transactions SET deleted_at = NULL WHERE deleted_at IS NOT NULL")
                                .executeUpdate();
                return updated > 0;
        }

        @Override
        @Transactional
        public boolean deleteAllDeleted() {
                int deleted = em.createNativeQuery(
                                "DELETE FROM transactions WHERE deleted_at IS NOT NULL")
                                .executeUpdate();
                return deleted > 0;
        }
}
