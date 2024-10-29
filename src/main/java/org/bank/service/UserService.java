package org.bank.service;

import org.bank.model.Customer;
import org.bank.model.Employee;
import org.bank.model.User;
import org.bank.repository.InMemoryRepository;

public class UserService {
    private final InMemoryRepository<User> userInMemoryRepository;

    public UserService(InMemoryRepository<User> userInMemoryRepository) {
        this.userInMemoryRepository = userInMemoryRepository;
    }

    public void createEmployee(int id, String firstName, String lastName, String email, String phoneNumber, int salary, String role) {
        if (userExistsByEmail(email)) {
            throw new RuntimeException("An employee with this email already exists");
        }
        User employee = new Employee(id, firstName, lastName, email, phoneNumber, salary, role);
        userInMemoryRepository.create(employee);
    }

    public int createCustomer(int id, String firstName, String lastName, String email, String phoneNumber) {
        if (userExistsByEmail(email)) {
            throw new RuntimeException("A customer with this email already exists");
        }
        User customer = new Customer(id, firstName, lastName, email, phoneNumber);
        return userInMemoryRepository.create(customer);
    }

    public User readUser(int id) {
        User user = userInMemoryRepository.read(id);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return user;
    }

    public void updateUser(User user) {
        if (!userExists(user.getId())) {
            throw new RuntimeException("User not found for update");
        }
        userInMemoryRepository.update(user);
    }

    public void deleteUser(int id) {
        if (!userExists(id)) {
            throw new RuntimeException("User not found for deletion");
        }
        userInMemoryRepository.delete(id);
    }

    private boolean userExistsByEmail(String email) {
        return userInMemoryRepository.findAll().stream()
                .anyMatch(u -> u.getEmail().equals(email));
    }

    private boolean userExists(int id) {
        return userInMemoryRepository.read(id) != null;
    }
}
