package br.com.jbank.notification.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evento de transferência recebido do JBank Core via RabbitMQ.
 * 
 * Este record deve ser idêntico ao TransferenciaEvent do JBank Core
 * para garantir a deserialização correta das mensagens.
 */
public record TransferenciaEvent(
    UUID transactionId,
    BigDecimal amount,
    String senderEmail,
    String receiverEmail,
    LocalDateTime timestamp
) {
}
