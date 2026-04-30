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
  - `GET /reportes?clienteId={clienteId}&fechaInicio={yyyy-MM-dd}&fechaFin={yyyy-MM-dd}`

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
- `customerId`
- `customerName`
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

La evidencia automatizada de F6 en `account-service` es la prueba `shouldCoverF6WithEndToEndBankingFlow`, que valida por HTTP la creacion de cuenta, el registro de movimiento y la verificacion del resultado observable final.

## Postman

- La colección editable del proyecto vive en `postman/`.
- `.postman/` es metadata local del cliente de Postman y no forma parte del entregable del repositorio.
- El environment local base es `postman/environments/local.environment.yaml`.
- Variables principales:
  - `customerServiceUrl`: `http://localhost:8081`
  - `accountServiceUrl`: `http://localhost:8082`
  - `customerResourceId`: id numérico interno de `/clientes/{id}`
  - `accountId`: id numérico interno de `/cuentas/{id}`
  - `movementId`: id numérico interno de `/movimientos/{id}`
- `customerResourceId` no es lo mismo que el campo de negocio `customerId`.
- Flujo recomendado de prueba manual:
  - ejecutar `POST /clientes` y copiar el `id` devuelto en `customerResourceId`
  - ejecutar `POST /cuentas` enviando `customerId` y `customerName`, y copiar el `id` devuelto en `accountId`
  - ejecutar `POST /movimientos` usando ese `accountId`
  - ejecutar `GET /reportes` usando `customerBusinessId` y el rango de fechas deseado
  - usar luego los requests `GET`, `PUT`, `PATCH` y `DELETE` sobre esos ids
- La colección actual está alineada con los endpoints reales implementados:
  - `GET|POST|PUT|PATCH|DELETE /clientes`
  - `GET|POST|PUT|PATCH|DELETE /cuentas`
  - `GET|POST|PUT|PATCH|DELETE /movimientos`
  - `GET /reportes`

## Movimientos y saldo

- `account-service` calcula internamente el saldo resultante de cada movimiento.
- Un valor positivo en `amount` representa un depósito.
- Un valor negativo en `amount` representa un retiro.
- La cuenta mantiene `availableBalance` como saldo disponible actual persistido.
- Cada movimiento persiste su `balance` como saldo resultante posterior a la transacción.
- El cliente ya no debe enviar `balance` como fuente de verdad para `POST /movimientos`.

 - Si un retiro o una actualizaciÃ³n de movimiento deja el saldo resultante por debajo de cero, la API responde `400 Bad Request`.
 - El body esperado para ese caso es:

```json
{
  "message": "Saldo no disponible",
  "error": "INSUFFICIENT_BALANCE",
  "status": 400
}
```

 - Cuando la operaciÃ³n es rechazada por saldo insuficiente, no se persiste el movimiento ni se modifica `availableBalance`.

## Reportes

- `account-service` expone `GET /reportes?clienteId={clienteId}&fechaInicio={yyyy-MM-dd}&fechaFin={yyyy-MM-dd}`.
- El reporte retorna una lista JSON plana, una fila por movimiento dentro del rango consultado.
- Cada fila incluye `fecha`, `cliente`, `numeroCuenta`, `tipo`, `saldoInicial`, `estado`, `movimiento` y `saldoDisponible`.
- El filtro usa `customerId` como identificador de negocio del cliente.
- Si no hay movimientos en el rango, la API responde `200` con lista vacia.
- Si `fechaInicio` es posterior a `fechaFin`, la API responde `400` con error `REPORT_QUERY_INVALID`.

## Estado actual

- Ya existe pipeline de CI para validar estructura, módulos Java y artefactos de contenedores.
- La primera funcionalidad implementada es el CRUD base para `clientes`, `cuentas` y `movimientos`.
- El repositorio ya exige pruebas automatizadas y cobertura mínima por módulo para sostener la calidad del backend.
