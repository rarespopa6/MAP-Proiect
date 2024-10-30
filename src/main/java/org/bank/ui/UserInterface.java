package org.bank.ui;

import org.bank.controller.AppController;
import org.bank.model.User;
import org.bank.repository.InMemoryRepository;
import org.bank.service.UserService;

import java.util.Scanner;

public class UserInterface {
    private final AppController appController;
    private final Scanner scanner = new Scanner(System.in);
    private User loggedInUser;

    public UserInterface(AppController appController) {
        this.appController = appController;
    }

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

    private void login() {
        System.out.print("Are you a Customer (C) or Employee (E)? ");
        String userType = scanner.nextLine().toUpperCase();

        System.out.print("Enter your ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        try {
            // Check if user exists based on user type
            loggedInUser = appController.readUser(id);
            if (loggedInUser == null) {
                System.out.println("User not found.");
                return;
            }

            if (userType.equals("C")) {
                customerActions();
            } else if (userType.equals("E")) {
                employeeActions();
            } else {
                System.out.println("Invalid user type.");
            }
        } catch (Exception e){
            System.out.println("User not found.");
        }
    }

    private void signUp() {
        System.out.print("Enter ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter First Name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter Last Name: ");
        String lastName = scanner.nextLine();

        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        System.out.print("Enter Phone Number: ");
        String phoneNumber = scanner.nextLine();

        try {
            int generatedId = appController.createCustomer(id, firstName, lastName, email, phoneNumber);
            System.out.println("Customer created with ID: " + generatedId);
            loggedInUser = appController.readUser(generatedId);
            customerActions();
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void customerActions() {
        while (true) {
            System.out.println("\n--- Customer Actions ---");
            System.out.println("1. View My Profile");
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

                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
    }

    private void viewMyProfile() {
        if (loggedInUser != null) {
            System.out.println("User Profile: " + loggedInUser);
        } else {
            System.out.println("No user is logged in.");
        }
    }

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

    private void createUser() {
        System.out.print("Enter ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter First Name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter Last Name: ");
        String lastName = scanner.nextLine();

        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        System.out.print("Enter Phone Number: ");
        String phoneNumber = scanner.nextLine();

        try {
            int generatedId = appController.createCustomer(id, firstName, lastName, email, phoneNumber);
            System.out.println("Customer created with ID: " + generatedId);
        } catch (RuntimeException e) {
            System.out.println("Customer with that email already exists.");
        }
    }

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
        try {
            appController.updateUser(userId, userFirstName, userLastName, userEmail, userPhone);
            System.out.println("User updated successfully.");
        } catch (RuntimeException e) {
            System.out.println("Error: User not found");
        }
    }

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

    private void listAllUsers() {
        for (User user : appController.getAllUsers()) {
            System.out.println(user);
        }
    }
}
