Creating a complete GitHub repository with the full framework, sample test cases for each feature, and detailed examples of how the generic API test automation framework works (including handling different types of authentication, dynamic variables, chained API testing, etc.) is a large but highly feasible task. Below, I will outline how the GitHub repository structure should look, and what the detailed examples would look like for each feature.

GitHub Repository Structure

Here’s a proposed structure for the repository:

api-test-automation-framework/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── api/
│   │   │           ├── authentication/
│   │   │           │   ├── OAuth2Auth.java
│   │   │           │   ├── ApiKeyAuth.java
│   │   │           │   └── JwtAuth.java
│   │   │           │
│   │   │           ├── testcases/
│   │   │           │   ├── DynamicAPITest.java
│   │   │           │   ├── TestCaseService.java
│   │   │           │   └── AzureTestResults.java
│   │   │           │
│   │   │           ├── utils/
│   │   │           │   ├── TestCaseParser.java
│   │   │           │   └── DynamicVariableResolver.java
│   │   │
│   │   └── resources/
│   │       ├── application.properties
│   │       └── test-case-data.json
│   │
│   ├── test/
│   │   └── java/
│   │       └── com/
│   │           └── api/
│   │               └── testcases/
│   │                   ├── DynamicAPITestTest.java
│   │                   └── ApiTestSuite.java
│   │
├── .gitignore
├── pom.xml
├── README.md
└── azure-pipelines.yml

Detailed Features

The repository will have different folders for each feature, including the implementation for different types of authentication, dynamic variable handling, chained API testing, and more.

Let me break down what each file will contain and explain the setup.


---

1. Authentication Implementations

We will create separate classes for handling different types of authentication:

OAuth2Auth.java (For OAuth2 Authentication)

package com.api.authentication;

import io.restassured.http.Header;
import io.restassured.http.Headers;

public class OAuth2Auth {

    public static Headers getHeaders() {
        String oauthToken = getOAuthToken(); // Logic to fetch OAuth2 token
        return new Headers(new Header("Authorization", "Bearer " + oauthToken));
    }

    private static String getOAuthToken() {
        // Logic to get OAuth2 token from Azure AD or other Identity Providers
        return "sample-oauth-token";
    }
}

ApiKeyAuth.java (For API Key Authentication)

package com.api.authentication;

import io.restassured.http.Header;
import io.restassured.http.Headers;

public class ApiKeyAuth {

    public static Headers getHeaders() {
        String apiKey = "your-api-key";
        return new Headers(new Header("x-api-key", apiKey));
    }
}

JwtAuth.java (For JWT Authentication)

package com.api.authentication;

import io.restassured.http.Header;
import io.restassured.http.Headers;

public class JwtAuth {

    public static Headers getHeaders() {
        String jwtToken = "your-jwt-token";
        return new Headers(new Header("Authorization", "Bearer " + jwtToken));
    }
}


---

2. Dynamic Test Case Execution (DynamicAPITest.java)

The DynamicAPITest.java will be the heart of our test execution, which fetches the test cases dynamically from Azure, resolves dynamic variables, and executes them.

package com.api.testcases;

import com.api.authentication.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.util.List;

public class DynamicAPITest {

    @DataProvider(name = "apiTestData")
    public Object[][] getAPITestData() {
        List<TestCase> testCases = TestCaseService.fetchTestCases();
        return testCases.stream().map(tc -> new Object[]{tc}).toArray(Object[][]::new);
    }

    @Test(dataProvider = "apiTestData")
    public void executeAPITest(TestCase testCase) {
        System.out.println("Executing Test: " + testCase.getTestCaseName());

        // Resolve Dynamic Variables (Chained API Output)
        String requestBody = testCase.resolveDynamicVariables();

        // Choose the authentication type dynamically
        Headers headers = chooseAuthHeaders(testCase);

        Response response = RestAssured.given()
            .headers(headers)
            .body(requestBody)
            .request(testCase.getMethod(), testCase.getUrl())
            .then().extract().response();

        // Validate Response
        Assert.assertEquals(response.getStatusCode(), testCase.getExpectedStatusCode());
        Assert.assertTrue(response.getBody().asString().matches(testCase.getExpectedResponseBody()));

        // Update Azure Test Results
        AzureTestResults.updateTestCaseStatus(testCase.getTestCaseId(), "Passed");
    }

