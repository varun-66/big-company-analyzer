#!/bin/bash

# Build script for organizational analyzer

set -e

PROJECT_ROOT="/home/claude/big-company-analyzer"
SRC_DIR="$PROJECT_ROOT/src/main/java"
OUT_DIR="$PROJECT_ROOT/build/classes"

echo "=== Building Organizational Analyzer ==="
echo ""

# Create output directory
mkdir -p "$OUT_DIR"

# Compile all Java files
echo "Compiling Java sources..."
javac -d "$OUT_DIR" \
    "$SRC_DIR/com/bigcompany/analyzer/model/Employee.java" \
    "$SRC_DIR/com/bigcompany/analyzer/repository/EmployeeRepository.java" \
    "$SRC_DIR/com/bigcompany/analyzer/service/SalaryAnalyzer.java" \
    "$SRC_DIR/com/bigcompany/analyzer/service/ReportingLineAnalyzer.java" \
    "$SRC_DIR/com/bigcompany/analyzer/OrganizationalAnalyzer.java"

echo "✓ Compilation successful"
echo ""

# Run with sample data
echo "=== Running with sample data (employees.csv) ==="
echo ""
java -cp "$OUT_DIR" com.bigcompany.analyzer.OrganizationalAnalyzer "$PROJECT_ROOT/employees.csv"

echo ""
echo ""
echo "=== Running with complex test data (test-data.csv) ==="
echo ""
java -cp "$OUT_DIR" com.bigcompany.analyzer.OrganizationalAnalyzer "$PROJECT_ROOT/test-data.csv"
