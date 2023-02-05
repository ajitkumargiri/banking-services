package com.ajit.account.service;

import com.ajit.account.dto.CustomerDTO;
import com.ajit.account.entity.Customer;
import com.ajit.account.mapper.CustomerMapper;
import com.ajit.account.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The type Customer service.
 */
@Slf4j
@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    /**
     * Instantiates a new Customer service.
     *
     * @param customerRepository the customer repository
     * @param customerMapper     the customer mapper
     */
    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    public CustomerDTO save(CustomerDTO customerDTO) {
        log.debug("Call to save Customer : {}", customerDTO);
        Customer customer = customerMapper.mapToEntity(customerDTO);
        customer = customerRepository.save(customer);
        return customerMapper.mapToDto(customer);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable("customers")
    public List<CustomerDTO> findAll() {
        log.debug("Call to get all Customers");
        List<CustomerDTO> customerDTOS = customerRepository.findAll().stream().map(customerMapper::mapToDto).collect(Collectors.toCollection(ArrayList::new));
        return customerDTOS;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerDTO> findCustomerById(Long id) {
        log.debug("Call to get Customer with ID: {}", id);
        return customerRepository.findById(id).map(customerMapper::mapToDto);
    }
}
