package org.bank.model;

import java.time.LocalDateTime;

public class SavingsAccount extends Account {
    private double interestRate;
    private int monthlyWithdrawalLimit = 3;
    private int withdrawalsThisMonth = 0;

    public SavingsAccount(int accountId, User owner, double balance, double interestRate) {
        super(accountId, owner, balance);
        this.interestRate = interestRate;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    @Override
    public String toString() {
        return "SavingsAccount{" +
                super.toString() +
                " ,interestRate=" + interestRate +
                ", monthlyWithdrawalLimit=" + monthlyWithdrawalLimit +
                ", withdrawalsThisMonth=" + withdrawalsThisMonth +
                '}';
    }
}
