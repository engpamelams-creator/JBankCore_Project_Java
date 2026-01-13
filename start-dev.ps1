# start-dev.ps1
# JBank Core API - Developer Experience Script

$ErrorActionPreference = "Stop"
$AppUrl = "http://localhost:8080"
$SwaggerUrl = "$AppUrl/swagger-ui/index.html"

Write-Host "Iniciando JBank Core API - Dev Mode" -ForegroundColor Cyan

# --- Auto-detect Java 21 ---
$requiredJavaVersion = "21"
$javaVersion = cmd /c "java -version 2>&1"
if ($javaVersion -notmatch "version .?21") {
    Write-Host "⚠️  Java 21 padrao nao detectado. Procurando instalacao..." -ForegroundColor Yellow
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

# Verificar novamente
# Verificar novamente
if ($env:JAVA_HOME) {
    $javaExe = "$env:JAVA_HOME\bin\java.exe"
    $verifyVersion = cmd /c """$javaExe"" -version 2>&1"
    if ($verifyVersion -notmatch "version .?21") {
        Write-Warning "Java 21 foi configurado mas a verificacao retornou: $verifyVersion"
    }
    else {
        Write-Host "✅ Ambiente configurado com sucesso para Java 21." -ForegroundColor Green
    }
}
else {
    # Fallback check
    $javaVersion = cmd /c "java -version 2>&1"
    if ($javaVersion -notmatch "version .?21") {
        Write-Error "❌ ERRO: Java 21 nao encontrado."
        exit 1
    }
}

# --- Maven Check ---tection / Auto-Provisioning ---
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
    Write-Warning "Maven nao encontrado."
    Write-Host "Baixando Maven Portatil (3.9.6) para rodar o projeto..." -ForegroundColor Yellow
    
    $MvnDir = Join-Path $PSScriptRoot ".maven-portable"
    $MvnZip = Join-Path $MvnDir "maven.zip"
    $MvnVersion = "3.9.6"
    
    if (-not (Test-Path $MvnDir)) { New-Item -ItemType Directory -Path $MvnDir | Out-Null }
    
    try {
        [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
        $RepoUrl = "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$MvnVersion/apache-maven-$MvnVersion-bin.zip"
        
        Write-Host "Downloading from $RepoUrl..." -ForegroundColor DarkGray
        Invoke-WebRequest -Uri $RepoUrl -OutFile $MvnZip -UseBasicParsing
        
        Write-Host "Extracting..." -ForegroundColor DarkGray
        Expand-Archive -Path $MvnZip -DestinationPath $MvnDir -Force
        
        # Determine strict path to mvn.cmd
        $ExtractedRoot = Join-Path $MvnDir "apache-maven-$MvnVersion"
        $MvnBinDir = Join-Path $ExtractedRoot "bin"
        $MvnCmd = Join-Path $MvnBinDir "mvn.cmd"
        
        # Add to PATH temporarily for this session to avoid issues
        $env:PATH = "$MvnBinDir;$env:PATH"
        
        Write-Host "Maven Portatil pronto: $MvnCmd" -ForegroundColor Green
    }
    catch {
        Write-Error "Falha ao baixar Maven: $_"
        Exit 1
    }
}

# --- 2. Background Browser Launcher ---
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

# --- 3. Run Application ---
Write-Host "Compilando e Subindo a Aplicacao..." -ForegroundColor Cyan
Write-Host "(Aguarde o Swagger abrir automaticamente)" -ForegroundColor DarkGray

# Execute Maven
& $MvnCmd spring-boot:run "-Dmaven.test.skip=true"
