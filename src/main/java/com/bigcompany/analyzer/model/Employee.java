package com.bigcompany.analyzer.model;

/**
 * Represents an employee in the organization.
 */
public class Employee {
    private final String id;
    private final String firstName;
    private final String lastName;
    private final double salary;
    private final String managerId;

    public Employee(String id, String firstName, String lastName, double salary, String managerId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
        this.managerId = managerId;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public double getSalary() {
        return salary;
    }

    public String getManagerId() {
        return managerId;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isCEO() {
        return managerId == null || managerId.trim().isEmpty();
    }

    @Override
    public String toString() {
        return String.format("%s %s (ID: %s, Salary: %.2f)", 
            firstName, lastName, id, salary);
    }
}
