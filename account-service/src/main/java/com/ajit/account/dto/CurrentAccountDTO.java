package com.ajit.account.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Current account dto.
 */
@Getter
@Setter
public class CurrentAccountDTO implements Serializable {

    private Long id;
    private BigDecimal balance;
    private Long customerId;
    private List<TransactionDTO> transactions;

    /**
     * Add transaction.
     *
     * @param transactionDTO the transaction dto
     */
    public void addTransaction(TransactionDTO transactionDTO) {
        if (CollectionUtils.isEmpty(transactions)) {
            transactions = new ArrayList<>();
        }
        this.transactions.add(transactionDTO);
    }
}
