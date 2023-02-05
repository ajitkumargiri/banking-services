package com.ajit.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * The type Transaction model.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionModel implements Serializable {

    private BigDecimal amount;

}
