package com.bigcompany.analyzer.repository;

import com.bigcompany.analyzer.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeRepositoryTest {

    private EmployeeRepository repository;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        repository = new EmployeeRepository();
    }

    @Test
    void testLoadValidFile() throws IOException {
        Path csvFile = createTestFile(
            "Id,firstName,lastName,salary,managerId",
            "123,Joe,Doe,60000,",
            "124,Martin,Chekov,45000,123",
            "125,Bob,Ronstad,47000,123"
        );

        repository.loadFromFile(csvFile.toString());

        Collection<Employee> employees = repository.getAllEmployees();
        assertEquals(3, employees.size());

        Employee ceo = repository.getCEO();
        assertNotNull(ceo);
        assertEquals("123", ceo.getId());
        assertEquals("Joe", ceo.getFirstName());
        assertEquals("Doe", ceo.getLastName());
        assertEquals(60000, ceo.getSalary());
        assertTrue(ceo.isCEO());
    }

    @Test
    void testGetDirectSubordinates() throws IOException {
        Path csvFile = createTestFile(
            "Id,firstName,lastName,salary,managerId",
            "123,Joe,Doe,60000,",
            "124,Martin,Chekov,45000,123",
            "125,Bob,Ronstad,47000,123",
            "300,Alice,Hasacat,50000,124"
        );

        repository.loadFromFile(csvFile.toString());

        List<Employee> ceoSubordinates = repository.getDirectSubordinates("123");
        assertEquals(2, ceoSubordinates.size());

        List<Employee> martinSubordinates = repository.getDirectSubordinates("124");
        assertEquals(1, martinSubordinates.size());
        assertEquals("Alice", martinSubordinates.get(0).getFirstName());
    }

    @Test
    void testIsManager() throws IOException {
        Path csvFile = createTestFile(
            "Id,firstName,lastName,salary,managerId",
            "123,Joe,Doe,60000,",
            "124,Martin,Chekov,45000,123",
            "125,Bob,Ronstad,47000,123"
        );

        repository.loadFromFile(csvFile.toString());

        assertTrue(repository.isManager("123"));
        assertFalse(repository.isManager("124"));
        assertFalse(repository.isManager("125"));
    }

    @Test
    void testNoCEOThrowsException() throws IOException {
        Path csvFile = createTestFile(
            "Id,firstName,lastName,salary,managerId",
            "124,Martin,Chekov,45000,123",
            "125,Bob,Ronstad,47000,123"
        );

        assertThrows(IllegalArgumentException.class, () -> {
            repository.loadFromFile(csvFile.toString());
        });
    }

    @Test
    void testMultipleCEOsThrowsException() throws IOException {
        Path csvFile = createTestFile(
            "Id,firstName,lastName,salary,managerId",
            "123,Joe,Doe,60000,",
            "124,Martin,Chekov,45000,"
        );

        assertThrows(IllegalArgumentException.class, () -> {
            repository.loadFromFile(csvFile.toString());
        });
    }

    @Test
    void testInvalidManagerIdThrowsException() throws IOException {
        Path csvFile = createTestFile(
            "Id,firstName,lastName,salary,managerId",
            "123,Joe,Doe,60000,",
            "124,Martin,Chekov,45000,999"
        );

        assertThrows(IllegalArgumentException.class, () -> {
            repository.loadFromFile(csvFile.toString());
        });
    }

    @Test
    void testInvalidSalaryThrowsException() throws IOException {
        Path csvFile = createTestFile(
            "Id,firstName,lastName,salary,managerId",
            "123,Joe,Doe,invalid,",
            "124,Martin,Chekov,45000,123"
        );

        assertThrows(IllegalArgumentException.class, () -> {
            repository.loadFromFile(csvFile.toString());
        });
    }

    @Test
    void testEmptyLinesAreSkipped() throws IOException {
        Path csvFile = createTestFile(
            "Id,firstName,lastName,salary,managerId",
            "123,Joe,Doe,60000,",
            "",
            "124,Martin,Chekov,45000,123"
        );

        repository.loadFromFile(csvFile.toString());
        assertEquals(2, repository.getAllEmployees().size());
    }

    private Path createTestFile(String... lines) throws IOException {
        Path file = tempDir.resolve("test.csv");
        Files.write(file, List.of(lines));
        return file;
    }
}
