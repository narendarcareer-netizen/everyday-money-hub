package com.narender.moneyhub.dto;

import com.narender.moneyhub.domain.AccountType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateAccountRequest {

    @NotBlank
    private String ownerName;

    @NotNull
    private AccountType type;

    /** Opening balance in cents (for CREDIT_CARD: amount currently owed). */
    @Min(0)
    private long openingBalanceCents;

    /** Required when type is CREDIT_CARD. */
    private Long creditLimitCents;

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public AccountType getType() { return type; }
    public void setType(AccountType type) { this.type = type; }
    public long getOpeningBalanceCents() { return openingBalanceCents; }
    public void setOpeningBalanceCents(long openingBalanceCents) { this.openingBalanceCents = openingBalanceCents; }
    public Long getCreditLimitCents() { return creditLimitCents; }
    public void setCreditLimitCents(Long creditLimitCents) { this.creditLimitCents = creditLimitCents; }
}
