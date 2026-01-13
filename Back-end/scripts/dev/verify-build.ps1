# Script to verify Maven compilation
Write-Host "🔍 Verificando compilação do projeto..." -ForegroundColor Cyan

# Set Maven path
$env:PATH = "$PWD\.maven-portable\bin;$env:PATH"

# Run Maven compile
Write-Host "`n📦 Executando Maven compile..." -ForegroundColor Yellow
& mvn clean compile -DskipTests

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n✅ Compilação bem-sucedida!" -ForegroundColor Green
} else {
    Write-Host "`n❌ Erro na compilação!" -ForegroundColor Red
    exit 1
}
