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

class ReportingLineAnalyzerTest {

    private EmployeeRepository repository;
    private ReportingLineAnalyzer analyzer;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        repository = new EmployeeRepository();
        analyzer = new ReportingLineAnalyzer(repository);
    }

    @Test
    void testShortReportingLine() throws IOException {
        // Employee 124 -> Manager 123 (CEO)
        // 1 manager between employee and CEO (the manager is also CEO, so 0 actually)
        // Wait, let me recalculate: employee -> CEO means 0 managers between
        Path csvFile = createTestFile(
            "Id,firstName,lastName,salary,managerId",
            "123,Joe,Doe,60000,",
            "124,Martin,Chekov,45000,123"
        );

        repository.loadFromFile(csvFile.toString());
        List<ReportingLineAnalyzer.ReportingLineIssue> issues = analyzer.analyzeLongReportingLines();

        assertTrue(issues.isEmpty());
    }

    @Test
    void testReportingLineExactlyAtLimit() throws IOException {
        // Chain: 305 -> 304 -> 303 -> 302 -> 301 -> 123 (CEO)
        // That's 5 managers between 305 and CEO (counting all but CEO and employee)
        // Actually: employee -> mgr1 -> mgr2 -> mgr3 -> mgr4 -> CEO = 4 managers between
        Path csvFile = createTestFile(
            "Id,firstName,lastName,salary,managerId",
            "123,Joe,Doe,60000,",
            "301,Level1,Manager,55000,123",
            "302,Level2,Manager,50000,301",
            "303,Level3,Manager,45000,302",
            "304,Level4,Manager,40000,303",
            "305,Employee,Deep,35000,304"
        );

        repository.loadFromFile(csvFile.toString());
        List<ReportingLineAnalyzer.ReportingLineIssue> issues = analyzer.analyzeLongReportingLines();

        // 4 managers between should be OK
        assertTrue(issues.isEmpty());
    }

    @Test
    void testReportingLineTooLong() throws IOException {
        // Chain: 306 -> 305 -> 304 -> 303 -> 302 -> 301 -> 123 (CEO)
        // That's 5 managers between employee and CEO
        Path csvFile = createTestFile(
            "Id,firstName,lastName,salary,managerId",
            "123,Joe,Doe,60000,",
            "301,Level1,Manager,55000,123",
            "302,Level2,Manager,50000,301",
            "303,Level3,Manager,45000,302",
            "304,Level4,Manager,40000,303",
            "305,Level5,Manager,35000,304",
            "306,Employee,Deep,30000,305"
        );

        repository.loadFromFile(csvFile.toString());
        List<ReportingLineAnalyzer.ReportingLineIssue> issues = analyzer.analyzeLongReportingLines();

        assertEquals(1, issues.size());
        ReportingLineAnalyzer.ReportingLineIssue issue = issues.get(0);
        assertEquals("Employee", issue.getEmployee().getFirstName());
        assertEquals(5, issue.getManagersCount());
        assertEquals(1, issue.getExcessManagers());
    }

    @Test
    void testMultipleEmployeesWithLongReportingLines() throws IOException {
        Path csvFile = createTestFile(
            "Id,firstName,lastName,salary,managerId",
            "123,Joe,Doe,60000,",
            "301,Level1,Manager,55000,123",
            "302,Level2,Manager,50000,301",
            "303,Level3,Manager,45000,302",
            "304,Level4,Manager,40000,303",
            "305,Level5,Manager,35000,304",
            "306,Employee1,Deep,30000,305",
            "307,Employee2,Deep,30000,305"
        );

        repository.loadFromFile(csvFile.toString());
        List<ReportingLineAnalyzer.ReportingLineIssue> issues = analyzer.analyzeLongReportingLines();

        assertEquals(2, issues.size());
    }

    @Test
    void testReportingChainBuilding() throws IOException {
        Path csvFile = createTestFile(
            "Id,firstName,lastName,salary,managerId",
            "123,Joe,Doe,60000,",
            "301,Level1,Manager,55000,123",
            "302,Level2,Manager,50000,301",
            "303,Employee,Deep,45000,302"
        );

        repository.loadFromFile(csvFile.toString());
        List<ReportingLineAnalyzer.ReportingLineIssue> issues = analyzer.analyzeLongReportingLines();

        // Should have no issues (2 managers between employee and CEO)
        assertTrue(issues.isEmpty());
    }

    @Test
    void testVeryLongReportingLine() throws IOException {
        // Create a very deep hierarchy - 7 levels
        Path csvFile = createTestFile(
            "Id,firstName,lastName,salary,managerId",
            "123,Joe,Doe,60000,",
            "301,L1,Manager,55000,123",
            "302,L2,Manager,50000,301",
            "303,L3,Manager,45000,302",
            "304,L4,Manager,40000,303",
            "305,L5,Manager,35000,304",
            "306,L6,Manager,30000,305",
            "307,L7,Manager,25000,306",
            "308,Employee,Bottom,20000,307"
        );

        repository.loadFromFile(csvFile.toString());
        List<ReportingLineAnalyzer.ReportingLineIssue> issues = analyzer.analyzeLongReportingLines();

        assertEquals(1, issues.size());
        ReportingLineAnalyzer.ReportingLineIssue issue = issues.get(0);
        assertEquals(7, issue.getManagersCount());
        assertEquals(3, issue.getExcessManagers());
        
        // Verify reporting chain
        List<com.bigcompany.analyzer.model.Employee> chain = issue.getReportingChain();
        assertEquals(9, chain.size()); // employee + 7 managers + CEO
        assertEquals("Employee", chain.get(0).getFirstName());
        assertEquals("Joe", chain.get(chain.size() - 1).getFirstName());
    }

    private Path createTestFile(String... lines) throws IOException {
        Path file = tempDir.resolve("test.csv");
        Files.write(file, List.of(lines));
        return file;
    }
}
