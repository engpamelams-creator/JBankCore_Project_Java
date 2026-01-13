#!/bin/bash
# Script de Segurança para evitar Commit de Senhas
# Autor: Pamela
# Uso: Rodar antes de enviar código para produção

echo "🛡️  Iniciando varredura de segurança no código..."

# Procura por palavras chave perigosas
GREP_RESULT=$(grep -rnE "password=|secret=|api_key=" ./src)

if [ ! -z "$GREP_RESULT" ]; then
    echo "❌ PERIGO: Encontrei possíveis senhas hardcoded no código:"
    echo "$GREP_RESULT"
    echo "Por favor, use Variáveis de Ambiente (\${ENV_VAR}) antes de commitar."
    exit 1
else
    echo "✅ Código Limpo. Nenhuma credencial exposta detectada."
    exit 0
fi
