package com.ajit.account.service;

import com.ajit.account.dto.CustomerDTO;

import java.util.List;
import java.util.Optional;

/**
 * The interface Customer service.
 */
public interface CustomerService {

    /**
     * Save customer dto.
     *
     * @param customerDTO the customer dto
     * @return the customer dto
     */
    CustomerDTO save(CustomerDTO customerDTO);

    /**
     * Find all list.
     *
     * @return the list
     */
    List<CustomerDTO> findAll();

    /**
     * Find customer by id optional.
     *
     * @param id the id
     * @return the optional
     */
    Optional<CustomerDTO> findCustomerById(Long id);

}
