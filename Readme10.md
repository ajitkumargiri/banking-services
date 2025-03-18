To make the authentication, data service, and other features configurable and modular, we need to follow a more enterprise-centric approach where:

1. Authentication mechanisms (e.g., OAuth2, API Key, JWT, Certificate) should be dynamically configurable based on properties.


2. Data Service should be easily configurable to fetch test data from multiple sources (Azure Test Plan, JSON files, databases).


3. Test Execution and Customization should allow enterprise developers to plug in their custom logic without changing the core framework.



Below is a more modular, configurable, and customizable Spring Boot-based framework where enterprise developers can easily add or modify authentication mechanisms, data sources, and response validation strategies without touching the core code.


---

1. Structure of the Repository

api-test-automation-framework/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── api/
│   │   │           ├── auth/
│   │   │           │   ├── AuthConfig.java
│   │   │           │   ├── AuthFactory.java
│   │   │           │   ├── OAuth2Auth.java
│   │   │           │   ├── ApiKeyAuth.java
│   │   │           │   ├── JwtAuth.java
│   │   │           │   └── CertificateAuth.java
│   │   │           ├── service/
│   │   │           │   ├── DataServiceConfig.java
│   │   │           │   ├── TestDataService.java
│   │   │           │   ├── AzureTestPlanDataService.java
│   │   │           │   └── JsonFileTestDataService.java
│   │   │           ├── testcases/
│   │   │           │   ├── DynamicAPITest.java
│   │   │           │   ├── TestCaseService.java
│   │   │           │   └── TestCase.java
│   │   │           └── utils/
│   │   │               ├── TestCaseParser.java
│   │   │               ├── DynamicVariableResolver.java
│   │   │               ├── JsonResponseValidator.java
│   │   │               └── ResponseValidator.java
│   │   ├── resources/
│   │   │   ├── application.properties
│   │   │   └── test-case-data.json
│   │   └── test/
│   │       └── java/
│   │           └── com/
│   │               └── api/
│   │                   └── testcases/
│   │                       └── DynamicAPITestTest.java
├── .gitignore
├── pom.xml
├── azure-pipelines.yml
└── README.md


---

2. Configuring Authentication Mechanisms

We will make the authentication system modular and configurable. The AuthConfig and AuthFactory will allow for easy extensibility.

AuthConfig.java

This class will allow developers to configure the authentication type in application.properties or YAML file.

