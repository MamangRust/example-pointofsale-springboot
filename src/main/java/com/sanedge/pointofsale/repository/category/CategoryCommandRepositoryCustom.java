package com.sanedge.pointofsale.repository.category;

import com.sanedge.pointofsale.models.category.Category;

public interface CategoryCommandRepositoryCustom {
    Category trashed(Long categoryId);

    Category restore(Long categoryId);

    Category deletePermanent(Long categoryId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}