package org.bank.service;

import org.bank.model.Account;
import org.bank.model.Customer;
import org.bank.model.Employee;
import org.bank.model.User;
import org.bank.repository.InMemoryRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

public class UserService {
    private final InMemoryRepository<User> userInMemoryRepository = new InMemoryRepository<>();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void createEmployee(int id, String firstName, String lastName, String email, String phoneNumber, String password, int salary, String role) {
        if (userExistsByEmail(email)) {
            throw new RuntimeException("An employee with this email already exists");
        }
        String hashedPassword = passwordEncoder.encode(password);
        User employee = new Employee(id, firstName, lastName, email, phoneNumber, hashedPassword, salary, role);

        userInMemoryRepository.create(employee);
    }

    public int createCustomer(String firstName, String lastName, String email, String phoneNumber, String password) {
        if (userExistsByEmail(email)) {
            throw new RuntimeException("A customer with this email already exists");
        }
        String hashedPassword = passwordEncoder.encode(password);
        User customer = new Customer(firstName, lastName, email, phoneNumber, hashedPassword);

        return userInMemoryRepository.create(customer);
    }

    public User readUser(int id) {
        User user = userInMemoryRepository.read(id);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return user;
    }

    public void updateUser(int id, String firstName, String lastName, String email, String phoneNumber, String password) {
        if (!userExists(id)) {
            throw new RuntimeException("User not found for update");
        }

        String hashedPassword = passwordEncoder.encode(password);

        User updatedUser = new Customer(id, firstName, lastName, email, phoneNumber, hashedPassword);
        userInMemoryRepository.update(updatedUser);
    }

    public void deleteUser(int id) {
        if (!userExists(id)) {
            throw new RuntimeException("User not found for deletion");
        }
        userInMemoryRepository.delete(id);
    }

    public List<User> getAllUsers(){
        return userInMemoryRepository.findAll();
    }

    public void addAccountToCustomer(int customerId, Account account) {
        User user = userInMemoryRepository.read(customerId);

        if (user instanceof Customer customer) {
            customer.addAccount(account);
        } else {
            throw new RuntimeException("Customer not found or not a valid customer type.");
        }
    }

    public User getUserByEmail(String email) {
        return userInMemoryRepository.findAll().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    private boolean userExistsByEmail(String email) {
        return userInMemoryRepository.findAll().stream()
                .anyMatch(u -> u.getEmail().equals(email));
    }

    private boolean userExists(int id) {
        return userInMemoryRepository.read(id) != null;
    }
}
