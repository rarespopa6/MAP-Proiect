package org.bank.service;

import org.bank.config.DBConfig;
import org.bank.model.*;
import org.bank.repository.DBRepository;
import org.bank.repository.FileRepository;
import org.bank.repository.InMemoryRepository;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class that manages bank accounts by providing functionality to create checking and savings accounts,
 * retrieve accounts for a specific customer, and close accounts.
 */
public class AccountService {
    private final DBRepository<Account> accountRepository = new DBRepository<>(Account.class, DBConfig.ACCOUNTS_TABLE);
    private final DBRepository<CoOwnershipRequest> coOwnershipRequestRepo = new DBRepository<>(CoOwnershipRequest.class, DBConfig.COOWNERSHIP_TABLE);
    private List<CreditCard> creditCardList = new ArrayList<>();

    /**
     * Retrieves all accounts associated with a specified customer ID.
     *
     * @param customerId the unique identifier of the customer whose accounts are to be retrieved
     * @return a list of accounts that belong to the specified customer
     */
    public List<Account> getAccountsForCustomer(int customerId) throws IOException {
//        accountRepository.findAll().forEach(System.out::println);
        return accountRepository.findAll().stream()
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
        int accountId = accountRepository.create(newAccount);
        newAccount.setId(accountId);

        for (User customer : customers) {
            if (customer instanceof Customer) {
                ((Customer) customer).addAccount(newAccount);
            }
        }

//        System.out.println(newAccount);
        //accountInMemoryRepository.writeUserAccountRelation(newAccount);

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
        int accountId = accountRepository.create(newAccount);
        newAccount.setId(accountId);

        for (User customer : customers) {
            if (customer instanceof Customer) {
                ((Customer) customer).addAccount(newAccount);
            }
        }

        //accountInMemoryRepository.writeUserAccountRelation(newAccount);

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
        Account account = accountRepository.read(accountId);

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

        try {
            accountRepository.delete(accountId);
        } catch (Exception e){}
    }

    /**
     * Adds the specified amount to the balance of the provided account.
     *
     * @param account the account to which the balance will be added
     * @param amount the amount to add to the account's balance
     */
    public void addBalance(Account account, double amount) {
        account.setBalance(account.getBalance() + amount);
        accountRepository.update(account);
    }

    /**
     * Subtracts the specified amount from the balance of the provided account.
     * If the balance is insufficient, throws an exception.
     *
     * @param account the account from which the balance will be subtracted
     * @param amount the amount to subtract from the account's balance
     * @throws RuntimeException if the account has insufficient funds
     */
    public void subtractBalance(Account account, double amount) {
        if (account.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds");
        }

        account.setBalance(account.getBalance() - amount);
        accountRepository.update(account);
    }

    /**
     * Retrieves the account with the specified ID.
     *
     * @param id the ID of the account to retrieve
     * @return the account with the specified ID, or null if not found
     */
    public Account getAccountByid(int id) {
        return accountRepository.read(id);
    }

