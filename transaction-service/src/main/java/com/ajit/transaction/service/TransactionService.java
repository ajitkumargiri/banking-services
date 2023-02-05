package com.ajit.transaction.service;

import com.ajit.transaction.dto.TransactionDTO;

import java.util.List;

/**
 * The interface Transaction service.
 */
public interface TransactionService {

    /**
     * Save transaction transaction dto.
     *
     * @param transactionDTO the transaction dto
     * @return the transaction dto
     */
    TransactionDTO saveTransaction(TransactionDTO transactionDTO);

    /**
     * Find transaction by account id list.
     *
     * @param id the id
     * @return the list
     */
    List<TransactionDTO> findTransactionByAccountId(Long id);

}
