package com.ajit.account.utils;

import com.ajit.account.controller.model.CurrentAccountModel;
import com.ajit.account.dto.CurrentAccountDTO;
import com.ajit.account.dto.CustomerDTO;
import com.ajit.account.entity.CurrentAccount;
import com.ajit.account.entity.Customer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MockData {
    public static CurrentAccountDTO getCurrentAccountDTO() {
        CurrentAccountDTO currentAccountDTO = new CurrentAccountDTO();
        currentAccountDTO.setCustomerId(10l);
        currentAccountDTO.setBalance(new BigDecimal(100));
        return currentAccountDTO;
    }

    public static CurrentAccountModel getCurrentAccountModel() {
        CurrentAccountModel currentAccountModel = new CurrentAccountModel();
        currentAccountModel.setCustomerId(10l);
        currentAccountModel.setInitialCredit(new BigDecimal(100));
        return currentAccountModel;
    }

    public static CurrentAccount getCurrentAccountEntity() {
        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setId(1l);
        currentAccount.setCustomer(getCustomerEntity());
        currentAccount.setBalance(new BigDecimal(100));
        return currentAccount;
    }

    private static CurrentAccount getCurrentAccountEntityWithOutCustomer() {
        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setId(1l);
        currentAccount.setBalance(new BigDecimal(100));
        return currentAccount;
    }

    public static CustomerDTO getCustomerDTO() {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setName("Ajit");
        customerDTO.setSurname("Giri");
        customerDTO.addCurrentAccount(getCurrentAccountDTO());
        return customerDTO;
    }

    public static Customer getCustomerEntity() {
        Customer customer = new Customer();
        customer.setId(1l);
        customer.setName("ajit");
        customer.setSurname("giri");
        Set<CurrentAccount> currentAccountSet = new HashSet<>();
        currentAccountSet.add(getCurrentAccountEntityWithOutCustomer());
        customer.setCurrentAccounts(currentAccountSet);
        return customer;
    }

    public static List<Customer> getCustomerEntityList() {
        List<Customer> customerList = new ArrayList<>();
        customerList.add(getCustomerEntity());
        return customerList;
    }
}
