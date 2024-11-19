package org.bank;


import org.bank.ui.UserInterface;

public class Main {
    public static void main(String[] args) {
        UserInterface ui = new UserInterface();
        ui.start();

        // TODO List
        // LoanMapper
        // TransactionMapper
        // CreditCardMapper
        // AccountLogsMapper
        // BankMapper
        // + de adaugat numele tabelelor in DBConfig
        // + de adaugat in DBRepository la logica de INSERT/UPDATE (cu populate)

        // UI: Employee Actions -> View all Transactions + View Account Logs for Account
    }
}