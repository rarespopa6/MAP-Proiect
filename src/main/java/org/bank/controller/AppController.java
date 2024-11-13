package org.bank.controller;

import org.bank.model.*;
import org.bank.service.AccountService;
import org.bank.service.LoanService;
import org.bank.service.UserService;

import java.io.IOException;
import java.util.List;

/**
 * Controller class that acts as an intermediary between the user interface and the underlying business logic.
 * This class handles user-related and account-related operations.
 */
public class AppController {
    private final UserService userService = new UserService();
    private final AccountService accountService = new AccountService();
    private final LoanService loanService = new LoanService();

    /**
     * Creates a new customer and returns their unique identifier.
     *
     * @param firstName   the first name of the customer
     * @param lastName    the last name of the customer
     * @param email       the email address of the customer
     * @param phoneNumber the phone number of the customer
     * @param password    the password for the customer account
     * @return the unique identifier of the newly created customer
     */
    public int createCustomer(String firstName, String lastName, String email, String phoneNumber, String password) throws IOException {
        return userService.createCustomer(firstName, lastName, email, phoneNumber, password);
    }

    /**
     * Retrieves a user based on their unique identifier.
     *
     * @param id the unique identifier of the user
     * @return the User object if found, null otherwise
     */
    public User readUser(int id) {
        return userService.readUser(id);
    }

    /**
     * Retrieves a user based on their email address.
     *
     * @param email the email address of the user
     * @return the User object if found, null otherwise
     */
    public User readUser(String email) throws IOException {
        return userService.getUserByEmail(email);
    }

    /**
     * Updates the details of an existing user.
     *
     * @param id          the unique identifier of the user to update
     * @param firstName   the new first name of the user
     * @param lastName    the new last name of the user
     * @param email       the new email address of the user
     * @param phoneNumber the new phone number of the user
     * @param password    the new password for the user account
     */
    public void updateUser(int id, String firstName, String lastName, String email, String phoneNumber, String password) {
        userService.updateUser(id, firstName, lastName, email, phoneNumber, password);
    }

    /**
     * Deletes a user based on their unique identifier.
     *
     * @param id the unique identifier of the user to delete
     */
    public void deleteUser(int id) {
        userService.deleteUser(id);
    }

    /**
     * Retrieves a list of all users in the system.
     *
     * @return a list of User objects
     */
    public List<User> getAllUsers() throws IOException {
        return userService.getAllUsers();
    }

    /**
     * Retrieves a list of accounts associated with a specific customer.
     *
     * @param customerId the unique identifier of the customer
     * @return a list of Account objects associated with the customer
     */
    public List<Account> getAccountsForCustomer(int customerId) throws IOException {
        return accountService.getAccountsForCustomer(customerId);
    }

    /**
     * Creates a checking account for a specific customer.
     *
     * @param customerId     the unique identifier of the customer
     * @param initialDeposit the initial deposit for the checking account
     * @return the newly created CheckingAccount object
     */
    public Account createCheckingAccount(int customerId, double initialDeposit) {
        return accountService.createCheckingAccount(List.of(readUser(customerId)), initialDeposit);
    }

    /**
     * Creates a savings account for a specific customer.
     *
     * @param customerId     the unique identifier of the customer
     * @param initialDeposit the initial deposit for the savings account
     * @return the newly created SavingsAccount object
     */
    public Account createSavingsAccount(int customerId, double initialDeposit) {
        return accountService.createSavingsAccount(List.of(readUser(customerId)), initialDeposit);
    }

    /**
     * Closes an account for a specific customer.
     *
     * @param customerId the unique identifier of the customer
     * @param accountId  the unique identifier of the account to close
     */
    public void closeAccount(int customerId, int accountId) {
        accountService.closeAccountForCustomer(customerId, accountId);
    }

    /**
     * Makes a new loan for a customer and credits the loan amount to the specified account.
     *
     * @param borrower the customer who is taking out the loan
     * @param account the account to which the loan amount will be credited
     * @param amount the amount of the loan
     * @param termMonths the term of the loan in months
     */
    public void getLoan(Customer borrower, Account account, double amount, int termMonths) {
        this.loanService.getNewLoan(borrower, amount, termMonths);
        this.accountService.addBalance(account, amount);
    }

    /**
     * Pays off a loan for a customer and debits the payment amount from the specified account.
     *
     * @param loanId the id of the loan to be paid
     * @param borrower the customer who is paying off the loan
     * @param account the account from which the payment will be deducted
     * @param paymentAmount the amount to be paid off
     */
    public void payLoan(int loanId, Customer borrower, Account account, double paymentAmount) {
        Loan loan = this.loanService.getLoans(borrower).stream()
                .filter(l -> l.getId() == loanId)
                .findFirst()
                .orElse(null);

        if (loan != null) {
            double actualPayment = this.loanService.payLoan(borrower, loan, paymentAmount);

            try {
                this.accountService.subtractBalance(account, actualPayment);
            } catch (Exception e) {
                throw e;
            }
        } else {
            System.out.println("Loan not found.");
        }
    }

