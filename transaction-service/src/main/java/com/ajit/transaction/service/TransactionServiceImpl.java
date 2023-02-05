package com.ajit.transaction.service;

import com.ajit.transaction.dto.TransactionDTO;
import com.ajit.transaction.entity.Transaction;
import com.ajit.transaction.mapper.TransactionMapper;
import com.ajit.transaction.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Transaction service.
 */
@Slf4j
@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    /**
     * Instantiates a new Transaction service.
     *
     * @param transactionRepository the transaction repository
     * @param transactionMapper     the transaction mapper
     */
    public TransactionServiceImpl(TransactionRepository transactionRepository, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public TransactionDTO saveTransaction(TransactionDTO transactionDTO) {
        log.debug("Call to save Transaction : {}", transactionDTO);
        Transaction transaction = transactionMapper.mapToEntity(transactionDTO);
        transaction = transactionRepository.save(transaction);
        return transactionMapper.mapToDto(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> findTransactionByAccountId(Long currentAccountId) {
        log.debug("Call to get Transaction by currentAccountId : {}", currentAccountId);
        return transactionRepository.findByCurrentAccountId(currentAccountId).stream().map(transactionMapper::mapToDto).collect(Collectors.toCollection(LinkedList::new));
    }
}
