package com.sanedge.pointofsale.repository.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.models.category.Category;

@Repository
public interface CategoryCommandRepository extends JpaRepository<Category, Long>, CategoryCommandRepositoryCustom {
}
