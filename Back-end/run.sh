#!/bin/bash

# ========================================
#  JBank Core - One-Click Start (Linux/Mac)
# ========================================

echo ""
echo "╔═══════════════════════════════════════════════════════════╗"
echo "║                                                           ║"
echo "║        🚀 JBank Core - Starting...                       ║"
echo "║                                                           ║"
echo "╚═══════════════════════════════════════════════════════════╝"
echo ""

# Check if Docker is running
echo "[1/3] Checking Docker..."
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running!"
    echo "Please start Docker and try again."
    exit 1
fi
echo "✅ Docker is running"

# Build the project
echo ""
echo "[2/3] Building JBank Core..."
cd Back-end
./mvnw clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi
cd ..
echo "✅ Build successful"

# Start Docker Compose
echo ""
echo "[3/3] Starting Docker containers..."
docker-compose up --build -d

if [ $? -eq 0 ]; then
    echo ""
    echo "╔═══════════════════════════════════════════════════════════╗"
    echo "║                                                           ║"
    echo "║        ✅ JBank Core is running!                         ║"
    echo "║                                                           ║"
    echo "║        📊 Swagger UI:                                    ║"
    echo "║        http://localhost:8080/swagger-ui.html             ║"
    echo "║                                                           ║"
    echo "║        🔍 Actuator Health:                               ║"
    echo "║        http://localhost:8080/actuator/health             ║"
    echo "║                                                           ║"
    echo "║        🐰 RabbitMQ Management:                           ║"
    echo "║        http://localhost:15672                            ║"
    echo "║        (user: guest / pass: guest)                       ║"
    echo "║                                                           ║"
    echo "╚═══════════════════════════════════════════════════════════╝"
    echo ""
    echo "Press Ctrl+C to stop the containers"
    echo ""
    docker-compose logs -f jbank-core
else
    echo "❌ Failed to start containers!"
    exit 1
fi
