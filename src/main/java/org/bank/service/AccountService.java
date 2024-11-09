package org.bank.service;

import org.bank.model.*;
import org.bank.repository.InMemoryRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class that manages bank accounts by providing functionality to create checking and savings accounts,
 * retrieve accounts for a specific customer, and close accounts.
 */
public class AccountService {
    private final InMemoryRepository<Account> accountInMemoryRepository = new InMemoryRepository<>();

    /**
     * Retrieves all accounts associated with a specified customer ID.
     *
     * @param customerId the unique identifier of the customer whose accounts are to be retrieved
     * @return a list of accounts that belong to the specified customer
     */
    public List<Account> getAccountsForCustomer(int customerId) {
        return accountInMemoryRepository.findAll().stream()
                .filter(account -> account.getCustomers().stream().anyMatch(user -> user.getId() == customerId))
                .collect(Collectors.toList());
    }

    /**
     * Creates a new checking account for a list of customers with an initial deposit.
     * The account is stored in the repository and associated with each specified customer.
     *
     * @param customers a list of users who will be associated with the checking account
     * @param initialDeposit the initial deposit amount for the checking account
     * @return the newly created checking account
     */
    public Account createCheckingAccount(List<User> customers, double initialDeposit) {
        // TODO transaction Fee logic
        Account newAccount = new CheckingAccount(customers, initialDeposit, 0.5);
        int accountId = accountInMemoryRepository.create(newAccount);
        newAccount.setId(accountId);

        for (User customer : customers) {
            if (customer instanceof Customer) {
                ((Customer) customer).addAccount(newAccount);
            }
        }

        return newAccount;
    }

    /**
     * Creates a new savings account for a list of customers with an initial deposit.
     * The account is saved in the repository and associated with each specified customer.
     *
     * @param customers a list of users who will be associated with the savings account
     * @param initialDeposit the initial deposit amount for the savings account
     * @return the newly created savings account
     */
    public Account createSavingsAccount(List<User> customers, double initialDeposit) {
        // TODO Interest Rate logic
        Account newAccount = new SavingsAccount(customers, initialDeposit, 4.5);
        int accountId = accountInMemoryRepository.create(newAccount);
        newAccount.setId(accountId);

        for (User customer : customers) {
            if (customer instanceof Customer) {
                ((Customer) customer).addAccount(newAccount);
            }
        }

        return newAccount;
    }

    /**
     * Closes a specified account for a customer by removing the account from each associated customer
     * and deleting it from the repository.
     *
     * @param customerId the unique identifier of the customer who owns the account
     * @param accountId the unique identifier of the account to be closed
     * @throws RuntimeException if the account is not found or if the customer does not have access to the account
     */
    public void closeAccountForCustomer(int customerId, int accountId) {
        Account account = accountInMemoryRepository.read(accountId);

        if (account == null) {
            throw new RuntimeException("Account not found.");
        }

        if (account.getCustomers().stream().noneMatch(user -> user.getId() == customerId)) {
            throw new RuntimeException("User does not have access to this account.");
        }

        account.getCustomers().forEach(customer -> {
            if (customer instanceof Customer) {
                ((Customer) customer).removeAccount(account);
            }
        });

        accountInMemoryRepository.delete(accountId);
    }

    public void addBalance(Account account, double amount) {
        account.setBalance(account.getBalance() + amount);
    }

    public void subtractBalance(Account account, double amount) {
        if(account.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds");
        }

        account.setBalance(account.getBalance() - amount);
    }

    public Account getAccountByid(int id){
        return accountInMemoryRepository.read(id);
    }

    public void depositToAccount(int accountId, int userId, double amount) {
        Account account = getAccountByid(accountId);

        if (account == null) {
            throw new RuntimeException("Account not found.");
        }

        boolean isOwner = account.getCustomers().stream()
                .anyMatch(customer -> customer.getId() == userId);

        if (!isOwner) {
            throw new RuntimeException("This account does not belong to the user.");
        }

        addBalance(account, amount);
    }

    public void withdrawFromAccount(int accountId, int userId, double amount) {
        Account account = getAccountByid(accountId);

        if (account == null) {
            throw new RuntimeException("Account not found.");
        }

        boolean isOwner = account.getCustomers().stream()
                .anyMatch(customer -> customer.getId() == userId);

        if (!isOwner) {
            throw new RuntimeException("This account does not belong to the user.");
        }

        if (account.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance for withdrawal.");
        }

        subtractBalance(account, amount);
    }
}
