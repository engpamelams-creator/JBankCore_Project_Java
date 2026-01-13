package br.com.jbank.pixvalidator.model;

import br.com.jbank.pixvalidator.enums.PixKeyType;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request DTO para validação de chave PIX.
 * 
 * @author Pamela Menezes
 */
public class ValidationRequest {
    
    @JsonProperty("key")
    private String key;
    
    @JsonProperty("type")
    private PixKeyType type;
    
    // Constructors
    public ValidationRequest() {
    }
    
    public ValidationRequest(String key, PixKeyType type) {
        this.key = key;
        this.type = type;
    }
    
    // Getters and Setters
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
    
    @Override
    public String toString() {
        return "ValidationRequest{" +
                "key='" + key + '\'' +
                ", type=" + type +
                '}';
    }
}
