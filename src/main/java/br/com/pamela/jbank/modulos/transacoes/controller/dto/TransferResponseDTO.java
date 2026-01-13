package br.com.pamela.jbank.modulos.transacoes.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Dados retornados após uma transferência bem-sucedida.
 * Não expomos a Entidade Transaction diretamente para proteger o domínio.
 */
public record TransferResponseDTO(
    UUID transactionId,
    BigDecimal amount,
    LocalDateTime timestamp,
    String status
) {}
