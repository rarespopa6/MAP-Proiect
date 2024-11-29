package org.bank.service;

import org.bank.model.Customer;
import org.bank.model.Loan;
import org.bank.model.exception.ValidationException;

import java.util.List;

/**
 * Service class that manages loans by providing functionality to create new loans, pay off loans, and retrieve loans
 */
public class LoanService {
    public LoanService() {
    }

    /**
     * Makes a new loan for a customer and adds it to the customer's loan list
     *
     * @param borrower the customer who is taking out the loan
     * @param loanAmount the amount of the loan
     * @param termMonths the term of the loan in months
     */
    public void getNewLoan(Customer borrower, double loanAmount, int termMonths) {
        if (termMonths < 6 || termMonths > 120) {
            throw new ValidationException("Invalid term Months (6-120).");
        }
        Loan loan = new Loan(borrower, loanAmount, termMonths);
        List<Loan> loans = borrower.getLoanList();
        loan.setId(loans.size() + 1);
        borrower.addLoan(loan);
    }

    /**
     * Used to pay off a loan. If the payment exceeds the loan amount,
     * the loan is paid off and removed from the customer's loan list.
     *
     * @param borrower the customer who is paying off the loan
     * @param loan the loan to be paid off
     * @param payment the amount to be paid off
     */
    public double payLoan(Customer borrower, Loan loan, double payment) {
        double paymentToBeProcessed = Math.min(payment, loan.getLoanAmount());

        double remainingAmount = loan.getLoanAmount() - paymentToBeProcessed;
        loan.setLoanAmount(remainingAmount);

        if (remainingAmount <= 0) {
            borrower.removeLoan(loan);
            System.out.println("Loan fully paid off!");
        } else {
            System.out.println("Payed successfully " + paymentToBeProcessed + ". Remaining: " + remainingAmount);
        }

        return paymentToBeProcessed;
    }


    /**
     * Retrieves all loans associated with a specified customer
     *
     * @param borrower the customer whose loans are to be retrieved
     * @return a list of loans associated with the specified customer
     */
    public List<Loan> getLoans(Customer borrower) {
        return borrower.getLoanList();
    }

    /**
     * @param loanId the id of the loan to be retrieved
     * @param borrower the customer who owns the loan
     * @return the loan with the specified id, or null if no such loan exists
     */
    public Loan getLoanById(int loanId, Customer borrower) {
        return getLoans(borrower).stream()
                .filter(l -> l.getId() == loanId)
                .findFirst()
                .orElse(null);
    }


    /**
     * Retrieves all loans associated with a specified customer, sorted by loan amount
     *
     * @param borrower the customer whose loans are to be retrieved
     * @return a list of loans associated with the specified customer, sorted by loan amount
     */
    public List<Loan> getLoansSortedByAmount(Customer borrower) {
        List<Loan> loans = getLoans(borrower);
        loans.sort((l1, l2) -> Double.compare(l1.getLoanAmount(), l2.getLoanAmount()));
        return loans;
    }
}
