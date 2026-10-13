package com.narender.moneyhub.dto;

import com.narender.moneyhub.domain.MoneyTransfer;
import com.narender.moneyhub.domain.TransferStatus;

public class TransferResponse {

    private Long id;
    private Long fromAccountId;
    private Long toAccountId;
    private long amountCents;
    private TransferStatus status;

    public static TransferResponse from(MoneyTransfer t) {
        TransferResponse r = new TransferResponse();
        r.id = t.getId();
        r.fromAccountId = t.getFromAccountId();
        r.toAccountId = t.getToAccountId();
        r.amountCents = t.getAmountCents();
        r.status = t.getStatus();
        return r;
    }

    public Long getId() { return id; }
    public Long getFromAccountId() { return fromAccountId; }
    public Long getToAccountId() { return toAccountId; }
    public long getAmountCents() { return amountCents; }
    public TransferStatus getStatus() { return status; }
}
