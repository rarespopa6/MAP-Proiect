package org.bank;


import org.bank.controller.AppController;
import org.bank.model.User;
import org.bank.repository.FileRepository;
import org.bank.repository.InMemoryRepository;
import org.bank.service.UserService;
import org.bank.ui.UserInterface;

public class Main {
    public static void main(String[] args) {
        AppController controller = new AppController(new UserService(new InMemoryRepository<>()));
        UserInterface ui = new UserInterface(controller);
        ui.start();
    }
}