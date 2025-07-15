# Test Guide for Modular Blockchain Java

This guide explains how to effectively run, analyze, and debug tests for the Modular Blockchain Java project.

## Running Tests

### Running All Tests

To run all tests in the project:

```bash
mvn test
```

### Running Individual Tests

To run a specific test class:

```bash
mvn test -Dtest=BlockchainTest
```

To run a specific test method:

```bash
mvn test -Dtest=BlockchainTest#testAddBlock
```

To run multiple specific test classes:

```bash
mvn test -Dtest=BlockchainTest,ProofOfWorkTest
```

### Running Tests with Different Configurations

To run tests with a specific environment configuration:

```bash
BLOCKCHAIN_ENV=dev mvn test
```

## Viewing Test Coverage

The project uses JaCoCo for test coverage reporting.

### Generating Coverage Reports

To generate test coverage reports:

```bash
mvn verify
```

### Viewing Coverage Reports

After running the command above, you can find the HTML coverage report at:

```
target/site/jacoco/index.html
```

Open this file in your browser to see:

- Overall project coverage
- Package-level coverage
- Class-level coverage
- Method and line coverage details

### Coverage Thresholds

The project aims for:
- Line coverage: 80%
- Branch coverage: 70%
- Method coverage: 80%

## Debugging Test Failures

### Understanding Test Output

When tests fail, Maven provides:
- The name of the failing test
- The expected vs. actual values
- The line number where the failure occurred

### Common Debugging Techniques

1. **Enable Debug Logging**

   Add the following to your test to see detailed logs:

   ```java
   @BeforeEach
   void setupLogging() {
       LoggingUtils.setBlockchainLogLevel("DEBUG");
   }
   ```

2. **Using Breakpoints**

   If using an IDE like IntelliJ or Eclipse:
   - Set breakpoints in your test or implementation code
   - Run the test in debug mode
   - Inspect variables and execution flow

3. **Isolating Test Failures**

   If a test is failing intermittently:
   - Run the test in isolation
   - Check for dependencies on other tests
   - Look for race conditions or timing issues

4. **Examining Test Reports**

   Detailed test reports are available at:

   ```
   target/surefire-reports/
   ```

   These include:
   - Plain text reports (*.txt)
   - XML reports for CI integration (*.xml)

### Troubleshooting Common Issues

1. **Configuration Issues**
   - Check if tests are using the correct configuration files
   - Verify environment variables are set correctly

2. **Concurrency Problems**
   - Look for shared state between tests
   - Use thread-safe collections when needed

3. **Resource Leaks**
   - Ensure resources are properly closed in @AfterEach or @AfterAll methods
   - Check for unclosed file handles or connections

4. **Assertion Errors**
   - Use more specific assertions with descriptive messages
   - Consider using assertAll for multiple related assertions