package com.sanedge.pointofsale.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.models.order.Order;

@Repository
public interface OrderCommandRepository extends JpaRepository<Order, Long>, OrderCommandRepositoryCustom {
}