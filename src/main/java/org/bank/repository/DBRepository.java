package org.bank.repository;

import org.bank.config.DBConfig;
import org.bank.model.*;
import org.bank.model.mapper.AccountMapper;
import org.bank.model.mapper.Mapper;
import org.bank.model.mapper.UserMapper;

import java.sql.*;
import java.util.*;

public class DBRepository<T extends Identifiable> implements IRepository<T> {
    private final String tableName;
    private final Class<T> type;
    private final Map<Class<?>, Mapper<?>> mappers = new HashMap<>();
    private final String dbUrl = DBConfig.DB_URL;
    private final String dbUser= DBConfig.DB_USER;
    private final String dbPassword= DBConfig.DB_PASSWORD;

    public DBRepository(Class<T> type, String tableName) {
        this.type = type;
        this.tableName = tableName;

        registerMapper(Account.class, new AccountMapper());
        registerMapper(CheckingAccount.class, new AccountMapper());
        registerMapper(SavingsAccount.class, new AccountMapper());

        registerMapper(User.class, new UserMapper());
        registerMapper(Customer.class, new UserMapper());
        registerMapper(Employee.class, new UserMapper());
    }

    public void registerMapper(Class<?> clazz, Mapper<?> mapper) {
        mappers.put(clazz, mapper);
    }

    @SuppressWarnings("unchecked")
    private Mapper<T> getMapper() {
        Mapper<?> mapper = mappers.get(type);
        if (mapper == null) {
            throw new IllegalArgumentException("No mapper found for type: " + type.getName());
        }
        return (Mapper<T>) mapper;
    }

