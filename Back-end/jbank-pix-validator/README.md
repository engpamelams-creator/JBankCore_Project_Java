# 🚀 JBank PIX Validator - Quarkus Microservice

> **Microsserviço de alta performance** para validação de chaves PIX usando **Quarkus** (Supersônico & Subatômico)

![Quarkus](https://img.shields.io/badge/Quarkus-3.6.4-4695EB?style=for-the-badge&logo=quarkus&logoColor=white)
![Java 21](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)

---

## ⚡ Por Que Quarkus?

| Característica | Spring Boot | Quarkus |
|---|---|---|
| **Startup Time** | ~2-3 segundos | **0.05 segundos** |
| **Memory Usage** | ~250 MB | **40 MB** |
| **Throughput** | Alto | **Muito Alto** |

### Benefícios
- ⚡ **Supersônico**: Startup em milissegundos
- 🪶 **Subatômico**: Consumo mínimo de memória
- ☁️ **Cloud Native**: Otimizado para containers
- 💰 **Economia**: Menos recursos = menor custo na nuvem

---

## 🎯 Funcionalidades

### Endpoint Principal

**POST** `/api/pix/validate`

**Request:**
```json
{
  "key": "pamela@example.com",
  "type": "EMAIL"
}
```

**Response:**
```json
{
  "valid": true,
  "key": "pamela@example.com",
  "type": "EMAIL",
  "message": "Valid PIX key format"
}
```

### Tipos de Chave Suportados

1. **EMAIL**: Validação de formato de email
2. **CPF**: Validação de 11 dígitos
3. **PHONE**: Validação de telefone brasileiro (+55)
4. **RANDOM**: UUID/GUID (chave aleatória)

---

## 🚀 Como Executar

### Opção 1: Modo Desenvolvimento (Dev Mode)

```bash
cd jbank-pix-validator
../mvnw.cmd quarkus:dev
```

Acesse:
- API: http://localhost:8082/api/pix/validate
- Swagger UI: http://localhost:8082/swagger-ui
- Health: http://localhost:8082/q/health

### Opção 2: Build e Run (JVM)

```bash
# Build
../mvnw.cmd clean package

# Run
java -jar target/quarkus-app/quarkus-run.jar
```

### Opção 3: Docker

```bash
# Build da imagem
docker build -f Dockerfile.jvm -t jbank-pix-validator:1.0.0 .

# Run
docker run -p 8082:8082 jbank-pix-validator:1.0.0
```

---

## 🧪 Testando

### Teste 1: Email Válido
```bash
curl -X POST http://localhost:8082/api/pix/validate \
  -H "Content-Type: application/json" \
  -d '{"key":"pamela@example.com","type":"EMAIL"}'
```

### Teste 2: CPF Válido
```bash
curl -X POST http://localhost:8082/api/pix/validate \
  -H "Content-Type: application/json" \
  -d '{"key":"12345678901","type":"CPF"}'
```

### Teste 3: Telefone Válido
```bash
curl -X POST http://localhost:8082/api/pix/validate \
  -H "Content-Type: application/json" \
  -d '{"key":"+5511987654321","type":"PHONE"}'
```

### Teste 4: UUID Válido
```bash
curl -X POST http://localhost:8082/api/pix/validate \
  -H "Content-Type: application/json" \
  -d '{"key":"123e4567-e89b-12d3-a456-426614174000","type":"RANDOM"}'
```

---

## 📊 Performance

### Métricas Estimadas

| Métrica | Valor |
|---------|-------|
| Startup Time | ~0.8s (JVM) / 0.05s (Native) |
| Memory Usage | ~120 MB (JVM) / 40 MB (Native) |
| Validation Time | ~1ms |
| Throughput | ~15k req/s (JVM) / 20k req/s (Native) |

---

## 🛠️ Estrutura do Projeto

```
jbank-pix-validator/
├── src/
│   ├── main/
│   │   ├── java/br/com/jbank/pixvalidator/
│   │   │   ├── enums/
│   │   │   │   └── PixKeyType.java
│   │   │   ├── model/
│   │   │   │   ├── ValidationRequest.java
│   │   │   │   └── ValidationResponse.java
│   │   │   ├── service/
│   │   │   │   └── PixValidatorService.java
│   │   │   └── resource/
│   │   │       └── PixValidatorResource.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── pom.xml
├── Dockerfile.jvm
└── README.md
```

---

## 📚 Documentação da API

Acesse a documentação interativa (Swagger UI):

👉 **http://localhost:8082/swagger-ui**

---

## 🔮 Próximos Passos

### Melhorias Futuras
- [ ] Build nativo com GraalVM (startup em 0.05s!)
- [ ] Integração com JBank Core via HTTP
- [ ] Cache de validações (Redis)
- [ ] Métricas com Micrometer + Prometheus
- [ ] Testes de carga (JMeter/Gatling)

---

## 🎓 Tecnologias Utilizadas

- **Quarkus 3.6.4** - Framework Cloud Native
- **Java 21** - Linguagem
- **RESTEasy Reactive** - REST API
- **Jackson** - JSON Serialization
- **SmallRye OpenAPI** - Documentação automática
- **SmallRye Health** - Health checks

---

**Desenvolvido por Pamela Menezes** 🚀  
**Parte do Ecossistema JBank**
