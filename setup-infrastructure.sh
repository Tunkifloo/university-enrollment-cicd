#!/bin/bash

# Detener el script si hay errores
set -e

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}   SETUP DE INFRAESTRUCTURA - UNIVERSIDAD SYSTEM      ${NC}"
echo ""

# 1. GESTIÓN DE ARCHIVOS DE CONFIGURACIÓN
echo -e "${YELLOW} Verificando archivos de configuración...${NC}"

# Definir la ruta segura donde tienes tus configs
CONFIG_DIR="/home/ubuntu/config"

# Verificar docker-compose.yml
if [ ! -f "docker-compose.yml" ]; then
    echo -e "${RED} Error: No se encuentra docker-compose.yml${NC}"
    echo "Asegúrate de ejecutar este script dentro de la carpeta del repositorio."
    exit 1
fi

# Gestionar .env
if [ -f "$CONFIG_DIR/.env" ]; then
    echo -e "   -> Copiando .env desde $CONFIG_DIR"
    cp "$CONFIG_DIR/.env" .
elif [ -f ".env" ]; then
    echo -e "   -> Usando .env existente en directorio local"
else
    echo -e "${RED} Error Crítico: No se encuentra el archivo .env${NC}"
    echo "   No está en $CONFIG_DIR ni en la carpeta actual."
    exit 1
fi

# Gestionar ngrok.yml
if [ -f "$CONFIG_DIR/ngrok.yml" ]; then
    echo -e "   -> Copiando ngrok.yml desde $CONFIG_DIR"
    cp "$CONFIG_DIR/ngrok.yml" .
elif [ -f "ngrok.yml" ]; then
    echo -e "   -> Usando ngrok.yml existente en directorio local"
else
    echo -e "${RED} Error Crítico: No se encuentra el archivo ngrok.yml${NC}"
    echo "   No está en $CONFIG_DIR ni en la carpeta actual."
    exit 1
fi

echo -e "${GREEN} Todos los archivos necesarios están listos.${NC}"
echo ""

# 2. VERIFICACIÓN DE DOCKER
echo -e "${YELLOW} Verificando estado de Docker...${NC}"
if ! command -v docker &> /dev/null; then
    echo -e "${RED} Docker no está instalado.${NC}"
    exit 1
fi

if ! docker ps &> /dev/null; then
    echo -e "${RED} Docker no está corriendo o el usuario no tiene permisos.${NC}"
    exit 1
fi

echo -e "${GREEN} Docker está operativo.${NC}"
echo ""

# 3. LEVANTAR INFRAESTRUCTURA
echo -e "${BLUE} LEVANTANDO SERVICIOS DE INFRAESTRUCTURA${NC}"
echo "-------------------------------------------"

echo -e "${YELLOW}[1/7] Bases de Datos PostgreSQL...${NC}"
docker-compose up -d postgres-auth postgres-audit postgres-matriculas
echo "      Esperando inicialización de BD (10s)..."
sleep 10

echo -e "${YELLOW}[2/7] Zookeeper (para Kafka)...${NC}"
docker-compose up -d zookeeper
echo "      Esperando Zookeeper (10s)..."
sleep 10

echo -e "${YELLOW}[3/7] Kafka...${NC}"
docker-compose up -d kafka
echo "      Esperando Kafka (15s)..."
sleep 15

echo -e "${YELLOW}[4/7] Kafka UI...${NC}"
docker-compose up -d kafka-ui
sleep 2

echo -e "${YELLOW}[5/7] RabbitMQ...${NC}"
docker-compose up -d rabbitmq
echo "      Esperando RabbitMQ (10s)..."
sleep 10

echo -e "${YELLOW}[6/7] SonarQube (Análisis de código)...${NC}"
docker-compose up -d sonarqube
echo "      Esperando arranque inicial de SonarQube (20s)..."
sleep 20

echo -e "${YELLOW}[7/7] Ngrok (Túnel seguro)...${NC}"
docker-compose up -d ngrok
sleep 5

echo ""
echo -e "${GREEN} INFRAESTRUCTURA COMPLETADA EXITOSAMENTE${NC}"
echo ""

# 4. RESUMEN Y ACCESOS
echo -e "${YELLOW} Estado actual de los contenedores:${NC}"
echo ""
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep -E "postgres|zookeeper|kafka|rabbitmq|sonarqube|ngrok"

echo ""
echo -e "${BLUE} INFORMACIÓN DE ACCESO${NC}"
echo "-------------------------------------------"
echo "  SonarQube:"
echo "    Local:   http://localhost:9000"
echo "    Público: https://presumably-legible-terrier.ngrok-free.app"
echo "    Login:   admin / admin (cambiar al entrar)"
echo ""
echo "  RabbitMQ Management:"
echo "    URL:     http://localhost:15672"
echo "    Login:   guest / guest"
echo ""
echo "  Kafka UI:"
echo "    URL:     http://localhost:8081"
echo ""
echo "  Ngrok Dashboard:"
echo "    URL:     http://localhost:4040"
echo ""
echo -e "${BLUE}======================================================${NC}"
echo -e "${GREEN} LISTO: Ahora puedes ejecutar el Workflow de GitHub Actions${NC}"
echo -e "${BLUE}======================================================${NC}"