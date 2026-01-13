package br.com.pamela.jbank.modulos.transacoes;

import br.com.pamela.jbank.BaseIntegrationTest;
import br.com.pamela.jbank.modulos.carteiras.domain.Wallet;
import br.com.pamela.jbank.modulos.carteiras.domain.WalletRepository;
import br.com.pamela.jbank.modulos.transacoes.controller.dto.TransferRequestDTO;
import br.com.pamela.jbank.modulos.transacoes.service.TransferService;
import br.com.pamela.jbank.modulos.usuarios.domain.User;
import br.com.pamela.jbank.modulos.usuarios.domain.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransferServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TransferService transferService;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    private Wallet senderWallet;
    private Wallet receiverWallet;

    @BeforeEach
    void setup() {
        walletRepository.deleteAll();
        userRepository.deleteAll();

        var user1 = userRepository.save(User.builder().name("Pamela Sender").email("pamela@jbank.com").build());
        var user2 = userRepository.save(User.builder().name("Dev Receiver").email("dev@jbank.com").build());

        senderWallet = walletRepository.save(Wallet.builder()
                .user(user1)
                .balance(new BigDecimal("1000.00")) // R$ 1000,00
                .build());

        receiverWallet = walletRepository.save(Wallet.builder()
                .user(user2)
                .balance(BigDecimal.ZERO)
                .build());
    }

    @Test
    @DisplayName("Should transfer money successfully")
    void shouldTransferSuccess() {
        // Act
        TransferRequestDTO request = new TransferRequestDTO(senderWallet.getId(), receiverWallet.getId(), new BigDecimal("100.00"));
        transferService.performTransfer(request);

        var updatedSender = walletRepository.findById(senderWallet.getId()).orElseThrow();
        var updatedReceiver = walletRepository.findById(receiverWallet.getId()).orElseThrow();

        // Assert com BigDecimal: usar compareTo é mais seguro que equals
        assertEquals(0, updatedSender.getBalance().compareTo(new BigDecimal("900.00")));
        assertEquals(0, updatedReceiver.getBalance().compareTo(new BigDecimal("100.00")));
    }

    @Test
    @DisplayName("RACE CONDITION CHECK: Should handle concurrent transfers correctly with Pessimistic Lock")
    void shouldHandleConcurrentTransfers() throws InterruptedException {
        // Scenario: 10 threads trying to transfer R$ 100,00 each.
        // Initial Balance: R$ 1000,00.
        // Expected Result: All 10 succeed, balance becomes 0.
        // WITHOUT LOCK: Some updates would be lost, and balance might remain positive (e.g. 500).

        int threadCount = 10;
        BigDecimal transferAmount = new BigDecimal("100.00");
        
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    transferService.performTransfer(new TransferRequestDTO(senderWallet.getId(), receiverWallet.getId(), transferAmount));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        var finalSender = walletRepository.findById(senderWallet.getId()).orElseThrow();
        var finalReceiver = walletRepository.findById(receiverWallet.getId()).orElseThrow();
        
        System.out.println("Success Count: " + successCount.get());
        System.out.println("Error Count: " + errorCount.get());
        System.out.println("Final Sender Balance: " + finalSender.getBalance());

        // Verificações
        assertEquals(10, successCount.get(), "All 10 transactions should succeed sequentially due to locking");
        assertEquals(0, finalSender.getBalance().compareTo(BigDecimal.ZERO), "Balance should be zero");
        assertEquals(0, finalReceiver.getBalance().compareTo(new BigDecimal("1000.00")), "Receiver should have 1000");
    }
}
