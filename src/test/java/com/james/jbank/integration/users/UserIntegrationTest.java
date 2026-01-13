package com.james.jbank.integration.users;

import com.james.jbank.integration.AbstractIntegrationTest;
import com.james.jbank.modules.users.dto.CreateUserDTO;
import com.james.jbank.modules.users.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserIntegrationTest extends AbstractIntegrationTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        RestAssured.reset();
        RestAssured.port = port;
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        CreateUserDTO user = new CreateUserDTO(
            "James",
            "Bond",
            "007",
            "james.bond@mi6.com",
            "secret"
        );

        given()
            .contentType(ContentType.JSON)
            .body(user)
        .when()
            .post("/users")
        .then()
            .statusCode(201)
            .body("firstName", equalTo("James"))
            .body("email", equalTo("james.bond@mi6.com"));
    }
}