    /**
     * Deposits the specified amount into the account with the given ID,
     * provided the user is an owner of the account.
     *
     * @param accountId the ID of the account to deposit into
     * @param userId the ID of the user making the deposit
     * @param amount the amount to deposit
     * @throws RuntimeException if the account is not found, the user is not an owner of the account,
     *                          or if there are other issues with the deposit
     */
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
        account.getAccountLogs().addDepositLog(amount);
        accountRepository.update(account);
    }

    /**
     * Withdraws the specified amount from the account with the given ID,
     * provided the user is an owner of the account and has sufficient balance.
     *
     * @param accountId the ID of the account to withdraw from
     * @param userId the ID of the user making the withdrawal
     * @param amount the amount to withdraw
     * @throws RuntimeException if the account is not found, the user is not an owner of the account,
     *                          the account has insufficient balance, or if there are other issues with the withdrawal
     */
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
        account.getAccountLogs().addWithdrawLog(amount);
        accountRepository.update(account);
    }

    /**
     * Creates a co-ownership request for an account, with a specified requester and account owner.
     *
     * @param account the account to request co-ownership for
     * @param requester the customer requesting co-ownership
     * @param accountOwner the customer who owns the account
     * @return the created co-ownership request
     */
    public CoOwnershipRequest createCoOwnershipRequest(Account account, Customer requester, Customer accountOwner) {
        CoOwnershipRequest request = new CoOwnershipRequest(account, requester, accountOwner);
        coOwnershipRequestRepo.create(request);
        return request;
    }

    /**
     * Retrieves all co-ownership requests for a specific customer that are not approved yet.
     *
     * @param customerId the ID of the customer to retrieve requests for
     * @return a list of co-ownership requests for the customer that are not approved
     */
    public List<CoOwnershipRequest> getRequestsForCustomer(int customerId) {
        List<CoOwnershipRequest> customerRequests = coOwnershipRequestRepo.findAll();
        customerRequests.removeIf(request ->
                request.getAccountOwner().getId() != customerId || request.isApproved());
        return customerRequests;
    }

    /**
     * Approves a co-ownership request by its ID and adds the requester to the account as a co-owner.
     * Deletes the request after approval.
     *
     * @param requestId the ID of the co-ownership request to approve
     * @throws RuntimeException if the request is not found, is already approved, or if there are issues with the approval
     */
    public void approveCoOwnershipRequest(int requestId) {
        System.out.println("Approving Co-Ownership Request with ID: " + requestId);
        CoOwnershipRequest request = coOwnershipRequestRepo.read(requestId);

        if (request != null && !request.isApproved()) {
            request.setApproved(true);
            Account account = request.getAccount();

            if (account != null) {

                List<User> currentCustomers = account.getCustomers();
                if (currentCustomers == null) {
                    currentCustomers = new ArrayList<>();
                }

                if (request.getRequester() == null) {
                    throw new IllegalStateException("Requester is null");
                }

                account.addCustomer(request.getRequester());
                accountRepository.update(account);
            } else {
                throw new RuntimeException("Account is null for request " + requestId);
            }

            coOwnershipRequestRepo.delete(request.getId());
        } else {
            throw new RuntimeException("Request not found or already approved.");
        }
    }

    /**
     * Returns a list of transactions for the specified account, sorted by date in descending order.
     *
     * @param account the account to retrieve transactions for
     * @return a list of transactions for the specified account, sorted by date in descending order
     */
    public List<Transaction> getTransactionsSortedByDate(CheckingAccount account) {
        return account.getTransactionList().stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Transaction::getDate).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Makes a transaction from one checking account to another,
     * transferring the specified amount and saves the transaction.
     *
     * @param selectedAccount the account to transfer the amount from
     * @param destinationAccount the account to transfer the amount to
     * @param amount the amount to transfer
     */
    public void makeTransaction(CheckingAccount selectedAccount,
                                CheckingAccount destinationAccount, double amount) {
        subtractBalance(selectedAccount, amount);
        addBalance(destinationAccount, amount);
        Transaction transaction = new Transaction(selectedAccount, destinationAccount, amount);
        transaction.setId(selectedAccount.getTransactionList().size() + 1);
        selectedAccount.getTransactionList().add(transaction);
        selectedAccount.getAccountLogs().addTransactionLog(destinationAccount, amount);
    }

    /**
     * Retrieves the logs for the specified account.
     *
     * @param account the account to get logs for
     * @return a list of logs for the specified account
     */
    public List<String> getAccountLogs(Account account) {
        return account.getAccountLogs().getLogs();
    }

    /**
     * Retrieves a list of accounts for a specific user, sorted by balance.
     *
     * @param userId The ID of the user.
     * @return A list of accounts for the given user ID, sorted in descending order by balance.
     */
    public List<Account> getAccountsSortedByBalance(int userId) {
        return accountRepository.findAll().stream()
                .filter(account -> account.getCustomers().stream().anyMatch(user -> user.getId() == userId))
                .sorted(Comparator.comparingDouble(Account::getBalance).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of accounts for a specific user, sorted by creation date.
     *
     * @param userId The ID of the user.
     * @return A list of accounts for the given user ID, sorted in descending order by creation date.
     */
    public List<Account> getAccountsSortedByCreationDate(int userId) {
        return accountRepository.findAll().stream()
                .filter(account -> account.getCustomers().stream().anyMatch(user -> user.getId() == userId))
                .sorted(Comparator.comparing(Account::getCreationTime).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of transactions for the specified account.
     *
     * @param account the account to get transactions for
     * @return a list of transactions for the specified account
     */
    public List<Transaction> getTransactionsForAccount(CheckingAccount account) {
        return account.getTransactionList();
    }

    /**
     * Retrieves a list of accounts with a balance above the specified amount.
     *
     * @param amount the amount to filter accounts by
     * @return a list of accounts with a balance above the specified amount
     */
    public List<Account> getAccountsWithBalanceAboveAmount(int userId, double amount) {
        return accountRepository.findAll().stream()
                .filter(account -> account.getCustomers().stream().anyMatch(user -> user.getId() == userId))
                .filter(account -> account.getBalance() > amount)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of transactions for the specified account that are above the specified amount.
     *
     * @param account the account to get transactions for
     * @param amount the amount to filter transactions by
     * @return a list of transactions for the specified account that are above the specified amount
     */
    public List<Transaction> getTransactionsAboveAmount(CheckingAccount account, double amount) {
        return account.getTransactionList().stream()
                .filter(transaction -> transaction.getAmount() > amount)
                .collect(Collectors.toList());
    }

    /**
     * Function to generate a random 16-digit card number.
     *
     * @return a random 16-digit card number
     */
    public String generateRandomCardNumber() {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            cardNumber.append(random.nextInt(10));
        }

        return cardNumber.toString();
    }

    /**
     * Function to generate a random 3-digit CVV number.
     *
     * @return a random 3-digit CVV number
     */
    public String generateRandomCVV() {
        Random random = new Random();
        StringBuilder cvv = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            cvv.append(random.nextInt(10));
        }

        return cvv.toString();
    }

    /**
     * Generates a credit card for a customer and links it to an account.
     *
     * @param customer the customer to generate a card for
     * @param account the account to link a card with
     */
    public void generateCardForAccount(Customer customer, Account account) {
        CreditCard creditCard = new CreditCard();
        creditCard.setCardNumber(Long.parseLong(generateRandomCardNumber()));
        creditCard.setCvv(Integer.parseInt(generateRandomCVV()));
        creditCard.setOwner(customer);
        creditCard.setCurrentBalance(account.getBalance());
        creditCard.setExpiryDate(account.getCreationTime().plusYears(10).toLocalDate());
        creditCard.setAccount(account);

        creditCard.setCardId(creditCardList.size() + 1);
        this.creditCardList.add(creditCard);
    }

    /**
     * Returns a list of credit cards owned by the specified customer.
     *
     * @param customer the customer to get credit cards for
     * @return a list of credit cards owned by the customer
     */
    public List<CreditCard> getCreditCardsForCustomer(Customer customer) {
        return this.creditCardList.stream()
                .filter(creditCard -> creditCard.getOwner().equals(customer))
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of accounts of all users.
     *
     * @return a list of accounts
     */
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
}
