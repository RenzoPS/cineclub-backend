# CineClub Backend

Backend desarrollado con Spring Boot para la gestión de un sistema de cine club. Este proyecto permite administrar películas, salas, funciones y reservas de asientos.

## Descripción

CineClub Backend es una API REST que proporciona funcionalidades para:

- Gestión de películas
- Administración de salas de cine
- Programación de funciones (screenings)
- Sistema de asientos y reservas
- Autenticación y autorización de usuarios

## Dependencias

- **Java 21**
- **Spring Boot 3.5.6**
- **Spring Web** - Framework web para crear APIs REST
- **Spring Data JPA** - Persistencia de datos con JPA/Hibernate
- **Spring Validation** - Validación de datos
- **Spring Security** - Autenticación y autorización
- **Spring Test** - Testing con JUnit y Mockito
- **PostgreSQL Driver** - Conector para base de datos PostgreSQL
- **MapStruct** - Mapeo automático entre objetos
- **Lombok** - Reducción de código boilerplate
- **JWT** (API, Implementation, Jackson) - Manejo de tokens JWT
- **SpringDoc OpenAPI 2.8.13** - Documentación automática de API con Swagger UI
- **Maven** - Gestión de dependencias y build

## Requisitos Previos

- Java 21 o superior
- Maven 3.6+
- Docker y Docker Compose

## Variables de Entorno

Crea un archivo `.env` en la raíz del proyecto con las siguientes variables:

```env
# Puerto del servidor
SERVER_PORT=tu_puerto_sv

# Configuración de PostgreSQL
POSTGRES_USER=tu_usuario
POSTGRES_PASSWORD=tu_contraseña
POSTGRES_DB=tu_db
POSTGRES_PORT=tu_puerto_db
POSTGRES_URL=tu_url
# JWT=tu_secret_token (DESHABILITADO)

```
### Descripción de Variables

| Variable | Descripción |
|----------|-------------|
| `SERVER_PORT` | Puerto en el que corre la aplicación Spring Boot |
| `POSTGRES_USER` | Usuario de PostgreSQL |
| `POSTGRES_PASSWORD` | Contraseña de PostgreSQL |
| `POSTGRES_DB` | Nombre de la base de datos |
| `POSTGRES_PORT` | Puerto de PostgreSQL |
| `POSTGRES_URL` | URL de conexión JDBC a PostgreSQL |

## Instalación y Ejecución

1. **Clona el repositorio**
   ```bash
   git clone https://github.com/RenzoPS/cineclub-backend
   cd cineclub-backend
   ```

2. **Configura las variables de entorno**
   ```bash
   #Editar archivo .env previamente creado
   ```

3. **Inicia la base de datos con Docker**
   ```bash
   docker-compose up -d
   ```

4. **Compila y ejecuta la aplicación**
   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```
### En Windows

Usa `mvnw.cmd` en lugar de `./mvnw`:
```cmd
mvnw.cmd clean install
mvnw.cmd spring-boot:run
```
## Documentación de la API

Una vez que la aplicación esté corriendo, puedes acceder a la documentación interactiva de Swagger en:

```
http://localhost:8080/swagger-ui/index.html
```
## Ejecutar Tests

```bash
./mvnw test
```
## Estructura del Proyecto

```
src/main/java/com/cineclub/
├── controllers/     # Controladores REST
├── dtos/           # Data Transfer Objects
├── entities/       # Entidades JPA (Movie, Room, Screening, Seat, User)
├── mappers/        # Mappers de MapStruct
├── repositories/   # Repositorios JPA
├── security/       # Configuración de seguridad
├── services/       # Lógica de negocio
└── specifications/ # Especificaciones para queries dinámicas
```
## Base de Datos

El proyecto utiliza PostgreSQL como base de datos. La configuración de JPA está establecida en modo `create-drop`, lo que significa que:

- Las tablas se crean automáticamente al iniciar la aplicación
- Las tablas se eliminan al detener la aplicación

**Nota:** Para que los datos en la base de datos se guarden y no se eliminen en cada renicio cambiar a `update` en `application.yml`.

## Contribución

Este proyecto fue desarrollado como parte de una pasantía.

