package com.ajit.account.service;

import com.ajit.account.controller.model.CurrentAccountModel;
import com.ajit.account.dto.CurrentAccountDTO;

import java.util.Optional;

/**
 * The interface Current account service.
 */
public interface CurrentAccountService {

    // CurrentAccountDTO saveCurrentAccount(CurrentAccountDTO currentAccountDTO);

    /**
     * Save current account current account dto.
     *
     * @param currentAccountModel the current account model
     * @return the current account dto
     */
    CurrentAccountDTO saveCurrentAccount(CurrentAccountModel currentAccountModel);

    /**
     * Find current account by id optional.
     *
     * @param id the id
     * @return the optional
     */
    Optional<CurrentAccountDTO> findCurrentAccountById(Long id);

    /**
     * Update current account current account dto.
     *
     * @param currentAccountDTO the current account dto
     * @return the current account dto
     */
    CurrentAccountDTO updateCurrentAccount(CurrentAccountDTO currentAccountDTO);
}
