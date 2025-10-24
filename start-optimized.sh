#!/bin/bash

# Start the Detector application with optimized JVM settings
# This script provides maximum control over JVM flags for performance tuning

JAVA_OPTS="-XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:G1HeapRegionSize=16m \
  -Xms2g \
  -Xmx4g \
  -XX:+UseStringDeduplication \
  -XX:+DisableExplicitGC \
  -XX:+AlwaysPreTouch \
  -server"

# Pass through detector token from argument or use default
TOKEN=${1:-ajryQmxU5XwN7jXWWSl6i4Lvk3TVG98m}

echo "Starting Detector application with optimized JVM settings..."
echo "JVM Options: $JAVA_OPTS"
echo "Detector Token: $TOKEN"
echo ""

./gradlew bootRun --args="--detector.token=$TOKEN" -Dorg.gradle.jvmargs="$JAVA_OPTS"
