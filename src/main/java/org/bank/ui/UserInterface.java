package org.bank.ui;

import org.bank.controller.AppController;
import org.bank.model.*;

import java.util.List;
import java.util.Scanner;

/**
 * UserInterface class handles the interaction with the user,
 * allowing customers and employees to perform various banking operations.
 */
public class UserInterface {
    private final AppController appController = new AppController();
    private final Scanner scanner = new Scanner(System.in);
    private User loggedInUser;
    private Account selectedAccount;

    /**
     * Starts the user interface and handles user interactions in a loop.
     * Provides options to log in, sign up, or exit the application.
     */
    public void start() {
        while (true) {
            System.out.println("\n--- Banking Administration System ---");
            System.out.println("1. Login");
            System.out.println("2. Sign Up");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            int option = scanner.nextInt();
            scanner.nextLine();

            if (option == 0) {
                System.out.println("Exiting...");
                break;
            }

            switch (option) {
                case 1:
                    login();
                    break;
                case 2:
                    signUp();
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }

        scanner.close();
    }

    /**
     * Handles user login, verifies user credentials, and directs to respective actions based on user type.
     */
    private void login() {
        System.out.print("Are you a Customer (C) or Employee (E)? ");
        String userType = scanner.nextLine().toUpperCase();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        User user = appController.readUser(email);

        try {
            // Check if user exists based on user type
            if (user == null) {
                System.out.println("User not found.");
                return;
            }

            System.out.print("Password: ");
            String password = scanner.nextLine();

            if (!user.comparePassword(password)) {
                System.out.println("Incorrect password.");
                return;
            }

            loggedInUser = user;
            System.out.println("Logged in successfully.");

            if (userType.equals("C")) {
                customerActions();
            } else if (userType.equals("E")) {
                employeeActions();
            } else {
                System.out.println("Invalid user type.");
            }
        } catch (Exception e) {
            System.out.println("User not found.");
        }
    }

    /**
     * Allows a new user to sign up by collecting their information and creating a customer account.
     */
    private void signUp() {
        System.out.print("Enter First Name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter Last Name: ");
        String lastName = scanner.nextLine();

        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        System.out.print("Enter Phone Number: ");
        String phoneNumber = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        try {
            int generatedId = appController.createCustomer(firstName, lastName, email, phoneNumber, password);
            System.out.println("Customer created with ID: " + generatedId);
            loggedInUser = appController.readUser(generatedId);
            customerActions();
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Displays actions available to a logged-in customer and handles user choices.
     */
    private void customerActions() {
        while (true) {
            System.out.println("\n--- Customer Actions ---");
            System.out.println("1. My Profile");
            System.out.println("2. My Accounts");
            System.out.println("3. Open New Account");
            System.out.println("4. Close Account");
            System.out.println("5. Loans");
            System.out.println("0. Logout");
            System.out.print("Choose an option: ");

            int option = scanner.nextInt();
            scanner.nextLine();

            if (option == 0) {
                System.out.println("Logging out...");
                loggedInUser = null; // Clear logged in user
                break;
            }

            switch (option) {
                case 1:
                    viewMyProfile();
                    break;
                case 2:
                    myAccounts();
                    break;
                case 3:
                    openNewAccount();
                    break;
                case 4:
                    closeAccount();
                    break;
                case 5:
                    setSelectedAccount();
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
    }

    /**
     * Displays the logged-in user's profile information.
     */
    private void viewMyProfile() {
        if (loggedInUser != null) {
            System.out.println("User Profile: " + loggedInUser);
        } else {
            System.out.println("No user is logged in.");
        }
    }

    /**
     * Displays all accounts associated with the logged-in customer.
     */
    private void myAccounts() {
        if (loggedInUser instanceof Customer) {
            Customer customer = (Customer) loggedInUser;
            List<Account> accounts = appController.getAccountsForCustomer(customer.getId());

            if (accounts.isEmpty()) {
                System.out.println("No accounts found.");
            } else {
                System.out.println("Your Accounts:");
                for (Account account : accounts) {
                    System.out.println(account);
                }
            }
        } else {
            System.out.println("No customer is logged in.");
        }
    }

    /**
     * Prompts the customer to open a new account and handles the creation process.
     */
    private void openNewAccount() {
        if (loggedInUser instanceof Customer) {
            Customer customer = (Customer) loggedInUser;

            System.out.println("Choose Account type:");
            System.out.println("1. Checking Account (0.5% transaction fee)  2. Savings Account (4.5%)");
            System.out.print("Option: ");

            int option = scanner.nextInt();
            scanner.nextLine();

            Account newAccount = null;

            if (option != 1 && option != 2) {
                System.out.println("Invalid option.");
                return;
            }

            if (option == 1) {
                System.out.println("New Checking Account");
                System.out.print("Enter initial deposit: ");
                double initialDeposit = scanner.nextDouble();
                scanner.nextLine();

                newAccount = appController.createCheckingAccount(customer.getId(), initialDeposit);
            } else if (option == 2) {
                System.out.println("New Savings Account");
                System.out.print("Enter initial deposit: ");
                double initialDeposit = scanner.nextDouble();
                scanner.nextLine();

                newAccount = appController.createSavingsAccount(customer.getId(), initialDeposit);
            }
            System.out.println("New account opened: " + newAccount);
        } else {
            System.out.println("No customer is logged in.");
        }
    }

    /**
     * Allows the customer to close one of their accounts after confirmation.
     */
    private void closeAccount() {
        System.out.print("Enter Account ID to close: ");
        int accountId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Are you sure you want to close this account? (yes/no): ");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("yes")) {
            try {
                appController.closeAccount(loggedInUser.getId(), accountId);
                System.out.println("Account closed successfully.");
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("Account closure cancelled.");
        }
    }

    /**
     * Displays actions available to a logged-in employee and handles user choices.
     */
    private void employeeActions() {
        while (true) {
            System.out.println("\n--- Employee Actions ---");
            System.out.println("1. Add Customer");
            System.out.println("2. Read User");
            System.out.println("3. Update Customer");
            System.out.println("4. Delete User");
            System.out.println("5. List all Users");
            System.out.println("0. Logout");
            System.out.print("Choose an option: ");

            int option = scanner.nextInt();
            scanner.nextLine();

            if (option == 0) {
                System.out.println("Logging out...");
                loggedInUser = null; // Clear logged in user
                break;
            }

            switch (option) {
                case 1:
                    createUser();
                    break;
                case 2:
                    readUser();
                    break;
                case 3:
                    updateUser();
                    break;
                case 4:
                    deleteUser();
                    break;
                case 5:
                    listAllUsers();
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
    }

    /**
     * Allows an employee to create a new customer account.
     */
    private void createUser() {
        System.out.print("Enter First Name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter Last Name: ");
        String lastName = scanner.nextLine();

        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        System.out.print("Enter Phone Number: ");
        String phoneNumber = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        try {
            int generatedId = appController.createCustomer(firstName, lastName, email, phoneNumber, password);
            System.out.println("Customer created with ID: " + generatedId);
        } catch (RuntimeException e) {
            System.out.println("Customer with that email already exists.");
        }
    }

    /**
     * Reads and displays information about a user based on the provided ID.
     */
    private void readUser() {
        System.out.print("Enter ID to read: ");
        int readId = scanner.nextInt();
        scanner.nextLine();

        try {
            User user = appController.readUser(readId);
            if (user != null) {
                System.out.println("User found: " + user);
            } else {
                System.out.println("User not found.");
            }
        } catch (Exception e) {
            System.out.println("User not found.");
        }
    }

    /**
     * Updates an existing user's information based on the provided details.
     */
    private void updateUser() {
        System.out.print("Enter ID: ");
        int userId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter First Name: ");
        String userFirstName = scanner.nextLine();

        System.out.print("Enter Last Name: ");
        String userLastName = scanner.nextLine();

        System.out.print("Enter Email: ");
        String userEmail = scanner.nextLine();

        System.out.print("Enter Phone Number: ");
        String userPhone = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        try {
            appController.updateUser(userId, userFirstName, userLastName, userEmail, userPhone, password);
            System.out.println("User updated successfully.");
        } catch (RuntimeException e) {
            System.out.println("Error: User not found");
        }
    }

    /**
     * Deletes a user based on the provided ID.
     */
    private void deleteUser() {
        System.out.print("Enter ID to delete: ");
        int deleteId = scanner.nextInt();
        scanner.nextLine();

        try {
            appController.deleteUser(deleteId);
            System.out.println("User deleted successfully.");
        } catch (RuntimeException e) {
            System.out.println("Error: User not found");
        }
    }

    /**
     * Lists all users in the system.
     */
    private void listAllUsers() {
        for (User user : appController.getAllUsers()) {
            System.out.println(user);
        }
    }

    /**
     * Allows the customer to select a checking account and perform loan-related actions.
     */
    private void setSelectedAccount() {
        System.out.print("Select a Checking Account: ");

        List<Account> checkingAccounts = appController.getAccountsForCustomer(loggedInUser.getId())
                .stream()
                .filter(account -> account instanceof CheckingAccount)
                .toList();

        if(checkingAccounts.isEmpty()) {
            System.out.println("No checking accounts found.");
            return;
        }

        for (Account account : checkingAccounts) {
            System.out.println(account);
        }

        int accountId = scanner.nextInt();

        this.selectedAccount = checkingAccounts.stream()
                .filter(account -> account.getId() == accountId)
                .findFirst()
                .orElse(null);

        loanActions();
    }

    /**
     * Displays loan-related actions available to the customer and handles user choices.
     */
    private void loanActions() {
        while (true) {
            System.out.println("\n--- Loan Actions ---");
            System.out.println("1. Apply for Loan");
            System.out.println("2. Pay Loan");
            System.out.println("3. View Ongoing Loans");
            System.out.println("0. Back");
            System.out.print("Choose an option: ");

            int option = scanner.nextInt();
            scanner.nextLine();

            if (option == 0) {
                break;
            }

            switch (option) {
                case 1:
                    applyForLoan();
                    break;
                case 2:
                    payLoan();
                    break;
                case 3:
                    listAllLoans();
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
    }

    /**
     * Prompts the customer to apply for a loan and handles the loan creation process.
     */
    private void applyForLoan() {
        System.out.print("Enter loan amount: ");
        double loanAmount = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Enter term in months: ");
        int termMonths = scanner.nextInt();
        scanner.nextLine();

        this.appController.getLoan((Customer) loggedInUser, selectedAccount, loanAmount, termMonths);
    }

    /**
     * Lists all ongoing loans associated with the logged-in customer.
     */
    private void listAllLoans() {
        for (Loan loan : this.appController.viewLoansStatus((Customer) loggedInUser)) {
            System.out.println(loan);
        }
    }

    /**
     * Allows the customer to pay off an existing loan.
     */
    private void payLoan() {
        System.out.print("Enter loan ID: ");
        int loanId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter payment amount: ");
        double paymentAmount = scanner.nextDouble();
        scanner.nextLine();

        this.appController.payLoan(loanId, (Customer) loggedInUser, selectedAccount, paymentAmount);
    }
}
