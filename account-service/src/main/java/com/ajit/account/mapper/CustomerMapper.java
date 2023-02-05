package com.ajit.account.mapper;

import com.ajit.account.dto.CustomerDTO;
import com.ajit.account.entity.Customer;
import org.mapstruct.Mapper;

/**
 * The interface Customer mapper.
 */
@Mapper(componentModel = "spring", uses = CurrentAccountMapper.class)
public interface CustomerMapper {
    /**
     * Map to entity customer.
     *
     * @param dto the dto
     * @return the customer
     */
    Customer mapToEntity(CustomerDTO dto);

    /**
     * Map to dto customer dto.
     *
     * @param entity the entity
     * @return the customer dto
     */
    CustomerDTO mapToDto(Customer entity);

}
