package com.ajit.transaction.controller;

import com.ajit.transaction.dto.TransactionDTO;
import com.ajit.transaction.model.TransactionModel;
import com.ajit.transaction.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * The type Transaction controller.
 */
@Slf4j
@RestController
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Instantiates a new Transaction controller.
     *
     * @param transactionService the transaction service
     */
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Create transaction transaction dto.
     *
     * @param currentAccountId the current account id
     * @param transactionModel the transaction model
     * @return the transaction dto
     */
    @PostMapping("/accounts/{currentAccountId}/transactions")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionDTO createTransaction(@PathVariable Long currentAccountId, @RequestBody TransactionModel transactionModel) {
        log.debug("REST call to save Transaction : {} for account: {}", transactionModel, currentAccountId);
        TransactionDTO transactionDTO = new TransactionDTO(transactionModel.getAmount(), currentAccountId);
        return transactionService.saveTransaction(transactionDTO);
    }

    /**
     * Gets all transactions by account id.
     *
     * @param currentAccountId the current account id
     * @return the all transactions by account id
     */
    @GetMapping("/accounts/{currentAccountId}/transactions")
    public List<TransactionDTO> getAllTransactionsByAccountId(@PathVariable Long currentAccountId) {
        log.debug("REST call to get all Transactions");
        return transactionService.findTransactionByAccountId(currentAccountId);
    }
}