package com.api.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConfig {

    @Value("${auth.type}")
    private String authType;

    @Value("${auth.oauth2.clientId}")
    private String oauth2ClientId;

    @Value("${auth.oauth2.clientSecret}")
    private String oauth2ClientSecret;

    @Value("${auth.apiKey}")
    private String apiKey;

    @Value("${auth.jwt.token}")
    private String jwtToken;

    @Value("${auth.cert.filePath}")
    private String certFilePath;

    @Value("${auth.cert.password}")
    private String certPassword;

    // Getters
    public String getAuthType() {
        return authType;
    }

    public String getOauth2ClientId() {
        return oauth2ClientId;
    }

    public String getOauth2ClientSecret() {
        return oauth2ClientSecret;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public String getCertFilePath() {
        return certFilePath;
    }

    public String getCertPassword() {
        return certPassword;
    }
}

AuthFactory.java

This factory class will create the appropriate authentication mechanism based on the configuration.

package com.api.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthFactory {

    @Autowired
    private AuthConfig authConfig;

    public Auth getAuth() {
        switch (authConfig.getAuthType().toLowerCase()) {
            case "oauth2":
                return new OAuth2Auth(authConfig.getOauth2ClientId(), authConfig.getOauth2ClientSecret());
            case "apikey":
                return new ApiKeyAuth(authConfig.getApiKey());
            case "jwt":
                return new JwtAuth(authConfig.getJwtToken());
            case "certificate":
                return new CertificateAuth(authConfig.getCertFilePath(), authConfig.getCertPassword());
            default:
                throw new IllegalArgumentException("Unsupported Auth Type: " + authConfig.getAuthType());
        }
    }
}

OAuth2Auth.java (Example for OAuth2 Authentication)

package com.api.auth;

import io.restassured.http.Headers;
import io.restassured.http.Header;

public class OAuth2Auth implements Auth {

    private final String clientId;
    private final String clientSecret;

    public OAuth2Auth(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public Headers getHeaders() {
        String oauthToken = getOAuthToken();
        return new Headers(new Header("Authorization", "Bearer " + oauthToken));
    }

    private String getOAuthToken() {
        // Logic to fetch OAuth2 token from Identity Provider (Azure AD, etc.)
        return "sample-oauth-token";
    }
}

3. Configuring Data Service for Test Case Data

You can choose different data sources for test cases (e.g., Azure Test Plans, JSON files, etc.).

DataServiceConfig.java

package com.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataServiceConfig {

    @Autowired
    private TestDataService testDataService;

    @Bean
    public TestDataService getTestDataService() {
        // Configuration logic, could read from properties
        String dataSource = "json";  // Dynamically from properties or environment
        if ("azure".equalsIgnoreCase(dataSource)) {
            return new AzureTestPlanDataService();
        } else {
            return new JsonFileTestDataService();
        }
    }
}

TestDataService.java (Interface for data service)

package com.api.service;

import com.api.testcases.TestCase;
import java.util.List;

public interface TestDataService {
    List<TestCase> fetchTestCases();
}

JsonFileTestDataService.java (For JSON file data)

package com.api.service;

import com.api.testcases.TestCase;
import com.api.utils.TestCaseParser;
import java.util.List;

public class JsonFileTestDataService implements TestDataService {

    @Override
    public List<TestCase> fetchTestCases() {
        // Reading from JSON file or another source
        return TestCaseParser.parseTestCases("test-case-data.json");
    }
}

AzureTestPlanDataService.java (For Azure DevOps data)

package com.api.service;

import com.api.testcases.TestCase;
import java.util.List;

public class AzureTestPlanDataService implements TestDataService {

    @Override
    public List<TestCase> fetchTestCases() {
        // Fetch test cases from Azure DevOps API
        return AzureTestCaseFetcher.fetchTestCases();
    }
}

4. Test Execution and Response Validation

DynamicAPITest.java (Modularized)

package com.api.testcases;

import com.api.auth.AuthFactory;
import com.api.service.TestDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.List;

public class DynamicAPITest {

    @Autowired
    private AuthFactory authFactory;

    @Autowired
    private TestDataService testDataService;

    @Test
    public void executeAPITests() {
        List<TestCase> testCases = testDataService.fetchTestCases();

        for (TestCase testCase : testCases) {
            // Dynamic test execution logic
            // Use authFactory to get correct Auth object
            // Send API request, validate responses, etc.
        }
    }
}

5. Configuration File (application.properties)

This file allows you to configure the authentication type, data source, and other global settings.

# Authentication Config
auth.type=oauth2
auth.oauth2.clientId=your-client-id
auth.oauth2.clientSecret=your-client-secret
auth.apiKey=your-api-key
auth.jwt.token=your-jwt-token
auth.cert.filePath=path/to/cert.pfx
auth.cert.password=your-cert-password

# Data Service Config
data.service.type=azure   # Choices: azure, json

6. How to Customize

Authentication: Add a new class that implements the Auth interface and modify the AuthFactory if needed.

Test Data: Implement a new TestDataService for any custom data source (e.g., database, CSV, etc.).

Test Execution: You can implement custom logic inside the DynamicAPITest class, including adding additional test steps or validations.

Response Validation: Create custom ResponseValidator implementations and add them to the test execution logic.



---

7. How to Use

1. Clone the Repository: Clone the repo into your enterprise project.


2. Configure Authentication: Set the auth type and necessary details in the application.properties file.


3. Configure Test Data: Choose your data source (Azure Test Plan or JSON file) by setting data.service.type.


4. Run Tests: Execute the tests via Maven, CI/CD, or via the DynamicAPITest class.


5. Extend the Framework: Developers can add new authentication mechanisms, test data sources, or response validations without modifying core code.




---

8. Conclusion

This design makes the framework modular, configurable, and extendable. Enterprise developers can easily add their own logic for authentication, test data fetching, response validation, and test execution. This setup reduces the dependency on hardcoded configurations, and everything can be configured through properties files or custom logic.

