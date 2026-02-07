package com.bigcompany.analyzer.service;

import com.bigcompany.analyzer.model.Employee;
import com.bigcompany.analyzer.repository.EmployeeRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Analyzes reporting line lengths in the organization.
 * 
 * Assumption: The reporting line is counted as the number of managers between
 * an employee and the CEO (not including the employee itself, but potentially 
 * including the CEO as the top manager). If there are MORE than 4 managers 
 * between an employee and CEO, it's too long.
 */
public class ReportingLineAnalyzer {
    private static final int MAX_MANAGERS_BETWEEN = 4;

    private final EmployeeRepository repository;

    public ReportingLineAnalyzer(EmployeeRepository repository) {
        this.repository = repository;
    }

    public static class ReportingLineIssue {
        private final Employee employee;
        private final int managersCount;
        private final int excessManagers;
        private final List<Employee> reportingChain;

        public ReportingLineIssue(Employee employee, int managersCount, List<Employee> reportingChain) {
            this.employee = employee;
            this.managersCount = managersCount;
            this.excessManagers = managersCount - MAX_MANAGERS_BETWEEN;
            this.reportingChain = reportingChain;
        }

        public Employee getEmployee() {
            return employee;
        }

        public int getManagersCount() {
            return managersCount;
        }

        public int getExcessManagers() {
            return excessManagers;
        }

        public List<Employee> getReportingChain() {
            return reportingChain;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%s has a reporting line that is too long by %d level(s) ",
                employee.getFullName(), excessManagers));
            sb.append(String.format("(%d managers between employee and CEO, maximum is %d)%n",
                managersCount, MAX_MANAGERS_BETWEEN));
            sb.append("  Reporting chain: ");
            
            for (int i = 0; i < reportingChain.size(); i++) {
                if (i > 0) {
                    sb.append(" -> ");
                }
                sb.append(reportingChain.get(i).getFullName());
            }
            
            return sb.toString();
        }
    }

    public List<ReportingLineIssue> analyzeLongReportingLines() {
        List<ReportingLineIssue> issues = new ArrayList<>();

        for (Employee employee : repository.getAllEmployees()) {
            if (!employee.isCEO()) {
                ReportingLineIssue issue = checkReportingLine(employee);
                if (issue != null) {
                    issues.add(issue);
                }
            }
        }

        return issues;
    }

    private ReportingLineIssue checkReportingLine(Employee employee) {
        List<Employee> reportingChain = buildReportingChain(employee);
        
        // Count managers between employee and CEO
        // reportingChain includes: employee -> manager1 -> manager2 -> ... -> CEO
        // So managers between = reportingChain.size() - 2 (exclude employee and CEO)
        // But we want to count all managers in the chain up to CEO
        // Let's reconsider: "managers between them and the CEO"
        // If employee -> manager -> CEO, there is 1 manager between
        // reportingChain = [employee, manager, CEO], size=3, managers between = 3-2 = 1
        
        int managersBetween = reportingChain.size() - 2;

        if (managersBetween > MAX_MANAGERS_BETWEEN) {
            return new ReportingLineIssue(employee, managersBetween, reportingChain);
        }

        return null;
    }

    /**
     * Builds the reporting chain from employee to CEO.
     * Returns list: [employee, manager1, manager2, ..., CEO]
     */
    private List<Employee> buildReportingChain(Employee employee) {
        List<Employee> chain = new ArrayList<>();
        Employee current = employee;

        // Prevent infinite loops in case of circular references
        int maxIterations = 1000;
        int iterations = 0;

        while (current != null && iterations < maxIterations) {
            chain.add(current);
            
            if (current.isCEO()) {
                break;
            }

            current = repository.getEmployee(current.getManagerId());
            iterations++;
        }

        if (iterations >= maxIterations) {
            throw new IllegalStateException(
                "Circular reference detected in reporting chain for employee: " + employee.getId());
        }

        return chain;
    }
}
