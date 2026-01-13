package br.com.jbank.core.infra.messaging;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do RabbitMQ para o JBank Core.
 * 
 * Define a fila de notificações e configura o RabbitTemplate para serialização JSON.
 * Esta configuração permite que o JBank Core publique eventos de transferência
 * que serão consumidos pelo serviço de notificação.
 */
@Configuration
public class RabbitMQConfig {

    /**
     * Nome da fila onde eventos de transferência serão publicados.
     * Esta fila será consumida pelo jbank-notification service.
     */
    public static final String TRANSACTION_NOTIFICATION_QUEUE = "transaction-notification-queue";

    /**
     * Define a fila de notificações de transações.
     * 
     * Configurações:
     * - durable: true - A fila sobrevive a reinicializações do RabbitMQ
     * - exclusive: false - Múltiplos consumidores podem se conectar
     * - autoDelete: false - A fila não é deletada quando não há consumidores
     * 
     * @return Queue configurada
     */
    @Bean
    public Queue transactionNotificationQueue() {
        return new Queue(TRANSACTION_NOTIFICATION_QUEUE, true, false, false);
    }

    /**
     * Configura o conversor de mensagens para JSON.
     * 
     * Usa Jackson para serializar/deserializar objetos Java para/de JSON.
     * Isso permite enviar POJOs diretamente via RabbitTemplate.
     * 
     * @return MessageConverter configurado para JSON
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Configura o RabbitTemplate com o conversor JSON.
     * 
     * O RabbitTemplate é usado para enviar mensagens ao RabbitMQ.
     * 
     * @param connectionFactory Factory de conexão fornecida pelo Spring Boot
     * @return RabbitTemplate configurado
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
