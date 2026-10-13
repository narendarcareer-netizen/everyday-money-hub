package com.narender.moneyhub.service;

import com.narender.moneyhub.domain.*;
import com.narender.moneyhub.dto.CreateAccountRequest;
import com.narender.moneyhub.dto.CreateTransferRequest;
import com.narender.moneyhub.dto.OverviewResponse;
import com.narender.moneyhub.events.MoneyHubEventPublisher;
import com.narender.moneyhub.repo.AccountRepository;
import com.narender.moneyhub.repo.MoneyTransferRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MoneyHubServiceTest {

    @Autowired
    private MoneyHubService service;

    @Autowired
    private AccountRepository accounts;

    @MockBean
    private MoneyHubEventPublisher events; // events are a side effect; not under test here

    private Account account(String owner, AccountType type, long balanceCents, Long limit) {
        CreateAccountRequest req = new CreateAccountRequest();
        req.setOwnerName(owner);
        req.setType(type);
        req.setOpeningBalanceCents(balanceCents);
        req.setCreditLimitCents(limit);
        return service.createAccount(req);
    }

    private CreateTransferRequest transfer(Long from, Long to, long cents) {
        CreateTransferRequest req = new CreateTransferRequest();
        req.setFromAccountId(from);
        req.setToAccountId(to);
        req.setAmountCents(cents);
        return req;
    }

    @Test
    void transferMovesMoneyBetweenAssetAccounts() {
        Account checking = account("Test", AccountType.CHECKING, 100_00, null);
        Account savings = account("Test", AccountType.SAVINGS, 0, null);

        service.transfer(transfer(checking.getId(), savings.getId(), 40_00));

        assertEquals(60_00, service.getAccount(checking.getId()).getBalanceCents());
        assertEquals(40_00, service.getAccount(savings.getId()).getBalanceCents());
    }

    @Test
    void transferToCreditCardIsAPayment() {
        Account checking = account("Test", AccountType.CHECKING, 100_00, null);
        Account card = account("Test", AccountType.CREDIT_CARD, 80_00, 500_00L);

        service.transfer(transfer(checking.getId(), card.getId(), 30_00));

        assertEquals(70_00, service.getAccount(checking.getId()).getBalanceCents());
        assertEquals(50_00, service.getAccount(card.getId()).getBalanceCents()); // owed went down
    }

    @Test
    void cashAdvanceCannotExceedCreditLimit() {
        Account card = account("Test", AccountType.CREDIT_CARD, 480_00, 500_00L);
        Account checking = account("Test", AccountType.CHECKING, 0, null);

        assertThrows(InsufficientFundsException.class,
                () -> service.transfer(transfer(card.getId(), checking.getId(), 50_00)));
    }

    @Test
    void insufficientFundsRejected() {
        Account checking = account("Test", AccountType.CHECKING, 10_00, null);
        Account savings = account("Test", AccountType.SAVINGS, 0, null);

        assertThrows(InsufficientFundsException.class,
                () -> service.transfer(transfer(checking.getId(), savings.getId(), 99_99)));
    }

    @Test
    void sameAccountTransferRejected() {
        Account checking = account("Test", AccountType.CHECKING, 100_00, null);

        assertThrows(IllegalArgumentException.class,
                () -> service.transfer(transfer(checking.getId(), checking.getId(), 10_00)));
    }

    @Test
    void creditCardRequiresLimit() {
        CreateAccountRequest req = new CreateAccountRequest();
        req.setOwnerName("Test");
        req.setType(AccountType.CREDIT_CARD);
        req.setOpeningBalanceCents(0);

        assertThrows(IllegalArgumentException.class, () -> service.createAccount(req));
    }

    @Test
    void overviewRollsUpNetWorth() {
        account("Jane", AccountType.CHECKING, 5_000_00, null);
        account("Jane", AccountType.INVESTMENT, 20_000_00, null);
        account("Jane", AccountType.CREDIT_CARD, 1_200_00, 10_000_00L);

        OverviewResponse view = service.overview("Jane");

        assertEquals(3, view.getAccounts().size());
        assertEquals(5_000_00, view.getTotalCashCents());
        assertEquals(20_000_00, view.getTotalInvestmentsCents());
        assertEquals(1_200_00, view.getTotalCreditOwedCents());
        assertEquals(23_800_00, view.getNetWorthCents());
    }
}
