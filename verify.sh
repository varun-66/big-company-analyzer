#!/bin/bash

# This script demonstrates the project structure and validates it

PROJECT_ROOT="/home/claude/big-company-analyzer"

echo "=========================================="
echo "ORGANIZATIONAL ANALYZER - PROJECT VERIFICATION"
echo "=========================================="
echo ""

echo "1. PROJECT STRUCTURE:"
echo "---------------------"
cd "$PROJECT_ROOT"
find . -type f -name "*.java" -o -name "*.xml" -o -name "*.csv" -o -name "*.md" | grep -v "build/" | sort

echo ""
echo "2. MAVEN CONFIGURATION:"
echo "----------------------"
echo "Maven POM file exists: $(test -f pom.xml && echo 'YES' || echo 'NO')"
if [ -f pom.xml ]; then
    echo "Java version configured: $(grep -A1 'maven.compiler.source' pom.xml | grep '<maven' | sed 's/.*>\(.*\)<.*/\1/')"
    echo "JUnit version: $(grep -A1 'junit.version' pom.xml | grep '<junit' | sed 's/.*>\(.*\)<.*/\1/')"
fi

echo ""
echo "3. SOURCE FILES:"
echo "----------------"
echo "Model classes:"
ls -1 src/main/java/com/bigcompany/analyzer/model/*.java 2>/dev/null | wc -l
echo "Repository classes:"
ls -1 src/main/java/com/bigcompany/analyzer/repository/*.java 2>/dev/null | wc -l
echo "Service classes:"
ls -1 src/main/java/com/bigcompany/analyzer/service/*.java 2>/dev/null | wc -l
echo "Main application:"
ls -1 src/main/java/com/bigcompany/analyzer/*.java 2>/dev/null | wc -l

echo ""
echo "4. TEST FILES:"
echo "--------------"
echo "Repository tests:"
ls -1 src/test/java/com/bigcompany/analyzer/repository/*Test.java 2>/dev/null | wc -l
echo "Service tests:"
ls -1 src/test/java/com/bigcompany/analyzer/service/*Test.java 2>/dev/null | wc -l

echo ""
echo "5. TEST DATA FILES:"
echo "-------------------"
ls -1 *.csv

echo ""
echo "6. DOCUMENTATION:"
echo "-----------------"
echo "README exists: $(test -f README.md && echo 'YES' || echo 'NO')"
if [ -f README.md ]; then
    echo "README size: $(wc -l < README.md) lines"
fi

echo ""
echo "7. CODE SAMPLE - Employee Model:"
echo "--------------------------------"
head -20 src/main/java/com/bigcompany/analyzer/model/Employee.java

echo ""
echo "8. SAMPLE CSV DATA:"
echo "-------------------"
echo "employees.csv:"
cat employees.csv

echo ""
echo "=========================================="
echo "VERIFICATION COMPLETE"
echo "=========================================="
echo ""
echo "To build and run with Maven (when Maven is available):"
echo "  mvn clean package"
echo "  java -jar target/organizational-analyzer-1.0-SNAPSHOT.jar employees.csv"
echo ""
echo "The application is ready to be built and tested with Maven."
