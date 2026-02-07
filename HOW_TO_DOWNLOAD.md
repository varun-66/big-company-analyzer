# How to Check Project Structure and Download

## 📁 Project Structure Overview

```
big-company-analyzer/
│
├── 📄 README.md                    # Complete documentation
├── 📄 SOLUTION_NOTES.md            # Design decisions & implementation details
├── 📄 DELIVERY_SUMMARY.md          # Quick start guide
├── 📄 pom.xml                      # Maven build configuration
│
├── 📂 src/main/java/               # Production source code
│   └── com/bigcompany/analyzer/
│       ├── OrganizationalAnalyzer.java          # Main application
│       │
│       ├── 📂 model/
│       │   └── Employee.java                     # Employee data model
│       │
│       ├── 📂 repository/
│       │   └── EmployeeRepository.java           # Data loading & validation
│       │
│       └── 📂 service/
│           ├── SalaryAnalyzer.java               # Manager salary analysis
│           └── ReportingLineAnalyzer.java        # Reporting line depth analysis
│
├── 📂 src/test/java/               # JUnit 5 test suite
│   └── com/bigcompany/analyzer/
│       ├── 📂 repository/
│       │   └── EmployeeRepositoryTest.java
│       │
│       └── 📂 service/
│           ├── SalaryAnalyzerTest.java
│           └── ReportingLineAnalyzerTest.java
│
├── 📄 employees.csv                # Sample data from requirements
├── 📄 test-data.csv                # Complex test scenario
├── 📄 build.sh                     # Manual build script (if Maven unavailable)
└── 📄 verify.sh                    # Project verification script

```

## 🔍 How to Check the Project Structure

### Option 1: Using the File Browser (Easiest)

1. Look for the **folder icon** above (in Claude's interface)
2. Click on the **big-company-analyzer** folder
3. Browse through all the files in the web interface
4. You can view any file by clicking on it

### Option 2: Download and Explore Locally

1. **Click the download button** on the folder above
2. Extract the ZIP file on your computer
3. Open in your favorite IDE:
   - IntelliJ IDEA
   - Eclipse
   - VS Code
   - NetBeans

### Option 3: Import into IDE

After downloading:

#### For IntelliJ IDEA:
1. File → Open
2. Select the `big-company-analyzer` folder
3. IntelliJ will automatically detect it's a Maven project
4. Wait for Maven to download dependencies

#### For Eclipse:
1. File → Import → Existing Maven Projects
2. Select the `big-company-analyzer` folder
3. Click Finish

#### For VS Code:
1. File → Open Folder
2. Select `big-company-analyzer`
3. Install Java Extension Pack if prompted

## 📥 How to Download

### Method 1: Download via Claude Interface

Look for the **download icon** (⬇️) next to the folder name above, then:
1. Click the download button
2. Save the ZIP file to your computer
3. Extract it to your preferred location
4. Open in your IDE

### Method 2: Download Individual Files

If you want to review specific files first:
1. Click on any file in the browser above
2. Read the content
3. Copy the content or download individual files

## 🏗️ Building the Project

Once downloaded, open a terminal in the project directory:

```bash
# Navigate to project directory
cd big-company-analyzer

# Build with Maven
mvn clean package

# This will:
# - Compile all Java source files
# - Run all JUnit tests
# - Create an executable JAR file
```

## ✅ Verify Everything Works

### Run the verification script (Linux/Mac):
```bash
chmod +x verify.sh
./verify.sh
```

### Or manually check:
```bash
# Check Java version (need Java 17+)
java -version

# Check Maven version (need Maven 3.6+)
mvn -version

# Count source files
find src/main/java -name "*.java" | wc -l
# Should show: 5

# Count test files
find src/test/java -name "*.java" | wc -l
# Should show: 3
```

## 🧪 Running Tests

```bash
# Run all tests
mvn test

# Run tests with detailed output
mvn test -X

# Run a specific test class
mvn test -Dtest=SalaryAnalyzerTest
```

## 🚀 Running the Application

```bash
# After building with 'mvn clean package'
java -jar target/organizational-analyzer-1.0-SNAPSHOT.jar employees.csv

# Or run directly with Maven
mvn exec:java -Dexec.mainClass="com.bigcompany.analyzer.OrganizationalAnalyzer" \
              -Dexec.args="employees.csv"
```

## 📝 What to Review First

I recommend reviewing files in this order:

1. **DELIVERY_SUMMARY.md** - Quick overview (3 min read)
2. **README.md** - Complete documentation (5 min read)
3. **employees.csv** - Sample data format
4. **OrganizationalAnalyzer.java** - Main application logic
5. **SalaryAnalyzer.java** - Salary analysis implementation
6. **ReportingLineAnalyzer.java** - Reporting line implementation
7. **SOLUTION_NOTES.md** - Deep dive into design decisions
8. **Test files** - See how everything is tested

## 🔧 Troubleshooting

### Can't see the download button?
- The folder should appear above with a download option
- Try refreshing the page
- Check if your browser is blocking downloads

### Maven not installed?
```bash
# Ubuntu/Debian
sudo apt-get install maven

# Mac with Homebrew
brew install maven

# Windows with Chocolatey
choco install maven
```

### Java version too old?
```bash
# Ubuntu/Debian
sudo apt-get install openjdk-17-jdk

# Mac with Homebrew
brew install openjdk@17

# Windows - download from:
# https://adoptium.net/
```

## 📊 Project Statistics

- **Total Java Files**: 8 (5 source + 3 test)
- **Lines of Code**: ~1,200 (including comments and tests)
- **Test Coverage**: All major components
- **Documentation**: 3 comprehensive markdown files
- **Sample Data**: 2 CSV files

## ✨ Key Files Explained

| File | Purpose |
|------|---------|
| `pom.xml` | Maven configuration - defines dependencies and build process |
| `Employee.java` | Data model - represents a single employee |
| `EmployeeRepository.java` | Loads CSV, validates data, manages relationships |
| `SalaryAnalyzer.java` | Checks if managers earn 20-50% more than subordinates |
| `ReportingLineAnalyzer.java` | Finds employees with >4 managers to CEO |
| `OrganizationalAnalyzer.java` | Main entry point - orchestrates analysis |
| `*Test.java` | JUnit tests - comprehensive test coverage |

## 🎯 Next Steps After Download

1. ✅ Extract the ZIP file
2. ✅ Open in your IDE
3. ✅ Run `mvn clean package` to build
4. ✅ Run `mvn test` to verify tests pass
5. ✅ Try running with sample data: `java -jar target/organizational-analyzer-1.0-SNAPSHOT.jar employees.csv`
6. ✅ Review the code and tests
7. ✅ Modify `employees.csv` to test with your own data

## 💡 Tips

- The code is well-commented - read the JavaDoc
- Each class has a single responsibility
- Tests show how to use each component
- All assumptions are documented
- The README has example output

Need help with anything specific? Just ask!
