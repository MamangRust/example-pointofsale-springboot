package com.sanedge.pointofsale.repository.transaction;

import com.sanedge.pointofsale.models.transaction.Transaction;

public interface TransactionCommandRepositoryCustom {
    Transaction trashed(Long transactionId);

    Transaction restore(Long transactionId);

    Transaction deletePermanent(Long transactionId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}