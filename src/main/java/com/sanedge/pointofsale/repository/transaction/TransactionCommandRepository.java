package com.sanedge.pointofsale.repository.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.models.transaction.Transaction;

@Repository
public interface TransactionCommandRepository
        extends JpaRepository<Transaction, Long>, TransactionCommandRepositoryCustom {
}