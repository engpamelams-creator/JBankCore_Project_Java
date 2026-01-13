# JBank Notification Service

Microsserviço responsável por processar notificações de transferências do JBank Core.

## 🎯 Objetivo

Este serviço consome eventos de transferência via RabbitMQ e envia notificações por email aos usuários (simulado via logs).

## 🏗️ Arquitetura

- **Tipo**: Consumer (Event-Driven)
- **Porta**: 8081
- **Fila**: `transaction-notification-queue`
- **Tecnologias**: Spring Boot 3.4.1, Java 21, RabbitMQ

## 🚀 Como Executar

### Pré-requisitos

1. Docker rodando (para RabbitMQ)
2. Java 21 instalado
3. Maven instalado

### Passo a Passo

```bash
# 1. Iniciar RabbitMQ (na raiz do JBankCore)
cd ..
docker-compose up -d rabbitmq

# 2. Voltar para o diretório do notification service
cd jbank-notification

# 3. Compilar o projeto
mvn clean install

# 4. Executar o serviço
mvn spring-boot:run
```

## 📊 Verificação

Após iniciar, você verá:

```
╔═══════════════════════════════════════════════════════════╗
║                                                           ║
║   🔔 JBank Notification Service Started Successfully! 🔔  ║
║                                                           ║
║   Port: 8081                                              ║
║   Queue: transaction-notification-queue                  ║
║   Status: Listening for events...                         ║
║                                                           ║
╚═══════════════════════════════════════════════════════════╝
```

Quando uma transferência for realizada no JBank Core, você verá logs como:

```
========================================
📨 NEW NOTIFICATION EVENT RECEIVED
========================================
Transaction ID: 123e4567-e89b-12d3-a456-426614174000
Amount: R$ 100,00
Timestamp: 2026-01-13T19:54:45
----------------------------------------
📧 Email sent to SENDER: sender@example.com
   Subject: Transferência Realizada com Sucesso
   Message: Você transferiu R$ 100,00 para receiver@example.com
📧 Email sent to RECEIVER: receiver@example.com
   Subject: Você Recebeu uma Transferência
   Message: Você recebeu R$ 100,00 de sender@example.com
✅ Notifications sent successfully!
========================================
```

## 🔧 Configuração

Edite `src/main/resources/application.yml` para alterar:

- Porta do servidor
- Configurações do RabbitMQ
- Concorrência de consumidores
- Níveis de log

## 🎨 Próximos Passos (Produção)

1. Integrar com serviço real de email (SendGrid, AWS SES)
2. Adicionar suporte para SMS via Twilio
3. Implementar Dead Letter Queue (DLQ) para falhas
4. Adicionar métricas e monitoramento (Prometheus, Grafana)
5. Implementar templates de email com HTML