    private Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public int create(T obj) {
        if (obj == null) throw new IllegalArgumentException("Object to create cannot be null");

        String sql = buildInsertSql(obj);
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            populateInsertStatement(stmt, obj);
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    obj.setId(generatedId);

                    if (obj instanceof Account) {
                        Account account = (Account) obj;
                        populateAccountUserRelationship(conn, account);
                    }

                    return generatedId;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    @Override
    public T read(int id) {
        String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    T entity = getMapper().map(rs);
                    populateRelationships(entity, conn);
                    return entity;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(T obj) {
        if (obj == null) throw new IllegalArgumentException("Object to update cannot be null");

        String sql = buildUpdateSql(obj);
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            populateUpdateStatement(stmt, obj);
            stmt.executeUpdate();

            if (obj instanceof Account) {
                Account account = (Account) obj;

                populateAccountUserRelationship(conn, account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM " + tableName + " WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<T> findAll() {
        List<T> results = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                T entity = getMapper().map(rs);
                populateRelationships(entity, conn);
                results.add(entity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    private void populateRelationships(T entity, Connection conn) throws SQLException {
        if (entity instanceof Account) {
            Account account = (Account) entity;
            String sql = "SELECT u.* FROM users u " +
                    "JOIN accountuser au ON u.id = au.user_id " +
                    "WHERE au.account_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, account.getId());
                try (ResultSet rs = stmt.executeQuery()) {
                    List<User> users = new ArrayList<>();
                    while (rs.next()) {
                        Customer customer = new Customer(
                                rs.getInt("id"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getString("email"),
                                rs.getString("phone_number"),
                                rs.getString("password")
                        );
                        users.add(customer);
                    }
                    account.setOwner(users);
                }
            }
        }
    }


    private String buildInsertSql(T obj) {
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(tableName).append(" (");

        List<String> fields = new ArrayList<>();
        if (obj instanceof Account) {
            fields.addAll(Arrays.asList("balance", "creation_time", "type"));
            if (obj instanceof CheckingAccount) {
                fields.add("transaction_fee");
            } else if (obj instanceof SavingsAccount) {
                fields.add("interest_rate");
            }
        } else if (obj instanceof User) {
            fields.addAll(Arrays.asList("first_name", "last_name", "email", "phone_number", "password", "type"));
            if (obj instanceof Customer) {

            } else if (obj instanceof Employee) {
                fields.addAll(Arrays.asList("salary", "role"));
            }
        }

        sql.append(String.join(", ", fields)).append(") VALUES (");
        sql.append(String.join(", ", Collections.nCopies(fields.size(), "?"))).append(")");
        return sql.toString();
    }


    private void populateInsertStatement(PreparedStatement stmt, T obj) throws SQLException {
        int index = 1;

        if (obj instanceof Account) {
            Account account = (Account) obj;
            stmt.setDouble(index++, account.getBalance());
            stmt.setTimestamp(index++, Timestamp.valueOf(account.getCreationTime()));

            // Setăm tipul contului
            if (obj instanceof CheckingAccount) {
                stmt.setString(index++, "CHECKING");
                stmt.setDouble(index++, ((CheckingAccount) account).getTransactionFee());
            } else if (obj instanceof SavingsAccount) {
                stmt.setString(index++, "SAVINGS");
                stmt.setDouble(index++, ((SavingsAccount) account).getInterestRate());
            }
        } else if (obj instanceof User) {
            User user = (User) obj;
            stmt.setString(index++, user.getFirstName());
            stmt.setString(index++, user.getLastName());
            stmt.setString(index++, user.getEmail());
            stmt.setString(index++, user.getPhoneNumber());
            stmt.setString(index++, user.getPassword());

            // Setăm tipul utilizatorului
            if (obj instanceof Customer) {
                stmt.setString(index++, "CUSTOMER");
            } else if (obj instanceof Employee) {
                stmt.setString(index++, "EMPLOYEE");
                Employee employee = (Employee) obj;
                stmt.setInt(index++, employee.getSalary());
                stmt.setString(index++, employee.getRole());
            }
        } else if (obj instanceof CoOwnershipRequest) {
            CoOwnershipRequest request = (CoOwnershipRequest) obj;

            // Inserăm cererea de co-ownership
            stmt.setInt(index++, request.getAccount().getId()); // account_id
            stmt.setInt(index++, request.getRequester().getId()); // requester_id
            stmt.setInt(index++, request.getAccountOwner().getId()); // owner_id
            stmt.setBoolean(index++, request.isApproved()); // approved (true/false)
        }
    }



    private String buildUpdateSql(T obj) {
        StringBuilder sql = new StringBuilder("UPDATE ");
        sql.append(tableName).append(" SET ");

        List<String> fields = new ArrayList<>();
        if (obj instanceof Account) {
            fields.addAll(Arrays.asList("balance = ?", "creation_time = ?"));
            if (obj instanceof CheckingAccount) {
                fields.add("transaction_fee = ?");
            } else if (obj instanceof SavingsAccount) {
                fields.add("interest_rate = ?");
            }
        } else if (obj instanceof User) {
            fields.addAll(Arrays.asList("first_name = ?", "last_name = ?", "email = ?", "phone_number = ?", "password = ?"));
            if (obj instanceof Employee) {
                fields.addAll(Arrays.asList("salary = ?", "role = ?"));
            }
        }

        sql.append(String.join(", ", fields)).append(" WHERE id = ?");
        return sql.toString();
    }


    private void populateUpdateStatement(PreparedStatement stmt, T obj) throws SQLException {
        int index = 1;

        if (obj instanceof Account) {
            Account account = (Account) obj;
            stmt.setDouble(index++, account.getBalance());
            stmt.setTimestamp(index++, Timestamp.valueOf(account.getCreationTime()));

            if (obj instanceof CheckingAccount) {
                stmt.setDouble(index++, ((CheckingAccount) account).getTransactionFee());
            } else if (obj instanceof SavingsAccount) {
                stmt.setDouble(index++, ((SavingsAccount) account).getInterestRate());
            }
        } else if (obj instanceof User) {
            User user = (User) obj;
            stmt.setString(index++, user.getFirstName());
            stmt.setString(index++, user.getLastName());
            stmt.setString(index++, user.getEmail());
            stmt.setString(index++, user.getPhoneNumber());
            stmt.setString(index++, user.getPassword());

            if (obj instanceof Employee) {
                Employee employee = (Employee) obj;
                stmt.setInt(index++, employee.getSalary());
                stmt.setString(index++, employee.getRole());
            }
        }

        stmt.setInt(index, obj.getId());
    }

    private void populateAccountUserRelationship(Connection conn, Account account) throws SQLException {
        String sqlCheck = "SELECT COUNT(*) FROM accountuser WHERE account_id = ? AND user_id = ?";
        String sqlInsert = "INSERT INTO accountuser (account_id, user_id) VALUES (?, ?)";

        try (PreparedStatement checkStmt = conn.prepareStatement(sqlCheck)) {
            for (User owner : account.getCustomers()) {
                checkStmt.setInt(1, account.getId());
                checkStmt.setInt(2, owner.getId());

                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        // Dacă nu există deja relația, o adăugăm
                        try (PreparedStatement insertStmt = conn.prepareStatement(sqlInsert)) {
                            insertStmt.setInt(1, account.getId());
                            insertStmt.setInt(2, owner.getId());
                            insertStmt.executeUpdate();
                        }
                    }
                }
            }
        }
    }
}
