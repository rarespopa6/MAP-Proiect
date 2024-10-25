package org.bank.model;

import java.util.ArrayList;
import java.util.List;

public class Customer extends User{
    private List<Account> accountList;

    public Customer(int id, String firstName, String lastName, String email, String phoneNumber) {
        super(id, firstName, lastName, email, phoneNumber);
        this.accountList = new ArrayList<>();
    }

    public List<Account> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<Account> accountList) {
        this.accountList = accountList;
    }

    @Override
    public String toString() {
        return "Customer{" +
                super.toString() +
                " ,accountList=" + accountList +
                '}';
    }
}
