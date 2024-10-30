package org.bank.service;

import org.bank.model.*;
import org.bank.repository.InMemoryRepository;

import java.util.List;
import java.util.stream.Collectors;

public class AccountService {
    private final InMemoryRepository<Account> accountInMemoryRepository = new InMemoryRepository<>();

    public List<Account> getAccountsForCustomer(int customerId) {
        return accountInMemoryRepository.findAll().stream()
                .filter(account -> account.getCustomers().stream().anyMatch(user -> user.getId() == customerId))
                .collect(Collectors.toList());
    }

    public Account createCheckingAccount(List<User> customers, double initialDeposit) {
        // TODO logica de transaction Fee
        Account newAccount = new CheckingAccount(customers, initialDeposit, 0.5);
        int accountId = accountInMemoryRepository.create(newAccount);
        newAccount.setAccountId(accountId);

        for (User customer : customers) {
            if (customer instanceof Customer) {
                ((Customer) customer).addAccount(newAccount);
            }
        }

        return newAccount;
    }

    public Account createSavingsAccount(List<User> customers, double initialDeposit) {
        // TODO logica de Interest Rate
        Account newAccount = new SavingsAccount(customers, initialDeposit, 4.5);
        int accountId = accountInMemoryRepository.create(newAccount);
        newAccount.setAccountId(accountId);

        for (User customer : customers) {
            if (customer instanceof Customer) {
                ((Customer) customer).addAccount(newAccount);
            }
        }

        return newAccount;
    }

    public void closeAccountForCustomer(int customerId, int accountId) {
        Account account = accountInMemoryRepository.read(accountId);

        if (account == null) {
            throw new RuntimeException("Account not found.");
        }

        // Verificăm dacă utilizatorul este proprietarul contului
        if (account.getCustomers().stream().noneMatch(user -> user.getId() == customerId)) {
            throw new RuntimeException("User does not have access to this account.");
        }

        // Ștergem contul din lista conturilor utilizatorului
        account.getCustomers().forEach(customer -> {
            if (customer instanceof Customer) {
                ((Customer) customer).removeAccount(account);
            }
        });

        // Ștergem contul din repo-ul general de conturi
        accountInMemoryRepository.delete(accountId);
    }

}
