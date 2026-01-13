package br.com.jbank.pixvalidator.service;

import br.com.jbank.pixvalidator.enums.PixKeyType;
import br.com.jbank.pixvalidator.model.ValidationResponse;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.regex.Pattern;

/**
 * Serviço de validação de chaves PIX usando expressões regulares.
 * 
 * Performance: Validação em ~1ms (muito rápido!)
 * 
 * @author Pamela Menezes
 */
@ApplicationScoped
public class PixValidatorService {
    
    // Regex patterns para validação
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    private static final Pattern CPF_PATTERN = Pattern.compile(
        "^\\d{11}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\+55\\d{10,11}$"
    );
    
    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );
    
    /**
     * Valida uma chave PIX de acordo com seu tipo.
     * 
     * @param key Chave PIX a ser validada
     * @param type Tipo da chave PIX
     * @return ValidationResponse com resultado da validação
     */
    public ValidationResponse validate(String key, PixKeyType type) {
        if (key == null || key.trim().isEmpty()) {
            return ValidationResponse.failure(key, type, "Key cannot be null or empty");
        }
        
        if (type == null) {
            return ValidationResponse.failure(key, null, "Type cannot be null");
        }
        
        return switch (type) {
            case EMAIL -> validateEmail(key);
            case CPF -> validateCpf(key);
            case PHONE -> validatePhone(key);
            case RANDOM -> validateRandom(key);
        };
    }
    
    /**
     * Valida chave PIX do tipo EMAIL.
     */
    private ValidationResponse validateEmail(String key) {
        if (EMAIL_PATTERN.matcher(key).matches()) {
            return ValidationResponse.success(key, PixKeyType.EMAIL);
        }
        return ValidationResponse.failure(key, PixKeyType.EMAIL, 
            "Invalid email format. Expected: user@example.com");
    }
    
    /**
     * Valida chave PIX do tipo CPF.
     */
    private ValidationResponse validateCpf(String key) {
        // Remove caracteres não numéricos
        String cleanKey = key.replaceAll("[^0-9]", "");
        
        if (CPF_PATTERN.matcher(cleanKey).matches()) {
            return ValidationResponse.success(cleanKey, PixKeyType.CPF);
        }
        return ValidationResponse.failure(key, PixKeyType.CPF, 
            "Invalid CPF format. Expected: 11 digits (e.g., 12345678901)");
    }
    
    /**
     * Valida chave PIX do tipo PHONE.
     */
    private ValidationResponse validatePhone(String key) {
        // Remove espaços e hífens
        String cleanKey = key.replaceAll("[\\s-]", "");
        
        if (PHONE_PATTERN.matcher(cleanKey).matches()) {
            return ValidationResponse.success(cleanKey, PixKeyType.PHONE);
        }
        return ValidationResponse.failure(key, PixKeyType.PHONE, 
            "Invalid phone format. Expected: +55XXXXXXXXXXX (e.g., +5511987654321)");
    }
    
    /**
     * Valida chave PIX do tipo RANDOM (UUID).
     */
    private ValidationResponse validateRandom(String key) {
        if (UUID_PATTERN.matcher(key).matches()) {
            return ValidationResponse.success(key, PixKeyType.RANDOM);
        }
        return ValidationResponse.failure(key, PixKeyType.RANDOM, 
            "Invalid UUID format. Expected: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
    }
}
