package org.bank.ui;

import org.bank.controller.AppController;
import org.bank.model.User;
import org.bank.repository.InMemoryRepository;
import org.bank.service.UserService;

import java.util.Scanner;

public class UserInterface {
    private final AppController appController;
    private final Scanner scanner = new Scanner(System.in);

    public UserInterface(AppController appController) {
        this.appController = appController;
    }

    public void start(){
        while (true) {
            System.out.println("\n--- User Management System ---");
            System.out.println("1. Add Customer");
            System.out.println("2. Read User");
            System.out.println("3. Delete User");
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
                    } catch (RuntimeException e){
                        System.out.println("Customer with that email already exists.");
                    }
                    break;

                case 2:
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
                        break;
                    } catch (Exception e){
                        System.out.println("User not found.");
                    }

                case 3:
                    System.out.print("Enter ID to delete: ");
                    int deleteId = scanner.nextInt();
                    scanner.nextLine();  // Consumăm newline

                    try {
                        appController.deleteUser(deleteId);
                        System.out.println("User deleted successfully.");
                    } catch (RuntimeException e) {
                        System.out.println("Error: User not found");
                    }
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }

        scanner.close();


    }
}
