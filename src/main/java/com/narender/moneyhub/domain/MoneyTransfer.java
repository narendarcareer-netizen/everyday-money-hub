package com.narender.moneyhub.domain;

import jakarta.persistence.*;
import java.time.Instant;

/** Immutable record of one money movement between two accounts in the hub. */
@Entity
@Table(name = "money_transfers")
public class MoneyTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long fromAccountId;

    @Column(nullable = false)
    private Long toAccountId;

    @Column(nullable = false)
    private long amountCents;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected MoneyTransfer() {
    }

    public MoneyTransfer(Long fromAccountId, Long toAccountId, long amountCents, TransferStatus status) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amountCents = amountCents;
        this.status = status;
    }

    public Long getId() { return id; }
    public Long getFromAccountId() { return fromAccountId; }
    public Long getToAccountId() { return toAccountId; }
    public long getAmountCents() { return amountCents; }
    public TransferStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
