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
- Endpoints:
  - `GET /clientes`
  - `GET /clientes/{id}`
  - `POST /clientes`
  - `PUT /clientes/{id}`
  - `PATCH /clientes/{id}`
  - `DELETE /clientes/{id}`

### account-service

- Responsable de `Cuenta` y `Movimiento`.
- Puerto por defecto: `8082`.
- Base de datos por defecto: `account_service`.
- Variables de entorno:
  - `ACCOUNT_SERVICE_PORT`
  - `ACCOUNT_SERVICE_DB_URL`
  - `ACCOUNT_SERVICE_DB_USERNAME`
  - `ACCOUNT_SERVICE_DB_PASSWORD`
- Endpoints de cuentas:
  - `GET /cuentas`
  - `GET /cuentas/{id}`
  - `POST /cuentas`
  - `PUT /cuentas/{id}`
  - `PATCH /cuentas/{id}`
  - `DELETE /cuentas/{id}`
- Endpoints de movimientos:
  - `GET /movimientos`
  - `GET /movimientos/{id}`
  - `POST /movimientos`
  - `PUT /movimientos/{id}`
  - `PATCH /movimientos/{id}`
  - `DELETE /movimientos/{id}`

## Modelo inicial

### Persona

- `id`
- `name`
- `gender`
- `age`
- `identification`
- `address`
- `phone`

### Cliente

- hereda de `Persona`
- `customerId`
- `password`
- `status`

### Cuenta

- `id`
- `accountNumber`
- `accountType`
- `initialBalance`
- `status`

### Movimiento

- `id`
- `movementDate`
- `movementType`
- `amount`
- `balance`
- `accountId`

## Ejecución local

### customer-service

```powershell
cd customer-service
.\mvnw.cmd spring-boot:run
```

### account-service

```powershell
cd account-service
.\mvnw.cmd spring-boot:run
```

## Validación local

```powershell
cd customer-service
.\mvnw.cmd verify

cd ..\account-service
.\mvnw.cmd verify
```

## CI

- El pipeline de CI vive en `.github/workflows/ci.yml`.
- Los checks esperados para el ruleset son:
  - `repository-validation`
  - `java-validation`
  - `container-validation`
- El workflow corre en `pull_request` hacia `develop` y `main`.
- El workflow corre en `push` sobre `feature/*`, `develop` y `main`.
- En el estado actual del repositorio, `container-validation` está diseñado para omitir su ejecución si todavía no existen Dockerfiles o `docker-compose.yml`.
- El check `java-validation` ejecuta `./mvnw -B verify` por módulo.
- `verify` compila, ejecuta pruebas unitarias e integración y valida cobertura con JaCoCo.
- Cada microservicio exige una cobertura mínima de líneas superior al 80%.
- Si falla alguna prueba o la cobertura queda por debajo del umbral, el pipeline falla.
- Cuando existan Dockerfiles y `docker-compose.yml`, el check `container-validation` validará build de imágenes y sintaxis de compose.

## Pruebas y cobertura

- `customer-service` incluye pruebas unitarias de servicio y pruebas de integración HTTP/JPA para `/clientes`.
- `account-service` incluye pruebas unitarias de servicio y pruebas de integración HTTP/JPA para `/cuentas` y `/movimientos`.
- Ambos módulos validan escenarios felices y no felices de recurso inexistente, payload inválido y manejo de errores.
- Los reportes HTML de cobertura se generan en:
  - `customer-service/target/site/jacoco/index.html`
  - `account-service/target/site/jacoco/index.html`

## Estado actual

- Ya existe pipeline de CI para validar estructura, módulos Java y artefactos de contenedores.
- La primera funcionalidad implementada es el CRUD base para `clientes`, `cuentas` y `movimientos`.
- El repositorio ya exige pruebas automatizadas y cobertura mínima por módulo para sostener la calidad del backend.
