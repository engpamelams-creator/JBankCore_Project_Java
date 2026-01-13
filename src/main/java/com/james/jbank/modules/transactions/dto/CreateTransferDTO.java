package com.james.jbank.modules.transactions.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateTransferDTO(
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    BigDecimal value,

    @NotNull(message = "Payer ID is required")
    Long payerId,

    @NotNull(message = "Payee ID is required")
    Long payeeId
) {}
