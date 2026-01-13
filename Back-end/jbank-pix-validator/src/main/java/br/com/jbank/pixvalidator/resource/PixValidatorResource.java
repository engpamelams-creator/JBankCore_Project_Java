package br.com.jbank.pixvalidator.resource;

import br.com.jbank.pixvalidator.model.ValidationRequest;
import br.com.jbank.pixvalidator.model.ValidationResponse;
import br.com.jbank.pixvalidator.service.PixValidatorService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

/**
 * REST Resource para validação de chaves PIX.
 * 
 * Endpoint: POST /api/pix/validate
 * 
 * Performance: ~1ms por validação (Quarkus é supersônico!)
 * 
 * @author Pamela Menezes
 */
@Path("/api/pix")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "PIX Validator", description = "High-performance PIX key validation API")
public class PixValidatorResource {
    
    private static final Logger LOG = Logger.getLogger(PixValidatorResource.class);
    
    @Inject
    PixValidatorService validatorService;
    
    /**
     * Valida uma chave PIX.
     * 
     * @param request Dados da chave PIX a ser validada
     * @return ValidationResponse com resultado da validação
     */
    @POST
    @Path("/validate")
    @Operation(
        summary = "Validate PIX key",
        description = "Validates a PIX key format based on its type (EMAIL, CPF, PHONE, RANDOM)"
    )
    @APIResponse(
        responseCode = "200",
        description = "Validation completed successfully",
        content = @Content(schema = @Schema(implementation = ValidationResponse.class))
    )
    @APIResponse(
        responseCode = "400",
        description = "Invalid request"
    )
    public Response validate(ValidationRequest request) {
        LOG.infof("Validating PIX key: type=%s, key=%s", request.getType(), request.getKey());
        
        try {
            ValidationResponse response = validatorService.validate(request.getKey(), request.getType());
            
            if (response.isValid()) {
                LOG.infof("✅ Valid PIX key: %s", request.getKey());
            } else {
                LOG.warnf("❌ Invalid PIX key: %s - %s", request.getKey(), response.getMessage());
            }
            
            return Response.ok(response).build();
            
        } catch (Exception e) {
            LOG.errorf(e, "Error validating PIX key: %s", request.getKey());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ValidationResponse(false, request.getKey(), request.getType(), 
                            "Internal server error: " + e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Health check endpoint.
     */
    @GET
    @Path("/health")
    @Operation(summary = "Health check", description = "Check if the service is running")
    public Response health() {
        return Response.ok()
                .entity("{\"status\":\"UP\",\"service\":\"jbank-pix-validator\"}")
                .build();
    }
}
