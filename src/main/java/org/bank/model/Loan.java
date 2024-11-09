package org.bank.model;


/**
 * Represents a loan that a customer can take out from the bank.
 * Requires a borrower, loan amount, interest rate, and term in months.
 */
public class Loan {
    private int id;
    private Customer borrower;
    private double loanAmount;
    private static double interestRate = 0.05;
    private int termMonths;

    /**
     * @param borrower the customer who is taking out the loan
     * @param loanAmount the amount of the loan
     * @param termMonths the term of the loan in months
     */
    public Loan(Customer borrower, double loanAmount, int termMonths) {
        this.borrower = borrower;
        this.loanAmount = loanAmount;
        this.termMonths = termMonths;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Customer getBorrower() {
        return borrower;
    }

    public void setBorrower(Customer borrower) {
        this.borrower = borrower;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(double amount) {
        this.loanAmount = amount;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public int getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(int termMonths) {
        this.termMonths = termMonths;
    }

    public double calculateMonthlyPayment() {
        double monthlyInterestRate = interestRate / 12;
        return this.loanAmount * monthlyInterestRate / (1 - Math.pow(1 + monthlyInterestRate, - this.termMonths));
    }

    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", borrower=" + borrower +
                ", loanAmount=" + loanAmount +
                ", termMonths=" + termMonths +
                '}';
    }
}
