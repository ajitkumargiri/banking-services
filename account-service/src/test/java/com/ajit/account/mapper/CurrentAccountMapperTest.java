package com.ajit.account.mapper;

import com.ajit.account.controller.model.CurrentAccountModel;
import com.ajit.account.dto.CurrentAccountDTO;
import com.ajit.account.entity.CurrentAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static com.ajit.account.utils.MockData.getCurrentAccountDTO;
import static com.ajit.account.utils.MockData.getCurrentAccountEntity;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CurrentAccountMapperTest {

    @Autowired
    private CurrentAccountMapper currentAccountMapper;

    private CurrentAccount currentAccount;
    private CurrentAccountDTO currentAccountDTO;

    @BeforeEach
    public void init() {
        currentAccount = getCurrentAccountEntity();
        currentAccountDTO = getCurrentAccountDTO();
    }

    @Test
    void mapToEntity() {
        currentAccount = currentAccountMapper.mapToEntity(currentAccountDTO);
        assertThat(currentAccount).isNotNull();
        assertThat(currentAccount.getId()).isEqualTo(currentAccountDTO.getId());
        assertThat(currentAccount.getBalance()).isEqualTo(currentAccountDTO.getBalance());
        assertThat(currentAccount.getCustomer().getId()).isEqualTo(currentAccountDTO.getCustomerId());
    }

    @Test
    void mapToDto() {
        currentAccountDTO = currentAccountMapper.mapToDto(currentAccount);
        assertThat(currentAccountDTO).isNotNull();
        assertThat(currentAccountDTO.getId()).isEqualTo(currentAccount.getId());
        assertThat(currentAccountDTO.getBalance()).isEqualTo(currentAccount.getBalance());
        assertThat(currentAccountDTO.getCustomerId()).isEqualTo(currentAccount.getCustomer().getId());
    }

    @Test
    void mapModelToDto() {
        CurrentAccountModel currentAccountModel = new CurrentAccountModel(1l, BigDecimal.valueOf(100));
        currentAccount = currentAccountMapper.mapModelToEntity(currentAccountModel);
        assertThat(currentAccount).isNotNull();
        assertThat(currentAccount.getBalance()).isEqualTo(BigDecimal.valueOf(100));
        assertThat(currentAccount.getCustomer().getId()).isEqualTo(1l);
    }
}