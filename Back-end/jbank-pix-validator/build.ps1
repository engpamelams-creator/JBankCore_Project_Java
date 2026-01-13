# PowerShell script para compilar o JBank PIX Validator (Quarkus)
# Autor: Pamela Menezes

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  JBank PIX Validator - Build Script   " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$mvnWrapper = "..\mvnw.cmd"

# Verifica se o Maven Wrapper existe
if (Test-Path $mvnWrapper) {
    Write-Host "✓ Maven Wrapper encontrado" -ForegroundColor Green
}
else {
    Write-Host "✗ Maven Wrapper não encontrado" -ForegroundColor Red
    Write-Host "  Certifique-se de estar na pasta jbank-pix-validator" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "Compilando projeto Quarkus..." -ForegroundColor Cyan
Write-Host ""

# Executa o build
& $mvnWrapper clean package -DskipTests

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "  ✅ Build concluído com sucesso!      " -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Para executar:" -ForegroundColor Yellow
    Write-Host "  java -jar target\quarkus-app\quarkus-run.jar" -ForegroundColor White
    Write-Host ""
    Write-Host "Ou use o modo dev:" -ForegroundColor Yellow
    Write-Host "  ..\mvnw.cmd quarkus:dev" -ForegroundColor White
}
else {
    Write-Host ""
    Write-Host "❌ Build falhou!" -ForegroundColor Red
    exit 1
}
