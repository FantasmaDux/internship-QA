#!/bin/bash

echo "Moving to project root folder..."
cd test-automation
echo

echo "Running tests..."

TOTAL=0
FAILURES=0

while IFS= read -r line; do
    echo "$line" 

    if [[ "$line" =~ Tests\ run: ]]; then
        SUMMARY="$line"
    fi
done < <(./mvnw test 2>&1)

if [ -n "$SUMMARY" ]; then
    TOTAL=$(echo "$SUMMARY" | awk -F',' '{print $1}' | awk '{print $4}')
    FAILURES=$(echo "$SUMMARY" | awk -F',' '{print $2}' | awk '{print $2}')
    ERRORS=$(echo "$SUMMARY" | awk -F',' '{print $3}' | awk '{print $2}')
fi

PASSED=$((TOTAL - FAILURES - ERRORS))

echo
echo "========================================"
echo "TEST RESULTS"
echo "========================================"
echo
echo "Total tests run : $TOTAL"
echo "Passed         : $PASSED"
echo "Failures       : $FAILURES"
echo

cd ..

if [ "$FAILURES" -ne 0 ] || [ "$ERRORS" -ne 0 ]; then
    echo "Test execution failed!"
    exit 1
fi

echo "All tests passed!"