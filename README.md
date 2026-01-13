# 🏦 JBank Core API

![Java 21](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)
![Spring Boot 3.4](https://img.shields.io/badge/Spring_Boot-3.4+-green?style=for-the-badge&logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=for-the-badge&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue?style=for-the-badge&logo=docker)
![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen?style=for-the-badge)

> **Uma API bancária robusta focada em integridade de dados, concorrência e arquitetura escalável.**

---

## 📖 Sobre o Projeto

O **JBank Core** não é apenas mais um CRUD simples. Este projeto simula o núcleo ("core banking") de um Provedor de Serviços de Pagamento (PSP), projetado para lidar com transações financeiras críticas onde a consistência dos dados é inegociável.

O objetivo principal aqui foi resolver problemas reais do mundo financeiro, como **Race Conditions** (Condições de Corrida) em ambientes distribuídos e garantir a precisão decimal absoluta em operações monetárias.

A arquitetura segue o estilo **Modular Monolith** (inspirado no padrão NestJS), organizando o código por domínios funcionais (`modulos/usuarios`, `modulos/transacoes`) ao invés de camadas técnicas puras, facilitando a manutenção e futura extração para microsserviços.

---

## 🚀 Destaques Técnicos (Por que este código é diferente?)

### 1. 🛡️ Concorrência e Thread Safety (Pessimistic Locking)
Em sistemas bancários, o maior pesadelo é o "Double Spending" (Gasto Duplo). Se duas requisições chegarem ao mesmo milissegundo para debitar uma conta, um sistema ingênuo permitiria ambas.

Neste projeto, implementamos **Pessimistic Locking (`PESSIMISTIC_WRITE`)** direto no banco de dados (PostgreSQL).
*   **O que isso faz:** Garante consistência ACID "travando" a linha da carteira durante a transação.
*   **Resultado:** Impede matematicamente que uma conta sofra débitos simultâneos que excedam o saldo, prevenindo o clássico problema de *Race Condition*.

### 2. 💰 Integridade Financeira Absoluta
Esqueça o `double` ou `float`. Computadores têm dificuldade em representar decimais binários (padrão IEEE 754), o que gera erros de arredondamento bizarros (ex: `0.1 + 0.2 = 0.30000000000000004`).
*   **Solução Sênior:** Todo o tratamento monetário utiliza **`BigDecimal`** com precisão controlada. Isso garante que cada centavo seja rastreado e calculado com exatidão contábil.

### 3. 🏗️ Arquitetura Modular (Domain-Driven)
Ao invés de espalhar logica em `services` genéricos, o projeto é organizado em módulos funcionais:
*   `src/main/java/br/com/pamela/jbank/modulos/transacoes`
*   `src/main/java/br/com/pamela/jbank/modulos/usuarios`

Isso mostra que o projeto foi pensado para um contexto de negócio, facilitando o onboarding de novos desenvolvedores e a escalabilidade do time.

### 4. 🧪 Qualidade e Testes
*   **Testcontainers:** Testes de integração que sobem um banco PostgreSQL real em container Docker, garantindo que o SQL e os Locks funcionem na prática, não apenas no mock.
*   **RFC 7807:** Padronização de erros da API para facilitar o consumo por front-ends e parceiros.

---

## 🛠️ Stack Tecnológica

*   **Linguagem:** Java 21 LTS
*   **Framework:** Spring Boot 3.4+ (Web, Data JPA, Validation)
*   **Banco de Dados:** PostgreSQL (Compatível com Supabase)
*   **Migrations:** Flyway
*   **Testes:** JUnit 5, Mockito, Testcontainers
*   **Ferramentas:** Docker, Lombok, Maven

---

## 🏃‍♂️ Como Rodar

### Pré-requisitos
*   Java 21 instalado
*   Docker (opcional, para testes e banco local)

### Executando a Aplicação
Se você tiver o Docker instalado, pode rodar o banco rapidamente:

```bash
docker run --name jbank-postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres
```

Em seguida, execute a aplicação via Maven:

```bash
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

### Rodando os Testes
Para garantir que a lógica de concorrência e integridade está funcionando:

```bash
./mvnw clean verify
```

---

## 🗺️ Próximos Passos (Roadmap)

- [ ] Implementar Autenticação Stateless com **JWT (OAuth2 Resource Server)**.
- [ ] Criar pipeline de **CI/CD** com GitHub Actions.
- [ ] Deploy automático na nuvem (AWS ou Render).
- [ ] Adicionar **Webhooks** para notificação de transações.

---

## 📬 Autor

Desenvolvido com 💜 por **Pamela**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/pamela-menezes/) 
[![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/pamela-menezes)
