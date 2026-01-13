package br.com.jbank.core.modulos.pix.dto;

import br.com.jbank.core.modulos.pix.domain.PixKeyType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public class PixDTOs {

    public record PixKeyRequest(
            @NotNull(message = "Key type is required")
            PixKeyType type,

            @NotNull(message = "Key value is required")
            @Size(min = 3, max = 100, message = "Key must be between 3 and 100 characters")
            String key
    ) {}

    public record PixKeyResponse(
            UUID id,
            String key,
            PixKeyType type,
            boolean active,
            LocalDateTime createdAt
    ) {}
}
