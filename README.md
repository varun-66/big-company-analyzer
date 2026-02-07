# Big Company Organizational Analyzer

A Java application to analyze organizational structure and identify potential improvements in manager salaries and reporting lines.

## Features

The application analyzes employee data and reports:

1. **Manager Salary Analysis**
   - Identifies managers earning less than 20% above their subordinates' average salary
   - Identifies managers earning more than 50% above their subordinates' average salary
   - Reports the deviation amount for each issue

2. **Reporting Line Analysis**
   - Identifies employees with more than 4 managers between them and the CEO
   - Shows the complete reporting chain
   - Reports how many excess levels exist

## Requirements

- Java 17 or higher
- Maven 3.6 or higher

## Building the Application

```bash
mvn clean package
```

This will compile the code, run all tests, and create an executable JAR file in the `target/` directory.

## Running Tests

```bash
mvn test
```

## Running the Application

```bash
java -jar target/organizational-analyzer-1.0-SNAPSHOT.jar employees.csv
```

Or using Maven:

```bash
mvn exec:java -Dexec.mainClass="com.bigcompany.analyzer.OrganizationalAnalyzer" -Dexec.args="employees.csv"
```

## Input File Format

The application expects a CSV file with the following structure:

```csv
Id,firstName,lastName,salary,managerId
123,Joe,Doe,60000,
124,Martin,Chekov,45000,123
125,Bob,Ronstad,47000,123
```

### File Requirements

- First line must be a header (will be skipped)
- Each subsequent line represents one employee
- Fields: `Id`, `firstName`, `lastName`, `salary`, `managerId`
- CEO has an empty `managerId` field
- Maximum 1000 employees supported
- Empty lines are ignored

## Assumptions

The following assumptions were made in the implementation:

1. **Manager Salary Rules**
   - A manager should earn between 20% and 50% more than the average salary of their **direct subordinates only**
   - Employees without subordinates are not evaluated (not considered managers)
   - The comparison is against the average, not individual subordinate salaries

2. **Reporting Line Rules**
   - "Managers between employee and CEO" counts all managers in the chain excluding the employee itself and excluding the CEO
   - For example: Employee → Manager1 → Manager2 → CEO = 2 managers between
   - Maximum allowed is 4 managers between employee and CEO
   - More than 4 is considered too long

3. **Data Validation**
   - There must be exactly one CEO (employee with no manager)
   - All non-CEO employees must have valid manager IDs
   - Salary values must be valid numbers
   - Employee IDs must be unique

4. **Error Handling**
   - Invalid data (missing CEO, circular references, invalid manager IDs) will cause the application to exit with an error message
   - File not found or read errors are reported clearly

## Project Structure

```
organizational-analyzer/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/bigcompany/analyzer/
│   │           ├── OrganizationalAnalyzer.java    # Main application
│   │           ├── model/
│   │           │   └── Employee.java               # Employee model
│   │           ├── repository/
│   │           │   └── EmployeeRepository.java     # Data loading
│   │           └── service/
│   │               ├── SalaryAnalyzer.java         # Salary analysis
│   │               └── ReportingLineAnalyzer.java  # Reporting line analysis
│   └── test/
│       └── java/
│           └── com/bigcompany/analyzer/
│               ├── repository/
│               │   └── EmployeeRepositoryTest.java
│               └── service/
│                   ├── SalaryAnalyzerTest.java
│                   └── ReportingLineAnalyzerTest.java
├── employees.csv              # Sample data file
├── pom.xml                    # Maven configuration
└── README.md                  # This file
```

## Design Decisions

### Code Quality
- **Immutability**: Employee model uses final fields for thread safety
- **Separation of Concerns**: Clear separation between data access (repository), business logic (services), and presentation (main class)
- **Testability**: Services accept repository as dependency for easy testing
- **Readability**: Descriptive method and variable names, comprehensive comments

### Architecture
- **Repository Pattern**: Centralizes data loading and employee lookups
- **Service Layer**: Encapsulates business rules for salary and reporting line analysis
- **Value Objects**: `SalaryIssue` and `ReportingLineIssue` clearly represent analysis results

### Testing Strategy
- Unit tests for each component
- Edge case testing (boundaries, empty data, invalid input)
- Integration testing through temporary file creation
- JUnit 5 for modern testing features

## Example Output

```
================================================================================
ORGANIZATIONAL STRUCTURE ANALYSIS
================================================================================

SALARY ANALYSIS
--------------------------------------------------------------------------------

Managers earning LESS than they should:

  • Martin Chekov earns less than they should by 15000.00 
    (current: 45000.00, avg subordinate: 50000.00, expected minimum: 60000.00)

Total issues found: 1 (1 underpaid, 0 overpaid)

REPORTING LINE ANALYSIS
--------------------------------------------------------------------------------

✓ All employees have acceptable reporting line length (max 4 managers).

================================================================================
ANALYSIS COMPLETE
================================================================================
```

## License

This is a sample application for Big Company organizational analysis.
