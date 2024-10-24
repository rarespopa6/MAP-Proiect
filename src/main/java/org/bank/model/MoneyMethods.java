package org.bank.model;

public interface MoneyMethods {
    void deposit(double amount) throws RuntimeException;
    void withdraw(double amount) throws RuntimeException;
}
