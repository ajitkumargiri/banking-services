To further enhance the Spring Boot API Test Automation Framework, I'll add support for the following:

1. No Auth: A configuration to handle API tests that don’t require any authentication.


2. Certificate-Based Authentication: Add support for certificate authentication using client certificates.


3. Generic Test Plan Format: A configuration and example for a generic test plan format that can be used to define your API tests.



1. No Auth Configuration

To allow for tests that don’t require any authentication, we can extend the existing configuration system to handle a NoAuth scenario.

Update application.properties

Add a new property to handle "No Authentication":

# Authentication Type (OAuth2, JWT, ApiKey, NoAuth, Certificate)
auth.type=NoAuth

# OAuth2 Configuration
oauth2.token_url=https://oauth2.example.com/token
oauth2.client_id=your-client-id
oauth2.client_secret=your-client-secret
oauth2.grant_type=client_credentials

# JWT Configuration
jwt.token=your-jwt-token

# API Key Configuration
api_key.key=your-api-key

# Certificate Configuration
certificate.path=classpath:/certs/client-certificate.p12
certificate.password=your-certificate-password

Update AuthFactory.java

Extend the AuthFactory class to support the NoAuth type:

package com.api.authentication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthFactory {

    @Value("${auth.type}")
    private String authType;

    public AuthenticationMethod getAuthenticationMethod() {
        switch (authType) {
            case "OAuth2":
                return new OAuth2Auth();
            case "JWT":
                return new JwtAuth();
            case "ApiKey":
                return new ApiKeyAuth();
            case "NoAuth":
                return new NoAuth();
            case "Certificate":
                return new CertificateAuth();
            default:
                throw new IllegalArgumentException("Unsupported authentication type: " + authType);
        }
    }
}

Implement NoAuth.java

package com.api.authentication;

import io.restassured.http.Headers;
import io.restassured.http.Header;
import org.springframework.stereotype.Component;

@Component
public class NoAuth implements AuthenticationMethod {

    @Override
    public Headers getHeaders() {
        // No authentication headers required
        return new Headers();
    }
}

2. Certificate-Based Authentication

To add certificate-based authentication in Spring Boot, you can use Java's KeyStore and SSLContext. The certificate.path and certificate.password properties in the application.properties will define the path to your certificate file and its password.

Update AuthFactory.java to Add Certificate Authentication

package com.api.authentication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthFactory {

    @Value("${auth.type}")
    private String authType;

    public AuthenticationMethod getAuthenticationMethod() {
        switch (authType) {
            case "OAuth2":
                return new OAuth2Auth();
            case "JWT":
                return new JwtAuth();
            case "ApiKey":
                return new ApiKeyAuth();
            case "NoAuth":
                return new NoAuth();
            case "Certificate":
                return new CertificateAuth();
            default:
                throw new IllegalArgumentException("Unsupported authentication type: " + authType);
        }
    }
}

Implement CertificateAuth.java

This class handles client certificate-based authentication using KeyStore for SSL certificates.

package com.api.authentication;

import io.restassured.http.Headers;
import io.restassured.http.Header;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.restassured.RestAssured;
import java.io.File;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

@Component
public class CertificateAuth implements AuthenticationMethod {

    @Value("${certificate.path}")
    private String certificatePath;

    @Value("${certificate.password}")
    private String certificatePassword;

    @Override
    public Headers getHeaders() {
        // Load the client certificate
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            File certificateFile = new File(certificatePath);
            keyStore.load(certificateFile.toURI().toURL().openStream(), certificatePassword.toCharArray());

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, certificatePassword.toCharArray());

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
            RestAssured.config = RestAssured.config().sslConfig(RestAssured.config().getSSLConfig().sslContext(sslContext));

            return new Headers();  // No additional headers for certificate-based auth
        } catch (Exception e) {
            throw new RuntimeException("Failed to load certificate for authentication", e);
        }
    }
}

3. Generic Test Plan Format

A generic test plan format allows you to define your test cases and their configurations in a structured format. In this case, we will use a JSON format that holds all the relevant information like test case ID, request method, URL, request body, expected status code, authentication type, and dynamic variables.

test-case-data.json

[
  {
    "testCaseId": "TC-001",
    "testCaseName": "User Data Transformation API Test",
    "method": "POST",
    "url": "https://api.example.com/transformUserData",
    "requestBody": "{ \"userId\": \"{{userId}}\" }",
    "headers": {
      "Authorization": "Bearer {{authToken}}"
    },
    "expectedStatusCode": 200,
    "expectedResponseBody": ".*\"employeeId\":\\d+.*",
    "authType": "OAuth2",
    "dynamicVariables": ["userId", "employeeId"],
    "dependency": "None"
  },
  {
    "testCaseId": "TC-002",
    "testCaseName": "Simple Public API Test Without Auth",
    "method": "GET",
    "url": "https://api.example.com/publicData",
    "requestBody": "",
    "headers": {},
    "expectedStatusCode": 200,
    "expectedResponseBody": ".*\"status\": \"success\".*",
    "authType": "NoAuth",
    "dynamicVariables": [],
    "dependency": "None"
  }
]

TestCase.java

package com.api.testcases;

import java.util.List;

public class TestCase {

    private String testCaseId;
    private String testCaseName;
    private String method;
    private String url;
    private String requestBody;
    private String expectedStatusCode;
    private String expectedResponseBody;
    private String authType;
    private List<String> dynamicVariables;
    private String dependency;

    // Getters and Setters
}

4. Example Usage and Explanation

Authentication Choices

OAuth2: This method is used when your API requires OAuth2 token authentication. You provide the token URL, client ID, and client secret, and it automatically handles token expiration.

JWT: This method uses a JWT token for authentication. The token is provided in the Authorization header in the format Bearer {token}.

API Key: This method adds an API key as a custom header, for example, x-api-key: {apiKey}.

No Auth: For public APIs that don’t require authentication, you can use this option. No authentication headers will be added to the request.

Certificate: This method uses client certificates for SSL authentication. The certificate file and password are configured in the application.properties file. The test will load the certificate, configure the SSL context, and send requests using that certificate.


How to Use the Framework

1. Clone the Repository: Clone the repository into your local machine.


2. Configure Authentication: In the application.properties, select the authentication type you want (e.g., OAuth2, JWT, NoAuth, Certificate).


3. Define Test Cases: Update the test-case-data.json file with your API tests. You can define the API request method, URL, expected status code, dynamic variables, and authentication type.


4. Run Tests: You can run the tests using Maven or through your IDE:

Using Maven:

mvn spring-boot:run

Running Tests: The tests are executed in parallel as specified in the DynamicAPITest.java class.




Test Output

The results will be logged, and you can use TestNG or JUnit for test result reporting. The test results are logged in the console or a log file for your reference.


---

5. Conclusion

This framework provides a configurable API test automation solution that can handle multiple authentication types (OAuth2, JWT, API Key, Certificate, and NoAuth) and supports dynamic API testing with chained variables and dependencies. The framework is designed to be easily configurable via the application.properties file and can be integrated into a CI/CD pipeline.

You can extend and modify this framework to handle more complex test cases, dynamic responses, and further integrations.

