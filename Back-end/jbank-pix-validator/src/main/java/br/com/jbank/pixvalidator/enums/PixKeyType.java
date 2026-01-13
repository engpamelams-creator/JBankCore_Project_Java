package br.com.jbank.pixvalidator.enums;

/**
 * Tipos de chave PIX suportados pelo sistema.
 * 
 * @author Pamela Menezes
 */
public enum PixKeyType {
    /**
     * Chave PIX do tipo Email
     */
    EMAIL,
    
    /**
     * Chave PIX do tipo CPF (11 dígitos)
     */
    CPF,
    
    /**
     * Chave PIX do tipo Telefone (+55...)
     */
    PHONE,
    
    /**
     * Chave PIX do tipo Aleatória (UUID)
     */
    RANDOM
}
