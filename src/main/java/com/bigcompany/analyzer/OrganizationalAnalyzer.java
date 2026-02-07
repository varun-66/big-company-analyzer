package com.bigcompany.analyzer;

import com.bigcompany.analyzer.repository.EmployeeRepository;
import com.bigcompany.analyzer.service.ReportingLineAnalyzer;
import com.bigcompany.analyzer.service.SalaryAnalyzer;

import java.io.IOException;
import java.util.List;

/**
 * Main application to analyze organizational structure.
 * 
 * Usage: java -jar organizational-analyzer.jar <path-to-csv-file>
 */
public class OrganizationalAnalyzer {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java -jar organizational-analyzer.jar <path-to-csv-file>");
            System.exit(1);
        }

        String filePath = args[0];

        try {
            OrganizationalAnalyzer analyzer = new OrganizationalAnalyzer();
            analyzer.analyze(filePath);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid data: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void analyze(String filePath) throws IOException {
        // Load employee data
        EmployeeRepository repository = new EmployeeRepository();
        repository.loadFromFile(filePath);

        System.out.println("=".repeat(80));
        System.out.println("ORGANIZATIONAL STRUCTURE ANALYSIS");
        System.out.println("=".repeat(80));
        System.out.println();

        // Analyze manager salaries
        analyzeSalaries(repository);
        
        System.out.println();
        
        // Analyze reporting lines
        analyzeReportingLines(repository);
        
        System.out.println();
        System.out.println("=".repeat(80));
        System.out.println("ANALYSIS COMPLETE");
        System.out.println("=".repeat(80));
    }

    private void analyzeSalaries(EmployeeRepository repository) {
        SalaryAnalyzer salaryAnalyzer = new SalaryAnalyzer(repository);
        List<SalaryAnalyzer.SalaryIssue> salaryIssues = salaryAnalyzer.analyzeManagerSalaries();

        System.out.println("SALARY ANALYSIS");
        System.out.println("-".repeat(80));

        if (salaryIssues.isEmpty()) {
            System.out.println("✓ All manager salaries are within acceptable range (20%-50% above average).");
        } else {
            // Separate issues into underpaid and overpaid
            List<SalaryAnalyzer.SalaryIssue> underpaid = salaryIssues.stream()
                .filter(SalaryAnalyzer.SalaryIssue::isEarningTooLittle)
                .toList();
            
            List<SalaryAnalyzer.SalaryIssue> overpaid = salaryIssues.stream()
                .filter(issue -> !issue.isEarningTooLittle())
                .toList();

            if (!underpaid.isEmpty()) {
                System.out.println("\nManagers earning LESS than they should:");
                System.out.println();
                for (SalaryAnalyzer.SalaryIssue issue : underpaid) {
                    System.out.println("  • " + issue);
                }
            }

            if (!overpaid.isEmpty()) {
                System.out.println("\nManagers earning MORE than they should:");
                System.out.println();
                for (SalaryAnalyzer.SalaryIssue issue : overpaid) {
                    System.out.println("  • " + issue);
                }
            }

            System.out.println();
            System.out.println(String.format("Total issues found: %d (%d underpaid, %d overpaid)",
                salaryIssues.size(), underpaid.size(), overpaid.size()));
        }
    }

    private void analyzeReportingLines(EmployeeRepository repository) {
        ReportingLineAnalyzer reportingAnalyzer = new ReportingLineAnalyzer(repository);
        List<ReportingLineAnalyzer.ReportingLineIssue> reportingIssues = 
            reportingAnalyzer.analyzeLongReportingLines();

        System.out.println("REPORTING LINE ANALYSIS");
        System.out.println("-".repeat(80));

        if (reportingIssues.isEmpty()) {
            System.out.println("✓ All employees have acceptable reporting line length (max 4 managers).");
        } else {
            System.out.println("\nEmployees with reporting lines that are TOO LONG:");
            System.out.println();
            
            for (ReportingLineAnalyzer.ReportingLineIssue issue : reportingIssues) {
                System.out.println("  • " + issue);
                System.out.println();
            }
            
            System.out.println(String.format("Total issues found: %d", reportingIssues.size()));
        }
    }
}
