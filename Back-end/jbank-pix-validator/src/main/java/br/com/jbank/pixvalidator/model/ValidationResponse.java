package br.com.jbank.pixvalidator.model;

import br.com.jbank.pixvalidator.enums.PixKeyType;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response DTO para validação de chave PIX.
 * 
 * @author Pamela Menezes
 */
public class ValidationResponse {
    
    @JsonProperty("valid")
    private boolean valid;
    
    @JsonProperty("key")
    private String key;
    
    @JsonProperty("type")
    private PixKeyType type;
    
    @JsonProperty("message")
    private String message;
    
    // Constructors
    public ValidationResponse() {
    }
    
    public ValidationResponse(boolean valid, String key, PixKeyType type, String message) {
        this.valid = valid;
        this.key = key;
        this.type = type;
        this.message = message;
    }
    
    // Static factory methods
    public static ValidationResponse success(String key, PixKeyType type) {
        return new ValidationResponse(true, key, type, "Valid PIX key format");
    }
    
    public static ValidationResponse failure(String key, PixKeyType type, String reason) {
        return new ValidationResponse(false, key, type, reason);
    }
    
    // Getters and Setters
    public boolean isValid() {
        return valid;
    }
    
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public PixKeyType getType() {
        return type;
    }
    
    public void setType(PixKeyType type) {
        this.type = type;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return "ValidationResponse{" +
                "valid=" + valid +
                ", key='" + key + '\'' +
                ", type=" + type +
                ", message='" + message + '\'' +
                '}';
    }
}
