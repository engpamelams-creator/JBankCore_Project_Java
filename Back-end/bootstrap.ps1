# bootstrap.ps1
# Script to bootstrap the official Maven Wrapper (mvnw)
# This downloads a standalone Maven, runs 'mvn wrapper:wrapper', and cleans up.

Write-Host "🚀 Bootstrapping Maven Wrapper..." -ForegroundColor Cyan

$MvnVersion = "3.9.6"
$MvnUrl = "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$MvnVersion/apache-maven-$MvnVersion-bin.zip"
$TempDir = Join-Path $PSScriptRoot ".temp-maven"
$MvnZip = Join-Path $TempDir "maven.zip"

# Create temp dir
if (Test-Path $TempDir) { Remove-Item $TempDir -Recurse -Force }
New-Item -ItemType Directory -Path $TempDir | Out-Null

try {
    Write-Host "⬇️ Downloading Maven $MvnVersion..." -ForegroundColor Yellow
    # Force TLS 1.2+ for older PowerShells
    [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
    Invoke-WebRequest -Uri $MvnUrl -OutFile $MvnZip -UseBasicParsing

    Write-Host "📦 Extracting..." -ForegroundColor Yellow
    Expand-Archive -Path $MvnZip -DestinationPath $TempDir -Force

    $MvnBin = Join-Path $TempDir "apache-maven-$MvnVersion\bin\mvn.cmd"
    
    Write-Host "🛠️ Generating Maven Wrapper..." -ForegroundColor Green
    # Run the wrapper generation
    & $MvnBin wrapper:wrapper

    Write-Host "✅ Maven Wrapper generated successfully!" -ForegroundColor Green
    Write-Host "🧹 Cleaning up..." -ForegroundColor Yellow
}
catch {
    Write-Error "Failed to bootstrap Maven: $_"
}
finally {
    if (Test-Path $TempDir) { Remove-Item $TempDir -Recurse -Force }
}
