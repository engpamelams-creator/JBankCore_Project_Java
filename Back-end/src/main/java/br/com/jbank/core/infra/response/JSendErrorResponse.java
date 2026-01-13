package br.com.jbank.core.infra.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * JSend response for system errors.
 * <p>
 * Used when an exception is thrown during request processing.
 * Should not expose internal implementation details to the client.
 * </p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JSendErrorResponse extends JSendResponse {
    
    private final String message;
    private final Integer code;
    private final Object data;
    
    private JSendErrorResponse(String message, Integer code, Object data) {
        super(JSendStatus.ERROR);
        this.message = message;
        this.code = code;
        this.data = data;
    }
    
    /**
     * Factory method to create an error response with just a message.
     * 
     * @param message The error message
     * @return A new JSendErrorResponse instance
     */
    public static JSendErrorResponse of(String message) {
        return new JSendErrorResponse(message, null, null);
    }
    
    /**
     * Factory method to create an error response with message and error code.
     * 
     * @param message The error message
     * @param code Application-specific error code
     * @return A new JSendErrorResponse instance
     */
    public static JSendErrorResponse of(String message, Integer code) {
        return new JSendErrorResponse(message, code, null);
    }
    
    /**
     * Factory method to create an error response with message, code, and additional data.
     * 
     * @param message The error message
     * @param code Application-specific error code
     * @param data Additional error context (use sparingly, avoid exposing internals)
     * @return A new JSendErrorResponse instance
     */
    public static JSendErrorResponse of(String message, Integer code, Object data) {
        return new JSendErrorResponse(message, code, data);
    }
    
    public String getMessage() {
        return message;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public Object getData() {
        return data;
    }
}
