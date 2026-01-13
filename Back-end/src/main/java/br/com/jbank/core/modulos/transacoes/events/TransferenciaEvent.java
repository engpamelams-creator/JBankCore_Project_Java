package br.com.jbank.core.modulos.transacoes.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evento publicado quando uma transferência é concluída com sucesso.
 * 
 * Este record é imutável e será serializado como JSON para envio via RabbitMQ.
 * Contém todas as informações necessárias para o serviço de notificação processar o evento.
 * 
 * @param transactionId ID único da transação
 * @param amount Valor transferido
 * @param senderEmail Email do remetente (para notificação)
 * @param receiverEmail Email do destinatário (para notificação)
 * @param timestamp Momento em que a transferência foi concluída
 */
public record TransferenciaEvent(
    UUID transactionId,
    BigDecimal amount,
    String senderEmail,
    String receiverEmail,
    LocalDateTime timestamp
) {
    /**
     * Factory method para criar um evento a partir de dados da transação.
     * 
     * @param transactionId ID da transação
     * @param amount Valor transferido
     * @param senderEmail Email do remetente
     * @param receiverEmail Email do destinatário
     * @return Novo evento com timestamp atual
     */
    public static TransferenciaEvent of(
            UUID transactionId,
            BigDecimal amount,
            String senderEmail,
            String receiverEmail) {
        return new TransferenciaEvent(
            transactionId,
            amount,
            senderEmail,
            receiverEmail,
            LocalDateTime.now()
        );
    }
}
