package br.com.jbank.notification.listener;

import br.com.jbank.notification.model.TransferenciaEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Listener que consome eventos de transferência da fila RabbitMQ.
 * 
 * Este componente representa o coração do microsserviço de notificação.
 * Ele escuta a fila 'transaction-notification-queue' e processa cada evento
 * de transferência recebido, simulando o envio de emails.
 * 
 * Em produção, este listener seria integrado com serviços reais de email
 * como SendGrid, AWS SES, ou SMTP tradicional.
 */
@Component
@Slf4j
public class NotificationListener {

    private static final NumberFormat CURRENCY_FORMATTER = 
        NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    /**
     * Consome eventos de transferência da fila RabbitMQ.
     * 
     * O @RabbitListener automaticamente:
     * - Conecta-se à fila especificada
     * - Deserializa o JSON para TransferenciaEvent
     * - Invoca este método para cada mensagem
     * - Gerencia acknowledgment (ACK) automático após processamento bem-sucedido
     * 
     * @param event Evento de transferência recebido
     */
    @RabbitListener(queues = "transaction-notification-queue")
    public void handleTransferNotification(TransferenciaEvent event) {
        log.info("========================================");
        log.info("📨 NEW NOTIFICATION EVENT RECEIVED");
        log.info("========================================");
        log.info("Transaction ID: {}", event.transactionId());
        log.info("Amount: {}", CURRENCY_FORMATTER.format(event.amount()));
        log.info("Timestamp: {}", event.timestamp());
        log.info("----------------------------------------");
        
        try {
            // Simula o envio de email para o remetente
            sendEmailToSender(event);
            
            // Simula o envio de email para o destinatário
            sendEmailToReceiver(event);
            
            log.info("✅ Notifications sent successfully!");
            log.info("========================================\n");
            
        } catch (Exception e) {
            log.error("❌ Failed to send notifications for transaction [{}]: {}", 
                event.transactionId(), e.getMessage(), e);
            // Em produção, aqui poderíamos:
            // - Reenviar para uma Dead Letter Queue (DLQ)
            // - Salvar em um banco de dados de falhas
            // - Enviar alerta para monitoramento (Datadog, New Relic, etc.)
            throw e; // Propaga para RabbitMQ fazer retry conforme configurado
        }
    }

    /**
     * Simula o envio de email para o remetente da transferência.
     * 
     * Em produção, este método integraria com um serviço de email real.
     */
    private void sendEmailToSender(TransferenciaEvent event) {
        // Simula delay de processamento de email
        simulateEmailProcessing();
        
        log.info("📧 Email sent to SENDER: {}", event.senderEmail());
        log.info("   Subject: Transferência Realizada com Sucesso");
        log.info("   Message: Você transferiu {} para {}", 
            CURRENCY_FORMATTER.format(event.amount()), 
            event.receiverEmail());
    }

    /**
     * Simula o envio de email para o destinatário da transferência.
     * 
     * Em produção, este método integraria com um serviço de email real.
     */
    private void sendEmailToReceiver(TransferenciaEvent event) {
        // Simula delay de processamento de email
        simulateEmailProcessing();
        
        log.info("📧 Email sent to RECEIVER: {}", event.receiverEmail());
        log.info("   Subject: Você Recebeu uma Transferência");
        log.info("   Message: Você recebeu {} de {}", 
            CURRENCY_FORMATTER.format(event.amount()), 
            event.senderEmail());
    }

    /**
     * Simula o tempo de processamento de envio de email.
     * Remove este método em produção.
     */
    private void simulateEmailProcessing() {
        try {
            Thread.sleep(500); // 500ms para simular chamada de API externa
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
