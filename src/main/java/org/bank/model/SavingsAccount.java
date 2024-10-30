package org.bank.model;

import java.time.LocalDateTime;
import java.util.List;

public class SavingsAccount extends Account {
    private double interestRate;
    private int monthlyWithdrawalLimit = 3;
    private int withdrawalsThisMonth = 0;

    public SavingsAccount(List<User> owner, double balance, double interestRate) {
        super(owner, balance);
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
