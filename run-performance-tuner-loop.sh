#!/bin/bash

# Performance Tuner Loop Script
# Runs the performance-tuner command 30 times in sequence

ITERATIONS=30
COUNTER=1

echo "Starting performance tuner loop - will run $ITERATIONS iterations"
echo "================================================"

while [ $COUNTER -le $ITERATIONS ]; do
    echo ""
    echo "[$COUNTER/$ITERATIONS] Running performance-tuner..."
    echo "------------------------------------------------"

    # Run the performance-tuner slash command via Claude Code
    # -p: Print mode (non-interactive)
    # --dangerously-skip-permissions: Skip permission checks
    claude -p --dangerously-skip-permissions "/performance-tuner"

    EXIT_CODE=$?

    if [ $EXIT_CODE -eq 0 ]; then
        echo "✓ Iteration $COUNTER completed successfully"
    else
        echo "✗ Iteration $COUNTER failed with exit code $EXIT_CODE"
        echo "Do you want to continue? (y/n)"
        read -r response
        if [ "$response" != "y" ]; then
            echo "Stopping at iteration $COUNTER"
            exit 1
        fi
    fi

    COUNTER=$((COUNTER + 1))

    # Optional: Add a small delay between iterations
    if [ $COUNTER -le $ITERATIONS ]; then
        echo "Waiting 2 seconds before next iteration..."
        sleep 2
    fi
done

echo ""
echo "================================================"
echo "✓ Completed all $ITERATIONS iterations"
echo "================================================"
