package com.ajit.account.repository;


import com.ajit.account.entity.CurrentAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The interface Current account repository.
 */
@Repository
public interface CurrentAccountRepository extends JpaRepository<CurrentAccount, Long> {}
