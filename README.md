# Detector

Given component is responsible for verification of bank transactions.

* Bitbucket: [https://bitbucket.org/bitwebou/detector](https://bitbucket.org/bitwebou/detector)
* Dashboard: [https://dash.sandbox.bitw3b.eu/public-dashboards/0d1829f98adf4945bb5cec11a4e57a23](https://dash.sandbox.bitw3b.eu/public-dashboards/0d1829f98adf4945bb5cec11a4e57a23)

## Registering

For app to run you need to register. You need a team name and a fork of the detector repository.
Once you have  a team and a fork, you can register with the following CURL command:

**DO NOT FORGET TO CHANGE TEAM NAME IN BODY**

`curl -X POST 'https://devday.sandbox.bitw3b.eu/detectors' -H 'Content-Type: application/json' --data-raw '{"name": "Plepic"}'`

As a response you will get token. Put that token into src/main/java/resources/application.properties

You should be set to go. If you have issues, please feel free to ask for help. 

## Database setup

Detector database runs locally in Docker container
* `docker compose up -d`

## Running

* Running in IDE is best option
* Running in terminal `./gradlew bootRun --args='--detector.token=ajryQmxU5XwN7jXWWSl6i4Lvk3TVG98m'`
* Running with optimized JVM settings: `./start-optimized.sh [token]`

### JVM Performance Tuning

The application includes optimized JVM settings for high-throughput transaction processing:

**Automatic (Recommended):**
1. **Gradle**: JVM flags are automatically applied when using `./gradlew bootRun`
2. **Shell Script**: Use `./start-optimized.sh` for standalone execution

**Manual Configuration:**
The JVM flags are configured in:
- `build.gradle` - Automatically applied to `bootRun` task
- `.mvn/jvm.config` - Maven-style configuration file
- `start-optimized.sh` - Standalone startup script

**Optimizations Applied:**
- **G1GC**: Modern low-latency garbage collector with 200ms max pause time
- **Heap**: 2GB min, 4GB max (adjust based on available memory)
- **String Deduplication**: Reduces memory footprint for duplicate strings
- **Performance Flags**: Pre-touch memory pages, disable explicit GC

**For IDE Users:**
Add these VM options to your run configuration:
```
-XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:G1HeapRegionSize=16m -Xms2g -Xmx4g -XX:+UseStringDeduplication -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -server
```

## Service limitations

* Each api token is limited to 50 concurrent requests.
* Each api token is limited to 10000 pending transactions.

## Evaluation

Evaluators will start going through registered teams and executing applications in an isolated environment.
Evaluators will run a single instance of application and let it run for 3 minutes to get a baseline. 

***Testing machine:***
* 2 vCPU 
* 8 GB ram
