package br.com.jbank.core.modulos.pix.controller;

import br.com.jbank.core.infra.response.JSendSuccessResponse;
import br.com.jbank.core.modulos.pix.dto.PixDTOs;
import br.com.jbank.core.modulos.pix.service.PixService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pix/keys")
@Tag(name = "Pix Module", description = "Management of Pix Keys and Payments")
@SecurityRequirement(name = "bearerAuth")
public class PixController {

    private final PixService pixService;

    @Autowired
    public PixController(PixService pixService) {
        this.pixService = pixService;
    }

    @PostMapping
    @Operation(summary = "Create a new Pix Key")
    public ResponseEntity<JSendSuccessResponse<PixDTOs.PixKeyResponse>> createKey(@RequestBody @Valid PixDTOs.PixKeyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(JSendSuccessResponse.of(pixService.createKey(request)));
    }

    @GetMapping
    @Operation(summary = "List all Pix Keys for the authenticated user")
    public ResponseEntity<JSendSuccessResponse<List<PixDTOs.PixKeyResponse>>> listKeys() {
        return ResponseEntity.ok(JSendSuccessResponse.of(pixService.listMyKeys()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Pix Key")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteKey(@PathVariable UUID id) {
        pixService.deleteKey(id);
    }

}
