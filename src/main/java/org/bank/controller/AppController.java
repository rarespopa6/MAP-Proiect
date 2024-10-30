package org.bank.controller;

import org.bank.model.User;
import org.bank.service.UserService;

import java.util.List;

public class AppController {
    private final UserService userService;

    public AppController(UserService userService) {
        this.userService = userService;
    }

    public int createCustomer(int id, String firstName, String lastName, String email, String phoneNumber){
        return userService.createCustomer(id, firstName, lastName, email, phoneNumber);
    }

    public User readUser(int id){
        return userService.readUser(id);
    }

    public void updateUser(int id, String firstName, String lastName, String email, String phoneNumber) {
        userService.updateUser(id, firstName, lastName, email, phoneNumber);
    }

    public void deleteUser(int id) {
        userService.deleteUser(id);
    }

    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }
}
