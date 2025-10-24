# JVM Performance Tuning Configuration

## Overview

This document describes the JVM performance optimizations applied to the Detector application for high-throughput transaction processing.

## Applied Optimizations

### Garbage Collection (G1GC)
- **`-XX:+UseG1GC`**: Enables G1 (Garbage First) garbage collector
  - Modern, low-latency GC designed for large heap sizes
  - Better predictability than default GC for high-throughput applications

- **`-XX:MaxGCPauseMillis=200`**: Target maximum GC pause time of 200ms
  - Balances throughput with latency requirements
  - Reduces transaction processing delays during GC cycles

- **`-XX:G1HeapRegionSize=16m`**: Sets G1 region size to 16MB
  - Optimized for 2-4GB heap size
  - Improves memory management efficiency

### Heap Sizing
- **`-Xms2g`**: Initial heap size of 2GB
  - Avoids heap expansion overhead during startup
  - Matches expected steady-state memory usage

- **`-Xmx4g`**: Maximum heap size of 4GB
  - Provides headroom for peak transaction loads
  - Fits within 8GB RAM testing environment (leaves 4GB for OS and off-heap)

### Memory Optimizations
- **`-XX:+UseStringDeduplication`**: Enables automatic string deduplication
  - Reduces memory footprint for duplicate string objects
  - Particularly effective for transaction IDs, account numbers, etc.

### Performance Flags
- **`-XX:+DisableExplicitGC`**: Disables System.gc() calls
  - Prevents application code from triggering full GCs
  - GC runs only when JVM determines it's necessary

- **`-XX:+AlwaysPreTouch`**: Pre-touches all heap memory pages at startup
  - Eliminates page fault delays during runtime
  - Improves predictable latency

- **`-server`**: Enables server-class JVM optimizations
  - Aggressive JIT compilation for long-running applications
  - Optimizes for throughput over startup time

## Expected Performance Improvements

1. **Reduced GC Pause Times**: 10-30% reduction in pause durations
2. **Improved Throughput**: 10-20% increase in transactions/minute
3. **Lower Latency Variance**: More predictable response times
4. **Better Memory Efficiency**: Reduced memory churn and allocation overhead

## Usage

### Automatic (Gradle)
```bash
./gradlew bootRun --args='--detector.token=YOUR_TOKEN'
```
JVM flags are automatically applied via `build.gradle` configuration.

### Shell Script
```bash
./start-optimized.sh [token]
```

### IDE Configuration
Add to VM options in Run/Debug Configuration:
```
-XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:G1HeapRegionSize=16m -Xms2g -Xmx4g -XX:+UseStringDeduplication -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -server
```

## Monitoring

To verify the optimizations are active, check the application logs for:
```
Java HotSpot(TM) 64-Bit Server VM
Using G1 GC
```

## Tuning for Different Environments

### Low Memory (< 4GB)
```
-Xms1g -Xmx2g -XX:G1HeapRegionSize=8m
```

### High Memory (> 8GB)
```
-Xms4g -Xmx6g -XX:G1HeapRegionSize=32m
```

## Reverting Changes

To disable JVM tuning:
1. Remove/comment out the `jvmArgs` section in `build.gradle`
2. Delete or rename `.mvn/jvm.config`
3. Use standard `./gradlew bootRun` without the optimized script

## References

- [G1GC Tuning Guide](https://docs.oracle.com/en/java/javase/21/gctuning/garbage-first-g1-garbage-collector1.html)
- [JVM Performance Tuning](https://docs.oracle.com/en/java/javase/21/gctuning/)
