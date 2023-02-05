package com.ajit.transaction.controller;

import com.ajit.transaction.dto.TransactionDTO;
import com.ajit.transaction.model.TransactionModel;
import com.ajit.transaction.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WebAppConfiguration
class TransactionControllerTest {

    @MockBean
    private TransactionService transactionService;
    private MockMvc mockMvc;

    @BeforeEach
    public void init(WebApplicationContext context) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @Transactional
    void createTransaction() throws Exception {
        given(this.transactionService.saveTransaction(Mockito.mock(TransactionDTO.class))).willReturn(new TransactionDTO(2l, new BigDecimal(210), 3l));
        TransactionModel model = new TransactionModel(new BigDecimal(200));
        ObjectMapper objectMapper = new ObjectMapper();
        String modelJson = objectMapper.writeValueAsString(model);

        mockMvc.perform(post("/accounts/1/transactions")
                                        .content(modelJson).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isCreated());
    }

    @Test
    @Transactional
    void getAllTransactionsByAccountId() throws Exception {
        List<TransactionDTO> transactions = new ArrayList<>();
        transactions.add(new TransactionDTO(1l, new BigDecimal(110), 2l));
        given(this.transactionService.findTransactionByAccountId(1l)).willReturn(transactions);
        mockMvc.perform(get("/accounts/1/transactions")
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType("application/json"));
    }
}