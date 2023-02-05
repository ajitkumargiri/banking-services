package com.ajit.transaction.mapper;

import com.ajit.transaction.dto.TransactionDTO;
import com.ajit.transaction.entity.Transaction;
import org.mapstruct.Mapper;

/**
 * The interface Transaction mapper.
 */
@Mapper(componentModel = "spring")
public interface TransactionMapper {
    /**
     * Map to entity transaction.
     *
     * @param dto the dto
     * @return the transaction
     */
    Transaction mapToEntity(TransactionDTO dto);

    /**
     * Map to dto transaction dto.
     *
     * @param entity the entity
     * @return the transaction dto
     */
    TransactionDTO mapToDto(Transaction entity);
}
