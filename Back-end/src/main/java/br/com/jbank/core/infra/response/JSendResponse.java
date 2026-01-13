package br.com.jbank.core.infra.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Abstract base class for all JSend responses.
 * <p>
 * Ensures all responses have a consistent structure with a status field.
 * </p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class JSendResponse {
    
    private final JSendStatus status;
    
    protected JSendResponse(JSendStatus status) {
        this.status = status;
    }
    
    public JSendStatus getStatus() {
        return status;
    }
}
