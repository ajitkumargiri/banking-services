package com.ajit.account.mapper;

import com.ajit.account.controller.model.CurrentAccountModel;
import com.ajit.account.dto.CurrentAccountDTO;
import com.ajit.account.entity.CurrentAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * The interface Current account mapper.
 */
@Mapper(componentModel = "spring")
public interface CurrentAccountMapper {
    /**
     * Map to entity current account.
     *
     * @param dto the dto
     * @return the current account
     */
    @Mapping(source = "customerId", target = "customer.id")
    CurrentAccount mapToEntity(CurrentAccountDTO dto);

    /**
     * Map to dto current account dto.
     *
     * @param entity the entity
     * @return the current account dto
     */
    @Mapping(source = "customer.id", target = "customerId")
    CurrentAccountDTO mapToDto(CurrentAccount entity);

    /**
     * Map model to entity current account.
     *
     * @param model the model
     * @return the current account
     */
    @Mapping(source = "customerId", target = "customer.id")
    @Mapping(source = "initialCredit", target = "balance")
    CurrentAccount mapModelToEntity(CurrentAccountModel model);
}
