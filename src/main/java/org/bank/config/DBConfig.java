package org.bank.config;

/**
 * Configuration class that holds the constants for database connection settings
 * and table names used in the application.
 * <p>
 * This class provides static constants for the database URL, user credentials,
 * and the table names that represent the core entities in the application
 * (Users, Accounts, AccountUser, and CoOwnershipRequest).
 * </p>
 */
public class DBConfig {

    /**
     * The URL of the PostgreSQL database.
     * Used for establishing a connection to the database.
     */
    public static final String DB_URL = "jdbc:postgresql://localhost:5432/bank";

    /**
     * The username used for connecting to the PostgreSQL database.
     */
    public static final String DB_USER = "postgres";

    /**
     * The password used for connecting to the PostgreSQL database.
     */
    public static final String DB_PASSWORD = "rares10";

    /**
     * The name of the Users table in the database.
     * This table stores user information (both customers and employees).
     */
    public static final String USERS_TABLE = "Users";

    /**
     * The name of the Accounts table in the database.
     * This table stores information about accounts (checking, savings, etc.).
     */
    public static final String ACCOUNTS_TABLE = "Accounts";

    /**
     * The name of the AccountUser table in the database.
     * This table establishes a relationship between accounts and users (owners).
     */
    public static final String ACCOUNT_USER_TABLE = "AccountUser";

    /**
     * The name of the CoOwnershipRequest table in the database.
     * This table stores requests for co-ownership of accounts.
     */
    public static final String COOWNERSHIP_TABLE = "coownership_request";
}
