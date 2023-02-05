package com.ajit.account.controller;

import com.ajit.account.entity.Customer;
import com.ajit.account.mapper.CustomerMapper;
import com.ajit.account.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static com.ajit.account.utils.MockData.getCustomerEntity;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WebAppConfiguration
class CustomerControllerTest {

    @MockBean
    private CustomerService customerService;
   @MockBean
    private TransactionServiceClient transactionServiceClient;
    @Autowired
    private CustomerMapper customerMapper;

    private MockMvc mockMvc;

    private Customer customer;

    @BeforeEach
    public void init(WebApplicationContext context) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        customer = getCustomerEntity();
    }

    @Test
    @Transactional
    void getCustomerInfo() throws Exception {
        given(customerService.findCustomerById(1l)).willReturn(Optional.of(customerMapper.mapToDto(customer)));
        given(transactionServiceClient.getAllTransactionsByAccountId(1l)).willReturn(Mockito.anyList());
        mockMvc.perform(get("/customers/1")
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType("application/json"))
                        .andExpect(jsonPath("$.id").value(1l))
                        .andExpect(jsonPath("$.name").value("ajit"));
    }

    @Test
    @Transactional
    void getCustomerNotFoundError() throws Exception {
        given(customerService.findCustomerById(1l)).willReturn(Optional.empty());
        given(transactionServiceClient.getAllTransactionsByAccountId(1l)).willReturn(Mockito.anyList());
        mockMvc.perform(get("/customers/1")
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isNotFound());
    }

}