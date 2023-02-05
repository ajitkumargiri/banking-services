package com.ajit.account.controller;

import com.ajit.account.controller.model.TransactionModel;
import com.ajit.account.dto.TransactionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * The interface Transaction service client.
 */
@FeignClient("transaction-service")
public interface TransactionServiceClient {

    /**
     * Create transaction transaction dto.
     *
     * @param currentAccountId the current account id
     * @param transactionModel the transaction model
     * @return the transaction dto
     */
    @RequestMapping(method = RequestMethod.POST, value = "/accounts/{currentAccountId}/transactions", consumes = "application/json")
    TransactionDTO createTransaction(@PathVariable Long currentAccountId, @RequestBody TransactionModel transactionModel);

    /**
     * Gets all transactions by account id.
     *
     * @param currentAccountId the current account id
     * @return the all transactions by account id
     */
    @RequestMapping(method = RequestMethod.GET, value = "/accounts/{currentAccountId}/transactions", consumes = "application/json")
    List<TransactionDTO> getAllTransactionsByAccountId(@PathVariable Long currentAccountId);
}