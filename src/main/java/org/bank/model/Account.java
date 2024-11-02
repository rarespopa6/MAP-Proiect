package org.bank.model;

import java.time.LocalDateTime;
import java.util.List;

public abstract class Account implements Identifiable{
    private int id;
    private List<User> customers;
    private double balance;
    private LocalDateTime creationTime;

    public Account(List<User> customers, double balance) {
        this.customers = customers;
        this.balance = balance;
        this.creationTime = LocalDateTime.now();
    }


    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
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
        return " accountId=" + id +
                ", customers=" + customers.toString() +
                ", balance=" + balance +
                ", creationTime=" + creationTime +
                '}';
    }
}
