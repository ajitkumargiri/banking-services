package com.ajit.account.controller;

import com.ajit.account.dto.CustomerDTO;
import com.ajit.account.exception.ResourceNotFoundException;
import com.ajit.account.repository.CustomerRepository;
import com.ajit.account.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * The type Customer controller.
 */
@Slf4j
@RestController
public class CustomerController {
    private final CustomerService customerService;
    private final TransactionServiceClient transactionServiceClient;

    /**
     * Instantiates a new Customer controller.
     *
     * @param customerService          the customer service
     * @param customerRepository       the customer repository
     * @param transactionServiceClient the transaction service client
     */
    public CustomerController(CustomerService customerService, CustomerRepository customerRepository, TransactionServiceClient transactionServiceClient) {
        this.customerService = customerService;
        this.transactionServiceClient = transactionServiceClient;
    }

    /**
     * Create customer response entity.
     *
     * @param customerDTO the customer dto
     * @return the response entity
     * @throws URISyntaxException the uri syntax exception
     */
    @PostMapping("/customers")
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) throws URISyntaxException {
        log.debug("REST Call to save Customer : {}", customerDTO);
        CustomerDTO savedCustomerDTO = customerService.save(customerDTO);
        return new ResponseEntity<>(savedCustomerDTO, HttpStatus.CREATED);

    }

    /**
     * Gets customer info.
     *
     * @param customerId the customer id
     * @return the customer info
     */
    @Operation(summary = "Endpoint will output the user information showing Name, Surname, balance, and transactions of the accounts")
    @GetMapping("/customers/{customerId}")
    public ResponseEntity<CustomerDTO> getCustomerInfo(@PathVariable Long customerId) {
        log.debug("api Call to get Customer : {}", customerId);
        Optional<CustomerDTO> customerDTOOptional = customerService.findCustomerById(customerId);
        final CustomerDTO customerDTO = customerDTOOptional.orElseThrow(() -> new ResourceNotFoundException("Customer " + customerId + " not found"));
        // calling transaction-service to fetch all transactions for specific account
        customerDTO.getCurrentAccounts().forEach(currentAccountDTO -> {
            currentAccountDTO.setTransactions(transactionServiceClient.getAllTransactionsByAccountId(currentAccountDTO.getId()));
        });

        return ResponseEntity.ok(customerDTO);
    }

    /**
     * Gets all customers.
     *
     * @return the all customers
     */
    @GetMapping("/customers")
    public List<CustomerDTO> getAllCustomers() {
        log.debug("REST Call to get all Customers");
        return customerService.findAll();
    }
}