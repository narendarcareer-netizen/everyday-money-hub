package com.narender.moneyhub.dto;

import java.util.List;

/**
 * The "everyday" single view: every account a client holds, plus rolled-up
 * totals. netWorthCents = cash + investments − credit owed.
 */
public class OverviewResponse {

    private String owner;
    private List<AccountResponse> accounts;
    private long totalCashCents;
    private long totalInvestmentsCents;
    private long totalCreditOwedCents;
    private long netWorthCents;

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    public List<AccountResponse> getAccounts() { return accounts; }
    public void setAccounts(List<AccountResponse> accounts) { this.accounts = accounts; }
    public long getTotalCashCents() { return totalCashCents; }
    public void setTotalCashCents(long totalCashCents) { this.totalCashCents = totalCashCents; }
    public long getTotalInvestmentsCents() { return totalInvestmentsCents; }
    public void setTotalInvestmentsCents(long totalInvestmentsCents) { this.totalInvestmentsCents = totalInvestmentsCents; }
    public long getTotalCreditOwedCents() { return totalCreditOwedCents; }
    public void setTotalCreditOwedCents(long totalCreditOwedCents) { this.totalCreditOwedCents = totalCreditOwedCents; }
    public long getNetWorthCents() { return netWorthCents; }
    public void setNetWorthCents(long netWorthCents) { this.netWorthCents = netWorthCents; }
}
