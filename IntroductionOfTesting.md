# Introduction of Testing
### Overview
This document provides an overview of the testing strategies


| Aspect                  | Unit Tests                                                                                | Integration Tests                                                                                                                                       | End-to-End (E2E) Tests                                                                                                          |
|-------------------------|-------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------|
| **Scope**               | Individual components or functions in isolation.                                          | Multiple components interacting together.                                                                                                               | The entire application as a whole.                                                                                              |
| **Focus**               | Single classes (e.g., services, utilities).                                               | Interactions (e.g., between services & database, controllers & services).                                                                               | Full user scenarios (UI, API, database, external services).                                                                     |
| **Characteristics**     | - Fastest to run.<br>- Use mocks/stubs for dependencies.<br>- High number, more detailed. | - Slower than unit tests.<br>- Use real instances of classes and often a real or in-memory database.<br>- Moderate number, cover interaction scenarios. | - Most comprehensive.<br>- Mimic real user interactions.<br>- Slowest, fewer in number.<br>- Closest to production environment. |
| **Tools & Annotations** | - JUnit or similar.<br>- `@MockBean`, `@InjectMocks` for mocking.                         | - `@SpringBootTest`, `@WebMvcTest`.<br>- `MockMvc`, `TestRestTemplate`.                                                                                 | - `@SpringBootTest` with `WebEnvironment.RANDOM_PORT` or `DEFINED_PORT`.<br>- `TestRestTemplate`, `WebTestClient`, Selenium.    |
| **Purpose**             | Verify correctness of specific code parts.                                                | Validate integration of different application layers.                                                                                                   | Ensure overall functionality and user experience.                                                                               |

## Running Tests with Gradle Kotlin Script
In a Gradle Kotlin DSL (build.gradle.kts), you can set up tasks to run different types of tests. Below is an example script that defines tasks for running unit, integration, and E2E tests:
```kotlin
tasks {
    test {
        useJUnitPlatform {
            includeTags("unit")
        }
    }

    register<Test>("integrationTest") {
        useJUnitPlatform {
            includeTags("integration")
        }
        mustRunAfter("test")
    }

    register<Test>("e2eTest") {
        useJUnitPlatform {
            includeTags("e2e")
        }
        mustRunAfter("integrationTest")
    }
}
```

In this script:

The test task is configured to run tests tagged with unit.
integrationTest and e2eTest tasks are defined for running tests tagged with integration and e2e, respectively.
mustRunAfter ensures a proper sequence of test execution.
To use this setup, you would annotate your test classes in your project with @Tag("unit"), @Tag("integration"), or @Tag("e2e") depending on the type of test.

To run the tests, use the following commands in the terminal:

```shell
./gradlew test            # Run unit tests
./gradlew integrationTest # Run integration tests
./gradlew e2eTest         # Run E2E tests
```

## Additional Resources

- [Spring Boot Testing Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-testing)
- [JUnit User Guide](https://junit.org/junit5/docs/current/user-guide/)
