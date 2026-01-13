package br.com.pamela.jbank.modulos.transacoes.controller.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferDTO(UUID senderId, UUID receiverId, BigDecimal amount) {
}
