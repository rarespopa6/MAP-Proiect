package org.bank.controller;

import org.bank.model.Account;
import org.bank.model.User;
import org.bank.service.AccountService;
import org.bank.service.UserService;

import java.util.List;

public class AppController {
    private final UserService userService = new UserService();
    private final AccountService accountService = new AccountService();

    public int createCustomer(String firstName, String lastName, String email, String phoneNumber, String password){
        return userService.createCustomer(firstName, lastName, email, phoneNumber, password);
    }

    public User readUser(int id){
        return userService.readUser(id);
    }

    public User readUser(String email) {
        return userService.getUserByEmail(email);
    }

    public void updateUser(int id, String firstName, String lastName, String email, String phoneNumber, String password) {
        userService.updateUser(id, firstName, lastName, email, phoneNumber, password);
    }

    public void deleteUser(int id) {
        userService.deleteUser(id);
    }

    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    public List<Account> getAccountsForCustomer(int customerId) {
        return accountService.getAccountsForCustomer(customerId);
    }

    public Account createCheckingAccount(int customerId, double initialDeposit) {
        return accountService.createCheckingAccount(List.of(readUser(customerId)), initialDeposit);
    }

    public Account createSavingsAccount(int customerId, double initialDeposit) {
        return accountService.createSavingsAccount(List.of(readUser(customerId)), initialDeposit);
    }

    public void closeAccount(int customerId, int accountId) {
        accountService.closeAccountForCustomer(customerId, accountId);
    }

}
