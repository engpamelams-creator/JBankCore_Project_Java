# 🛡️ JBank Defense Protocol (DevOps-defense)

Este documento descreve as camadas de segurança implementadas no JBank Core API.

## 1. Autenticação & Autorização
- **Padrão:** JWT (JSON Web Tokens).
- **Algoritmo:** HMAC256.
- **Política:** Stateless (Sem sessão no servidor).

## 2. Proteção Ativa (Firewall de Aplicação)
- **Rate Limiting:** Implementado via **Bucket4j**.
  - *Limite Público:* 10 req/min (Login).
  - *Limite Autenticado:* 100 req/min.
- **Objetivo:** Mitigação de ataques de Força Bruta e DDoS na camada de aplicação.

## 3. Proteção de Dados (Data at Rest)
- **Criptografia:** AES-256 via JPA Attribute Converters.
- **Campos Protegidos:** CPF, Email e Senha Transacional.
- **Conformidade:** Adequado à LGPD (Lei Geral de Proteção de Dados).

## 4. Auditoria de Dependências
- **Ferramenta:** OWASP Dependency Check.
- **Pipeline:** Varredura automática no build do Maven para detectar CVEs (Common Vulnerabilities and Exposures).

---
**Responsável pela Segurança:** Pamela (Engenheira de Software)
