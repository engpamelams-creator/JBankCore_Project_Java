# start-dev.ps1
# JBank Core API - Developer Experience Script

$ErrorActionPreference = "Stop"
$AppUrl = "http://localhost:8080"
$SwaggerUrl = "$AppUrl/swagger-ui/index.html"

# --- 0. Logs Directory Setup ---
$LogsDir = Join-Path $PSScriptRoot "logs"
if (-not (Test-Path $LogsDir)) {
    New-Item -ItemType Directory -Path $LogsDir | Out-Null
    Write-Host "📂 Pasta 'logs' criada." -ForegroundColor Gray
}

Write-Host "Iniciando JBank Core API - Dev Mode" -ForegroundColor Cyan

# --- 1. Auto-detect Java 21 ---
$requiredJavaVersion = "21"
$javaVersion = cmd /c "java -version 2>&1"

# Logic for detection (retained from original)
if ($javaVersion -notmatch "version .?21") {
    Write-Host "⚠️  Java 21 padrão não detectado. Procurando instalação..." -ForegroundColor Yellow
    $potentialPaths = @(
        "C:\Program Files\Java\jdk-21*",
        "C:\Program Files\Common Files\Oracle\Java\javapath",
        "$env:LOCALAPPDATA\Programs\Common\Microsoft\OpenJDK\jdk-21*"
    )

    foreach ($pathPattern in $potentialPaths) {
        $found = Get-Item $pathPattern -ErrorAction SilentlyContinue | Sort-Object Name -Descending | Select-Object -First 1
        if ($found) {
            $jdkPath = $found.FullName
            if (Test-Path "$jdkPath\bin\java.exe") {
                Write-Host "✅ JDK 21 encontrado em: $jdkPath" -ForegroundColor Green
                $env:JAVA_HOME = $jdkPath
                $env:Path = "$jdkPath\bin;$env:Path"
                break
            }
        }
    }
}

# Verify again
if ($env:JAVA_HOME) {
    $javaExe = "$env:JAVA_HOME\bin\java.exe"
    $verifyVersion = cmd /c """$javaExe"" -version 2>&1"
    if ($verifyVersion -notmatch "version .?21") {
        Write-Warning "Java 21 foi configurado mas a verificação retornou: $verifyVersion"
    }
    else {
        Write-Host "✅ Ambiente configurado com sucesso para Java 21." -ForegroundColor Green
    }
}
else {
    $javaVersion = cmd /c "java -version 2>&1"
    if ($javaVersion -notmatch "version .?21") {
        Write-Error "❌ ERRO: Java 21 não encontrado."
        exit 1
    }
}

# --- 2. Maven Check / Provisioning ---
$MvnCmd = $null
if (Test-Path ".\mvnw.cmd") {
    $MvnCmd = ".\mvnw.cmd"
    Write-Host "Usando Maven Wrapper (mvnw.cmd)." -ForegroundColor Gray
}
elseif (Get-Command "mvn" -ErrorAction SilentlyContinue) {
    $MvnCmd = "mvn"
    Write-Host "Usando Maven do Sistema (mvn)." -ForegroundColor Gray
}
else {
    Write-Warning "Maven não encontrado."
    Write-Host "Baixando Maven Portátil (3.9.6)..." -ForegroundColor Yellow
    
    $MvnDir = Join-Path $PSScriptRoot ".maven-portable"
    $MvnZip = Join-Path $MvnDir "maven.zip"
    $MvnVersion = "3.9.6"
    
    if (-not (Test-Path $MvnDir)) { New-Item -ItemType Directory -Path $MvnDir | Out-Null }
    
    try {
        [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
        $RepoUrl = "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$MvnVersion/apache-maven-$MvnVersion-bin.zip"
        
        Write-Host "Downloading from $RepoUrl..." -ForegroundColor DarkGray
        Invoke-WebRequest -Uri $RepoUrl -OutFile $MvnZip -UseBasicParsing
        Expand-Archive -Path $MvnZip -DestinationPath $MvnDir -Force
        
        $ExtractedRoot = Join-Path $MvnDir "apache-maven-$MvnVersion"
        $MvnBinDir = Join-Path $ExtractedRoot "bin"
        $MvnCmd = Join-Path $MvnBinDir "mvn.cmd"
        $env:PATH = "$MvnBinDir;$env:PATH"
        
        Write-Host "Maven Portátil pronto: $MvnCmd" -ForegroundColor Green
    }
    catch {
        Write-Error "Falha ao baixar Maven: $_"
        Exit 1
    }
}

# --- 3. Background Browser Launcher ---
$jobScript = {
    param($SwaggerUrl)
    $MaxRetries = 30
    for ($i = 0; $i -lt $MaxRetries; $i++) {
        try {
            $resp = Invoke-WebRequest -Uri $SwaggerUrl -Method Head -TimeoutSec 1 -ErrorAction SilentlyContinue
            if ($resp.StatusCode -eq 200) {
                Start-Process $SwaggerUrl
                return
            }
        }
        catch {}
        Start-Sleep -Seconds 2
    }
}
Start-Job -ScriptBlock $jobScript -ArgumentList $SwaggerUrl | Out-Null

# --- 4. Run Application (With Log Redirection) ---
Write-Host "Compilando e Subindo a Aplicação..." -ForegroundColor Cyan
Write-Host "(Logs de erro serão salvos em logs/run_error.log)" -ForegroundColor DarkGray
Write-Host "(Aguarde o Swagger abrir automaticamente)" -ForegroundColor DarkGray

# Define Log Paths
$ErrorLog = Join-Path $LogsDir "run_error.log"
$BuildLog = Join-Path $LogsDir "build_output.log"

# Clean old logs
if (Test-Path $ErrorLog) { Remove-Item $ErrorLog }

# Execute Maven
try {
    # We pipe error to the log file but keep stdout visible or piped as needed.
    # PowerShell redirection: 2> filename redirects error stream.
    & $MvnCmd spring-boot:run "-Dmaven.test.skip=true" 2> $ErrorLog
}
catch {
    Write-Error "Falha na execução. Verifique logs/run_error.log"
    throw $_
}
