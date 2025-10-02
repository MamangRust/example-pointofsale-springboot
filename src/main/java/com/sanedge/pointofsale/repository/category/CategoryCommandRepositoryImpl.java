package com.sanedge.pointofsale.repository.category;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.models.category.Category;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class CategoryCommandRepositoryImpl implements CategoryCommandRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public Category trashed(Long categoryId) {
        return (Category) em.createNativeQuery(
                "UPDATE categories SET deleted_at = CURRENT_TIMESTAMP " +
                        "WHERE category_id = :categoryId AND deleted_at IS NULL " +
                        "RETURNING *",
                Category.class)
                .setParameter("categoryId", categoryId)
                .getSingleResult();
    }

    @Override
    @Transactional
    public Category restore(Long categoryId) {
        return (Category) em.createNativeQuery(
                "UPDATE categories SET deleted_at = NULL " +
                        "WHERE category_id = :categoryId AND deleted_at IS NOT NULL " +
                        "RETURNING *",
                Category.class)
                .setParameter("categoryId", categoryId)
                .getSingleResult();
    }

    @Override
    @Transactional
    public Category deletePermanent(Long categoryId) {
        return (Category) em.createNativeQuery(
                "DELETE FROM categories " +
                        "WHERE category_id = :categoryId AND deleted_at IS NOT NULL " +
                        "RETURNING *",
                Category.class)
                .setParameter("categoryId", categoryId)
                .getSingleResult();
    }

    @Override
    @Transactional
    public boolean restoreAllDeleted() {
        int updated = em.createNativeQuery(
                "UPDATE categories SET deleted_at = NULL WHERE deleted_at IS NOT NULL")
                .executeUpdate();
        return updated > 0;
    }

    @Override
    @Transactional
    public boolean deleteAllDeleted() {
        int deleted = em.createNativeQuery(
                "DELETE FROM categories WHERE deleted_at IS NOT NULL")
                .executeUpdate();
        return deleted > 0;
    }
}
