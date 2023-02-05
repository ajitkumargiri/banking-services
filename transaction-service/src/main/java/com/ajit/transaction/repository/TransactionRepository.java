package com.ajit.transaction.repository;

import com.ajit.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The interface Transaction repository.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    /**
     * Find by current account id list.
     *
     * @param currentAccountId the current account id
     * @return the list
     */
    List<Transaction> findByCurrentAccountId(Long currentAccountId);
}
