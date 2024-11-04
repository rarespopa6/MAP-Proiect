package org.bank.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer within the banking system.
 * A customer can own multiple bank accounts and has personal details.
 */
public class Customer extends User {
    private List<Account> accountList;

    /**
     * Constructs a Customer instance with specified details, including a unique identifier.
     *
     * @param id          the unique identifier of the customer
     * @param firstName   the first name of the customer
     * @param lastName    the last name of the customer
     * @param email       the email address of the customer
     * @param phoneNumber the phone number of the customer
     * @param password    the hashed password for customer authentication
     */
    public Customer(int id, String firstName, String lastName, String email, String phoneNumber, String password) {
        super(id, firstName, lastName, email, phoneNumber, password);
        this.accountList = new ArrayList<>();
    }

    /**
     * Constructs a Customer instance with specified details, without an identifier.
     *
     * @param firstName   the first name of the customer
     * @param lastName    the last name of the customer
     * @param email       the email address of the customer
     * @param phoneNumber the phone number of the customer
     * @param password    the hashed password for customer authentication
     */
    public Customer(String firstName, String lastName, String email, String phoneNumber, String password) {
        super(firstName, lastName, email, phoneNumber, password);
        this.accountList = new ArrayList<>();
    }

    /**
     * Adds a bank account to the customer's account list.
     *
     * @param account the account to be added
     */
    public void addAccount(Account account) {
        this.accountList.add(account);
    }

    /**
     * Removes a bank account from the customer's account list.
     *
     * @param account the account to be removed
     */
    public void removeAccount(Account account) {
        accountList.remove(account);
    }

    /**
     * Retrieves the list of accounts owned by the customer.
     *
     * @return a list of accounts owned by the customer
     */
    public List<Account> getAccountList() {
        return accountList;
    }

    /**
     * Sets the list of accounts owned by the customer.
     *
     * @param accountList the list of accounts to be set
     */
    public void setAccountList(List<Account> accountList) {
        this.accountList = accountList;
    }

    /**
     * Returns a string representation of the customer, including personal details.
     *
     * @return a string representation of the customer
     */
    @Override
    public String toString() {
        return "Customer{" +
                super.toString() +
                '}';
    }
}
