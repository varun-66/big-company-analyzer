package com.bigcompany.analyzer.repository;

import com.bigcompany.analyzer.model.Employee;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Loads and manages employee data from CSV files.
 */
public class EmployeeRepository {
    private static final String CSV_DELIMITER = ",";
    private final Map<String, Employee> employees = new HashMap<>();
    private final Map<String, List<Employee>> subordinatesMap = new HashMap<>();
    private Employee ceo;

    /**
     * Loads employees from a CSV file.
     * 
     * Assumptions:
     * - First line is header and will be skipped
     * - CSV format: Id,firstName,lastName,salary,managerId
     * - Empty managerId indicates CEO
     * - Salary values are valid numbers
     * 
     * @param filePath path to the CSV file
     * @throws IOException if file cannot be read
     * @throws IllegalArgumentException if data is invalid
     */
    public void loadFromFile(String filePath) throws IOException {
        employees.clear();
        subordinatesMap.clear();
        ceo = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip header
                }

                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }

                Employee employee = parseEmployee(line);
                employees.put(employee.getId(), employee);

                if (employee.isCEO()) {
                    if (ceo != null) {
                        throw new IllegalArgumentException(
                            "Multiple CEOs found: " + ceo.getId() + " and " + employee.getId());
                    }
                    ceo = employee;
                }
            }
        }

        if (ceo == null) {
            throw new IllegalArgumentException("No CEO found in the data");
        }

        buildSubordinatesMap();
    }

    private Employee parseEmployee(String line) {
        String[] parts = line.split(CSV_DELIMITER, -1); // -1 to keep empty trailing fields
        
        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid CSV line: " + line);
        }

        String id = parts[0].trim();
        String firstName = parts[1].trim();
        String lastName = parts[2].trim();
        double salary;
        
        try {
            salary = Double.parseDouble(parts[3].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid salary for employee " + id + ": " + parts[3]);
        }

        String managerId = parts[4].trim();
        if (managerId.isEmpty()) {
            managerId = null;
        }

        return new Employee(id, firstName, lastName, salary, managerId);
    }

    private void buildSubordinatesMap() {
        subordinatesMap.clear();

        for (Employee employee : employees.values()) {
            if (!employee.isCEO()) {
                String managerId = employee.getManagerId();
                
                if (!employees.containsKey(managerId)) {
                    throw new IllegalArgumentException(
                        "Employee " + employee.getId() + " has invalid manager ID: " + managerId);
                }

                subordinatesMap.computeIfAbsent(managerId, k -> new ArrayList<>()).add(employee);
            }
        }
    }

    public Employee getEmployee(String id) {
        return employees.get(id);
    }

    public Collection<Employee> getAllEmployees() {
        return employees.values();
    }

    public List<Employee> getDirectSubordinates(String managerId) {
        return subordinatesMap.getOrDefault(managerId, Collections.emptyList());
    }

    public Employee getCEO() {
        return ceo;
    }

    public boolean isManager(String employeeId) {
        return subordinatesMap.containsKey(employeeId);
    }
}
