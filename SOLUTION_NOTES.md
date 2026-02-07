# Solution Notes - Big Company Organizational Analyzer

## Overview
This solution provides a clean, well-tested Java application to analyze organizational structure focusing on manager compensation and reporting line depth.

## Key Design Decisions

### 1. Assumptions Made

#### Manager Salary Rules
- **20-50% rule applies to direct subordinates only**: The average is calculated from direct reports, not all downstream employees.
- **Employees without subordinates are excluded**: Only actual managers (those with direct reports) are evaluated.
- **Boundary inclusive**: Exactly 20% more (minimum) and exactly 50% more (maximum) are considered acceptable.

Example:
```
Manager salary: 60000
Direct subordinates: [45000, 47000]
Average: 46000
Acceptable range: [55200, 69000]
Result: 60000 is within range ✓
```

#### Reporting Line Rules
- **"Managers between"**: Counts all managers in the chain, excluding both the employee and the CEO.
- **Example chain**: Employee → Manager1 → Manager2 → CEO = 2 managers between
- **Maximum allowed**: 4 managers between employee and CEO
- **Excess calculation**: actual_count - 4

### 2. Architecture

```
┌─────────────────────────────────────────┐
│     OrganizationalAnalyzer (Main)       │
│  - Orchestrates analysis                │
│  - Handles I/O and presentation         │
└──────────────┬──────────────────────────┘
               │
       ┌───────┴────────┐
       │                │
┌──────▼─────┐   ┌──────▼──────┐
│  Salary    │   │  Reporting  │
│  Analyzer  │   │    Line     │
│            │   │  Analyzer   │
└──────┬─────┘   └──────┬──────┘
       │                │
       └───────┬────────┘
               │
     ┌─────────▼──────────┐
     │   Employee         │
     │   Repository       │
     │  - Data loading    │
     │  - Validation      │
     │  - Relationships   │
     └────────────────────┘
```

### 3. Code Quality Features

#### Immutability
- `Employee` class uses `final` fields
- Value objects (`SalaryIssue`, `ReportingLineIssue`) are immutable
- Thread-safe by design

#### Error Handling
- Comprehensive validation in `EmployeeRepository`:
  - Ensures exactly one CEO exists
  - Validates all manager IDs reference valid employees
  - Checks for circular references (max 1000 iterations)
  - Validates salary format
- Clear error messages for debugging

#### Testability
- Dependency injection: Services accept repository as constructor parameter
- Isolated unit tests for each component
- No static methods (except main)
- Clear separation of concerns

#### Readability
- Descriptive method names: `calculateAverageSalary()`, `buildReportingChain()`
- Comprehensive JavaDoc comments
- Well-structured output with clear formatting
- Meaningful variable names

### 4. Algorithm Details

#### Salary Analysis Algorithm
```java
for each employee:
    if employee.hasDirectSubordinates():
        avgSubSalary = average(directSubordinates.salaries)
        minExpected = avgSubSalary * 1.20
        maxExpected = avgSubSalary * 1.50
        
        if salary < minExpected:
            report: underpaid by (minExpected - salary)
        else if salary > maxExpected:
            report: overpaid by (salary - maxExpected)
```

Time Complexity: O(n) where n = number of employees
Space Complexity: O(n) for subordinates map

#### Reporting Line Analysis Algorithm
```java
for each employee (except CEO):
    chain = []
    current = employee
    
    while current != CEO:
        chain.add(current)
        current = current.manager
    
    managersCount = chain.length - 2  // exclude employee and CEO
    
    if managersCount > 4:
        report: too long by (managersCount - 4)
```

Time Complexity: O(n * d) where n = employees, d = max depth
Space Complexity: O(d) for each chain

### 5. Testing Strategy

#### Unit Tests Cover:
- **Happy path**: Valid data processing
- **Edge cases**: 
  - Exactly at boundaries (20%, 50%)
  - Multiple managers at different levels
  - Deep hierarchies
- **Error cases**:
  - No CEO
  - Multiple CEOs
  - Invalid manager references
  - Circular references
  - Invalid salary formats

#### Test Organization:
- `EmployeeRepositoryTest`: Data loading and validation
- `SalaryAnalyzerTest`: Manager compensation rules
- `ReportingLineAnalyzerTest`: Hierarchy depth analysis

### 6. Sample Output Explanation

```
SALARY ANALYSIS
--------------------------------------------------------------------------------

Managers earning LESS than they should:
  • Martin Chekov earns less than they should by 15000.00 
    (current: 45000.00, avg subordinate: 50000.00, expected minimum: 60000.00)
```

**Calculation breakdown:**
- Martin's salary: 45,000
- Alice's salary (direct subordinate): 50,000
- Expected minimum: 50,000 × 1.20 = 60,000
- Deviation: 60,000 - 45,000 = 15,000 (underpaid)

### 7. Extension Points

The design supports easy extension:

1. **Additional Rules**: Create new analyzer services implementing similar patterns
2. **Different Data Sources**: Implement alternative repository (database, REST API)
3. **Custom Reports**: Extend main class with new report methods
4. **Configurable Thresholds**: Extract constants to configuration file

### 8. Performance Considerations

- **Scalability**: Tested up to 1,000 employees as specified
- **Optimization**: Single-pass algorithms where possible
- **Memory**: O(n) storage for employee data and relationships
- **No Performance Bottlenecks**: All operations are O(n) or O(n*d) where d is bounded by organizational depth

### 9. Building and Running

```bash
# Build
mvn clean package

# Run tests
mvn test

# Execute
java -jar target/organizational-analyzer-1.0-SNAPSHOT.jar employees.csv

# Or with Maven exec plugin
mvn exec:java -Dexec.mainClass="com.bigcompany.analyzer.OrganizationalAnalyzer" \
              -Dexec.args="employees.csv"
```

### 10. Known Limitations and Future Enhancements

#### Current Limitations:
- CSV format only (not JSON, XML, or database)
- Single CEO requirement
- No support for matrix organizations
- English-only output

#### Potential Enhancements:
- Support for multiple data formats
- Configurable thresholds via properties file
- HTML/PDF report generation
- Integration with HR systems
- Historical trend analysis
- Salary recommendations
- Interactive CLI with options

## Conclusion

This solution prioritizes:
1. **Correctness**: Comprehensive tests ensure business rules are properly implemented
2. **Simplicity**: Clean architecture without over-engineering
3. **Readability**: Self-documenting code with clear naming
4. **Maintainability**: Well-structured with separation of concerns

The code is production-ready and can be extended as business needs evolve.
