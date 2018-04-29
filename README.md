# Project Title

REST API that captures realtime transaction statistics. Imlemented in Spring Boot. Tested on OSX 10.13.4 w/ Java 1.8.0_66 and Ubuntu 16.04.1 LTS w/ Java 1.8.0_162.

## Getting Started

Just clone the github project, cd into the directory, and run bootRun to build and start the application:

* git clone https://github.com/danielpacker/stats-rest-api.git

* cd stats-rest-api

* ./gradlew bootRun

(you can also ./gradlew clean, ./gradlew build, ./gradlew test, etc)

### Overview of Architecture

* There is one REST conroller that defines the endpoints.
* The Statistics singleton acts as a cache and statistics service. All data is static and access is synchronized for thread safety.
** A list of DSS (DoubleSummaryStatistics) are maintained where the number of buckets are scaled up based on REFRESH_RATE_MS. This allows update and retrieval of statistics in O(1) time and space, as transaction are not stored, only summaries. More about REFRESH_RATE_MS below.
* Spring's @Schedule is used to update statistics every REFRESH_RATE_MS milliseconds. The class StatisticsTicker has a doTick() method which calls the update code in Statistics to refresh stats.
** In tests, StatisticsTicker's doTick() is used to simulat a clock tick and Statistics.clear() is used to clear stats.
* The Transaction and StatisticsView classes are POJO's, and StatisticsView is used for JSON in controller.

### Assumptions & Explanations

* It's assumed that transactions with future timestamps can be ignored like past timestamps
* Real-time resolution is configurable and is determined by the REFRESH_RATE_MS in the Statistics class. If this is modified, the number of buckets will increase to provide greater time resolution. E.g. for a value of 1000 you will get 60 buckets (one per second) and for 100 you will get 600 buckets (one per 1/10 second). Shipped w/ 250ms second resolution.

### Prerequisites

The only prerequisites are Java 8 SE and the Java 8 SDK. Untested on other versions, but may work.

## Running the tests

./gradlew test

### Test Coverage

* Test 1: default state (statistics should all be set at 0)
* Test 2: one transaction is submitted and stats are appropriately updated (end to end)
* Test 3: multiple transactions submitted and stats appropriately updated (end to end)
* Test 4: single transaction w/ future timestamp is rejected w/ 204 status (end to end)
* Test 5: single tranasaction w/ past timestamp is rejected w/ 204 status (end to end)

## Built With

* Spring Boot
* Gradle
* JUnit

## Authors

* **Daniel Packer** - *Author* - [Daniel Packer](https://github.com/danielpacker)

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

