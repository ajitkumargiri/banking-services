package com.ajit.transaction.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * The type Transaction dto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO implements Serializable {

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
