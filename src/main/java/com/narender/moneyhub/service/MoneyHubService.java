package com.narender.moneyhub.service;

import com.narender.moneyhub.domain.*;
import com.narender.moneyhub.dto.CreateAccountRequest;
import com.narender.moneyhub.dto.CreateTransferRequest;
import com.narender.moneyhub.dto.OverviewResponse;
import com.narender.moneyhub.events.MoneyHubEvent;
import com.narender.moneyhub.events.MoneyHubEventPublisher;
import com.narender.moneyhub.repo.AccountRepository;
import com.narender.moneyhub.repo.MoneyTransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MoneyHubService {

    private final AccountRepository accounts;
    private final MoneyTransferRepository transfers;
    private final MoneyHubEventPublisher events;

    public MoneyHubService(AccountRepository accounts,
                           MoneyTransferRepository transfers,
                           MoneyHubEventPublisher events) {
        this.accounts = accounts;
        this.transfers = transfers;
        this.events = events;
    }

    @Transactional
    public Account createAccount(CreateAccountRequest req) {
        if (req.getType() == AccountType.CREDIT_CARD && req.getCreditLimitCents() == null) {
            throw new IllegalArgumentException("creditLimitCents is required for CREDIT_CARD accounts");
        }
        Account account = new Account(
                req.getOwnerName(), req.getType(),
                req.getOpeningBalanceCents(), req.getCreditLimitCents());
        Account saved = accounts.save(account);
        events.publish(new MoneyHubEvent(MoneyHubEvent.Type.ACCOUNT_CREATED,
                "account-" + saved.getId(),
                "{\"accountId\":" + saved.getId() + ",\"type\":\"" + saved.getType() + "\"}"));
        return saved;
    }

    public List<Account> listAccounts() {
        return accounts.findAll();
    }

    public Account getAccount(Long id) {
        return accounts.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
    }

    /**
     * Moves money between two accounts. Asset accounts (checking/savings/
     * investment) are debited/credited directly. A credit card as destination
     * is a payment (owed goes down); as source it is a cash advance (owed goes
     * up, capped by the credit limit).
     */
    @Transactional
    public MoneyTransfer transfer(CreateTransferRequest req) {
        if (req.getFromAccountId().equals(req.getToAccountId())) {
            throw new IllegalArgumentException("fromAccountId and toAccountId must differ");
        }
        Account from = getAccount(req.getFromAccountId());
        Account to = getAccount(req.getToAccountId());
        long amount = req.getAmountCents();

        events.publish(new MoneyHubEvent(MoneyHubEvent.Type.TRANSFER_INITIATED,
                "transfer-" + from.getId() + "-" + to.getId(),
                "{\"from\":" + from.getId() + ",\"to\":" + to.getId() + ",\"amountCents\":" + amount + "}"));

        debit(from, amount);
        credit(to, amount);

        MoneyTransfer saved = transfers.save(
                new MoneyTransfer(from.getId(), to.getId(), amount, TransferStatus.COMPLETED));
        events.publish(new MoneyHubEvent(MoneyHubEvent.Type.TRANSFER_COMPLETED,
                "transfer-" + saved.getId(),
                "{\"transferId\":" + saved.getId() + ",\"amountCents\":" + amount + "}"));
        return saved;
    }

    private void debit(Account account, long amount) {
        if (account.getType() == AccountType.CREDIT_CARD) {
            long newOwed = account.getBalanceCents() + amount; // cash advance increases what is owed
            if (account.getCreditLimitCents() != null && newOwed > account.getCreditLimitCents()) {
                throw new InsufficientFundsException(
                        "Cash advance of " + amount + " cents would exceed the credit limit");
            }
            account.setBalanceCents(newOwed);
        } else {
            if (account.getBalanceCents() < amount) {
                throw new InsufficientFundsException(
                        "Account " + account.getId() + " has insufficient funds");
            }
            account.setBalanceCents(account.getBalanceCents() - amount);
        }
    }

    private void credit(Account account, long amount) {
        if (account.getType() == AccountType.CREDIT_CARD) {
            // payment: never let "owed" go below zero
            account.setBalanceCents(Math.max(0, account.getBalanceCents() - amount));
        } else {
            account.setBalanceCents(account.getBalanceCents() + amount);
        }
    }

    public List<MoneyTransfer> listTransfers() {
        return transfers.findAll();
    }

    /** The unified "everyday" view: all of one client's accounts plus rolled-up totals. */
    @Transactional(readOnly = true)
    public OverviewResponse overview(String owner) {
        List<Account> owned = accounts.findByOwnerName(owner);
        OverviewResponse view = new OverviewResponse();
        view.setOwner(owner);
        view.setAccounts(owned.stream()
                .map(com.narender.moneyhub.dto.AccountResponse::from)
                .toList());
        long cash = 0, investments = 0, creditOwed = 0;
        for (Account a : owned) {
            switch (a.getType()) {
                case CHECKING, SAVINGS -> cash += a.getBalanceCents();
                case INVESTMENT -> investments += a.getBalanceCents();
                case CREDIT_CARD -> creditOwed += a.getBalanceCents();
            }
        }
        view.setTotalCashCents(cash);
        view.setTotalInvestmentsCents(investments);
        view.setTotalCreditOwedCents(creditOwed);
        view.setNetWorthCents(cash + investments - creditOwed);
        return view;
    }
}
