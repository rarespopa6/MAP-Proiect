package org.bank.service;

import org.bank.model.Customer;
import org.bank.model.Loan;

import java.util.List;

public class LoanService {
    public LoanService() {
    }

    public void getNewLoan(Customer borrower, double loanAmount, int termMonths) {
        Loan loan = new Loan(borrower, loanAmount, termMonths);
        List<Loan> loans = borrower.getLoanList();
        loan.setId(loans.size());
        borrower.addLoan(loan);

    }

    public void payLoan(Customer borrower, Loan loan, double payment) {
        double remainingAmount = loan.getLoanAmount() - payment;
        loan.setLoanAmount(remainingAmount);
        if (remainingAmount <= 0) {
            borrower.removeLoan(loan);
            System.out.println("Loan fully paid off!");
        }
    }

    public List<Loan> getLoans(Customer borrower) {
        return borrower.getLoanList();
    }
}
