package com.bigcompany.analyzer.service;

import com.bigcompany.analyzer.model.Employee;
import com.bigcompany.analyzer.repository.EmployeeRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Analyzes manager salaries against their subordinates' average salaries.
 * 
 * Assumption: A manager should earn between 20% and 50% more than the 
 * average salary of their DIRECT subordinates only.
 */
public class SalaryAnalyzer {
    private static final double MIN_SALARY_RATIO = 1.20; // 20% more
    private static final double MAX_SALARY_RATIO = 1.50; // 50% more

    private final EmployeeRepository repository;

    public SalaryAnalyzer(EmployeeRepository repository) {
        this.repository = repository;
    }

    public static class SalaryIssue {
        private final Employee manager;
        private final double currentSalary;
        private final double averageSubordinateSalary;
        private final double expectedMinSalary;
        private final double expectedMaxSalary;
        private final double deviation;
        private final boolean earningTooLittle;

        public SalaryIssue(Employee manager, double avgSubSalary, double deviation, boolean earningTooLittle) {
            this.manager = manager;
            this.currentSalary = manager.getSalary();
            this.averageSubordinateSalary = avgSubSalary;
            this.expectedMinSalary = avgSubSalary * MIN_SALARY_RATIO;
            this.expectedMaxSalary = avgSubSalary * MAX_SALARY_RATIO;
            this.deviation = deviation;
            this.earningTooLittle = earningTooLittle;
        }

        public Employee getManager() {
            return manager;
        }

        public double getDeviation() {
            return deviation;
        }

        public boolean isEarningTooLittle() {
            return earningTooLittle;
        }

        @Override
        public String toString() {
            String direction = earningTooLittle ? "less" : "more";
            String expected = earningTooLittle ? 
                String.format("%.2f", expectedMinSalary) : 
                String.format("%.2f", expectedMaxSalary);
            
            return String.format("%s earns %s than they should by %.2f " +
                "(current: %.2f, avg subordinate: %.2f, expected %s: %s)",
                manager.getFullName(),
                direction,
                Math.abs(deviation),
                currentSalary,
                averageSubordinateSalary,
                earningTooLittle ? "minimum" : "maximum",
                expected);
        }
    }

    public List<SalaryIssue> analyzeManagerSalaries() {
        List<SalaryIssue> issues = new ArrayList<>();

        for (Employee employee : repository.getAllEmployees()) {
            if (repository.isManager(employee.getId())) {
                SalaryIssue issue = checkManagerSalary(employee);
                if (issue != null) {
                    issues.add(issue);
                }
            }
        }

        return issues;
    }

    private SalaryIssue checkManagerSalary(Employee manager) {
        List<Employee> subordinates = repository.getDirectSubordinates(manager.getId());
        
        if (subordinates.isEmpty()) {
            return null; // No subordinates, no rule to check
        }

        double avgSubordinateSalary = calculateAverageSalary(subordinates);
        double minExpectedSalary = avgSubordinateSalary * MIN_SALARY_RATIO;
        double maxExpectedSalary = avgSubordinateSalary * MAX_SALARY_RATIO;
        double currentSalary = manager.getSalary();

        if (currentSalary < minExpectedSalary) {
            double deviation = minExpectedSalary - currentSalary;
            return new SalaryIssue(manager, avgSubordinateSalary, deviation, true);
        } else if (currentSalary > maxExpectedSalary) {
            double deviation = currentSalary - maxExpectedSalary;
            return new SalaryIssue(manager, avgSubordinateSalary, deviation, false);
        }

        return null; // Salary is within acceptable range
    }

    private double calculateAverageSalary(List<Employee> employees) {
        return employees.stream()
            .mapToDouble(Employee::getSalary)
            .average()
            .orElse(0.0);
    }
}
