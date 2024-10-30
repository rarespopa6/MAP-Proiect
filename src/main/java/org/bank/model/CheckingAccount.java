package org.bank.model;

import java.time.LocalDateTime;
import java.util.List;

public class CheckingAccount extends Account {
    private double transactionFee;

    public CheckingAccount(List<User> owners, double balance, double transactionFee) {
        super(owners, balance);
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
