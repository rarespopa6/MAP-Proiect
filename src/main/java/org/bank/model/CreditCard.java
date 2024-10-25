package org.bank.model;

import java.time.LocalDate;

public class CreditCard {
    private long cardId;
    private User owner;
    private Account account;

    private double cardLimit;
    private double currentBalance;
    private double interestRate;

    private long cardNumber;
    private LocalDate expiryDate;
    private int cvv;

    public CreditCard(long cardId, User owner, Account account, double cardLimit, double currentBalance, double interestRate, long cardNumber, LocalDate expiryDate, int cvv) {
        this.cardId = cardId;
        this.owner = owner;
        this.account = account;

        this.cardLimit = cardLimit;
        this.currentBalance = currentBalance;
        this.interestRate = interestRate;

        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
    }

    public void fund(double amount) {
        if (amount > 0 && (currentBalance + amount <= cardLimit)) {
            currentBalance += amount;
        } else {
            throw new RuntimeException("Charge amount exceeds card limit or is invalid.");
        }
    }

    public void makePayment(double amount) {
        if (amount > 0 && amount <= currentBalance) {
            currentBalance -= amount;
        } else {
            throw new RuntimeException("Payment amount exceeds current balance or is invalid.");
        }
    }

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public double getCardLimit() {
        return cardLimit;
    }

    public void setCardLimit(double cardLimit) {
        this.cardLimit = cardLimit;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public long getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(long cardNumber) {
        this.cardNumber = cardNumber;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getCvv() {
        return cvv;
    }

    public void setCvv(int cvv) {
        this.cvv = cvv;
    }

    @Override
    public String toString() {
        return "CreditCard{" +
                "cardId=" + cardId +
                ", owner=" + owner +
                ", account=" + account +
                ", cardLimit=" + cardLimit +
                ", currentBalance=" + currentBalance +
                ", interestRate=" + interestRate +
                ", cardNumber=" + cardNumber +
                ", expiryDate=" + expiryDate +
                ", cvv=" + cvv +
                '}';
    }
}
