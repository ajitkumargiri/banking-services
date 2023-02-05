package com.ajit.account.controller;

import com.ajit.account.controller.model.CurrentAccountModel;
import com.ajit.account.controller.model.TransactionModel;
import com.ajit.account.dto.CurrentAccountDTO;
import com.ajit.account.dto.TransactionDTO;
import com.ajit.account.exception.BadRequestException;
import com.ajit.account.exception.InsufficientBalanceException;
import com.ajit.account.mapper.CurrentAccountMapper;
import com.ajit.account.service.CurrentAccountService;
import com.ajit.account.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;

import static com.ajit.account.validation.ValidationConstant.INITIAL_BALANCE_FOR_THE_ACCOUNT_CANNOT_BE_A_NEGATIVE_VALUE;
import static com.ajit.account.validation.ValidationConstant.INSUFFICIENT_BALANCE;
import static com.ajit.account.validation.ValidationConstant.INVALID_ACCOUNT_MSG;
import static com.ajit.account.validation.ValidationConstant.INVALID_CUSTOMER_IN_ACCOUNT_MSG;
import static com.ajit.account.validation.ValidationConstant.TRANSACTION_BALANCE_FOR_THE_ACCOUNT_CANNOT_BE_A_ZERO;

/**
 * The type Current account controller.
 */
@Slf4j
@RestController
public class CurrentAccountController {

    private final CustomerService customerService;
    private final CurrentAccountService currentAccountService;
    private final TransactionServiceClient transactionServiceClient;

    /**
     * Instantiates a new Current account controller.
     *
     * @param customerService          the customer service
     * @param currentAccountService    the current account service
     * @param currentAccountMapper     the current account mapper
     * @param transactionServiceClient the transaction service client
     */
    public CurrentAccountController(CustomerService customerService, CurrentAccountService currentAccountService, CurrentAccountMapper currentAccountMapper,
                    TransactionServiceClient transactionServiceClient) {
        this.customerService = customerService;
        this.currentAccountService = currentAccountService;
        this.transactionServiceClient = transactionServiceClient;
    }

    /**
     * Create current account response entity.
     *
     * @param currentAccountModel the current account model
     * @return the response entity
     * @throws BadRequestException the bad request exception
     */
    @Operation(summary = "New account will be opened connected which accepts the customer information (customerID, initialCredit)")
    @PostMapping("/customers/account")
    public ResponseEntity<CurrentAccountDTO> createCurrentAccount(@Valid @RequestBody CurrentAccountModel currentAccountModel)
                    throws BadRequestException {
        log.debug("Call to the API to create a new Current Account : {}", currentAccountModel);
        customerService.findCustomerById(currentAccountModel.getCustomerId()).orElseThrow(() -> new BadRequestException(INVALID_CUSTOMER_IN_ACCOUNT_MSG));
        if (currentAccountModel.getInitialCredit().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException(INITIAL_BALANCE_FOR_THE_ACCOUNT_CANNOT_BE_A_NEGATIVE_VALUE);
        }
        final CurrentAccountDTO result = currentAccountService.saveCurrentAccount(currentAccountModel);
        // calling transaction-service to create a new transaction
        TransactionDTO savedTransactionDTO = transactionServiceClient.createTransaction(result.getId(), new TransactionModel(currentAccountModel.getInitialCredit()));
        result.addTransaction(savedTransactionDTO);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    /**
     * Create customer transaction response entity.
     *
     * @param accountId        the account id
     * @param transactionModel the transaction model
     * @return the response entity
     * @throws BadRequestException the bad request exception
     */
    @PostMapping("/customers/account/{accountId}/transactions")
    public ResponseEntity<CurrentAccountDTO> createCustomerTransaction(@PathVariable Long accountId, @Valid @RequestBody TransactionModel transactionModel)
                    throws BadRequestException {
        log.debug("Call to the API to create a new transaction for Current Account with amount: {}", transactionModel);
        CurrentAccountDTO currentAccountDTO = currentAccountService.findCurrentAccountById(accountId).orElseThrow(() -> new BadRequestException(INVALID_ACCOUNT_MSG));
        if (transactionModel.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            throw new BadRequestException(TRANSACTION_BALANCE_FOR_THE_ACCOUNT_CANNOT_BE_A_ZERO);
        }
        if (currentAccountDTO.getBalance().add(transactionModel.getAmount()).compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientBalanceException(INSUFFICIENT_BALANCE);
        }
        transactionServiceClient.createTransaction(accountId, new TransactionModel(transactionModel.getAmount()));
        currentAccountDTO.setBalance(currentAccountDTO.getBalance().add(transactionModel.getAmount()));
        currentAccountDTO.setTransactions(transactionServiceClient.getAllTransactionsByAccountId(currentAccountDTO.getId()));
        currentAccountService.updateCurrentAccount(currentAccountDTO);
        return new ResponseEntity<>(currentAccountDTO, HttpStatus.CREATED);
    }
}