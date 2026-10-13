package com.narender.moneyhub.domain;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * One money account in the hub. Money is stored in minor units (cents) to
 * avoid floating-point rounding — standard practice for financial ledgers.
 *
 * Semantics of {@code balanceCents} depend on the type:
 *  - CHECKING / SAVINGS / INVESTMENT: asset value owned.
 *  - CREDIT_CARD: amount currently owed (a liability). Paying the card down
 *    reduces it; a cash advance increases it, capped by {@code creditLimitCents}.
 */
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ownerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    @Column(nullable = false)
    private long balanceCents;

    /** Only meaningful for CREDIT_CARD; the max owed allowed. */
    private Long creditLimitCents;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Account() {
    }

    public Account(String ownerName, AccountType type, long balanceCents, Long creditLimitCents) {
        this.ownerName = ownerName;
        this.type = type;
        this.balanceCents = balanceCents;
        this.creditLimitCents = creditLimitCents;
    }

    public Long getId() { return id; }
    public String getOwnerName() { return ownerName; }
    public AccountType getType() { return type; }
    public long getBalanceCents() { return balanceCents; }
    public Long getCreditLimitCents() { return creditLimitCents; }
    public AccountStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }

    public void setBalanceCents(long balanceCents) { this.balanceCents = balanceCents; }
    public void setStatus(AccountStatus status) { this.status = status; }
}
