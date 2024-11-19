package org.bank.config;


public class DBConfig {
    // SQL Connection Credentials
    public static final String DB_URL = "jdbc:postgresql://localhost:5432/bank";  // Folosește Windows Authentication
    public static final String DB_USER = "postgres";
    public static final String DB_PASSWORD = "rares10";

    public static final String USERS_TABLE = "Users";
    public static final String ACCOUNTS_TABLE = "Accounts";
    public static final String ACCOUNT_USER_TABLE = "AccountUser";
    public static final String COOWNERSHIP_TABLE = "Ownership";
}

