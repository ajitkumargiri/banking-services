package com.ajit.account.service;

import com.ajit.account.dto.CustomerDTO;
import com.ajit.account.entity.Customer;
import com.ajit.account.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.ajit.account.utils.MockData.getCustomerDTO;
import static com.ajit.account.utils.MockData.getCustomerEntity;
import static com.ajit.account.utils.MockData.getCustomerEntityList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class CustomerServiceImplTest {

    @MockBean
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerService customerService;

    private Customer customer;
    private CustomerDTO customerDTO;

    @BeforeEach
    public void init() {
        customer = getCustomerEntity();
        customerDTO = getCustomerDTO();
    }

    @Test
    @Transactional
    void save() {
        when(customerRepository.save(Mockito.any(Customer.class))).thenReturn(customer);
        CustomerDTO savedCustomerDTO = customerService.save(customerDTO);
        assertThat(savedCustomerDTO.getId().longValue()).isEqualTo(1l);
    }

    @Test
    @Transactional
    void findCustomerById() {
        when(customerRepository.findById(1l)).thenReturn(Optional.of(customer));
        Optional<CustomerDTO> customerDTOOptional = customerService.findCustomerById(1l);
        assertThat(customerDTOOptional).isPresent();
        assertThat(customerDTOOptional.orElse(null).getId()).isEqualTo(1l);
        assertThat(customerDTOOptional.orElse(null).getName()).isEqualTo("ajit");
        assertThat(customerDTOOptional.orElse(null).getSurname()).isEqualTo("giri");
    }

    @Test
    @Transactional
    void findAll() {
        when(customerRepository.findAll()).thenReturn(getCustomerEntityList());
        List<CustomerDTO> customerDTOList = customerService.findAll();
        assertThat(customerDTOList).isNotEmpty();
    }
}