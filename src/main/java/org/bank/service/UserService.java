package org.bank.service;

import org.bank.model.Account;
import org.bank.model.Customer;
import org.bank.model.Employee;
import org.bank.model.User;
import org.bank.repository.FileRepository;
import org.bank.repository.InMemoryRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.util.List;

/**
 * Service class that provides functionality for managing users, including customers and employees.
 * This service handles user creation, retrieval, updating, and deletion, as well as account management for customers.
 */
public class UserService {
    private final FileRepository<User> userInMemoryRepository = new FileRepository<>("data/users.csv");
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Creates a new employee and saves it in the repository. Ensures the email is unique.
     *
     * @param id the unique identifier for the employee.
     * @param firstName the first name of the employee.
     * @param lastName the last name of the employee.
     * @param email the email address of the employee.
     * @param phoneNumber the phone number of the employee.
     * @param password the plain-text password for the employee, which will be hashed.
     * @param salary the salary of the employee.
     * @param role the role of the employee.
     * @throws RuntimeException if an employee with the specified email already exists.
     */
    public void createEmployee(int id, String firstName, String lastName, String email, String phoneNumber, String password, int salary, String role) throws IOException {
        if (userExistsByEmail(email)) {
            throw new RuntimeException("An employee with this email already exists");
        }
        String hashedPassword = passwordEncoder.encode(password);
        User employee = new Employee(id, firstName, lastName, email, phoneNumber, hashedPassword, salary, role);

        userInMemoryRepository.create(employee);
    }

    /**
     * Creates a new customer and saves it in the repository. Ensures the email is unique.
     *
     * @param firstName the first name of the customer.
     * @param lastName the last name of the customer.
     * @param email the email address of the customer.
     * @param phoneNumber the phone number of the customer.
     * @param password the plain-text password for the customer, which will be hashed.
     * @return the unique ID assigned to the newly created customer.
     * @throws RuntimeException if a customer with the specified email already exists.
     */
    public int createCustomer(String firstName, String lastName, String email, String phoneNumber, String password) throws IOException {
        if (userExistsByEmail(email)) {
            throw new RuntimeException("A customer with this email already exists");
        }
        String hashedPassword = passwordEncoder.encode(password);
        User customer = new Customer(firstName, lastName, email, phoneNumber, hashedPassword);

        return userInMemoryRepository.create(customer);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the unique identifier of the user.
     * @return the user with the specified ID.
     * @throws RuntimeException if the user is not found.
     */
    public User readUser(int id) {
        User user = userInMemoryRepository.read(id);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return user;
    }

    /**
     * Updates an existing user's information in the repository.
     *
     * @param id the unique identifier of the user to update.
     * @param firstName the new first name of the user.
     * @param lastName the new last name of the user.
     * @param email the new email address of the user.
     * @param phoneNumber the new phone number of the user.
     * @param password the new plain-text password for the user, which will be hashed.
     * @throws RuntimeException if the user is not found.
     */
    public void updateUser(int id, String firstName, String lastName, String email, String phoneNumber, String password) {
        if (!userExists(id)) {
            throw new RuntimeException("User not found for update");
        }

        String hashedPassword = passwordEncoder.encode(password);

        User updatedUser = new Customer(id, firstName, lastName, email, phoneNumber, hashedPassword);
        userInMemoryRepository.update(updatedUser);
    }

    /**
     * Deletes a user from the repository by their ID.
     *
     * @param id the unique identifier of the user to delete.
     * @throws RuntimeException if the user is not found.
     */
    public void deleteUser(int id) {
        if (!userExists(id)) {
            throw new RuntimeException("User not found for deletion");
        }
        try {
            userInMemoryRepository.delete(id);
        } catch (Exception e){throw new RuntimeException("Can not delete user");}
    }

    /**
     * Retrieves a list of all users currently stored in the repository.
     *
     * @return a list of all users.
     */
    public List<User> getAllUsers() throws IOException {
        return userInMemoryRepository.findAll();
    }

    /**
     * Adds a bank account to a customer based on their ID.
     *
     * @param customerId the unique identifier of the customer.
     * @param account the account to be added to the customer.
     * @throws RuntimeException if the customer is not found or the user is not of type Customer.
     */
    public void addAccountToCustomer(int customerId, Account account) {
        User user = userInMemoryRepository.read(customerId);

        if (user instanceof Customer customer) {
            customer.addAccount(account);
        } else {
            throw new RuntimeException("Customer not found or not a valid customer type.");
        }
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email address of the user to find.
     * @return the user with the specified email, or {@code null} if not found.
     */
    public User getUserByEmail(String email) throws IOException {
        return userInMemoryRepository.findAll().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    /**
     * Checks if a user exists based on their email address.
     *
     * @param email the email address to check.
     * @return {@code true} if a user with the specified email exists, {@code false} otherwise.
     */
    private boolean userExistsByEmail(String email) throws IOException {
        return userInMemoryRepository.findAll().stream()
                .anyMatch(u -> u.getEmail().equals(email));
    }

    /**
     * Checks if a user exists in the repository by their ID.
     *
     * @param id the unique identifier of the user.
     * @return {@code true} if a user with the specified ID exists, {@code false} otherwise.
     */
    private boolean userExists(int id) {
        return userInMemoryRepository.read(id) != null;
    }
}
