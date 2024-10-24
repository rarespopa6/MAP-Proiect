package org.bank.model;

import java.time.LocalDateTime;

public class SavingsAccount extends Account implements MoneyMethods{
    private double interestRate;

    public SavingsAccount(int accountId, User owner, double balance, LocalDateTime creationTime, double interestRate) {
        super(accountId, owner, balance, creationTime);
        this.interestRate = interestRate;
    }

    @Override
    public void deposit(double amount) throws RuntimeException {
        // TODO logica de conturi
    }

    @Override
    public void withdraw(double amount) throws RuntimeException {

    }
}