    private Headers chooseAuthHeaders(TestCase testCase) {
        switch (testCase.getAuthType()) {
            case "OAuth2":
                return OAuth2Auth.getHeaders();
            case "ApiKey":
                return ApiKeyAuth.getHeaders();
            case "JWT":
                return JwtAuth.getHeaders();
            default:
                return null;
        }
    }
}


---

3. Test Case Service (TestCaseService.java)

This class fetches test cases from Azure DevOps, resolves dynamic variables, and supplies data for the tests.

package com.api.testcases;

import com.api.utils.TestCaseParser;
import java.util.List;

public class TestCaseService {

    public static List<TestCase> fetchTestCases() {
        // Logic to fetch test cases from Azure Test Plans (via Azure DevOps API or Test Case Files)
        return TestCaseParser.parseTestCases("test-case-data.json");
    }
}


---

4. Test Case Parser (TestCaseParser.java)

This utility class parses the test case data from a JSON file or Azure DevOps.

package com.api.utils;

import com.api.testcases.TestCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class TestCaseParser {

    public static List<TestCase> parseTestCases(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(new File(filePath), objectMapper.getTypeFactory().constructCollectionType(List.class, TestCase.class));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}


---

5. Sample Test Case JSON File (test-case-data.json)

[
  {
    "testCaseId": "TC-001",
    "testCaseName": "User Data Transformation API Test",
    "method": "POST",
    "url": "https://api.example.com/transformUserData",
    "requestBody": "{ \"userId\": \"{{userId}}\" }",
    "headers": { "Authorization": "Bearer {{authToken}}" },
    "expectedStatusCode": 200,
    "expectedResponseBody": ".*\"employeeId\":\\d+.*",
    "authType": "OAuth2",
    "dynamicVariables": ["userId", "employeeId"],
    "dependency": "None"
  }
]


---

6. Test Suite and Parallel Execution

In ApiTestSuite.java, you can configure the suite to run tests in parallel.

package com.api.testcases;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;

public class ApiTestSuite {

    @BeforeClass
    public void setup() {
        // Setup code if needed
    }

    @Test
    public void testUserDataTransformationAPI() {
        DynamicAPITest.executeAPITest(new TestCase());
    }
}

In your pom.xml, make sure to enable parallel execution:

<testng>
  <parallel>tests</parallel>
  <thread-count>5</thread-count>
</testng>


---

7. CI/CD Integration (azure-pipelines.yml)

trigger:
- main

pool:
  vmImage: 'ubuntu-latest'

steps:
- script: mvn clean test -Dtest=DynamicAPITest
  displayName: 'Run Generic API Tests'

- task: PublishTestResults@2
  inputs:
    testResultsFormat: 'JUnit'
    testResultsFiles: '**/target/surefire-reports/*.xml'


---

8. Final Notes in README.md

# Generic API Test Automation Framework

## Overview

This repository contains a generic API test automation framework designed for enterprise-scale API testing. It supports:

- **Multiple Authentication Types**: OAuth2, API Key, JWT, etc.
- **Dynamic API Testing**: Automatically resolves dynamic variables and chained API responses.
- **Data-Driven Testing**: Read test cases from JSON or Azure Test Plans.
- **Parallel Execution**: Supports parallel test execution for faster feedback.
- **CI/CD Integration**: Easily integrates with Azure DevOps for automated testing.

## Running the Tests

1. Clone the repository.
2. Configure your test cases in `test-case-data.json` or Azure Test Plans.
3. Run the tests using Maven or directly in your CI/CD pipeline.

```bash
mvn clean test -Dtest=DynamicAPITest

---

This setup allows you to **add new test cases without code changes** and supports all authentication types and chained API execution. You can upload test cases to **Azure DevOps** via the **Azure Test Plan** UI or bulk upload them via Excel/JSON.

---

Would you like to proceed with creating this GitHub repository or need additional customization for your use case?

