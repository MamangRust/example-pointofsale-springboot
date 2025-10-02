package com.sanedge.pointofsale.repository.product;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.models.Product;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class ProductCommandRepositoryImpl implements ProductCommandRepositoryCustom {

        @PersistenceContext
        private EntityManager em;

        @Override
        @Transactional
        public Product trashed(Long productId) {
                return (Product) em.createNativeQuery(
                                "UPDATE products SET deleted_at = CURRENT_TIMESTAMP " +
                                                "WHERE product_id = :productId AND deleted_at IS NULL " +
                                                "RETURNING *",
                                Product.class)
                                .setParameter("productId", productId)
                                .getSingleResult();
        }

        @Override
        @Transactional
        public Product restore(Long productId) {
                return (Product) em.createNativeQuery(
                                "UPDATE products SET deleted_at = NULL " +
                                                "WHERE product_id = :productId AND deleted_at IS NOT NULL " +
                                                "RETURNING *",
                                Product.class)
                                .setParameter("productId", productId)
                                .getSingleResult();
        }

        @Override
        @Transactional
        public Product deletePermanent(Long productId) {
                return (Product) em.createNativeQuery(
                                "DELETE FROM products " +
                                                "WHERE product_id = :productId AND deleted_at IS NOT NULL " +
                                                "RETURNING *",
                                Product.class)
                                .setParameter("productId", productId)
                                .getSingleResult();
        }

        @Override
        @Transactional
        public boolean restoreAllDeleted() {
                int updated = em.createNativeQuery(
                                "UPDATE products SET deleted_at = NULL WHERE deleted_at IS NOT NULL")
                                .executeUpdate();
                return updated > 0;
        }

        @Override
        @Transactional
        public boolean deleteAllDeleted() {
                int deleted = em.createNativeQuery(
                                "DELETE FROM products WHERE deleted_at IS NOT NULL")
                                .executeUpdate();
                return deleted > 0;
        }
}
