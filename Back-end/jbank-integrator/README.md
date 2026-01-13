# 🌐 JBank Integrator - External Integrations Gateway

> **Microsserviço Gateway** para integrações externas usando **Spring Cloud OpenFeign**

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![OpenFeign](https://img.shields.io/badge/OpenFeign-Enabled-blue?style=for-the-badge)
![Java 21](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)

---

## 🎯 Objetivo

Gateway centralizado para consumir APIs externas de forma **declarativa** usando **OpenFeign**, eliminando código boilerplate e facilitando manutenção.

---

## 🏗️ Arquitetura - Gateway Pattern

```
┌─────────────────────┐
│   JBank Core API    │
└──────────┬──────────┘
           │ HTTP
           ▼
┌─────────────────────┐         ┌──────────────────┐
│  JBank Integrator   │────────▶│   Brasil API     │
│     (Port 8083)     │  Feign  │  (brasilapi.com) │
│                     │         └──────────────────┘
│  - OpenFeign        │
│  - Gateway Pattern  │         ┌──────────────────┐
│                     │────────▶│  Open Finance    │
└─────────────────────┘  OAuth2 │  (Simulado)      │
                                └──────────────────┘
```

---

## 📊 Integrações

### 1. Brasil API (✅ REAL)

**Endpoint**: `GET /integrations/banks`

**Integração Real**: https://brasilapi.com.br/api/banks/v1

**Response**:
```json
[
  {
    "ispb": "00000000",
    "name": "BCO DO BRASIL S.A.",
    "code": 1,
    "fullName": "Banco do Brasil S.A."
  },
  {
    "ispb": "00360305",
    "name": "CAIXA ECONOMICA FEDERAL",
    "code": 104,
    "fullName": "Caixa Econômica Federal"
  }
]
```

### 2. Open Finance (🏗️ SIMULADO)

**Endpoint**: `GET /integrations/open-finance/balance/{consentId}`

**Response (Mock)**:
```json
{
  "balance": 15750.50,
  "currency": "BRL",
  "consentId": "consent-123",
  "accountHolder": "Pamela Menezes"
}
```

---

## 🚀 Como Executar

### Opção 1: Modo Desenvolvimento

```bash
cd jbank-integrator
../mvnw.cmd spring-boot:run
```

### Opção 2: Build e Run

```bash
# Build
../mvnw.cmd clean package

# Run
java -jar target/jbank-integrator-1.0.0.jar
```

Acesse:
- API: http://localhost:8083/integrations
- Health: http://localhost:8083/integrations/health

---

## 🧪 Testando

### Teste 1: Buscar Todos os Bancos (REAL)

```bash
curl http://localhost:8083/integrations/banks
```

**Resultado**: Lista com ~200 bancos brasileiros!

### Teste 2: Buscar Banco Específico

```bash
# Banco do Brasil (código 001)
curl http://localhost:8083/integrations/banks/1

# Nubank (código 260)
curl http://localhost:8083/integrations/banks/260
```

### Teste 3: Open Finance (Mock)

```bash
curl http://localhost:8083/integrations/open-finance/balance/consent-123
```

---

## 💡 OpenFeign vs RestTemplate

### RestTemplate (Júnior - Verboso)

```java
// 20+ linhas de código boilerplate
RestTemplate restTemplate = new RestTemplate();
HttpHeaders headers = new HttpHeaders();
headers.setContentType(MediaType.APPLICATION_JSON);
HttpEntity<String> entity = new HttpEntity<>(headers);

ResponseEntity<List<BankDTO>> response = restTemplate.exchange(
    "https://brasilapi.com.br/api/banks/v1",
    HttpMethod.GET,
    entity,
    new ParameterizedTypeReference<List<BankDTO>>() {}
);

List<BankDTO> banks = response.getBody();
```

### OpenFeign (Sênior - Declarativo)

```java
// 1 linha!
List<BankDTO> banks = brasilApiClient.getAllBanks();
```

---

## 🛠️ Estrutura do Projeto

```
jbank-integrator/
├── src/main/java/br/com/jbank/integrator/
│   ├── JBankIntegratorApplication.java    # @EnableFeignClients
│   │
│   ├── client/                            # Feign Clients
│   │   ├── BrasilApiClient.java          # Brasil API (Real)
│   │   └── OpenFinanceClient.java        # Open Finance (Mock)
│   │
│   ├── dto/                               # DTOs
│   │   ├── BankDTO.java
│   │   └── AccountBalanceDTO.java
│   │
│   ├── service/                           # Business Logic
│   │   ├── BankSearchService.java
│   │   └── OpenFinanceService.java
│   │
│   └── controller/                        # REST Endpoints
│       └── IntegratorController.java
│
├── src/main/resources/
│   └── application.yml                    # Port 8083
│
└── pom.xml                                # Spring Cloud OpenFeign
```

---

## 🎓 Conceitos Aprendidos

1. **OpenFeign**: Consumo declarativo de APIs
2. **Gateway Pattern**: Centralização de integrações
3. **Circuit Breaker**: Resiliência (futuro)
4. **Open Finance**: Arquitetura para dados bancários
5. **OAuth2**: Preparação para autenticação real

---

## 🔮 Próximos Passos

### Melhorias Imediatas
- [ ] Adicionar Resilience4j (Circuit Breaker)
- [ ] Cache com Redis para bancos
- [ ] Retry automático com Feign
- [ ] Métricas com Micrometer

### Integrações Futuras
- [ ] **Belvo API**: Agregação de contas reais
- [ ] **Banco do Brasil Open Finance**: Dados reais
- [ ] **BACEN API**: Taxas SELIC, câmbio
- [ ] **ViaCEP**: Validação de endereços

---

## 📚 Tecnologias

- **Spring Boot 3.4.1**
- **Spring Cloud OpenFeign**
- **Java 21**
- **Lombok**

---

**Desenvolvido por Pamela Menezes** 🚀  
**Parte do Ecossistema JBank**
