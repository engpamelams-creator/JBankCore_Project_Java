package br.com.pamela.jbank.modulos.pix.controller;

import br.com.pamela.jbank.modulos.pix.dto.PixDTOs;
import br.com.pamela.jbank.modulos.pix.service.PixService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pix/keys")
@RequiredArgsConstructor
@Tag(name = "Pix Module", description = "Management of Pix Keys and Payments")
@SecurityRequirement(name = "bearerAuth")
public class PixController {

    private final PixService pixService;

    @PostMapping
    @Operation(summary = "Create a new Pix Key")
    public ResponseEntity<PixDTOs.PixKeyResponse> createKey(@RequestBody @Valid PixDTOs.PixKeyRequest request) {
        return new ResponseEntity<>(pixService.createKey(request), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List all Pix Keys for the authenticated user")
    public ResponseEntity<List<PixDTOs.PixKeyResponse>> listKeys() {
        return ResponseEntity.ok(pixService.listMyKeys());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Pix Key")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteKey(@PathVariable UUID id) {
        pixService.deleteKey(id);
    }
}
