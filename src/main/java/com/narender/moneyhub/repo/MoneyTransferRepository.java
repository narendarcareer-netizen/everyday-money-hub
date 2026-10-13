package com.narender.moneyhub.repo;

import com.narender.moneyhub.domain.MoneyTransfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoneyTransferRepository extends JpaRepository<MoneyTransfer, Long> {
}
