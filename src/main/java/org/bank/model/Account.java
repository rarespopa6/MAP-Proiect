package org.bank.model;

import java.time.LocalDateTime;
import java.util.List;

public abstract class Account {
    private int accountId;
    private List<User> customers;
    private double balance;
    private LocalDateTime creationTime;

    public Account(List<User> customers, double balance) {
        this.customers = customers;
        this.balance = balance;
        this.creationTime = LocalDateTime.now();
    }


    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public List<User> getCustomers() {
        return customers;
    }

    public void setOwner(List<User> customers) {
        this.customers = customers;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    @Override
    public String toString() {
        return " accountId=" + accountId +
                ", customers=" + customers.toString() +
                ", balance=" + balance +
                ", creationTime=" + creationTime +
                '}';
    }
}
