package br.com.jbank.core.infra.response;

import java.util.Map;

/**
 * JSend response for business validation failures.
 * <p>
 * Used when the request is well-formed but fails business rules.
 * Examples: insufficient balance, duplicate email, invalid PIN.
 * </p>
 */
public class JSendFailResponse extends JSendResponse {
    
    private final Object data;
    
    private JSendFailResponse(Object data) {
        super(JSendStatus.FAIL);
        this.data = data;
    }
    
    /**
     * Factory method to create a fail response with a simple message.
     * 
     * @param message The failure message
     * @return A new JSendFailResponse instance
     */
    public static JSendFailResponse of(String message) {
        return new JSendFailResponse(Map.of("message", message));
    }
    
    /**
     * Factory method to create a fail response with detailed validation errors.
     * 
     * @param data Map containing field-level validation errors
     * @return A new JSendFailResponse instance
     */
    public static JSendFailResponse of(Map<String, String> data) {
        return new JSendFailResponse(data);
    }
    
    public Object getData() {
        return data;
    }
}
