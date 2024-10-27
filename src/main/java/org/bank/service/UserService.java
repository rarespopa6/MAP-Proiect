package org.bank.service;

import org.bank.model.Customer;
import org.bank.model.Employee;
import org.bank.model.User;
import org.bank.repository.FileRepository;

public class UserService {
    private final FileRepository<User> userFileRepository;

    public UserService(FileRepository<User> userFileRepository) {
        this.userFileRepository = userFileRepository;
    }

    public void createEmployee(int id, String firstName, String lastName, String email, String phoneNumber, int salary, String role) {
        User employee = new Employee(id, firstName, lastName, email, phoneNumber, salary, role);
        userFileRepository.create(employee);
    }

    public void createCustomer(int id, String firstName, String lastName, String email, String phoneNumber) {
        User customer = new Customer(id, firstName, lastName, email, phoneNumber);
        userFileRepository.create(customer);
    }

    public User readUser(int id){
        return userFileRepository.read(id);
    }

    public void updateUser(User user){
        userFileRepository.update(user);
    }

    public void deleteUser(int id){
        userFileRepository.delete(id);
    }
}
