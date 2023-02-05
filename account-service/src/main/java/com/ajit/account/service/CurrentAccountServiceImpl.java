package com.ajit.account.service;

import com.ajit.account.controller.model.CurrentAccountModel;
import com.ajit.account.dto.CurrentAccountDTO;
import com.ajit.account.entity.CurrentAccount;
import com.ajit.account.mapper.CurrentAccountMapper;
import com.ajit.account.repository.CurrentAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * The type Current account service.
 */
@Slf4j
@Service
@Transactional
public class CurrentAccountServiceImpl implements CurrentAccountService {

    private final CurrentAccountRepository currentAccountRepository;

    private final CurrentAccountMapper currentAccountMapper;

    /**
     * Instantiates a new Current account service.
     *
     * @param currentAccountRepository the current account repository
     * @param currentAccountMapper     the current account mapper
     */
    public CurrentAccountServiceImpl(CurrentAccountRepository currentAccountRepository, CurrentAccountMapper currentAccountMapper) {
        this.currentAccountRepository = currentAccountRepository;
        this.currentAccountMapper = currentAccountMapper;
    }

    @Override
    public CurrentAccountDTO saveCurrentAccount(CurrentAccountModel currentAccountModel) {
        log.debug("Call to save currentAccount : {}", currentAccountModel);
        CurrentAccount currentAccount = currentAccountMapper.mapModelToEntity(currentAccountModel);
        currentAccount = currentAccountRepository.save(currentAccount);
        return currentAccountMapper.mapToDto(currentAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CurrentAccountDTO> findCurrentAccountById(Long id) {
        log.debug("Call to get currentAccount with ID: {}", id);
        return currentAccountRepository.findById(id).map(currentAccountMapper::mapToDto);

    }

    @Override
    public CurrentAccountDTO updateCurrentAccount(CurrentAccountDTO currentAccountDTO) {
        log.debug("Call to update CurrentAccount : {}", currentAccountDTO);
        CurrentAccount currentAccount = currentAccountMapper.mapToEntity(currentAccountDTO);
        currentAccount = currentAccountRepository.save(currentAccount);
        return currentAccountMapper.mapToDto(currentAccount);
    }

}
