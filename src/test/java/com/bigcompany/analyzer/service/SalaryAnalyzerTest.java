package com.bigcompany.analyzer.service;

import com.bigcompany.analyzer.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SalaryAnalyzerTest {

    private EmployeeRepository repository;
    private SalaryAnalyzer analyzer;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        repository = new EmployeeRepository();
        analyzer = new SalaryAnalyzer(repository);
    }

    @Test
    void testManagerEarningWithinRange() throws IOException {
        // Average subordinate salary: (45000 + 47000) / 2 = 46000
        // Expected range: 55200 (20% more) to 69000 (50% more)
        // Manager salary: 60000 - within range
        Path csvFile = createTestFile(
            "Id,firstName,lastName,salary,managerId",
            "123,Joe,Doe,60000,",
            "124,Martin,Chekov,45000,123",
            "125,Bob,Ronstad,47000,123"
        );

        repository.loadFromFile(csvFile.toString());
        List<SalaryAnalyzer.SalaryIssue> issues = analyzer.analyzeManagerSalaries();

        assertTrue(issues.isEmpty());
    }

    @Test
    void testManagerEarningTooLittle() throws IOException {
        // Average subordinate salary: (45000 + 47000) / 2 = 46000
        // Expected minimum: 55200
        // Manager salary: 50000 - too low by 5200
        Path csvFile = createTestFile(
            "Id,firstName,lastName,salary,managerId",
            "123,Joe,Doe,50000,",
            "124,Martin,Chekov,45000,123",
            "125,Bob,Ronstad,47000,123"
        );

        repository.loadFromFile(csvFile.toString());
        List<SalaryAnalyzer.SalaryIssue> issues = analyzer.analyzeManagerSalaries();

        assertEquals(1, issues.size());
        SalaryAnalyzer.SalaryIssue issue = issues.get(0);
        assertTrue(issue.isEarningTooLittle());
        assertEquals("Joe", issue.getManager().getFirstName());
        assertEquals(5200, issue.getDeviation(), 0.01);
    }

    @Test
    void testManagerEarningTooMuch() throws IOException {
        // Average subordinate salary: (45000 + 47000) / 2 = 46000
        // Expected maximum: 69000
        // Manager salary: 75000 - too high by 6000
        Path csvFile = createTestFile(
            "Id,firstName,lastName,salary,managerId",
            "123,Joe,Doe,75000,",
            "124,Martin,Chekov,45000,123",
            "125,Bob,Ronstad,47000,123"
        );

        repository.loadFromFile(csvFile.toString());
        List<SalaryAnalyzer.SalaryIssue> issues = analyzer.analyzeManagerSalaries();

        assertEquals(1, issues.size());
        SalaryAnalyzer.SalaryIssue issue = issues.get(0);
        assertFalse(issue.isEarningTooLittle());
        assertEquals("Joe", issue.getManager().getFirstName());
        assertEquals(6000, issue.getDeviation(), 0.01);
    }

    @Test
    void testMultipleManagers() throws IOException {
        // CEO (123): subordinates avg = 46000, salary = 60000 (OK)
        // Manager (124): subordinate = 50000, salary = 45000 (too low)
        Path csvFile = createTestFile(
            "Id,firstName,lastName,salary,managerId",
            "123,Joe,Doe,60000,",
            "124,Martin,Chekov,45000,123",
            "125,Bob,Ronstad,47000,123",
            "300,Alice,Hasacat,50000,124"
        );

        repository.loadFromFile(csvFile.toString());
        List<SalaryAnalyzer.SalaryIssue> issues = analyzer.analyzeManagerSalaries();

        assertEquals(1, issues.size());
        assertEquals("Martin", issues.get(0).getManager().getFirstName());
        assertTrue(issues.get(0).isEarningTooLittle());
    }

    @Test
    void testEmployeeWithoutSubordinatesNotAnalyzed() throws IOException {
        Path csvFile = createTestFile(
            "Id,firstName,lastName,salary,managerId",
            "123,Joe,Doe,60000,",
            "124,Martin,Chekov,45000,123"
        );

        repository.loadFromFile(csvFile.toString());
        List<SalaryAnalyzer.SalaryIssue> issues = analyzer.analyzeManagerSalaries();

        // Martin has no subordinates, so no issue should be reported
        assertTrue(issues.isEmpty());
    }

    @Test
    void testExactBoundaries() throws IOException {
        // Test at exactly 20% more (minimum boundary)
        // Avg = 50000, min = 60000
        Path csvFile1 = createTestFile(
            "Id,firstName,lastName,salary,managerId",
            "123,Joe,Doe,60000,",
            "124,Martin,Chekov,50000,123"
        );
        repository.loadFromFile(csvFile1.toString());
        List<SalaryAnalyzer.SalaryIssue> issues1 = analyzer.analyzeManagerSalaries();
        assertTrue(issues1.isEmpty());

        // Test at exactly 50% more (maximum boundary)
        // Avg = 50000, max = 75000
        repository = new EmployeeRepository();
        analyzer = new SalaryAnalyzer(repository);
        Path csvFile2 = createTestFile(
            "Id,firstName,lastName,salary,managerId",
            "123,Joe,Doe,75000,",
            "124,Martin,Chekov,50000,123"
        );
        repository.loadFromFile(csvFile2.toString());
        List<SalaryAnalyzer.SalaryIssue> issues2 = analyzer.analyzeManagerSalaries();
        assertTrue(issues2.isEmpty());
    }

    private Path createTestFile(String... lines) throws IOException {
        Path file = tempDir.resolve("test.csv");
        Files.write(file, List.of(lines));
        return file;
    }
}
