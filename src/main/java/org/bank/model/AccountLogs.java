package org.bank.model;

import java.util.ArrayList;
import java.util.List;

public class AccountLogs {
    private Account account;
    private List<String> logs;

    public AccountLogs(Account account) {
        this.account = account;
        this.logs = new ArrayList<>();
    }

    public List<String> getLogs() {
        return logs;
    }

    public void setLogs(List<String> logs) {
        this.logs = logs;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void addTransactionLog(Account destinationAccount, double amount) {
        logs.add("Transaction: " + account.getId() + " -> " + destinationAccount.getId() + " : " + amount);
    }

    public void addLoanLog(double amount) {
        logs.add("Loan: " + amount);
    }

    public void addPaidLoanLog(double amount) {
        logs.add("Paid Loan: " + amount);
    }

    public void addDepositLog(double amount) {
        logs.add("Deposit: " + amount);
    }

    public void addWithdrawLog(double amount) {
        logs.add("Withdraw: " + amount);
    }

    @Override
    public String toString() {
        return "AccountLogs{" +
                "account=" + account +
                ", logs=" + logs +
                '}';
    }
}
