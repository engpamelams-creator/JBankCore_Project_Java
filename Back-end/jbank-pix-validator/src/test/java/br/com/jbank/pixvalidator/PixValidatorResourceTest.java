package br.com.jbank.pixvalidator;

import br.com.jbank.pixvalidator.enums.PixKeyType;
import br.com.jbank.pixvalidator.model.ValidationRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * Testes de integração para o endpoint de validação de PIX.
 * 
 * @author Pamela Menezes
 */
@QuarkusTest
public class PixValidatorResourceTest {
    
    @Test
    public void testValidEmailKey() {
        ValidationRequest request = new ValidationRequest("pamela@example.com", PixKeyType.EMAIL);
        
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/pix/validate")
        .then()
            .statusCode(200)
            .body("valid", is(true))
            .body("type", is("EMAIL"))
            .body("message", notNullValue());
    }
    
    @Test
    public void testInvalidEmailKey() {
        ValidationRequest request = new ValidationRequest("invalid-email", PixKeyType.EMAIL);
        
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/pix/validate")
        .then()
            .statusCode(200)
            .body("valid", is(false))
            .body("type", is("EMAIL"));
    }
    
    @Test
    public void testValidCpfKey() {
        ValidationRequest request = new ValidationRequest("12345678901", PixKeyType.CPF);
        
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/pix/validate")
        .then()
            .statusCode(200)
            .body("valid", is(true))
            .body("type", is("CPF"));
    }
    
    @Test
    public void testValidPhoneKey() {
        ValidationRequest request = new ValidationRequest("+5511987654321", PixKeyType.PHONE);
        
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/pix/validate")
        .then()
            .statusCode(200)
            .body("valid", is(true))
            .body("type", is("PHONE"));
    }
    
    @Test
    public void testValidRandomKey() {
        ValidationRequest request = new ValidationRequest(
            "123e4567-e89b-12d3-a456-426614174000", 
            PixKeyType.RANDOM
        );
        
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/pix/validate")
        .then()
            .statusCode(200)
            .body("valid", is(true))
            .body("type", is("RANDOM");
    }
    
    @Test
    public void testHealthEndpoint() {
        given()
        .when()
            .get("/api/pix/health")
        .then()
            .statusCode(200)
            .body("status", is("UP"));
    }
}
