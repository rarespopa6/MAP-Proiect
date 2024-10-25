package org.bank.model;

import java.time.LocalDateTime;

public class CheckingAccount extends Account {
    private static final double TRANSACTION_FEE = 0.5;

    public CheckingAccount(int accountId, User owner, double balance) {
        super(accountId, owner, balance);
    }

    @Override
    public void deposit(double amount) throws RuntimeException{
        if (amount <= 0){
            throw new RuntimeException("Invalid amount to deposit: " + amount);
        }
        this.setBalance(this.getBalance() + amount);
    }

    @Override
    public void withdraw(double amount) throws RuntimeException {
        if (amount <= 0){
            throw new RuntimeException("Invalid amount to withdraw: " + amount);
        }

        double totalAmount = amount *  (100 + TRANSACTION_FEE) / 100;
        double currentBalance = this.getBalance();

        if (totalAmount > currentBalance){
            throw new RuntimeException("You can't withdraw more money than you have.");
        }

        this.setBalance(currentBalance - totalAmount);
    }
}
