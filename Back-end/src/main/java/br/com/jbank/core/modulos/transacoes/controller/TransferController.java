package br.com.jbank.core.modulos.transacoes.controller;

import br.com.jbank.core.infra.response.JSendSuccessResponse;
import br.com.jbank.core.modulos.transacoes.controller.dto.TransferRequestDTO;
import br.com.jbank.core.modulos.transacoes.controller.dto.TransferResponseDTO;
import br.com.jbank.core.modulos.transacoes.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transfers")
@Tag(name = "Transferências", description = "Endpoints para gestão de transações financeiras")
public class TransferController {

    private final TransferService transferService;

    @Autowired
    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    @Operation(summary = "Realiza uma transferência entre carteiras", 
               description = "Inicia uma transferência segura utilizando Lock Pessimista no banco de dados.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Transferência realizada com sucesso"),
        @ApiResponse(responseCode = "422", description = "Saldo insuficiente ou erro de validação"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    })
    public ResponseEntity<JSendSuccessResponse<TransferResponseDTO>> createTransfer(@RequestBody @Valid TransferRequestDTO request) {
        
        // Pamela: O Controller deve ser "burro", apenas recebendo e passando para o Service (Core).
        // Toda regra (como validar saldo) fica no Service.
        TransferResponseDTO response = transferService.performTransfer(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(JSendSuccessResponse.of(response));
    }

}
