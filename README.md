# ejercicio-tecnico-backend-2

Backend bancario en monorepo con dos microservicios Spring Boot.

## Contexto base

- Backend en Java Spring Boot.
- Arquitectura de 2 microservicios.
- Base de datos relacional.
- Postman v9.13.2 como validador de API.

## Reglas de trabajo

- Se usará git flow.
- `main` será la rama protegida de producción.
- `develop` será la rama de integración.
- El trabajo funcional se hará en ramas `feature/*` creadas desde `develop`.
- La rama `main` deberá permanecer protegida y solo aceptar cambios mediante merge revisado.
- Los commits deben seguir conventional commits en español.
- Los commits deben ser extremadamente atómicos para facilitar revisión previa al merge.
- Por cada commit se debe realizar su `push` correspondiente.

## Arquitectura

- El repositorio se organiza como monorepo con dos aplicaciones Spring Boot independientes:
  - `customer-service`
  - `account-service`
- Cada servicio mantiene una estructura simple por capas:
  - `api`
  - `application`
  - `domain`
  - `infrastructure`
- La explicación funcional y arquitectónica debe vivir en este `README.md`.
- Los comentarios en código fuente deben usarse solo cuando mejoren legibilidad de un bloque que no se explique bien por nombres y estructura.

## Estructura

```text
.
├── .github/
├── customer-service/
└── account-service/
```

## Microservicios

### customer-service

- Responsable de `Persona` y `Cliente`.
- Puerto por defecto: `8081`.
- Base de datos por defecto: `customer_service`.
- Variables de entorno:
  - `CUSTOMER_SERVICE_PORT`
  - `CUSTOMER_SERVICE_DB_URL`
  - `CUSTOMER_SERVICE_DB_USERNAME`
  - `CUSTOMER_SERVICE_DB_PASSWORD`

### account-service

- Responsable de `Cuenta` y `Movimiento`.
- Puerto por defecto: `8082`.
- Base de datos por defecto: `account_service`.
- Variables de entorno:
  - `ACCOUNT_SERVICE_PORT`
  - `ACCOUNT_SERVICE_DB_URL`
  - `ACCOUNT_SERVICE_DB_USERNAME`
  - `ACCOUNT_SERVICE_DB_PASSWORD`

## CI

- El pipeline de CI vive en `.github/workflows/ci.yml`.
- Los checks esperados para el ruleset son:
  - `repository-validation`
  - `java-validation`
  - `container-validation`
- El workflow corre en `pull_request` hacia `develop` y `main`.
- El workflow corre en `push` sobre `feature/*`, `develop` y `main`.
- En el estado actual del repositorio, `java-validation` y `container-validation` están diseñados para omitir su ejecución de build si todavía no existen módulos Java, Dockerfiles o `docker-compose.yml`.
- Cuando existan `customer-service` y `account-service`, el check `java-validation` ejecutará build y pruebas usando `mvnw` o `gradlew` por módulo.
- Cuando existan Dockerfiles y `docker-compose.yml`, el check `container-validation` validará build de imágenes y sintaxis de compose.

## Estado actual

- Ya existe pipeline de CI para validar estructura, módulos Java y artefactos de contenedores.
- La primera funcionalidad en implementación es el CRUD base para `clientes`, `cuentas` y `movimientos`.
