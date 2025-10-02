package com.sanedge.pointofsale.repository.cashier;

import com.sanedge.pointofsale.models.cashier.Cashier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashierCommandRepository extends JpaRepository<Cashier, Long>, CashierCommandRepositoryCustom {
}
