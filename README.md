# 🏦 JBank Core API

> **Backend Fintech de Nível Empresarial com Arquitetura Modular Monolítica + Microsserviços** construído com Java 21, Spring Boot 3.4, RabbitMQ e princípios de Clean Architecture.

![Java 21](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot 3.4](https://img.shields.io/badge/Spring_Boot-3.4-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791?style=for-the-badge&logo=postgresql&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3.13-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED?style=for-the-badge&logo=docker&logoColor=white)

---

## 📋 Índice

- [Arquitetura & Design](#️-arquitetura--design)
- [Módulos](#-módulos)
- [Arquitetura de Microsserviços](#-arquitetura-de-microsserviços-event-driven)
- [Começando](#-começando)
- [Documentação da API](#-documentação-da-api)
- [Estrutura do Projeto](#️-estrutura-do-projeto)
- [Segurança](#-segurança)
- [Testes](#-testes)

---

## 🏗️ Arquitetura & Design

Este projeto adota uma **arquitetura híbrida**:

### 1. Modular Monolítico (JBank Core)
Código agrupado por **Domínio/Funcionalidade** (`modulos/*`) em vez de camadas técnicas. Esta abordagem garante alta coesão e baixo acoplamento.

### 2. Event-Driven Microservices
Microsserviço de notificação desacoplado usando **RabbitMQ** para comunicação assíncrona.

```
┌─────────────────────┐         ┌──────────────┐         ┌─────────────────────────┐
│   JBank Core API    │         │   RabbitMQ   │         │  Notification Service   │
│     (Port 8080)     │────────▶│   Message    │────────▶│      (Port 8081)        │
│                     │ Publish │    Broker    │ Consume │                         │
│  - Transferências   │         │              │         │  - Envio de Emails      │
│  - Autenticação     │         │   Queue:     │         │  - Envio de SMS         │
│  - Gestão Carteiras │         │  transaction-│         │  - Push Notifications   │
│  - PIX              │         │  notification│         │                         │
└─────────────────────┘         └──────────────┘         └─────────────────────────┘
         │                             │                            │
         ▼                             ▼                            ▼
   PostgreSQL                    Management UI              Logs/Email Service
   (Supabase)                   (Port 15672)               (Simulado via logs)
```

### Decisões Técnicas Chave

*   **Java 21**: Aproveitando Virtual Threads e sintaxe moderna.
*   **Segurança "Fort Knox"**:
    *   **JWT (Stateless)** autenticação + BCrypt.
    *   **Rate Limiting**: Bucket4j (Token Bucket) previne ataques de força bruta/DDoS.
    *   **Defesa em Profundidade**: PIN transacional obrigatório para transferências.
    *   **Privacidade de Dados**: Criptografia AES-256 para campos sensíveis (Email/CPF) em repouso.
*   **Controle de Concorrência**: Uso de `PESSIMISTIC_WRITE` locking em Carteiras para prevenir Race Conditions.
*   **Padrão JSend**: Respostas padronizadas da API (`success`, `fail`, `error`) para facilitar consumo e manutenção.
*   **Event-Driven Architecture**: RabbitMQ para desacoplamento de notificações.

---

## 📦 Módulos

### 1. Autenticação & Usuários (`/modulos/auth`, `/modulos/usuarios`)
*   **Funcionalidades**: Cadastro, Login, Geração de JWT, Perfil de Usuário, Definição de PIN Transacional.
*   **Segurança**: Criptografia de senha, Controle de acesso baseado em roles (MVP padrão: `ROLE_USER`).

### 2. Transações (`/modulos/transacoes`)
*   **Funcionalidades**: Transferências Peer-to-Peer (P2P) entre carteiras.
*   **Consistência**: Transações ACID com **Prevenção de Deadlock** (Ordenação de Recursos por ID).
*   **Precisão**: Uso estrito de `BigDecimal` para valores monetários.
*   **Event Publishing**: Publica eventos de transferência para RabbitMQ.

### 3. PIX (`/modulos/pix`)
*   **Funcionalidades**:
    *   **Chaves**: Registro de chaves únicas (CPF, EMAIL, PHONE, RANDOM).
    *   **Gerenciamento**: Listagem e Exclusão de Chaves do Usuário.
    *   **Validação**: Aplicação de regras (Máx. 5 chaves/usuário).

---

## 🚀 Arquitetura de Microsserviços (Event-Driven)

### 🎯 Benefícios

✅ **Desacoplamento** - Notificações separadas do core bancário  
✅ **Resiliência** - Transferências funcionam mesmo se notificações falharem  
✅ **Escalabilidade** - Múltiplas instâncias podem consumir a mesma fila  
✅ **Flexibilidade** - Fácil adicionar novos canais (SMS, Push, WhatsApp)

### 📦 Estrutura

```
JBankCore/
├── Back-end/                          # JBank Core (Monolito Modular)
│   ├── src/main/java/
│   │   ├── modulos/transacoes/
│   │   │   ├── events/TransferenciaEvent.java    # Evento publicado
│   │   │   └── service/TransferService.java      # Publica eventos
│   │   └── infra/messaging/
│   │       └── RabbitMQConfig.java               # Config RabbitMQ
│   └── pom.xml
│
├── jbank-notification/                # Microsserviço de Notificação
│   ├── src/main/java/
│   │   ├── listener/NotificationListener.java    # Consome eventos
│   │   ├── model/TransferenciaEvent.java
│   │   └── JBankNotificationApplication.java
│   └── pom.xml
│
└── docker-compose.yml                 # RabbitMQ + PostgreSQL
```

---

## 🚀 Começando

### ⚠️ Pré-requisitos

1. **Docker Desktop** - [Download](https://www.docker.com/products/docker-desktop/)
   - Abra e aguarde até estar completamente iniciado
   - Verifique: `docker --version`

2. **Java 21 JDK**
   - Verifique: `java -version`

3. **Maven** (Opcional - projeto tem wrapper)
   - Verifique: `mvn -version`

### 🎯 Opção 1: Execução Rápida (Apenas JBank Core)

Use o script PowerShell para iniciar apenas o core bancário:

```powershell
cd Back-end
.\scripts\dev\start-dev.ps1
```

*Verifica Java 21 → Compila → Inicia App → Abre Swagger UI*

### 🎯 Opção 2: Arquitetura Completa (Core + Microsserviços)

#### Passo 1: Iniciar Infraestrutura (RabbitMQ + PostgreSQL)

```powershell
# Entre na pasta Back-end
cd Back-end
docker-compose up -d
```

**Verificar:**
```powershell
docker ps
# Deve mostrar: jbank-rabbitmq e jbank-postgres
```

**Acessar RabbitMQ Management UI:**
- URL: http://localhost:15672
- Usuário: `guest` / Senha: `guest`

#### Passo 2: Iniciar JBank Core

**Terminal 1:**
```powershell
cd Back-end
.\scripts\dev\start-dev.ps1
```

Ou manualmente:
```powershell
cd Back-end
.\mvnw.cmd clean install -DskipTests
.\mvnw.cmd spring-boot:run
```

Aguarde ver: `Started JBankCoreApplication`

#### Passo 3: Iniciar Notification Service

**Terminal 2:**
```powershell
cd Back-end
.\scripts\notification\start-notification-service.ps1
```

Ou manualmente:
```powershell
cd Back-end\jbank-notification
..\mvnw.cmd clean install -DskipTests
..\mvnw.cmd spring-boot:run
```

Aguarde ver o banner:
```
╔═══════════════════════════════════════════════════════════╗
║   🔔 JBank Notification Service Started Successfully! 🔔  ║
╚═══════════════════════════════════════════════════════════╝
```

#### Passo 4: Verificar Tudo

**Terminal 3:**
```powershell
cd Back-end
.\scripts\infra\verify-microservices.ps1
```

Deve mostrar:
```
✅ RabbitMQ está rodando!
✅ JBank Core está rodando!
✅ Notification Service está rodando!
```

---

## 🧪 Testando a Arquitetura Event-Driven

### 1. Fazer uma Transferência

```http
POST http://localhost:8080/api/v1/transfers
Authorization: Bearer {seu_token_jwt}
Content-Type: application/json

{
  "senderId": "uuid-do-remetente",
  "receiverId": "uuid-do-destinatario",
  "amount": 100.00,
  "pin": "1234"
}
```

### 2. Observar os Logs

**Terminal 1 (JBank Core):**
```
Transfer completed successfully. Transaction ID: abc-123
📨 Event published for transaction [abc-123] to notification queue
```

**Terminal 2 (Notification Service):**
```
========================================
📨 NEW NOTIFICATION EVENT RECEIVED
========================================
Transaction ID: abc-123
Amount: R$ 100,00
----------------------------------------
📧 Email sent to SENDER: sender@example.com
📧 Email sent to RECEIVER: receiver@example.com
✅ Notifications sent successfully!
========================================
```

### 3. Verificar RabbitMQ UI

1. Acesse http://localhost:15672
2. Vá em **Queues** → `transaction-notification-queue`
3. Veja estatísticas de mensagens processadas

---

## 📚 Documentação da API

Uma vez em execução, acesse a **Swagger UI** para explorar os endpoints:
👉 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

### Endpoints Principais
*   `POST /auth/signup` - Registrar novo usuário
*   `POST /auth/login` - Obter Token JWT
*   `POST /api/v1/users/pin` - Definir PIN transacional
*   `POST /api/v1/transfers` - Enviar dinheiro
*   `POST /pix/keys` - Registrar Chave Pix
*   `GET /pix/keys` - Listar Chaves Pix do usuário

### Formato de Resposta JSend

Todas as respostas da API seguem o padrão JSend para consistência:

**Sucesso (2xx):**
```json
{
  "status": "success",
  "data": { /* payload */ }
}
```

**Falha de Validação (4xx):**
```json
{
  "status": "fail",
  "data": "Mensagem de erro ou objeto com detalhes"
}
```

**Erro do Sistema (5xx):**
```json
{
  "status": "error",
  "message": "Descrição do erro",
  "code": "OPTIONAL_ERROR_CODE"
}
```

---

## 🛠️ Estrutura do Projeto

```
JBankCore/
├── .gitignore                   # Configuração Git
├── README.md                    # Esta documentação
│
└── Back-end/                    # Todo o código do projeto
    ├── scripts/                 # Scripts organizados por categoria
    │   ├── dev/                 # Scripts de desenvolvimento
    │   │   ├── start-dev.ps1
    │   │   ├── cleanup-workspace.ps1
    │   │   └── verify-build.ps1
    │   ├── infra/               # Scripts de infraestrutura
    │   │   ├── bootstrap.ps1
    │   │   └── verify-microservices.ps1
    │   └── notification/        # Scripts do microsserviço
    │       └── start-notification-service.ps1
    │
    ├── src/main/java/br/com/jbank/core/
    │   ├── modulos/             # Módulos de Domínio (DDD)
    │   │   ├── auth/            # Autenticação
    │   │   ├── usuarios/        # Gestão de Usuários
    │   │   ├── transacoes/      # Transferências + Events
    │   │   │   ├── events/      # TransferenciaEvent
    │   │   │   └── service/     # TransferService (Publisher)
    │   │   ├── carteiras/       # Carteiras/Wallets
    │   │   └── pix/             # Sistema Pix
    │   └── infra/               # Infraestrutura
    │       ├── defense/         # Segurança (JWT, Rate Limit, Crypto)
    │       ├── messaging/       # RabbitMQ Config
    │       ├── response/        # Padrão JSend
    │       └── exception/       # Tratamento Global de Erros
    │
    ├── jbank-notification/      # Microsserviço de Notificação
    │   ├── src/main/java/br/com/jbank/notification/
    │   │   ├── listener/        # NotificationListener (Consumer)
    │   │   ├── model/           # TransferenciaEvent
    │   │   ├── config/          # RabbitMQ Config
    │   │   └── JBankNotificationApplication.java
    │   └── pom.xml
    │
    ├── DevOps-defense/          # Documentação de Segurança
    │   ├── SECURITY.md
    │   └── THREAT_MODEL.md
    │
    ├── docker-compose.yml       # RabbitMQ + PostgreSQL
    ├── pom.xml
    └── mvnw.cmd                 # Maven Wrapper
```

---

## 🔒 Segurança

### Camadas de Proteção "Fort Knox"

1. **Autenticação JWT Stateless**: Tokens assinados com HS256, validados em cada requisição.
2. **Rate Limiting**: Proteção contra ataques de força bruta e DDoS usando Bucket4j.
3. **PIN Transacional**: Camada adicional de segurança para operações financeiras sensíveis.
4. **Criptografia de Dados**: AES-256 para PII (CPF, Email) em repouso no banco de dados.
5. **Locking Pessimista**: Previne race conditions em operações de saldo de carteira.
6. **Tratamento Global de Exceções**: Respostas de erro padronizadas sem exposição de detalhes internos.

---

## 🧪 Testes

```bash
# Executar todos os testes
.\mvnw.cmd test

# Executar apenas testes de integração
.\mvnw.cmd verify -P integration-tests
```

---

## 🔧 Troubleshooting

### Erro: "mvn não é reconhecido"
**Solução**: Use o Maven Wrapper incluído no projeto:
```powershell
.\mvnw.cmd clean install
```

### Erro: "Docker não está rodando"
**Solução**: Abra o Docker Desktop e aguarde inicializar completamente.

### Erro: "Port 8080 already in use"
**Solução**: Pare outros serviços na porta 8080 ou mude a porta no `application.yml`.

### Notification Service não recebe eventos
**Solução**:
1. Verifique a fila no RabbitMQ UI: http://localhost:15672
2. Vá em "Queues" → "transaction-notification-queue"
3. Veja se há consumidores conectados

---

## 🎓 Conceitos Aprendidos

✅ **Event-Driven Architecture** - Comunicação assíncrona entre serviços  
✅ **RabbitMQ** - Message broker para desacoplamento  
✅ **Producer/Consumer Pattern** - Publicação e consumo de eventos  
✅ **Microservices** - Serviços independentes e escaláveis  
✅ **Resilience** - Falhas de notificação não quebram transferências  
✅ **Clean Architecture** - Separação de responsabilidades  
✅ **ACID Transactions** - Garantia de consistência de dados  

---

## 🔮 Próximos Passos

### Nível Intermediário
- [ ] Adicionar Dead Letter Queue (DLQ) para mensagens com falha
- [ ] Implementar Circuit Breaker com Resilience4j
- [ ] Adicionar métricas com Micrometer + Prometheus

### Nível Avançado
- [ ] Criar Anti-Fraud Service (análise de transações suspeitas em Python)
- [ ] Criar Extrato Service com CQRS (MongoDB para leitura)
- [ ] Criar BACEN Simulator (simula instabilidade de APIs externas)
- [ ] Implementar Saga Pattern para transações distribuídas

---

## 📚 Referências

- [Spring AMQP Documentation](https://spring.io/projects/spring-amqp)
- [RabbitMQ Tutorials](https://www.rabbitmq.com/getstarted.html)
- [Microservices Patterns - Chris Richardson](https://microservices.io/patterns/index.html)
- [Event-Driven Architecture - Martin Fowler](https://martinfowler.com/articles/201701-event-driven.html)

---

## 🤝 Contribuindo

Contribuições são bem-vindas! Por favor, siga os padrões de código estabelecidos:
*   Use `BigDecimal` para valores monetários.
*   Implemente locking apropriado para operações concorrentes.
*   Mantenha a separação de responsabilidades (Clean Architecture).
*   Escreva testes para novas funcionalidades.

---

## 📝 Licença

Este projeto é de código aberto e está disponível sob a licença MIT.

---

*Desenvolvido com ❤️ usando Java 21, Spring Boot 3.4 e RabbitMQ*  
**Por Pamela Menezes** 🚀
