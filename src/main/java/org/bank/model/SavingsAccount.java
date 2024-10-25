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
    public void deposit(double amount) throws RuntimeException {
        if (amount <= 0) {
            throw new RuntimeException("Invalid amount to deposit: " + amount);
        }

        double interest = this.getBalance() * (interestRate / 100);
        this.setBalance(this.getBalance() + amount + interest);
    }

    @Override
    public void withdraw(double amount) throws RuntimeException {
        if (amount <= 0) {
            throw new RuntimeException("Invalid amount to withdraw: " + amount);
        }

        if (withdrawalsThisMonth >= monthlyWithdrawalLimit) {
            throw new RuntimeException("Monthly withdrawal limit reached for SavingsAccount.");
        }

        double currentBalance = this.getBalance();
        if (amount > currentBalance) {
            throw new RuntimeException("Insufficient funds.");
        }

        this.setBalance(currentBalance - amount);
        withdrawalsThisMonth++;
    }
}
