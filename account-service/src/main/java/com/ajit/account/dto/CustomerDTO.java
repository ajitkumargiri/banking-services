package com.ajit.account.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.ajit.account.validation.ValidationConstant.SHOULD_NOT_BE_EMPTY;
import static com.ajit.account.validation.ValidationConstant.SHOULD_NOT_CONTAIN_ANY_NUMERIC_VALUE_OR_SPECIAL_CHARACTERS;

/**
 * The type Customer dto.
 */
@Getter
@Setter
public class CustomerDTO implements Serializable {

    private Long id;
    @NotEmpty(message = "Name" + SHOULD_NOT_BE_EMPTY)
    @Pattern(regexp = "^[a-zA-Z]*$", message = "Name" + SHOULD_NOT_CONTAIN_ANY_NUMERIC_VALUE_OR_SPECIAL_CHARACTERS)
    private String name;
    @NotEmpty(message = "Surname" + SHOULD_NOT_BE_EMPTY)
    @Pattern(regexp = "^[a-zA-Z]*$", message = "Surname" + SHOULD_NOT_CONTAIN_ANY_NUMERIC_VALUE_OR_SPECIAL_CHARACTERS)
    private String surname;
    private List<CurrentAccountDTO> currentAccounts;

    /**
     * Add current account.
     *
     * @param currentAccountDTO the current account dto
     */
    public void addCurrentAccount(CurrentAccountDTO currentAccountDTO) {
        if (CollectionUtils.isEmpty(currentAccounts)) {
            currentAccounts = new ArrayList<>();
        }
        this.currentAccounts.add(currentAccountDTO);
    }
}
