package org.bank.model;

import java.time.LocalDateTime;

public class CheckingAccount extends Account {
    private double transactionFee;

    public CheckingAccount(int accountId, User owner, double balance, double transactionFee) {
        super(accountId, owner, balance);
        this.transactionFee = transactionFee;
    }

    public double getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(double transactionFee) {
        this.transactionFee = transactionFee;
    }

    @Override
    public String toString() {
        return "CheckingAccount{" +
                super.toString() +
                " ,transactionFee=" + transactionFee +
                '}';
    }
}
