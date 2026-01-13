# 🕵️ Modelagem de Ameaças (Threat Model) - JBank

## 1. Superfície de Ataque
Identificação dos pontos de entrada vulneráveis da API.

| Ponto de Entrada | Risco Potencial | Mitigação Implementada | Status |
| :--- | :--- | :--- | :--- |
| **Login Endpoint** (`/auth`) | Brute Force / Credential Stuffing | **Rate Limiting (Bucket4j)** - Bloqueia IP após 10 tentativas. | ✅ Ativo |
| **Transferência** (`/transacoes`) | Session Hijacking (Roubo de Token) | **Senha Transacional** - Exige 2º fator (PIN) para debitar. | ✅ Ativo |
| **Banco de Dados** | Vazamento de Dados (Data Leak) | **Encryption at Rest (AES-256)** - CPF e Email ilegíveis sem chave. | ✅ Ativo |
| **Dependências** | Supply Chain Attack (Libs com vírus) | **OWASP Dependency Check** - Scan no Build. | ✅ Ativo |

## 2. Cenários de Ataque Simulados

### Cenário A: O "Hacker" de Força Bruta
- **Ação:** Script tenta 1000 senhas por minuto no usuário admin.
- **Defesa:** O Firewall de Aplicação detecta >10 req/min e bane o IP temporariamente (HTTP 429).

### Cenário B: O Funcionário Mal-intencionado
- **Ação:** Um DBA acessa o banco de dados diretamente para ler CPFs.
- **Defesa:** Os campos `cpf` e `email` aparecem como `Xy9#mKl...` (Criptografados).

---
*Documento mantido pela Equipe de Segurança (Pamela)*
