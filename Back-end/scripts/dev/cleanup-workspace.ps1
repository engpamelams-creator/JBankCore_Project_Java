# Script para limpar completamente o workspace Java
Write-Host "=== LIMPEZA COMPLETA DO WORKSPACE JAVA ===" -ForegroundColor Cyan

# 1. Limpar target do Maven
Write-Host "`n[1/4] Limpando diretorio target..." -ForegroundColor Yellow
if (Test-Path "target") {
    Remove-Item -Path "target" -Recurse -Force
    Write-Host "  ✓ Target removido" -ForegroundColor Green
}
else {
    Write-Host "  ✓ Target nao existe" -ForegroundColor Green
}

# 2. Limpar cache do VS Code Java
Write-Host "`n[2/4] Limpando cache do VS Code..." -ForegroundColor Yellow
$vscodeJavaCache = "$env:USERPROFILE\.vscode\extensions"
if (Test-Path $vscodeJavaCache) {
    Get-ChildItem -Path $vscodeJavaCache -Filter "redhat.java-*" -Directory | ForEach-Object {
        $cachePath = Join-Path $_.FullName "jdt_ws"
        if (Test-Path $cachePath) {
            Remove-Item -Path $cachePath -Recurse -Force -ErrorAction SilentlyContinue
            Write-Host "  ✓ Cache Java removido" -ForegroundColor Green
        }
    }
}

# 3. Verificar estrutura correta
Write-Host "`n[3/4] Verificando estrutura de pacotes..." -ForegroundColor Yellow
$correctPath = "src\main\java\br\com\jbank\core"
if (Test-Path $correctPath) {
    $fileCount = (Get-ChildItem -Path $correctPath -Recurse -Filter "*.java").Count
    Write-Host "  ✓ Estrutura correta encontrada: $fileCount arquivos Java" -ForegroundColor Green
}
else {
    Write-Host "  ✗ ERRO: Estrutura correta nao encontrada!" -ForegroundColor Red
    exit 1
}

# 4. Verificar se existe estrutura antiga
Write-Host "`n[4/4] Verificando estrutura antiga..." -ForegroundColor Yellow
$oldPaths = Get-ChildItem -Path "src" -Recurse -Directory -Filter "pamela" -ErrorAction SilentlyContinue
if ($oldPaths) {
    Write-Host "  ✗ AVISO: Estrutura antiga ainda existe!" -ForegroundColor Red
    $oldPaths | ForEach-Object { Write-Host "    - $($_.FullName)" -ForegroundColor Red }
}
else {
    Write-Host "  ✓ Nenhuma estrutura antiga encontrada" -ForegroundColor Green
}

Write-Host "`n=== LIMPEZA CONCLUIDA ===" -ForegroundColor Cyan
Write-Host "`nPROXIMOS PASSOS:" -ForegroundColor Yellow
Write-Host "1. Feche TODOS os arquivos abertos no VS Code" -ForegroundColor White
Write-Host "2. Pressione Ctrl+Shift+P" -ForegroundColor White
Write-Host "3. Digite: 'Java: Clean Java Language Server Workspace'" -ForegroundColor White
Write-Host "4. Clique em 'Restart and delete'" -ForegroundColor White
Write-Host "5. Pressione Ctrl+Shift+P novamente" -ForegroundColor White
Write-Host "6. Digite: 'Developer: Reload Window'" -ForegroundColor White
