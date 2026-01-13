# PowerShell script para testar a arquitetura Event-Driven do JBank
# Autor: Pamela Menezes
# Data: 2026-01-13

Write-Host @"
╔═══════════════════════════════════════════════════════════╗
║                                                           ║
║        JBank - Event-Driven Architecture Test             ║
║                                                           ║
╚═══════════════════════════════════════════════════════════╝
"@ -ForegroundColor Cyan

Write-Host "`n📋 Este script vai verificar se todos os componentes estão rodando:`n" -ForegroundColor Yellow

# 1. Verificar RabbitMQ
Write-Host "1️⃣  Verificando RabbitMQ..." -ForegroundColor Cyan
try {
    $response = Invoke-WebRequest -Uri "http://localhost:15672" -UseBasicParsing -TimeoutSec 5 -ErrorAction Stop
    Write-Host "   ✅ RabbitMQ está rodando!" -ForegroundColor Green
    Write-Host "   📊 Management UI: http://localhost:15672 (guest/guest)" -ForegroundColor Gray
}
catch {
    Write-Host "   ❌ RabbitMQ não está acessível" -ForegroundColor Red
    Write-Host "   💡 Execute: docker-compose up -d" -ForegroundColor Yellow
    exit 1
}

# 2. Verificar JBank Core
Write-Host "`n2️⃣  Verificando JBank Core..." -ForegroundColor Cyan
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -UseBasicParsing -TimeoutSec 5 -ErrorAction SilentlyContinue
    if ($response.StatusCode -eq 200) {
        Write-Host "   ✅ JBank Core está rodando!" -ForegroundColor Green
        Write-Host "   🌐 API: http://localhost:8080" -ForegroundColor Gray
    }
    else {
        throw "Status code não é 200"
    }
}
catch {
    Write-Host "   ❌ JBank Core não está acessível" -ForegroundColor Red
    Write-Host "   💡 Execute: cd Back-end && mvn spring-boot:run" -ForegroundColor Yellow
    exit 1
}

# 3. Verificar Notification Service
Write-Host "`n3️⃣  Verificando Notification Service..." -ForegroundColor Cyan
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8081/actuator/health" -UseBasicParsing -TimeoutSec 5 -ErrorAction SilentlyContinue
    if ($response.StatusCode -eq 200) {
        Write-Host "   ✅ Notification Service está rodando!" -ForegroundColor Green
        Write-Host "   🔔 Service: http://localhost:8081" -ForegroundColor Gray
    }
    else {
        throw "Status code não é 200"
    }
}
catch {
    Write-Host "   ❌ Notification Service não está acessível" -ForegroundColor Red
    Write-Host "   💡 Execute: cd jbank-notification && mvn spring-boot:run" -ForegroundColor Yellow
    exit 1
}

# 4. Verificar fila no RabbitMQ
Write-Host "`n4️⃣  Verificando fila RabbitMQ..." -ForegroundColor Cyan
try {
    $credentials = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("guest:guest"))
    $headers = @{
        Authorization = "Basic $credentials"
    }
    $queueInfo = Invoke-RestMethod -Uri "http://localhost:15672/api/queues/%2F/transaction-notification-queue" -Headers $headers -Method Get
    Write-Host "   ✅ Fila 'transaction-notification-queue' existe!" -ForegroundColor Green
    Write-Host "   📊 Mensagens na fila: $($queueInfo.messages)" -ForegroundColor Gray
    Write-Host "   📊 Consumidores ativos: $($queueInfo.consumers)" -ForegroundColor Gray
}
catch {
    Write-Host "   ⚠️  Fila ainda não foi criada (será criada automaticamente)" -ForegroundColor Yellow
}

Write-Host @"

╔═══════════════════════════════════════════════════════════╗
║                                                           ║
║              ✅ TODOS OS SERVIÇOS ESTÃO ONLINE!            ║
║                                                           ║
╚═══════════════════════════════════════════════════════════╝

📝 Próximos Passos:

1. Faça uma transferência via API do JBank Core
2. Observe os logs do Notification Service
3. Verifique a fila no RabbitMQ Management UI

🔗 Links Úteis:
   - JBank Core API: http://localhost:8080
   - Notification Service: http://localhost:8081
   - RabbitMQ Management: http://localhost:15672

"@ -ForegroundColor Green
