@echo off
REM ========================================
REM  JBank Core - One-Click Start (Windows)
REM ========================================

echo.
echo ╔═══════════════════════════════════════════════════════════╗
echo ║                                                           ║
echo ║        🚀 JBank Core - Starting...                       ║
echo ║                                                           ║
echo ╚═══════════════════════════════════════════════════════════╝
echo.

REM Check if Docker is running
echo [1/3] Checking Docker...
docker info >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Docker is not running!
    echo Please start Docker Desktop and try again.
    pause
    exit /b 1
)
echo ✅ Docker is running

REM Build the project
echo.
echo [2/3] Building JBank Core...
cd Back-end
call mvnw.cmd clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Build failed!
    pause
    exit /b 1
)
cd ..
echo ✅ Build successful

REM Start Docker Compose
echo.
echo [3/3] Starting Docker containers...
docker-compose up --build -d

if %ERRORLEVEL% EQ 0 (
    echo.
    echo ╔═══════════════════════════════════════════════════════════╗
    echo ║                                                           ║
    echo ║        ✅ JBank Core is running!                         ║
    echo ║                                                           ║
    echo ║        📊 Swagger UI:                                    ║
    echo ║        http://localhost:8080/swagger-ui.html             ║
    echo ║                                                           ║
    echo ║        🔍 Actuator Health:                               ║
    echo ║        http://localhost:8080/actuator/health             ║
    echo ║                                                           ║
    echo ║        🐰 RabbitMQ Management:                           ║
    echo ║        http://localhost:15672                            ║
    echo ║        (user: guest / pass: guest)                       ║
    echo ║                                                           ║
    echo ╚═══════════════════════════════════════════════════════════╝
    echo.
    echo Press Ctrl+C to stop the containers
    echo.
    docker-compose logs -f jbank-core
) else (
    echo ❌ Failed to start containers!
    pause
)
