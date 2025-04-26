package com.multi.tenants.api.repository;

import com.multi.tenants.api.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {
    Account findAccountByUsername(String username);

    Account findAccountByEmail(String email);

    Account findAccountByPhone(String phone);

    long countByGroupId(Long id);
}
