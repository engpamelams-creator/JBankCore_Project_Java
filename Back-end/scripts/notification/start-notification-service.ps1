# PowerShell script para iniciar o JBank Notification Service
# Autor: Pamela Menezes
# Data: 2026-01-13

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  JBank Notification Service - Startup  " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Caminho para o Maven Wrapper do projeto principal
$mvnWrapper = "..\Back-end\mvnw.cmd"

# Verifica se o Maven Wrapper existe
Write-Host "Verificando Maven Wrapper..." -ForegroundColor Yellow
if (Test-Path $mvnWrapper) {
    Write-Host "✓ Maven Wrapper encontrado" -ForegroundColor Green
}
else {
    Write-Host "✗ Maven Wrapper não encontrado em $mvnWrapper" -ForegroundColor Red
    Write-Host "  Execute este script a partir do diretório jbank-notification" -ForegroundColor Yellow
    exit 1
}

# Verifica se o RabbitMQ está rodando
Write-Host ""
Write-Host "Verificando RabbitMQ..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:15672" -UseBasicParsing -TimeoutSec 5 -ErrorAction Stop
    Write-Host "✓ RabbitMQ está rodando (Management UI acessível)" -ForegroundColor Green
}
catch {
    Write-Host "✗ RabbitMQ não está acessível. Inicie com: docker-compose up -d" -ForegroundColor Red
    Write-Host "  Aguardando 10 segundos antes de continuar..." -ForegroundColor Yellow
    Start-Sleep -Seconds 10
}

Write-Host ""
Write-Host "Iniciando JBank Notification Service..." -ForegroundColor Cyan
Write-Host ""

# Executa o Maven Wrapper
& $mvnWrapper spring-boot:run
