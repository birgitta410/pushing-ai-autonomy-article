

## Generate end-to-end journey test

Create a Karate test suite for every aggregate that goes through all the journeys of the API endpoints. Karate test suites go into `src/e2e/java`.

```
import com.intuit.karate.junit5.Karate;

public class AggregateNameKarateTestSuite {
    @Karate.Test
    Karate testAggregateName() {
        return Karate.run("aggregate_name").relativeTo(getClass());
    }
}
```

The `aggregate_name.feature` file should be located in the respective aggregate package in the `src/e2e/resources` directory.

Example structure (replace '...' with appropriate aggregate, entity and api endpoint names)

```
Feature: Complete ... journey testing all ... endpoints

  Background:
    * url demoBaseUrl

  Scenario: Complete CRUD journey
    # 1. Create a new ...
    Given path '/api/...'
    And request 
    """
    {
        ...
    }
    ...
```

The test should create a journey that tests all endpoints defined in `requirements.md`.

## Running the script

Start the application first, it will log to `application.log`.
Run the tests with `./gradlew e2e`.

## Definition of success

You are only done when the script covers all endpoints, and the whole test suite is succeeding. If there are errors in the test run, look at the application logs and debug the issue, then fix the code. Remember that the `requirements.md` is the source of truth that the code should comply with. 
You are only done once the feature script fully represents the journey through the application, and runs successfully. If the test suite is not running successfully, you are not finished and should continue debugging and fixing the code.