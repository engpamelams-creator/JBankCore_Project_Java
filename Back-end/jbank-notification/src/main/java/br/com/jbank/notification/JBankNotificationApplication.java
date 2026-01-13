package br.com.jbank.notification;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * JBank Notification Service - Microsserviço de Notificações
 * 
 * Este microsserviço é responsável por consumir eventos de transferência
 * do JBank Core e enviar notificações por email/SMS aos usuários.
 * 
 * Arquitetura:
 * - Event-Driven: Consome eventos via RabbitMQ
 * - Desacoplado: Funciona independentemente do JBank Core
 * - Resiliente: Continua processando mesmo se o Core estiver offline
 * - Escalável: Pode ter múltiplas instâncias consumindo a mesma fila
 * 
 * @author Pamela Menezes
 * @version 1.0
 * @since 2026-01-13
 */
@SpringBootApplication
@EnableRabbit
public class JBankNotificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(JBankNotificationApplication.class, args);
        System.out.println("""
            
            ╔═══════════════════════════════════════════════════════════╗
            ║                                                           ║
            ║   🔔 JBank Notification Service Started Successfully! 🔔  ║
            ║                                                           ║
            ║   Port: 8081                                              ║
            ║   Queue: transaction-notification-queue                  ║
            ║   Status: Listening for events...                         ║
            ║                                                           ║
            ╚═══════════════════════════════════════════════════════════╝
            
            """);
    }
}
