import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationTest {
    private Connection connection;

    @BeforeEach
    void setUp() throws Exception {
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");

        // Run schema.sql
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("RUNSCRIPT FROM 'classpath:schema.sql'");
        }

        // Run data.sql
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO account (name, balance) VALUES ('Account A', 100.0)");
            stmt.execute("INSERT INTO account (name, balance) VALUES ('Account B', 100.0)");
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void testFullCrudOperations() throws Exception {
        // 1. CREATE
        String createSql = "INSERT INTO account (name, balance) VALUES (?, ?)";
        int createdId;
        try (PreparedStatement stmt = connection.prepareStatement(createSql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, "John Doe");
            stmt.setBigDecimal(2, new BigDecimal("1000.00"));
            int rowsAffected = stmt.executeUpdate();

            assertEquals(1, rowsAffected, "Insert should affect 1 row");

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                assertTrue(generatedKeys.next(), "Generated keys should be available");
                createdId = generatedKeys.getInt(1);
                assertTrue(createdId > 0, "Generated ID should be greater than 0");
            }
        }

        // 2. READ
        String readSql = "SELECT * FROM account WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(readSql)) {
            stmt.setInt(1, createdId);
            try (ResultSet rs = stmt.executeQuery()) {
                assertTrue(rs.next(), "ResultSet should contain data");
                assertEquals("John Doe", rs.getString("name"), "Name should match");
                assertEquals(new BigDecimal("1000.00"), rs.getBigDecimal("balance"), "Balance should match");
            }
        }

        // 3. UPDATE
        String updateSql = "UPDATE account SET balance = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(updateSql)) {
            stmt.setBigDecimal(1, new BigDecimal("1500.00"));
            stmt.setInt(2, createdId);
            int rowsAffected = stmt.executeUpdate();

            assertEquals(1, rowsAffected, "Update should affect 1 row");
        }

        // Verify the update
        try (PreparedStatement stmt = connection.prepareStatement(readSql)) {
            stmt.setInt(1, createdId);
            try (ResultSet rs = stmt.executeQuery()) {
                assertTrue(rs.next(), "ResultSet should contain data");
                assertEquals(new BigDecimal("1500.00"), rs.getBigDecimal("balance"), "Updated balance should match");
            }
        }

        // 4. DELETE
        String deleteSql = "DELETE FROM account WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteSql)) {
            stmt.setInt(1, createdId);
            int rowsAffected = stmt.executeUpdate();

            assertEquals(1, rowsAffected, "Delete should affect 1 row");
        }

        // Verify the deletion
        try (PreparedStatement stmt = connection.prepareStatement(readSql)) {
            stmt.setInt(1, createdId);
            try (ResultSet rs = stmt.executeQuery()) {
                assertFalse(rs.next(), "ResultSet should not contain any data after deletion");
            }
        }
    }


}
