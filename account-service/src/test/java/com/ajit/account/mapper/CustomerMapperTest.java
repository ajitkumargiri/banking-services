package com.ajit.account.mapper;

import com.ajit.account.dto.CustomerDTO;
import com.ajit.account.entity.Customer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.ajit.account.utils.MockData.getCustomerDTO;
import static com.ajit.account.utils.MockData.getCustomerEntity;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
class CustomerMapperTest {

    @Autowired
    private CustomerMapper customerMapper;

    private Customer customer;
    private CustomerDTO customerDTO;

    @BeforeEach
    public void init() {
        customer = getCustomerEntity();
        customerDTO = getCustomerDTO();
    }

    @Test
    void mapToEntity() {
        customer = customerMapper.mapToEntity(customerDTO);
        assertThat(customer).isNotNull();
        assertThat(customer.getId()).isEqualTo(customerDTO.getId());
        assertThat(customer.getName()).isEqualTo(customerDTO.getName());
        assertThat(customer.getSurname()).isEqualTo(customerDTO.getSurname());
        assertThat(customer.getCurrentAccounts().stream().findFirst()).isNotEmpty();
        Assertions.assertThat(customer.getCurrentAccounts().stream().findFirst().get().getId()).isEqualTo(customerDTO.getCurrentAccounts().get(0).getId());
        Assertions.assertThat(customer.getCurrentAccounts().stream().findFirst().get().getBalance()).isEqualTo(customerDTO.getCurrentAccounts().get(0).getBalance());
        Assertions.assertThat(customer.getCurrentAccounts().stream().findFirst().get().getCustomer().getId()).isEqualTo(customerDTO.getCurrentAccounts().get(0).getCustomerId());
    }

    @Test
    void mapToDto() {
        customerDTO = customerMapper.mapToDto(customer);
        assertThat(customerDTO).isNotNull();
        assertThat(customerDTO.getId()).isEqualTo(customer.getId());
        assertThat(customerDTO.getName()).isEqualTo(customer.getName());
        assertThat(customerDTO.getSurname()).isEqualTo(customer.getSurname());
        assertThat(customerDTO.getCurrentAccounts().get(0).getId()).isEqualTo(customer.getCurrentAccounts().stream().findFirst().get().getId());
        assertThat(customerDTO.getCurrentAccounts().get(0).getBalance()).isEqualTo(customer.getCurrentAccounts().stream().findFirst().get().getBalance());

    }
}