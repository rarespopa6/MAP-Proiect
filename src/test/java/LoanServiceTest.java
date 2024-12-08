package org.bank.service;

import org.bank.model.Customer;
import org.bank.model.Loan;
import org.bank.model.exception.ValidationException;
import org.bank.repository.DBRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LoanServiceTest {

    @Mock
    private DBRepository<Loan> loanRepository;

    @Mock
    private DBRepository<Customer> customerRepository;

    @InjectMocks
    private LoanService loanService;

    private Customer customer;

    @BeforeEach
    public void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Create a mock customer
        customer = new Customer(1);
    }

    // Test for `getNewLoan`
    @Test
    public void testGetNewLoanValid() {
        Loan loan = new Loan(customer, 1000.0, 24);
        when(loanRepository.create(any(Loan.class))).thenReturn(1); // Mocking create to return a mock ID

        loanService.getNewLoan(customer, 1000.0, 24);

        // Verify that loanRepository.create() was called
        verify(loanRepository).create(any(Loan.class));
        assertEquals(1, loan.getId());  // Ensure that the loan ID was set correctly
        assertTrue(customer.getLoanList().contains(loan));  // Verify loan is added to customer
    }

    @Test
    public void testGetNewLoanInvalidTerm() {
        assertThrows(ValidationException.class, () -> {
            loanService.getNewLoan(customer, 1000.0, 5); // Invalid term (less than 6)
        });

        assertThrows(ValidationException.class, () -> {
            loanService.getNewLoan(customer, 1000.0, 121); // Invalid term (greater than 120)
        });
    }

    // Test for `payLoan`
    @Test
    public void testPayLoanFullPayment() {
        Loan loan = new Loan(customer, 1000.0, 24);
        loan.setId(1);
        when(loanRepository.findAll()).thenReturn(List.of(loan)); // Mocking the loan repository to return the loan
        when(loanRepository.update(any(Loan.class))).thenReturn(1);
        when(loanRepository.delete(anyInt())).thenReturn(1);

        double paid = loanService.payLoan(customer, loan, 1000.0);

        assertEquals(1000.0, paid);  // Full payment
        verify(loanRepository).delete(loan.getId()); // Verify delete was called
        verify(loanRepository).update(any(Loan.class)); // Verify update was called
        assertFalse(customer.getLoanList().contains(loan)); // Loan should be removed from customer's loan list
    }

    @Test
    public void testPayLoanPartialPayment() {
        Loan loan = new Loan(customer, 1000.0, 24);
        loan.setId(1);
        when(loanRepository.findAll()).thenReturn(List.of(loan)); // Mocking the loan repository to return the loan
        when(loanRepository.update(loan).thenReturn(loan);

        double paid = loanService.payLoan(customer, loan, 500.0);

        assertEquals(500.0, paid); // Partial payment
        verify(loanRepository).update(any(Loan.class)); // Verify update was called
        assertEquals(500.0, loan.getLoanAmount()); // Ensure loan amount is updated
    }

    // Test for `getLoans`
    @Test
    public void testGetLoans() {
        Loan loan1 = new Loan(customer, 1000.0, 24);
        Loan loan2 = new Loan(customer, 2000.0, 36);
        when(loanRepository.findAll()).thenReturn(List.of(loan1, loan2)); // Mock repository to return loans

        List<Loan> loans = loanService.getLoans(customer);

        assertEquals(2, loans.size());  // Verify that the correct number of loans are returned
        assertTrue(loans.contains(loan1));
        assertTrue(loans.contains(loan2));
    }

    @Test
    public void testGetLoansNoLoans() {
        when(loanRepository.findAll()).thenReturn(new ArrayList<>()); // Mock repository to return empty list

        List<Loan> loans = loanService.getLoans(customer);

        assertTrue(loans.isEmpty()); // No loans for this customer
    }

    // Test for `getLoanById`
    @Test
    public void testGetLoanByIdFound() {
        Loan loan = new Loan(customer, 1000.0, 24);
        loan.setId(1);
        when(loanRepository.findAll()).thenReturn(List.of(loan)); // Mock repository to return loan list

        Loan foundLoan = loanService.getLoanById(loan.getId(), customer);

        assertNotNull(foundLoan);  // Verify that the loan is found
        assertEquals(loan.getId(), foundLoan.getId());
    }

    @Test
    public void testGetLoanByIdNotFound() {
        Loan loan = new Loan(customer, 1000.0, 24);
        loan.setId(1);
        when(loanRepository.findAll()).thenReturn(List.of(loan)); // Mock repository to return loan list

        Loan foundLoan = loanService.getLoanById(999, customer);

        assertNull(foundLoan); // Loan does not exist
    }

    // Test for `getLoansSortedByAmount`
    @Test
    public void testGetLoansSortedByAmount() {
        Loan loan1 = new Loan(customer, 2000.0, 24);
        Loan loan2 = new Loan(customer, 1000.0, 24);
        when(loanRepository.findAll()).thenReturn(List.of(loan1, loan2)); // Mock repository to return loans

        List<Loan> sortedLoans = loanService.getLoansSortedByAmount(customer);

        assertEquals(2, sortedLoans.size());  // Verify the number of loans
        assertEquals(1000.0, sortedLoans.get(0).getLoanAmount());  // Ensure loans are sorted by amount
        assertEquals(2000.0, sortedLoans.get(1).getLoanAmount());
    }

    @Test
    public void testGetLoansSortedByAmountEmpty() {
        when(loanRepository.findAll()).thenReturn(new ArrayList<>()); // Mock repository to return empty list

        List<Loan> sortedLoans = loanService.getLoansSortedByAmount(customer);

        assertTrue(sortedLoans.isEmpty()); // No loans to sort
    }
}
