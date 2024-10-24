package org.bank.model;

import java.time.LocalDateTime;

public class CheckingAccount extends Account implements MoneyMethods{
    public CheckingAccount(int accountId, User owner, double balance, LocalDateTime creationTime) {
        super(accountId, owner, balance, creationTime);
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

        double currentBalance = this.getBalance();
        if (amount > currentBalance){
            throw new RuntimeException("You can't withdraw more money than you have.");
        }

        this.setBalance(currentBalance - amount);
    }
}
