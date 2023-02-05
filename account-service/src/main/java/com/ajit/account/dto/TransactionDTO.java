package com.ajit.account.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * The type Transaction dto.
 */
@Data
@NoArgsConstructor
public class TransactionDTO implements Serializable {

    // TODO: We can improve code maintainability and reduce code duplication by moving common classes to a shared module for both services. With only three classes present, it is feasible to duplicate these classes.

    private Long id;

    private BigDecimal amount;

    @JsonIgnore
    private Long currentAccountId;

    /**
     * Instantiates a new Transaction dto.
     *
     * @param amount           the amount
     * @param currentAccountId the current account id
     */
    public TransactionDTO(BigDecimal amount, Long currentAccountId) {
        this.amount = amount;
        this.currentAccountId = currentAccountId;
    }
}
