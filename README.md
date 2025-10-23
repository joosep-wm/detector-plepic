# Detector

Given component is responsible for verification of bank transactions.

* Bitbucket: [https://bitbucket.org/bitwebou/detector](https://bitbucket.org/bitwebou/detector)
* Dashboard: [https://dash.sandbox.bitw3b.eu/public-dashboards/0d1829f98adf4945bb5cec11a4e57a23](https://dash.sandbox.bitw3b.eu/public-dashboards/0d1829f98adf4945bb5cec11a4e57a23)

## Registering

For app to run you need to register. You need a team name and a fork of the detector repository.
Once you have  a team and a fork, you can register with the following CURL command:

**DO NOT FORGET TO CHANGE TEAM NAME IN BODY**

`curl -X POST 'https://devday.sandbox.bitw3b.eu/detectors' -H 'Content-Type: application/json' --data-raw '{"name": "<TEAM NAME>"}'`

As a response you will get token. Put that token into src/main/java/resources/application.properties

You should be set to go. If you have issues, please feel free to ask for help. 

## Database setup

Detector database runs locally in Docker container
* `docker compose up -d`

## Running

* Running in IDE is best option
* Running in terminal `./gradlew bootRun --args='--detector.token=<your_token>'`

## Service limitations

* Each api token is limited to 50 concurrent requests.
* Each api token is limited to 10000 pending transactions.

## Evaluation

Evaluators will start going through registered teams and executing applications in an isolated environment.
Evaluators will run a single instance of application and let it run for 3 minutes to get a baseline. 

***Testing machine:***
* 2 vCPU 
* 8 GB ram