    /**
     * Retrieves all loans associated with a specified customer.
     *
     * @param borrower the customer whose loans are to be retrieved
     * @return a list of loans associated with the specified customer
     */
    public List<Loan> viewLoansStatus(Customer borrower) {
        return this.loanService.getLoans(borrower);
    }

    /**
     * Retrieves a loan by its ID for a specific borrower.
     *
     * @param loanId the ID of the loan to retrieve
     * @param borrower the customer who is the borrower of the loan
     * @return the loan associated with the provided ID and borrower
     * @throws RuntimeException if the loan cannot be found or other errors occur
     */
    public Loan getLoanById(int loanId, Customer borrower) {
        return this.loanService.getLoanById(loanId, borrower);
    }

    /**
     * Deposits a specified amount of money into a user's account.
     *
     * @param accountId the ID of the account to deposit into
     * @param userId the ID of the user making the deposit
     * @param amount the amount of money to deposit
     * @throws RuntimeException if the account or user is invalid or if other errors occur
     */
    public void depositToAccount(int accountId, int userId, double amount) {
        accountService.depositToAccount(accountId, userId, amount);
    }

    /**
     * Withdraws a specified amount of money from a user's account.
     *
     * @param accountId the ID of the account to withdraw from
     * @param userId the ID of the user making the withdrawal
     * @param amount the amount of money to withdraw
     * @throws RuntimeException if the account or user is invalid, or if there are insufficient funds in the account
     */
    public void withdrawFromAccount(int accountId, int userId, double amount) {
        accountService.withdrawFromAccount(accountId, userId, amount);
    }

    /**
     * Allows a user to apply for co-ownership of an account.
     *
     * @param accountId the ID of the account the user wants to request co-ownership for
     * @param requesterId the ID of the user requesting co-ownership
     * @param accountOwnerEmail the email of the account owner
     * @return the co-ownership request created for the specified account and user
     * @throws RuntimeException if any invalid account, user, or owner details are provided
     */
    public CoOwnershipRequest applyForAccount(int accountId, int requesterId, String accountOwnerEmail) throws IOException {
        Account account = accountService.getAccountByid(accountId);
        Customer requester = (Customer) userService.readUser(requesterId);
        Customer accountOwner = (Customer) readUser(accountOwnerEmail);

        if (account == null || requester == null || accountOwner == null) {
            throw new RuntimeException("Invalid account or user details.");
        }

        return accountService.createCoOwnershipRequest(account, requester, accountOwner);
    }

    /**
     * Retrieves a list of pending co-ownership requests for a specific account owner.
     *
     * @param accountOwnerId the ID of the account owner to view pending co-ownership requests for
     * @return a list of co-ownership requests that are pending for the specified account owner
     */
    public List<CoOwnershipRequest> viewPendingRequests(int accountOwnerId) {
        return accountService.getRequestsForCustomer(accountOwnerId);
    }

    /**
     * Approves a pending co-ownership request.
     *
     * @param requestId the ID of the co-ownership request to approve
     * @throws RuntimeException if the request cannot be found or other errors occur
     */
    public void approveCoOwnership(int requestId) {
        accountService.approveCoOwnershipRequest(requestId);
    }

    /**
     * Gets a list of transactions made from a specified account.
     *
     * @param account the account for which to retrieve transactions
     * @return a list of transactions made from the specified account
     */
    public List<Transaction> getTransactionsForAccount(CheckingAccount account) {
        return accountService.getTransactionsForAccount(account);
    }

    /**
     * Initiates a transaction between two accounts.
     *
     * @param selectedAccount account from which the money is transferred
     * @param destinationAccount account to which the money is transferred
     * @param amount amount of money transferred
     */
    public void makeTransaction(CheckingAccount selectedAccount, CheckingAccount destinationAccount, double amount) {
        accountService.makeTransaction(selectedAccount, destinationAccount, amount);
    }


    /**
     * Gets a list of logs associated with a specified account.
     *
     * @param account the account for which to retrieve logs
     * @return a list of logs associated with the specified account
     */
    public List<String> getLogsForAccount(Account account) {
        return this.accountService.getAccountLogs(account);
    }

    /**
     * Retrieves accounts sorted by balance for a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of accounts sorted by balance.
     */
    public List<Account> getAccountsSortedByBalance(int userId) {
        return accountService.getAccountsSortedByBalance(userId);
    }

    /**
     * Retrieves accounts sorted by creation date for a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of accounts sorted by creation date.
     */
    public List<Account> getAccountsSortedByCreationDate(int userId) {
        return accountService.getAccountsSortedByCreationDate(userId);
    }

}
