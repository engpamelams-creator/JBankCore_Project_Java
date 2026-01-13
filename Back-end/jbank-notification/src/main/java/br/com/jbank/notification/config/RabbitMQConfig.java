package br.com.jbank.notification.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do RabbitMQ para o Notification Service.
 * 
 * Define o conversor de mensagens JSON para deserializar
 * os eventos recebidos do JBank Core.
 */
@Configuration
public class RabbitMQConfig {

    /**
     * Configura o conversor de mensagens para JSON.
     * 
     * Usa Jackson para deserializar JSON em objetos Java.
     * Deve ser o mesmo conversor usado no JBank Core.
     * 
     * @return MessageConverter configurado para JSON
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
