# Sistema de Matrículas Universitarias - Arquitectura de Microservicios

<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2024.0.1-blue)
![Java](https://img.shields.io/badge/Java-17-orange)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED)
![License](https://img.shields.io/badge/License-MIT-yellow)

Sistema de gestión de matrículas universitarias construido con arquitectura de microservicios, implementando patrones enterprise y mejores prácticas de desarrollo.

[Características](#características) • [Arquitectura](#arquitectura) • [Instalación](#instalación) • [Documentación](#documentación) • [Contribuir](#contribuir)

</div>

---

## 📋 Tabla de Contenidos

- [Descripción General](#descripción-general)
- [Características](#características)
- [Arquitectura del Sistema](#arquitectura-del-sistema)
    - [Microservicios](#microservicios)
    - [Infraestructura](#infraestructura)
- [Stack Tecnológico](#stack-tecnológico)
- [Requisitos Previos](#requisitos-previos)
- [Instalación y Configuración](#instalación-y-configuración)
    - [Instalación con Docker](#instalación-con-docker-recomendado)
    - [Instalación Local](#instalación-local)
- [Documentación de APIs](#documentación-de-apis)
- [Patrones de Diseño](#patrones-de-diseño)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Monitoreo y Observabilidad](#monitoreo-y-observabilidad)
- [Troubleshooting](#troubleshooting)
- [Roadmap](#roadmap)
- [Contribuir](#contribuir)
- [Licencia](#licencia)

---

## 🎯 Descripción General

Sistema empresarial de gestión de matrículas universitarias que permite administrar facultades, carreras, estudiantes y el proceso completo de matrícula académica. El sistema ha evolucionado desde una arquitectura monolítica a una moderna arquitectura de microservicios, proporcionando escalabilidad, resiliencia y mantenibilidad.

**Características principales:**
- 🏢 Gestión integral de facultades y carreras
- 👥 Sistema de autenticación y autorización con JWT
- 📧 Notificaciones por email automatizadas
- 📊 Auditoría completa de eventos del sistema
- 🔍 Service Discovery con Eureka
- 🚪 API Gateway centralizado con enrutamiento inteligente
- 🎨 Interfaz moderna con React y TypeScript

---

## ✨ Características

### Funcionales
- ✅ CRUD completo de Facultades y Carreras
- ✅ Sistema de autenticación basado en JWT
- ✅ Registro y gestión de usuarios
- ✅ Notificaciones automáticas por email
- ✅ Auditoría de todas las operaciones
- ✅ Validación de datos en múltiples capas
- ✅ Gestión de relaciones entre entidades

### No Funcionales
- 🚀 **Escalabilidad horizontal**: Cada microservicio puede escalar independientemente
- 🔒 **Seguridad**: JWT, validación de tokens, CORS configurado
- 📈 **Observabilidad**: Actuator endpoints, logging estructurado
- 🔄 **Resiliencia**: Health checks, reintentos automáticos
- 🐳 **Contenedorización**: Todo el sistema dockerizado
- 📊 **Event-Driven**: Comunicación asíncrona con Kafka y RabbitMQ

---

## 🏗️ Arquitectura del Sistema

### Diagrama de Arquitectura

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          CAPA DE CLIENTE                                 │
│                                                                           │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                    React Frontend (SPA)                          │  │
│  │              TypeScript + Zustand + Tailwind CSS                 │  │
│  └──────────────────────────────────────────────────────────────────┘  │
└───────────────────────────────────┬──────────────────────────────────────┘
                                    │ HTTP/REST (JSON)
┌───────────────────────────────────▼──────────────────────────────────────┐
│                       CAPA DE GATEWAY                                     │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │              Spring Cloud Gateway (Port 8080)                    │  │
│  │   • Enrutamiento Inteligente    • JWT Validation                │  │
│  │   • CORS                         • Load Balancing                │  │
│  └──────────────────────────────────────────────────────────────────┘  │
└───────────────────────────────────┬──────────────────────────────────────┘
                                    │
                    ┌───────────────┼───────────────┐
                    │               │               │
┌───────────────────▼───┐ ┌─────────▼────────┐ ┌──▼─────────────────────┐
│   Auth Service        │ │ Matriculas       │ │  Audit Service         │
│   (Port 8082)         │ │ Service          │ │  (Port 8084)           │
│                       │ │ (Port 8085)      │ │                        │
│ • User Management     │ │ • Faculty CRUD   │ │ • Event Logging        │
│ • JWT Generation      │ │ • Career CRUD    │ │ • Compliance           │
│ • Authentication      │ │ • Business Logic │ │ • Analytics            │
│                       │ │                  │ │                        │
│ PostgreSQL (Auth DB)  │ │ PostgreSQL       │ │ PostgreSQL (Audit DB)  │
└───────┬───────────────┘ └────┬─────────────┘ └────────┬───────────────┘
        │                      │                         │
        │                      │                         │
        ├──────────────────────┴─────────────────────────┤
        │                                                 │
┌───────▼─────────────────────────────────────────────────▼───────────────┐
│                    SERVICE DISCOVERY LAYER                              │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │              Eureka Server (Port 8761)                           │  │
│  │           • Service Registration                                 │  │
│  │           • Health Monitoring                                    │  │
│  └──────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│                   CAPA DE MENSAJERÍA ASÍNCRONA                          │
│                                                                           │
│  ┌──────────────────────────┐      ┌──────────────────────────────┐   │
│  │    RabbitMQ (5672)       │      │      Kafka (9092)            │   │
│  │                          │      │                              │   │
│  │  Queue: email.queue      │      │  Topics:                     │   │
│  │  Exchange: email.exchange│      │  • audit.events              │   │
│  │                          │      │  • user.registered           │   │
│  │  Consumer:               │      │  • faculty.created           │   │
│  │  ↓                       │      │  • career.created            │   │
│  │  Email Service           │      │                              │   │
│  │  (Port 8083)             │      │  Consumers: Audit Service    │   │
│  │                          │      │                              │   │
│  │  • SMTP Integration      │      │  Producers: Auth, Matriculas │   │
│  │  • Template Engine       │      │                              │   │
│  │  • Async Processing      │      │                              │   │
│  └──────────────────────────┘      └──────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│                        CAPA DE PERSISTENCIA                              │
│                                                                           │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐             │
│  │ PostgreSQL   │    │ PostgreSQL   │    │ PostgreSQL   │             │
│  │ Auth DB      │    │ Matriculas   │    │ Audit DB     │             │
│  │ (Port 5432)  │    │ DB           │    │ (Port 5433)  │             │
│  │              │    │ (Port 5434)  │    │              │             │
│  │ • Users      │    │ • Facultades │    │ • AuditLogs  │             │
│  │ • Roles      │    │ • Carreras   │    │ • Events     │             │
│  └──────────────┘    └──────────────┘    └──────────────┘             │
└─────────────────────────────────────────────────────────────────────────┘
```

### Microservicios

#### 1. **Eureka Server** (Service Discovery)
- **Puerto**: 8761
- **Propósito**: Registro y descubrimiento de servicios
- **Tecnologías**: Spring Cloud Netflix Eureka
- **Características**:
    - Dashboard de monitoreo de servicios
    - Health checks automáticos
    - Auto-registration de microservicios

#### 2. **API Gateway**
- **Puerto**: 8080
- **Propósito**: Punto de entrada único para todas las peticiones
- **Tecnologías**: Spring Cloud Gateway
- **Características**:
    - Enrutamiento dinámico basado en Eureka
    - Validación de tokens JWT
    - CORS configurado
    - Load balancing automático
    - Rate limiting
    - Request/Response logging

**Rutas configuradas:**
```yaml
/api/v1/auth/**        → auth-service
/api/v1/matriculas/**  → matriculas-service
/api/v1/audit/**       → audit-service
/api/v1/email/**       → email-service (admin only)
```

#### 3. **Auth Service**
- **Puerto**: 8082
- **Base de Datos**: PostgreSQL (auth_db)
- **Propósito**: Autenticación y gestión de usuarios
- **Características**:
    - Registro de usuarios
    - Login con JWT
    - Refresh tokens
    - Gestión de roles
    - Publicación de eventos de auditoría (Kafka)
    - Envío de emails de bienvenida (RabbitMQ)

**Endpoints principales:**
```
POST /api/v1/auth/register
POST /api/v1/auth/login
POST /api/v1/auth/refresh
GET  /api/v1/auth/profile
```

#### 4. **Matriculas Service**
- **Puerto**: 8085
- **Base de Datos**: PostgreSQL (matriculas_db)
- **Propósito**: Gestión de facultades y carreras
- **Características**:
    - CRUD de Facultades
    - CRUD de Carreras
    - Validaciones de negocio
    - Publicación de eventos (Kafka)
    - Envío de notificaciones (RabbitMQ)

**Endpoints principales:**
```
GET    /api/v1/matriculas/facultades
POST   /api/v1/matriculas/facultades
PUT    /api/v1/matriculas/facultades/{id}
DELETE /api/v1/matriculas/facultades/{id}

GET    /api/v1/matriculas/carreras
POST   /api/v1/matriculas/carreras
PUT    /api/v1/matriculas/carreras/{id}
DELETE /api/v1/matriculas/carreras/{id}
GET    /api/v1/matriculas/carreras/facultad/{facultadId}
```

#### 5. **Email Service**
- **Puerto**: 8083
- **Propósito**: Procesamiento asíncrono de emails
- **Características**:
    - Consumidor de RabbitMQ
    - Integración con SMTP (Gmail)
    - Templates HTML
    - Modo simulación para desarrollo
    - Reintentos automáticos

**Tipos de emails:**
- Email de bienvenida (registro de usuario)
- Notificaciones de facultades/carreras
- Alertas administrativas

#### 6. **Audit Service**
- **Puerto**: 8084
- **Base de Datos**: PostgreSQL (audit_db)
- **Propósito**: Auditoría y logging de eventos
- **Características**:
    - Consumidor de Kafka
    - Logging de todas las operaciones
    - Trazabilidad completa
    - Queries de auditoría

**Eventos auditados:**
- `user.registered` - Registro de usuarios
- `faculty.created` - Creación de facultades
- `faculty.updated` - Actualización de facultades
- `faculty.deleted` - Eliminación de facultades
- `career.created` - Creación de carreras
- `career.updated` - Actualización de carreras
- `career.deleted` - Eliminación de carreras

### Infraestructura

#### **RabbitMQ**
- **Puerto**: 5672 (AMQP)
- **Puerto Gestión**: 15672 (UI)
- **Propósito**: Mensajería asíncrona para emails
- **Configuración**:
  ```
  Queue: email.queue
  Exchange: email.exchange (direct)
  Routing Key: email.routing.key
  ```

#### **Apache Kafka**
- **Puerto**: 9092 (externo)
- **Puerto interno**: 29092
- **Propósito**: Event streaming para auditoría
- **UI de Gestión**: Puerto 8090 (Kafka UI)
- **Topics**:
    - `audit.events` - Eventos generales de auditoría
    - `user.registered` - Registro de usuarios
    - `faculty.created/updated/deleted` - Eventos de facultades
    - `career.created/updated/deleted` - Eventos de carreras

#### **PostgreSQL**
Tres bases de datos independientes:
1. **auth_db** (Puerto 5432) - Auth Service
2. **matriculas_db** (Puerto 5434) - Matriculas Service
3. **audit_db** (Puerto 5433) - Audit Service

---

## 🛠️ Stack Tecnológico

### Backend

| Tecnología | Versión | Propósito |
|-----------|---------|-----------|
| Java | 17 | Lenguaje de programación |
| Spring Boot | 3.5.6 | Framework principal |
| Spring Cloud | 2024.0.1 | Microservicios |
| Spring Cloud Gateway | - | API Gateway |
| Spring Cloud Netflix Eureka | - | Service Discovery |
| Spring Data JPA | - | Persistencia |
| Spring Security | - | Seguridad |
| Spring Kafka | - | Integración Kafka |
| Spring AMQP | - | Integración RabbitMQ |
| PostgreSQL | 16 | Base de datos |
| Flyway | - | Migración de BD |
| MapStruct | 1.6.3 | Mapeo de objetos |
| Lombok | 1.18.34 | Reducción de boilerplate |
| JWT (JJWT) | 0.12.6 | Tokens de autenticación |
| Maven | 3.8+ | Gestión de dependencias |

### Infraestructura

| Tecnología | Versión | Propósito |
|-----------|---------|-----------|
| Docker | 20.10+ | Contenedorización |
| Docker Compose | 2.0+ | Orquestación |
| Kafka | 3.6.1 | Event Streaming |
| Zookeeper | 3.8.3 | Coordinación Kafka |
| RabbitMQ | 3.13 | Message Broker |
| Nginx | latest | Servidor web frontend |

### Frontend

| Tecnología | Versión | Propósito |
|-----------|---------|-----------|
| React | 19 | UI Library |
| TypeScript | 5.9 | Lenguaje tipado |
| Vite | 7 | Build tool |
| Zustand | 5 | State management |
| Axios | - | HTTP client |
| Tailwind CSS | 3.4 | Estilos |

### Herramientas de Desarrollo

- **IntelliJ IDEA** - IDE principal
- **Postman** - Testing de APIs
- **DBeaver** - Cliente PostgreSQL
- **Git** - Control de versiones

---

## 📋 Requisitos Previos

### Opción 1: Docker (Recomendado para Producción)

```bash
✅ Docker Desktop 20.10 o superior
✅ Docker Compose 2.0 o superior
✅ 8GB de RAM disponible
✅ 10GB de espacio en disco
```

### Opción 2: Instalación Local (Desarrollo)

```bash
✅ Java JDK 17 o superior
✅ Maven 3.8 o superior
✅ Node.js 18 o superior
✅ npm 9 o superior
✅ PostgreSQL 16 o superior
✅ Kafka 3.6+ (con Zookeeper)
✅ RabbitMQ 3.13+
```

---

## 🚀 Instalación y Configuración

### Instalación con Docker (Recomendado)

#### 1. Clonar el Repositorio

```bash
git clone https://github.com/Tunkifloo/university-enrollment-system.git
cd university-enrollment-system
```

#### 2. Configurar Variables de Entorno

Crear archivo `.env` en la raíz del proyecto:

```bash
cp .env.example .env
```

**Contenido del archivo `.env`:**

```env
# ==================== SPRING PROFILES ====================
SPRING_PROFILES_ACTIVE=prod

# ==================== POSTGRES CONFIGURATION ====================
POSTGRES_USER=postgres
POSTGRES_PASSWORD=admin123

# Database Names
AUTH_DB_NAME=auth_db
MATRICULAS_DB_NAME=matriculas_db
AUDIT_DB_NAME=audit_db

# Database Ports (for external access)
POSTGRES_AUTH_PORT=5432
POSTGRES_MATRICULAS_PORT=5434
POSTGRES_AUDIT_PORT=5433

# ==================== JPA CONFIGURATION ====================
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false

# ==================== KAFKA CONFIGURATION ====================
KAFKA_PORT=9092
KAFKA_UI_PORT=8090
KAFKA_BROKER_ID=1
KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181
KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092,PLAINTEXT_INTERNAL://kafka:29092
KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=PLAINTEXT:PLAINTEXT,PLAINTEXT_INTERNAL:PLAINTEXT
KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1
KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1
KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1
KAFKA_AUTO_CREATE_TOPICS_ENABLE=true
KAFKA_LOG_RETENTION_HOURS=168

# Kafka Topics
KAFKA_AUDIT_TOPIC=audit.events
KAFKA_USER_REGISTERED_TOPIC=user.registered
KAFKA_FACULTY_CREATED_TOPIC=faculty.created
KAFKA_FACULTY_UPDATED_TOPIC=faculty.updated
KAFKA_FACULTY_DELETED_TOPIC=faculty.deleted
KAFKA_CAREER_CREATED_TOPIC=career.created
KAFKA_CAREER_UPDATED_TOPIC=career.updated
KAFKA_CAREER_DELETED_TOPIC=career.deleted
KAFKA_AUDIT_CONSUMER_GROUP=audit-service-group

# ==================== RABBITMQ CONFIGURATION ====================
RABBITMQ_PORT=5672
RABBITMQ_MANAGEMENT_PORT=15672
RABBITMQ_DEFAULT_USER=admin
RABBITMQ_DEFAULT_PASS=admin123
RABBITMQ_DEFAULT_VHOST=/

# RabbitMQ Queues and Exchanges
RABBITMQ_EMAIL_QUEUE=email.queue
RABBITMQ_EMAIL_EXCHANGE=email.exchange
RABBITMQ_EMAIL_ROUTING_KEY=email.routing.key

# ==================== JWT CONFIGURATION ====================
JWT_SECRET=dev-secret-key-change-in-production-min-256-bits-long-jhoneirokun777-university-system
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# ==================== SMTP CONFIGURATION ====================
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password
SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true
SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true
SMTP_FROM=your-email@gmail.com
EMAIL_ENABLED=true
EMAIL_SIMULATION_MODE=false
APP_NOTIFICATION_ADMIN_EMAIL=admin@university.com

# ==================== SERVICE PORTS ====================
EUREKA_PORT=8761
GATEWAY_PORT=8080
AUTH_SERVICE_PORT=8082
EMAIL_SERVICE_PORT=8083
AUDIT_SERVICE_PORT=8084
MATRICULAS_SERVICE_PORT=8085

# ==================== FRONTEND CONFIGURATION ====================
VITE_API_BASE_URL=http://localhost:8080/api/v1
VITE_DEV_PORT=5173

# ==================== LOGGING LEVELS ====================
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_APP=DEBUG
LOGGING_LEVEL_WEB=INFO
LOGGING_LEVEL_SQL=DEBUG
LOGGING_LEVEL_KAFKA=INFO
LOGGING_LEVEL_RABBITMQ=INFO
```

#### 3. Construir y Levantar los Servicios

```bash
# Construir imágenes y levantar todos los servicios
docker-compose up -d --build

# Ver logs de todos los servicios
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f auth-service
```

#### 4. Verificar el Estado de los Servicios

```bash
# Verificar que todos los contenedores estén corriendo
docker-compose ps

# Salida esperada:
NAME                                  STATUS              PORTS
university-system-eureka-server       Up 2 minutes        0.0.0.0:8761->8761/tcp
university-system-api-gateway         Up 2 minutes        0.0.0.0:8080->8080/tcp
university-system-auth-service        Up 2 minutes        0.0.0.0:8082->8082/tcp
university-system-email-service       Up 2 minutes        0.0.0.0:8083->8083/tcp
university-system-audit-service       Up 2 minutes        0.0.0.0:8084->8084/tcp
university-system-matriculas-service  Up 2 minutes        0.0.0.0:8085->8085/tcp
university-system-kafka               Up 2 minutes        0.0.0.0:9092->9092/tcp
university-system-rabbitmq            Up 2 minutes        0.0.0.0:5672->5672/tcp, 0.0.0.0:15672->15672/tcp
postgres-auth                         Up 2 minutes        0.0.0.0:5432->5432/tcp
postgres-matriculas                   Up 2 minutes        0.0.0.0:5434->5432/tcp
postgres-audit                        Up 2 minutes        0.0.0.0:5433->5432/tcp
matriculas-frontend                   Up 2 minutes        0.0.0.0:5173->80/tcp
```

#### 5. Acceder a las Interfaces

| Servicio | URL | Credenciales |
|----------|-----|--------------|
| **Frontend** | http://localhost:5173 | - |
| **API Gateway** | http://localhost:8080 | - |
| **Eureka Dashboard** | http://localhost:8761 | - |
| **RabbitMQ Management** | http://localhost:15672 | admin / admin123 |
| **Kafka UI** | http://localhost:8090 | - |

#### 6. Verificar Salud de los Servicios

```bash
# Health check del API Gateway
curl http://localhost:8080/actuator/health

# Health check del Auth Service
curl http://localhost:8082/actuator/health

# Health check del Matriculas Service
curl http://localhost:8085/actuator/health

# Health check del Audit Service
curl http://localhost:8084/actuator/health

# Health check del Email Service
curl http://localhost:8083/actuator/health
```

#### 7. Comandos Útiles de Docker

```bash
# Detener todos los servicios
docker-compose down

# Detener y eliminar volúmenes (⚠️ elimina datos)
docker-compose down -v

# Reconstruir un servicio específico
docker-compose up -d --build auth-service

# Ver logs en tiempo real
docker-compose logs -f

# Reiniciar un servicio
docker-compose restart matriculas-service

# Ejecutar comando en un contenedor
docker-compose exec auth-service sh
```

### Instalación Local (Desarrollo)

#### 1. Configurar Bases de Datos

```sql
-- Crear las tres bases de datos
CREATE DATABASE auth_db;
CREATE DATABASE matriculas_db;
CREATE DATABASE audit_db;

-- Crear usuario (opcional)
CREATE USER university_user WITH PASSWORD 'admin123';
GRANT ALL PRIVILEGES ON DATABASE auth_db TO university_user;
GRANT ALL PRIVILEGES ON DATABASE matriculas_db TO university_user;
GRANT ALL PRIVILEGES ON DATABASE audit_db TO university_user;
```

#### 2. Instalar y Configurar Kafka

```bash
# Descargar Kafka
wget https://downloads.apache.org/kafka/3.6.1/kafka_2.13-3.6.1.tgz
tar -xzf kafka_2.13-3.6.1.tgz
cd kafka_2.13-3.6.1

# Iniciar Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# Iniciar Kafka (en otra terminal)
bin/kafka-server-start.sh config/server.properties
```

#### 3. Instalar y Configurar RabbitMQ

```bash
# Ubuntu/Debian
sudo apt-get install rabbitmq-server
sudo systemctl start rabbitmq-server
sudo rabbitmq-plugins enable rabbitmq_management

# Windows (Chocolatey)
choco install rabbitmq

# macOS
brew install rabbitmq
brew services start rabbitmq
```

#### 4. Compilar y Ejecutar Microservicios

```bash
# Desde la raíz del proyecto
cd Backend

# Compilar todo el proyecto
mvn clean install -DskipTests

# Ejecutar cada servicio (en terminales separadas)

# 1. Eureka Server
cd eureka-server
mvn spring-boot:run

# 2. API Gateway
cd api-gateway
mvn spring-boot:run

# 3. Auth Service
cd auth-service
mvn spring-boot:run

# 4. Matriculas Service
cd matriculas-service
mvn spring-boot:run

# 5. Email Service
cd email-service
mvn spring-boot:run

# 6. Audit Service
cd audit-service
mvn spring-boot:run
```

#### 5. Ejecutar Frontend

```bash
cd Frontend
npm install
npm run dev
```

---

## 📚 Documentación de APIs

### Auth Service API

**Base URL**: `http://localhost:8080/api/v1/auth`

#### Registro de Usuario

```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "SecurePass123!",
  "firstName": "John",
  "lastName": "Doe"
}

Response 201 Created:
{
  "id": 1,
  "username": "johndoe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "roles": ["USER"],
  "createdAt": "2025-11-17T10:30:00"
}
```

#### Login

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "johndoe",
  "password": "SecurePass123!"
}

Response 200 OK:
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400
}
```

#### Obtener Perfil

```http
GET /api/v1/auth/profile
Authorization: Bearer {token}

Response 200 OK:
{
  "id": 1,
  "username": "johndoe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "roles": ["USER"]
}
```

### Matriculas Service API

**Base URL**: `http://localhost:8080/api/v1/matriculas`

#### Gestión de Facultades

```http
# Listar todas las facultades
GET /api/v1/matriculas/facultades
Authorization: Bearer {token}

Response 200 OK:
[
  {
    "facultadId": 1,
    "nombre": "Facultad de Ingeniería",
    "descripcion": "Facultad dedicada a la formación de ingenieros",
    "ubicacion": "Pabellón A - Campus Principal",
    "decano": "Dr. Juan Pérez Rodríguez",
    "fechaRegistro": "2025-01-15T08:00:00",
    "activo": true
  }
]

# Crear facultad
POST /api/v1/matriculas/facultades
Authorization: Bearer {token}
Content-Type: application/json

{
  "nombre": "Facultad de Ciencias",
  "descripcion": "Facultad enfocada en ciencias básicas",
  "ubicacion": "Pabellón B",
  "decano": "Dra. María González",
  "activo": true
}

# Actualizar facultad
PUT /api/v1/matriculas/facultades/1
Authorization: Bearer {token}
Content-Type: application/json

{
  "nombre": "Facultad de Ingeniería",
  "descripcion": "Descripción actualizada",
  "ubicacion": "Nueva ubicación",
  "decano": "Nuevo decano",
  "activo": true
}

# Eliminar facultad (soft delete)
DELETE /api/v1/matriculas/facultades/1
Authorization: Bearer {token}
```

#### Gestión de Carreras

```http
# Listar todas las carreras
GET /api/v1/matriculas/carreras
Authorization: Bearer {token}

# Listar carreras por facultad
GET /api/v1/matriculas/carreras/facultad/1
Authorization: Bearer {token}

# Crear carrera
POST /api/v1/matriculas/carreras
Authorization: Bearer {token}
Content-Type: application/json

{
  "facultadId": 1,
  "nombre": "Ingeniería de Sistemas",
  "descripcion": "Desarrollo de software y sistemas",
  "duracionSemestres": 10,
  "tituloOtorgado": "Ingeniero de Sistemas",
  "activo": true
}
```

### Swagger/OpenAPI Documentation

Cada microservicio expone su documentación Swagger:

- **Auth Service**: http://localhost:8082/swagger-ui.html
- **Matriculas Service**: http://localhost:8085/swagger-ui.html
- **Audit Service**: http://localhost:8084/swagger-ui.html

---

## 🎨 Patrones de Diseño

### Patrones de Arquitectura

#### 1. **Microservices Architecture**
División del sistema en servicios independientes, cada uno con su propia base de datos (Database per Service pattern).

#### 2. **API Gateway Pattern**
Punto de entrada único que enruta las peticiones a los microservicios correspondientes.

#### 3. **Service Discovery Pattern**
Eureka permite el registro dinámico y descubrimiento de servicios.

#### 4. **Circuit Breaker Pattern** (Implícito con Spring Cloud)
Protección contra fallos en cascada entre servicios.

#### 5. **Event-Driven Architecture**
Comunicación asíncrona mediante eventos (Kafka y RabbitMQ).

#### 6. **Database per Service Pattern**
Cada microservicio tiene su propia base de datos independiente.

### Patrones de Diseño del Backend

#### 1. **Layered Architecture**
```
Controllers → Services → Repositories → Entities
```

#### 2. **Repository Pattern**
```java
@Repository
public interface FacultadRepository extends JpaRepository<Facultad, Long> {
    Optional<Facultad> findByNombre(String nombre);
    List<Facultad> findByActivoTrue();
}
```

#### 3. **DTO Pattern**
```java
public record FacultadRequestDTO(
    @NotBlank String nombre,
    @Size(max = 500) String descripcion,
    String ubicacion,
    String decano,
    Boolean activo
) {}
```

#### 4. **Service Layer Pattern**
```java
@Service
@RequiredArgsConstructor
public class FacultadServiceImpl implements IFacultadService {
    private final FacultadRepository repository;
    private final FacultadMapper mapper;
    
    @Transactional
    public FacultadResponseDTO create(FacultadRequestDTO dto) {
        // Business logic
    }
}
```

#### 5. **Dependency Injection**
```java
@RequiredArgsConstructor // Constructor injection via Lombok
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
}
```

#### 6. **Global Exception Handling**
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        // Error handling
    }
}
```

### Patrones de Comunicación

#### 1. **Synchronous Communication (REST)**
```
Client → Gateway → Service (via Eureka)
```

#### 2. **Asynchronous Communication (Event-Driven)**

**Kafka (Auditoría):**
```java
@Service
public class AuditEventPublisher {
    private final KafkaTemplate<String, AuditEvent> kafkaTemplate;
    
    public void publishEvent(AuditEvent event) {
        kafkaTemplate.send("audit.events", event);
    }
}
```

**RabbitMQ (Emails):**
```java
@Service
public class EmailProducer {
    private final RabbitTemplate rabbitTemplate;
    
    public void sendEmail(EmailMessage message) {
        rabbitTemplate.convertAndSend("email.exchange", "email.routing.key", message);
    }
}
```

---

## 📁 Estructura del Proyecto

```
university-enrollment-system/
│
├── Backend/
│   ├── pom.xml                           # Parent POM
│   │
│   ├── common-lib/                       # Shared utilities and DTOs
│   │   ├── src/main/java/com/university/common/
│   │   │   ├── dto/                      # Shared DTOs
│   │   │   ├── util/                     # Utility classes
│   │   │   └── constants/                # Constants
│   │   └── pom.xml
│   │
│   ├── eureka-server/                    # Service Discovery
│   │   ├── src/main/java/
│   │   ├── src/main/resources/
│   │   │   └── application.yml
│   │   ├── Dockerfile
│   │   └── pom.xml
│   │
│   ├── api-gateway/                      # API Gateway
│   │   ├── src/main/java/com/university/gateway/
│   │   │   ├── config/
│   │   │   │   ├── GatewayConfig.java
│   │   │   │   ├── CorsConfig.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── filter/
│   │   │   │   └── JwtAuthenticationFilter.java
│   │   │   └── ApiGatewayApplication.java
│   │   ├── src/main/resources/
│   │   │   └── application.yml           # Gateway routes
│   │   ├── Dockerfile
│   │   └── pom.xml
│   │
│   ├── auth-service/                     # Authentication Service
│   │   ├── src/main/java/com/university/auth/
│   │   │   ├── controller/
│   │   │   │   └── AuthController.java
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java
│   │   │   │   └── JwtTokenProvider.java
│   │   │   ├── repository/
│   │   │   │   └── UserRepository.java
│   │   │   ├── model/
│   │   │   │   ├── User.java
│   │   │   │   └── Role.java
│   │   │   ├── dto/
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── RegisterRequest.java
│   │   │   │   └── AuthResponse.java
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── KafkaProducerConfig.java
│   │   │   └── event/
│   │   │       └── UserRegisteredEvent.java
│   │   ├── src/main/resources/
│   │   │   └── application.yml
│   │   ├── Dockerfile
│   │   └── pom.xml
│   │
│   ├── matriculas-service/               # Business Logic Service
│   │   ├── src/main/java/com/university/matriculas/
│   │   │   ├── controller/
│   │   │   │   ├── FacultadController.java
│   │   │   │   └── CarreraController.java
│   │   │   ├── service/
│   │   │   │   ├── FacultadService.java
│   │   │   │   └── CarreraService.java
│   │   │   ├── repository/
│   │   │   │   ├── FacultadRepository.java
│   │   │   │   └── CarreraRepository.java
│   │   │   ├── model/
│   │   │   │   ├── Facultad.java
│   │   │   │   └── Carrera.java
│   │   │   ├── dto/
│   │   │   │   ├── FacultadRequestDTO.java
│   │   │   │   ├── FacultadResponseDTO.java
│   │   │   │   ├── CarreraRequestDTO.java
│   │   │   │   └── CarreraResponseDTO.java
│   │   │   ├── mapper/
│   │   │   │   ├── FacultadMapper.java
│   │   │   │   └── CarreraMapper.java
│   │   │   ├── config/
│   │   │   │   ├── KafkaProducerConfig.java
│   │   │   │   └── RabbitMQConfig.java
│   │   │   └── event/
│   │   │       ├── FacultadCreatedEvent.java
│   │   │       └── CarreraCreatedEvent.java
│   │   ├── src/main/resources/
│   │   │   ├── application.yml
│   │   │   └── db/migration/
│   │   │       ├── V1__create_tables.sql
│   │   │       └── V2__insert_data.sql
│   │   ├── Dockerfile
│   │   └── pom.xml
│   │
│   ├── email-service/                    # Email Notification Service
│   │   ├── src/main/java/com/university/email/
│   │   │   ├── consumer/
│   │   │   │   └── EmailConsumer.java
│   │   │   ├── service/
│   │   │   │   └── EmailService.java
│   │   │   ├── dto/
│   │   │   │   └── EmailMessage.java
│   │   │   ├── config/
│   │   │   │   └── RabbitMQConfig.java
│   │   │   └── template/
│   │   │       └── EmailTemplateService.java
│   │   ├── src/main/resources/
│   │   │   ├── application.yml
│   │   │   └── templates/
│   │   │       ├── welcome.html
│   │   │       └── notification.html
│   │   ├── Dockerfile
│   │   └── pom.xml
│   │
│   └── audit-service/                    # Audit & Logging Service
│       ├── src/main/java/com/university/audit/
│       │   ├── consumer/
│       │   │   └── AuditEventConsumer.java
│       │   ├── service/
│       │   │   └── AuditService.java
│       │   ├── repository/
│       │   │   └── AuditLogRepository.java
│       │   ├── model/
│       │   │   └── AuditLog.java
│       │   ├── dto/
│       │   │   └── AuditEvent.java
│       │   └── config/
│       │       └── KafkaConsumerConfig.java
│       ├── src/main/resources/
│       │   └── application.yml
│       ├── Dockerfile
│       └── pom.xml
│
├── Frontend/
│   ├── src/
│   │   ├── features/
│   │   │   ├── auth/
│   │   │   │   ├── components/
│   │   │   │   ├── hooks/
│   │   │   │   ├── store/
│   │   │   │   └── pages/
│   │   │   ├── facultades/
│   │   │   │   ├── components/
│   │   │   │   ├── hooks/
│   │   │   │   ├── store/
│   │   │   │   └── FacultadesPage.tsx
│   │   │   └── carreras/
│   │   │       ├── components/
│   │   │       ├── hooks/
│   │   │       ├── store/
│   │   │       └── CarrerasPage.tsx
│   │   ├── shared/
│   │   │   ├── components/
│   │   │   ├── config/
│   │   │   │   └── api.config.ts
│   │   │   ├── types/
│   │   │   └── utils/
│   │   ├── App.tsx
│   │   └── main.tsx
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── package.json
│   └── vite.config.ts
│
├── docker-compose.yml                    # Orchestration
├── .env                                  # Environment variables
├── .env.example                          # Example env file
├── .gitignore
├── README.md                             # This file
└── LICENSE
```

---

## 📊 Monitoreo y Observabilidad

### Actuator Endpoints

Cada microservicio expone endpoints de Actuator para monitoreo:

```bash
# Health check
GET /actuator/health

# Metrics
GET /actuator/metrics

# Info
GET /actuator/info

# Prometheus (si está habilitado)
GET /actuator/prometheus
```

### Eureka Dashboard

Accede al dashboard de Eureka para ver todos los servicios registrados y su estado:

```
http://localhost:8761
```

### RabbitMQ Management Console

Monitorea colas, exchanges y mensajes:

```
http://localhost:15672
Username: admin
Password: admin123
```

### Kafka UI

Visualiza topics, particiones y mensajes de Kafka:

```
http://localhost:8090
```

### Logs

```bash
# Ver logs de todos los servicios
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f auth-service

# Seguir logs en tiempo real
docker-compose logs -f --tail=100 matriculas-service
```

---

## 🐛 Troubleshooting

### Problema: Servicios no se registran en Eureka

**Síntomas:**
- Los servicios no aparecen en el dashboard de Eureka
- Gateway no puede enrutar peticiones

**Solución:**
```bash
# 1. Verificar que Eureka esté corriendo
docker-compose logs eureka-server

# 2. Verificar configuración de Eureka en cada servicio
# application.yml debe tener:
eureka:
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka/
  instance:
    prefer-ip-address: true

# 3. Reiniciar servicios
docker-compose restart auth-service matriculas-service
```

### Problema: Kafka Deserialization Error

**Síntomas:**
```
ClassNotFoundException: com.university.auth.dto.UserRegisteredEvent
```

**Solución:**
Ya está resuelto en la configuración actual. Verificar que:
```yaml
# Producer (Auth/Matriculas Service)
spring:
  kafka:
    producer:
      properties:
        spring.json.add.type.headers: false

# Consumer (Audit Service)
spring:
  kafka:
    consumer:
      properties:
        spring.json.trusted.packages: "*"
        spring.json.value.default.type: com.university.auditservice.dto.AuditEvent
```

### Problema: Gateway devuelve 404

**Síntomas:**
- Peticiones a través del Gateway fallan con 404
- Acceso directo al servicio funciona

**Solución:**
```bash
# 1. Verificar que el servicio esté registrado en Eureka
curl http://localhost:8761/eureka/apps

# 2. Verificar rutas del Gateway
# application.yml del Gateway debe tener StripPrefix correcto
- id: auth-service
  uri: lb://auth-service
  predicates:
    - Path=/api/v1/auth/**
  filters:
    - StripPrefix=2  # Elimina /api/v1

# 3. Verificar context-path del servicio
# application.yml del servicio:
server:
  servlet:
    context-path: ""  # Debe estar vacío o coincidir con StripPrefix
```

### Problema: Emails no se envían

**Síntomas:**
- Los usuarios no reciben emails de bienvenida
- RabbitMQ muestra mensajes no procesados

**Solución:**
```bash
# 1. Verificar configuración SMTP
docker-compose logs email-service

# 2. Activar modo simulación para pruebas
EMAIL_SIMULATION_MODE=true

# 3. Verificar credenciales de Gmail
# Generar App Password en:
# https://myaccount.google.com/apppasswords

# 4. Verificar queue en RabbitMQ
http://localhost:15672 → Queues → email.queue
```

### Problema: Base de datos no se inicializa

**Síntomas:**
```
Relation "facultad" does not exist
```

**Solución:**
```bash
# 1. Verificar que Flyway esté configurado
spring:
  flyway:
    enabled: true
    baseline-on-migrate: true

# 2. O usar JPA DDL auto
spring:
  jpa:
    hibernate:
      ddl-auto: update

# 3. Eliminar y recrear volúmenes
docker-compose down -v
docker-compose up -d
```

### Problema: Puerto en uso

**Síntomas:**
```
Bind for 0.0.0.0:8080 failed: port is already allocated
```

**Solución:**
```bash
# Opción 1: Detener el proceso que usa el puerto
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9

# Opción 2: Cambiar puerto en .env
GATEWAY_PORT=8081
```

---

## 🗺️ Roadmap

### Versión 1.1 (Q1 2026)
- [ ] Módulo de Estudiantes
- [ ] Sistema de Matrículas
- [ ] Gestión de Periodos Académicos
- [ ] Tests unitarios y de integración
- [ ] CI/CD pipeline con GitHub Actions

### Versión 1.2 (Q2 2026)
- [ ] Módulo de Docentes
- [ ] Asignación de Horarios
- [ ] Sistema de Calificaciones
- [ ] Reportes y Dashboards
- [ ] Notificaciones push

### Versión 2.0 (Q3 2026)
- [ ] Módulo de Pagos
- [ ] Integración con pasarelas de pago
- [ ] Sistema de Becas
- [ ] API pública con rate limiting
- [ ] Aplicación móvil (React Native)

### Mejoras Técnicas
- [ ] Implementar Resilience4j (Circuit Breaker)
- [ ] Distributed Tracing con Sleuth y Zipkin
- [ ] Migración a Kubernetes
- [ ] Implementar GraphQL como alternativa a REST
- [ ] Cache distribuido con Redis
- [ ] Implementar SAGA pattern para transacciones distribuidas

---

## 🤝 Contribuir

¡Las contribuciones son bienvenidas! Por favor, sigue estos pasos:

### 1. Fork el Proyecto

```bash
git clone https://github.com/tu-usuario/university-enrollment-system.git
cd university-enrollment-system
```

### 2. Crear una Rama

```bash
git checkout -b feature/nueva-funcionalidad
```

### 3. Realizar Cambios

```bash
git add .
git commit -m "feat: agregar nueva funcionalidad X"
```

### 4. Push y Pull Request

```bash
git push origin feature/nueva-funcionalidad
```

Luego abre un Pull Request en GitHub.

### Estándares de Código

**Backend (Java):**
- Seguir convenciones de Java (camelCase, PascalCase)
- Usar Lombok para reducir boilerplate
- Documentar con Javadoc métodos públicos
- Escribir tests unitarios (JUnit 5)
- Cobertura mínima: 80%

**Frontend (TypeScript):**
- Seguir convenciones de React y TypeScript
- Usar functional components y hooks
- Componentes pequeños y reutilizables
- Escribir tests con Vitest

**Commits:**
Seguir [Conventional Commits](https://www.conventionalcommits.org/):
```
feat: nueva característica
fix: corrección de bug
docs: cambios en documentación
style: formateo de código
refactor: refactorización
test: agregar tests
chore: tareas de mantenimiento
```

---

## 📄 Licencia

Este proyecto está licenciado bajo la Licencia MIT. Ver el archivo [LICENSE](LICENSE) para más detalles.

```
MIT License

Copyright (c) 2025 Tunkifloo

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 👨‍💻 Autores

- **Adrian Cisneros Bartra**
- **Jhoel Maqui Saldaña**

---

- GitHub: [@Tunkifloo](https://github.com/Tunkifloo)
- Proyecto: [university-enrollment-system](https://github.com/Tunkifloo/university-enrollment-system)
- Email: nicolocisneros@gmail.com
---
- GitHub: [@JhoneiroLove](https://github.com/JhoneiroLove)
- Proyecto: [university-enrollment-system](https://github.com/Tunkifloo/university-enrollment-system)
- Email: jhoneiro12@hotmail.com

---

## 🙏 Agradecimientos

- **Spring Team** - Framework Spring Boot y Spring Cloud
- **Netflix OSS** - Eureka Server
- **Apache Software Foundation** - Kafka
- **Pivotal** - RabbitMQ
- **PostgreSQL Global Development Group** - PostgreSQL
- **React Team** - React y ecosystem
- **Comunidad Open Source** - Por todas las herramientas increíbles

---

## 📞 Soporte

Para reportar bugs, solicitar features o hacer preguntas:

- 🐛 **Issues**: [GitHub Issues](https://github.com/Tunkifloo/university-enrollment-system/issues)
- 💬 **Discussions**: [GitHub Discussions](https://github.com/Tunkifloo/university-enrollment-system/discussions)
- 📧 **Email**: nicolocisneros@gmail.com
- 📧 **Email**: jhoneiro12@hotmail.com

---

## 📈 Estadísticas del Proyecto

![GitHub stars](https://img.shields.io/github/stars/Tunkifloo/university-enrollment-system?style=social)
![GitHub forks](https://img.shields.io/github/forks/Tunkifloo/university-enrollment-system?style=social)
![GitHub watchers](https://img.shields.io/github/watchers/Tunkifloo/university-enrollment-system?style=social)

---

<div align="center">

**⭐ Si este proyecto te fue útil, considera darle una estrella en GitHub ⭐**

[⬆ Volver arriba](#sistema-de-matrículas-universitarias---arquitectura-de-microservicios)

---

**Hecho por [Tunkifloo](https://github.com/Tunkifloo)**

**Última actualización:** Noviembre 2025 | **Versión:** 1.0.0

</div>