'''code
Technical Design & Architecture for API Test Automation Framework

This document provides a detailed technical design & architecture for a modern, scalable, and enterprise-grade REST API test automation framework.


---

1️⃣ Key Features

✅ Modular & Reusable – Uses Spring Boot Starter for authentication.
✅ Data-Driven Testing – Reads test data dynamically from Azure Test Plans, JSON, or Excel.
✅ Validation Engine – Supports JSON Schema validation, custom matchers, and business rule validation.
✅ CI/CD Ready – Seamlessly integrates with Azure DevOps and supports parallel execution.
✅ Mocking & Service Virtualization – Uses WireMock for simulating backend services.
✅ Advanced Reporting – Generates Allure & Extent Reports for execution logs.
✅ Microservices & Cloud Ready – Supports testing across Azure, AWS, and Kubernetes-based deployments.


---

2️⃣ Architectural Diagram

🛠️ Layered Architecture

+--------------------------------------+
|  Test Execution (TestNG/JUnit)       |
|  - Reads test cases from Azure Test Plans |
|  - Executes API requests & validations |
+--------------------------------------+
|  API Request Handler (RestAssured)  |
|  - Sends API Requests               |
|  - Handles dynamic payload injection |
+--------------------------------------+
|  Authentication Module (Spring Boot Starter) |
|  - Supports OAuth2, JWT, API Keys           |
|  - Can be replaced without affecting tests  |
+--------------------------------------+
|  Data Provider Module                |
|  - Reads test data from Azure Test Plans |
|  - Supports JSON, Excel, YAML        |
+--------------------------------------+
|  Validation Engine                   |
|  - JSON Schema Validation            |
|  - Business Logic Validation         |
+--------------------------------------+
|  Reporting Module                     |
|  - Allure & Extent Reports            |
|  - Logs API request-response details  |
+--------------------------------------+


---

3️⃣ Technical Design

A. Project Structure

api-test-framework/
│── src/
│   ├── main/java/
│   │   ├── com.framework.auth/        # Authentication (Spring Boot Starter)
│   │   ├── com.framework.data/        # Test Data Provider
│   │   ├── com.framework.test/        # Test Execution Layer (TestNG)
│   │   ├── com.framework.validation/  # Validation & Assertions
│   │   ├── com.framework.reporting/   # Reporting (Allure, Extent Reports)
│   ├── test/resources/
│   │   ├── testdata.json              # Test Data Source
│   │   ├── user_schema.json           # JSON Schema Validation
│── pom.xml                             # Maven Dependencies
│── azure-pipelines.yml                 # Azure DevOps Pipeline Configuration


---

B. Authentication Module (Spring Boot Starter)

✅ Supports OAuth2, JWT, API Key, Basic Auth dynamically
✅ Can be used as a dependency across multiple projects

Authentication Service Implementation

@Component
public class AuthService {
    @Autowired
    private OAuthService oauthService;
    
    public String getAuthToken(AuthType authType) {
        switch (authType) {
            case OAUTH2: return "Bearer " + oauthService.fetchOAuthToken();
            case API_KEY: return "API-KEY " + System.getenv("API_KEY");
            default: return "";
        }
    }
}


---

C. Test Data Provider Module

✅ Reads test cases dynamically from Azure Test Plans
✅ Supports alternative JSON, Excel, YAML formats

Fetching Test Data from Azure DevOps

public class AzureTestPlanReader {
    private static final String BASE_URL = "https://dev.azure.com/{org}/{project}/_apis/test/Plans/";
    private static final String TOKEN = "YOUR_PERSONAL_ACCESS_TOKEN";

    public static JsonNode getTestCases(String planId) {
        return RestAssured.given()
            .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((":" + TOKEN).getBytes()))
            .get(BASE_URL + planId + "/Suites?api-version=7.1-preview.1")
            .then()
            .extract()
            .body()
            .as(JsonNode.class);
    }
}


---

D. API Test Execution (TestNG + RestAssured)

✅ Dynamically fetches test cases from Azure Test Plans
✅ Supports parameterized & data-driven testing

Test Execution Example

public class APITestExecutor {
    @Test(dataProvider = "testData")
    public void apiTest(String apiEndpoint, String expectedResponse) {
        Response response = RestAssured.given()
            .header("Authorization", new AuthService().getAuthToken(AuthType.OAUTH2))
            .get(apiEndpoint)
            .then()
            .statusCode(200)
            .extract()
            .response();

        Assert.assertEquals(response.asString(), expectedResponse);
    }
}

Test Data Provider

@DataProvider(name = "testData")
public Object[][] getData() {
    JsonNode testData = AzureTestPlanReader.getTestCases("PLAN_ID");
    List<Object[]> data = new ArrayList<>();

    for (JsonNode testCase : testData.get("value")) {
        data.add(new Object[]{
            testCase.get("apiEndpoint").asText(),
            testCase.get("expectedResponse").asText()
        });
    }

    return data.toArray(new Object[0][]);
}


---

E. Validation Module

✅ JSON Schema Validation
✅ Soft Assertions for multiple verifications

Schema Validation Example

@Test
public void validateApiResponseSchema() {
    RestAssured.given()
        .header("Authorization", new AuthService().getAuthToken(AuthType.OAUTH2))
        .get("/users")
        .then()
        .assertThat()
        .body(matchesJsonSchemaInClasspath("schemas/user_schema.json"));
}

Custom JSON Response Matchers

assertThat(response.jsonPath().getString("user.email"), matchesPattern(".+@example.com"));


---

F. Reporting Module (Allure & Extent Reports)

✅ Detailed execution logs & request-response capture
✅ Allure Reports for visual insights

TestNG Listener for Reporting

@Listeners({ExtentTestNGIReporterListener.class})
public class TestReportListener {
    @BeforeMethod
    public void setupReport() {
        ExtentReports report = new ExtentReports();
        report.attachReporter(new ExtentHtmlReporter("test-output/report.html"));
    }
}


---

G. Mocking & Service Virtualization (WireMock)

✅ Simulate API responses when real endpoints are unavailable

WireMock Example

stubFor(get(urlEqualTo("/users"))
    .willReturn(aResponse()
        .withStatus(200)
        .withBody("{\"message\": \"Success\"}")));


---

4️⃣ CI/CD Integration (Azure DevOps Pipeline)

✅ Runs tests automatically in CI/CD pipelines
✅ Publishes test results to Azure DevOps

Azure DevOps YAML Pipeline

trigger:
- main

pool:
  vmImage: 'ubuntu-latest'

steps:
- script: mvn clean test
  displayName: 'Run API Tests'

- task: PublishTestResults@2
  inputs:
    testResultsFormat: 'JUnit'
    testResultsFiles: '**/target/surefire-reports/*.xml'


---

5️⃣ Test Case Format in Azure Test Plans


---

6️⃣ Next Steps

✅ Prepare GitHub Repository with Maven & Docker Support

✅ Add Performance Testing with Gatling/JMeter

✅ Enhance Reporting with Slack Notifications


Would you like a sample repository with a running example?



'''
