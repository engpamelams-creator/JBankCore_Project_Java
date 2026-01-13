package br.com.jbank.integrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * JBank Integrator - External Integrations Gateway
 * 
 * This microservice acts as a gateway for external financial integrations:
 * - Brasil API: Real-time Brazilian banks data
 * - Open Finance: Simulated account balance (architecture ready for production)
 * 
 * @author Pamela Menezes
 */
@SpringBootApplication
@EnableFeignClients  // Enable Feign Clients for declarative HTTP calls
public class JBankIntegratorApplication {

    public static void main(String[] args) {
        System.out.println("""
            ╔═══════════════════════════════════════════════════════════╗
            ║                                                           ║
            ║        🌐 JBank Integrator - Starting...                 ║
            ║                                                           ║
            ║        External Integrations Gateway                     ║
            ║        - Brasil API (Real)                               ║
            ║        - Open Finance (Simulated)                        ║
            ║                                                           ║
            ║        Port: 8083                                        ║
            ║                                                           ║
            ╚═══════════════════════════════════════════════════════════╝
        """);
        
        SpringApplication.run(JBankIntegratorApplication.class, args);
    }
}
