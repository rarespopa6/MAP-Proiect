package org.bank.model;

import java.time.LocalDateTime;

public abstract class Account {
    private int accountId;
    private User owner;
    private double balance;
    private LocalDateTime creationTime;

    public Account(int accountId, User owner, double balance) {
        this.accountId = accountId;
        this.owner = owner;
        this.balance = balance;
        this.creationTime = LocalDateTime.now();
    }


    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
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
                ", owner=" + owner +
                ", balance=" + balance +
                ", creationTime=" + creationTime +
                '}';
    }
}
