package org.bank.model;

public class Employee extends User{
    private int salary;
    private String role;

    public Employee(int id, String firstName, String lastName, String email, String phoneNumber, int salary, String role) {
        super(id, firstName, lastName, email, phoneNumber);
        this.salary = salary;
        this.role = role;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Employee{" +
                super.toString() +
                " ,salary=" + salary +
                ", role='" + role + '\'' +
                '}';
    }
}
