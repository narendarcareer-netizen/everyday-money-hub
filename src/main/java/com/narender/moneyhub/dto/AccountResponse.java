package com.narender.moneyhub.dto;

import com.narender.moneyhub.domain.Account;
import com.narender.moneyhub.domain.AccountStatus;
import com.narender.moneyhub.domain.AccountType;

public class AccountResponse {

    private Long id;
    private String ownerName;
    private AccountType type;
    private long balanceCents;
    private Long creditLimitCents;
    private AccountStatus status;

    public static AccountResponse from(Account a) {
        AccountResponse r = new AccountResponse();
        r.id = a.getId();
        r.ownerName = a.getOwnerName();
        r.type = a.getType();
        r.balanceCents = a.getBalanceCents();
        r.creditLimitCents = a.getCreditLimitCents();
        r.status = a.getStatus();
        return r;
    }

    public Long getId() { return id; }
    public String getOwnerName() { return ownerName; }
    public AccountType getType() { return type; }
    public long getBalanceCents() { return balanceCents; }
    public Long getCreditLimitCents() { return creditLimitCents; }
    public AccountStatus getStatus() { return status; }
}
