package com.narender.moneyhub.repo;

import com.narender.moneyhub.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByOwnerName(String ownerName);
}
