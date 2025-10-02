package com.sanedge.pointofsale.repository.merchant;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.models.Merchant;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class MerchantCommandRepositoryImpl implements MerchantCommandRepositoryCustom {

        @PersistenceContext
        private EntityManager em;

        @Override
        @Transactional
        public Merchant trashed(Long merchantId) {
                return (Merchant) em.createNativeQuery(
                                "UPDATE merchants SET deleted_at = CURRENT_TIMESTAMP " +
                                                "WHERE merchant_id = :merchantId AND deleted_at IS NULL " +
                                                "RETURNING *",
                                Merchant.class)
                                .setParameter("merchantId", merchantId)
                                .getSingleResult();
        }

        @Override
        @Transactional
        public Merchant restore(Long merchantId) {
                return (Merchant) em.createNativeQuery(
                                "UPDATE merchants SET deleted_at = NULL " +
                                                "WHERE merchant_id = :merchantId AND deleted_at IS NOT NULL " +
                                                "RETURNING *",
                                Merchant.class)
                                .setParameter("merchantId", merchantId)
                                .getSingleResult();
        }

        @Override
        @Transactional
        public boolean deletePermanent(Long merchantId) {
                int deleted = em.createNativeQuery(
                                "DELETE FROM merchants WHERE merchant_id = :merchantId AND deleted_at IS NOT NULL")
                                .setParameter("merchantId", merchantId)
                                .executeUpdate();
                return deleted > 0;
        }

        @Override
        @Transactional
        public boolean restoreAllDeleted() {
                int updated = em.createNativeQuery(
                                "UPDATE merchants SET deleted_at = NULL WHERE deleted_at IS NOT NULL")
                                .executeUpdate();
                return updated > 0;
        }

        @Override
        @Transactional
        public boolean deleteAllDeleted() {
                int deleted = em.createNativeQuery(
                                "DELETE FROM merchants WHERE deleted_at IS NOT NULL")
                                .executeUpdate();
                return deleted > 0;
        }
}
