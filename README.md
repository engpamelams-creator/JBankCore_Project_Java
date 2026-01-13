# 🏦 JBank Core API

> **Backend Fintech de Nível Empresarial com Arquitetura Modular Monolítica** construído com Java 21, Spring Boot 3.4 e princípios de Clean Architecture.

![Java 21](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot 3.4](https://img.shields.io/badge/Spring_Boot-3.4-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED?style=for-the-badge&logo=docker&logoColor=white)

---

## 🏗️ Arquitetura & Design

Este projeto adota uma arquitetura **Modular Monolítica**, agrupando código por **Domínio/Funcionalidade** (`modulos/*`) em vez de camadas técnicas. Esta abordagem garante alta coesão e baixo acoplamento, pavimentando o caminho para uma eventual extração de microserviços, se necessário.

### Decisões Técnicas Chave

*   **Java 21**: Aproveitando Virtual Threads e sintaxe moderna.
*   **Segurança "Fort Knox"**:
    *   **JWT (Stateless)** autenticação + BCrypt.
    *   **Rate Limiting**: Bucket4j (Token Bucket) previne ataques de força bruta/DDoS.
    *   **Defesa em Profundidade**: PIN transacional obrigatório para transferências.
    *   **Privacidade de Dados**: Criptografia AES-256 para campos sensíveis (Email/CPF) em repouso.
*   **Controle de Concorrência**: Uso de `PESSIMISTIC_WRITE` locking em Carteiras para prevenir Race Conditions.
*   **Padrão JSend**: Respostas padronizadas da API (`success`, `fail`, `error`) para facilitar consumo e manutenção.
*   **Injeção de Dependência Explícita**: Uso de `@Autowired` em construtores para maior previsibilidade e validação.

---

## 📦 Módulos

### 1. Autenticação & Usuários (`/modulos/auth`, `/modulos/usuarios`)
*   **Funcionalidades**: Cadastro, Login, Geração de JWT, Perfil de Usuário, Definição de PIN Transacional.
*   **Segurança**: Criptografia de senha, Controle de acesso baseado em roles (MVP padrão: `ROLE_USER`).

### 2. Transações (`/modulos/transacoes`)
*   **Funcionalidades**: Transferências Peer-to-Peer (P2P) entre carteiras.
*   **Consistência**: Transações ACID com **Prevenção de Deadlock** (Ordenação de Recursos por ID).
*   **Precisão**: Uso estrito de `BigDecimal` para valores monetários.

### 3. Pix (`/modulos/pix`)
*   **Funcionalidades**:
    *   **Chaves**: Registro de chaves únicas (CPF, EMAIL, PHONE, RANDOM).
    *   **Gerenciamento**: Listagem e Exclusão de Chaves do Usuário.
    *   **Validação**: Aplicação de regras (Máx. 5 chaves/usuário).

---

## 🚀 Começando

### Pré-requisitos
*   **Java 21 JDK** (Script detecta automaticamente ou ajuda você a encontrá-lo)
*   **Maven** (Wrapper incluído)
*   **Docker** (Opcional, para testes de integração)

### Execução com Um Clique (PowerShell)
Utilizamos um script de experiência do desenvolvedor para automatizar o processo de build e execução.

```powershell
./start-dev.ps1
```
*Verifica Java 21 → Compila (Pulando Testes para Velocidade) → Inicia App → Abre Swagger UI*

### Execução Manual
```bash
mvn clean install
java -jar target/jbank-core-0.0.1-SNAPSHOT.jar
```

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
JBankCore_Project_Java/
├── Back-end/                    # Spring Boot API
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/br/com/jbank/core/
│   │   │   │   ├── modulos/     # Módulos de Domínio
│   │   │   │   │   ├── auth/    # Autenticação
│   │   │   │   │   ├── usuarios/ # Gestão de Usuários
│   │   │   │   │   ├── transacoes/ # Transferências
│   │   │   │   │   └── pix/     # Sistema Pix
│   │   │   │   └── infra/       # Infraestrutura
│   │   │   │       ├── defense/ # Segurança (JWT, Rate Limit, Crypto)
│   │   │   │       ├── response/ # Padrão JSend
│   │   │   │       └── exception/ # Tratamento Global de Erros
│   │   │   └── resources/
│   │   └── test/
│   ├── pom.xml
│   └── start-dev.ps1
└── README.md                    # Esta documentação
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
mvn test

# Executar apenas testes de integração
mvn verify -P integration-tests
```

---

## 📈 Melhorias Recentes

### ✅ Refatoração de Injeção de Dependência
*   Substituição de `@RequiredArgsConstructor` por construtores explícitos com `@Autowired` em todos os Services e Controllers.
*   **Benefício**: Maior previsibilidade e validação de injeção de dependência pelo Spring Framework.

### ✅ Implementação do Padrão JSend
*   Criação de classes de resposta padronizadas: `JSendSuccessResponse`, `JSendFailResponse`, `JSendErrorResponse`.
*   **Benefício**: API mais consistente e fácil de consumir, seguindo especificação JSend.

### ✅ Tratamento Global de Exceções
*   `GlobalExceptionHandler` centraliza o tratamento de erros e retorna respostas JSend apropriadas.
*   **Benefício**: Respostas de erro consistentes em toda a API, sem exposição de stack traces.

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

*Desenvolvido com ❤️ usando Java 21 e Spring Boot 3.4*
*Verificado pelo "The Exterminator" QA Code Review.*
