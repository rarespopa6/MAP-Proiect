import org.bank.config.DBConfig;
import org.bank.model.Customer;
import org.bank.model.Loan;
import org.bank.service.LoanService;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApplicationTest {
    LoanService loanService = new LoanService();


        @Test
        public void testDBConfig() {
            assertEquals("jdbc:postgresql://localhost:5432/bank", DBConfig.DB_URL);
            assertEquals("postgres", DBConfig.DB_USER);
            assertEquals("admin", DBConfig.DB_PASSWORD);
            assertEquals("Users", DBConfig.USERS_TABLE);
            assertEquals("Accounts", DBConfig.ACCOUNTS_TABLE);
            assertEquals("AccountUser", DBConfig.ACCOUNT_USER_TABLE);
            assertEquals("coownership_request", DBConfig.COOWNERSHIP_TABLE);
            assertEquals("Transactions", DBConfig.TRANSACTIONS_TABLE);
            assertEquals("Loans", DBConfig.LOANS_TABLE);
        }

        @Test
        public void testCrudOperations() {
            Customer borrower = new Customer(1);
            loanService.getNewLoan(borrower, 1000, 12);
            Loan loan = borrower.getLoanList().getFirst();
            assertEquals(1000, loan.getLoanAmount());
            assertEquals(12, loan.getTermMonths());
            loanService.payLoan(borrower, loan, 500);
            assertEquals(500, loan.getLoanAmount());
            loanService.payLoan(borrower, loan, 500);
            assertEquals(0, loan.getLoanAmount());
        }
}
