package com.ajit.account.service;

import com.ajit.account.dto.CurrentAccountDTO;
import com.ajit.account.entity.CurrentAccount;
import com.ajit.account.repository.CurrentAccountRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import static com.ajit.account.utils.MockData.getCurrentAccountEntity;
import static com.ajit.account.utils.MockData.getCurrentAccountModel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class CurrentAccountServiceImplTest {

    @MockBean
    private CurrentAccountRepository currentAccountRepository;

    @Autowired
    private CurrentAccountService currentAccountService;

    @Test
    @Transactional
    void saveCurrentAccount() {
        when(currentAccountRepository.save(Mockito.any(CurrentAccount.class))).thenReturn(getCurrentAccountEntity());
        CurrentAccountDTO savedCurrentAccountDTO = currentAccountService.saveCurrentAccount(getCurrentAccountModel());
        assertThat(savedCurrentAccountDTO.getId().longValue()).isEqualTo(1l);
    }

}