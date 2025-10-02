package com.sanedge.pointofsale.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.models.Product;

@Repository
public interface ProductCommandRepository extends JpaRepository<Product, Long>, ProductCommandRepositoryCustom {
}