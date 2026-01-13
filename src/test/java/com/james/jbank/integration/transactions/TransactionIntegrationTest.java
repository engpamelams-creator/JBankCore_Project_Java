package com.james.jbank.integration.transactions;

import com.james.jbank.integration.AbstractIntegrationTest;
import com.james.jbank.modules.users.domain.User;
import com.james.jbank.modules.users.repository.UserRepository;
import com.james.jbank.modules.wallets.domain.Wallet;
import com.james.jbank.modules.wallets.repository.WalletRepository;
import com.james.jbank.modules.transactions.dto.CreateTransferDTO;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class TransactionIntegrationTest extends AbstractIntegrationTest {

    @LocalServerPort
    private Integer port;

    @Autowired private UserRepository userRepository;
    @Autowired private WalletRepository walletRepository;

    private Wallet walletSender;
    private Wallet walletReceiver;

    @BeforeEach
    void setUp() {
        RestAssured.reset();
        RestAssured.port = port;
        // Clean DB
        walletRepository.deleteAll();
        userRepository.deleteAll();

        // Seed Data
        User sender = userRepository.save(new User("Sender", "Bond", "111", "sender@test.com", "pass"));
        User receiver = userRepository.save(new User("Receiver", "Bond", "222", "receiver@test.com", "pass"));

        walletSender = new Wallet(sender);
        walletSender.setBalance(new BigDecimal("100.00")); // Give some money
        walletRepository.save(walletSender);

        walletReceiver = new Wallet(receiver);
        walletReceiver.setBalance(new BigDecimal("0.00"));
        walletRepository.save(walletReceiver);
    }

    @Test
    void shouldPerformTransferSuccessfully() {
        CreateTransferDTO transfer = new CreateTransferDTO(
            new BigDecimal("50.00"),
            walletSender.getId(),
            walletReceiver.getId()
        );

        given()
            .contentType(ContentType.JSON)
            .body(transfer)
        .when()
            .post("/transactions")
        .then()
            .statusCode(201)
            .body("amount", is(50.0f)); 
            // Note: RestAssured/JsonPath maps BigDecimal to Float/Double in matches usually
    }
}
