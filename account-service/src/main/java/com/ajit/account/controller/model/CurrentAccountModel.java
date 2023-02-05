package com.ajit.account.controller.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

import static com.ajit.account.validation.ValidationConstant.INITIAL_BALANCE_FOR_THE_ACCOUNT_CANNOT_BE_A_NEGATIVE_VALUE;
import static com.ajit.account.validation.ValidationConstant.INITIAL_BALANCE_IS_REQUIRED_AND_CANNOT_BE_EMPTY;
import static com.ajit.account.validation.ValidationConstant.INVALID_CUSTOMER_IN_ACCOUNT_MSG;

/**
 * The type Current account model.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentAccountModel {
    @NotNull(message = INVALID_CUSTOMER_IN_ACCOUNT_MSG)
    private Long customerId;
    @NotNull(message = INITIAL_BALANCE_IS_REQUIRED_AND_CANNOT_BE_EMPTY)
    @Min(value = 0, message = INITIAL_BALANCE_FOR_THE_ACCOUNT_CANNOT_BE_A_NEGATIVE_VALUE)
    private BigDecimal initialCredit;
}