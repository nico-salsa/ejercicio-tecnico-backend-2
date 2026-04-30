# ejercicio-tecnico-backend-2

Backend bancario en monorepo con dos microservicios Spring Boot:

- `customer-service`
- `account-service`

Este repositorio ya contiene:

- CRUD de clientes, cuentas y movimientos
- reglas de saldo disponible y saldo insuficiente
- reporte de estado de cuenta por cliente y rango de fechas
- pruebas unitarias, pruebas de integracion y CI con cobertura
- despliegue con Docker Compose
- script `BaseDatos.sql`
- coleccion Postman JSON para validar endpoints

## Repositorio publico

Ruta del repositorio:

- `https://github.com/nico-salsa/ejercicio-tecnico-backend-2`

## Stack

- Java 17
- Spring Boot 3
- Spring Data JPA
- PostgreSQL 17
- JUnit 5
- Mockito
- Docker
- Docker Compose
- Postman

## Arquitectura

### customer-service

Responsable de:

- Persona
- Cliente

Puerto:

- `8081`

Base de datos:

- `customer_service`

Endpoints:

- `GET /clientes`
- `GET /clientes/{id}`
- `POST /clientes`
- `PUT /clientes/{id}`
- `PATCH /clientes/{id}`
- `DELETE /clientes/{id}`

### account-service

Responsable de:

- Cuenta
- Movimiento
- Reporte de estado de cuenta

Puerto:

- `8082`

Base de datos:

- `account_service`

Endpoints:

- `GET /cuentas`
- `GET /cuentas/{id}`
- `POST /cuentas`
- `PUT /cuentas/{id}`
- `PATCH /cuentas/{id}`
- `DELETE /cuentas/{id}`
- `GET /movimientos`
- `GET /movimientos/{id}`
- `POST /movimientos`
- `PUT /movimientos/{id}`
- `PATCH /movimientos/{id}`
- `DELETE /movimientos/{id}`
- `GET /reportes?clienteId={clienteId}&fechaInicio={yyyy-MM-dd}&fechaFin={yyyy-MM-dd}`

## Modelo implementado

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
- `availableBalance`
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

## Entregables incluidos

### 1. Script de base de datos

Archivo:

- [BaseDatos.sql](BaseDatos.sql)

Contiene:

- creacion de `customer_service`
- creacion de `account_service`
- esquema de tablas
- relaciones
- datos semilla alineados con los casos de uso del enunciado

### 2. Coleccion Postman JSON

Archivo:

- [postman/export/ejercicio-tecnico-backend-2.postman_collection.json](postman/export/ejercicio-tecnico-backend-2.postman_collection.json)

Incluye requests para:

- clientes
- cuentas
- movimientos
- saldo insuficiente
- reportes

### 3. Contenerizacion

Archivos:

- [customer-service/Dockerfile](customer-service/Dockerfile)
- [account-service/Dockerfile](account-service/Dockerfile)
- [docker-compose.yml](docker-compose.yml)

## Forma recomendada de ejecucion desde cero

Si vas a clonar este repositorio en otra maquina y quieres levantar todo con el menor esfuerzo posible, usa Docker Compose.

### Requisitos minimos

Instala:

- Git
- Docker Desktop

No necesitas instalar Java ni PostgreSQL si usas Docker Compose.

### Paso a paso con Docker

1. Clona el repositorio:

```powershell
git clone https://github.com/nico-salsa/ejercicio-tecnico-backend-2.git
cd ejercicio-tecnico-backend-2
```

2. Construye y levanta todo:

```powershell
docker compose up --build
```

3. Espera a que los tres contenedores queden arriba:

- `banking-postgres`
- `customer-service`
- `account-service`

4. Verifica que los servicios respondan:

```powershell
curl http://localhost:8081/clientes
curl http://localhost:8082/cuentas
```

5. Para detenerlos:

```powershell
docker compose down
```

6. Si quieres reinicializar completamente la base y volver a ejecutar `BaseDatos.sql` desde cero:

```powershell
docker compose down -v
docker compose up --build
```

`-v` elimina el volumen de PostgreSQL. Sin eso, los datos persisten.

## Que hace Docker Compose

- levanta PostgreSQL 17 en `localhost:5432`
- monta `BaseDatos.sql` como script de inicializacion
- crea y arranca `customer-service`
- crea y arranca `account-service`

Credenciales por defecto usadas por Compose:

- usuario: `postgres`
- password: `postgres`

## Forma alternativa de ejecucion local

Usa esta opcion solo si prefieres correr los servicios fuera de Docker.

### Requisitos

Instala:

- Java 17
- PostgreSQL 17

### Crear bases con el script

1. Abre `psql`:

```powershell
& "C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -h localhost -p 5432 -d postgres
```

2. Ejecuta el script:

```sql
\i C:/ruta/completa/ejercicio-tecnico-backend-2/BaseDatos.sql
```

3. Sal de `psql`:

```sql
\q
```

### Levantar customer-service

```powershell
$env:CUSTOMER_SERVICE_DB_URL="jdbc:postgresql://localhost:5432/customer_service"
$env:CUSTOMER_SERVICE_DB_USERNAME="postgres"
$env:CUSTOMER_SERVICE_DB_PASSWORD="TU_PASSWORD"
cd customer-service
.\mvnw.cmd spring-boot:run
```

### Levantar account-service

```powershell
$env:ACCOUNT_SERVICE_DB_URL="jdbc:postgresql://localhost:5432/account_service"
$env:ACCOUNT_SERVICE_DB_USERNAME="postgres"
$env:ACCOUNT_SERVICE_DB_PASSWORD="TU_PASSWORD"
cd account-service
.\mvnw.cmd spring-boot:run
```

## Como importar y usar Postman

### Importar la coleccion

1. Abre Postman.
2. Usa `Import`.
3. Selecciona:

- `postman/export/ejercicio-tecnico-backend-2.postman_collection.json`

### Variables que usa la coleccion

La coleccion trae estas variables:

- `customerServiceUrl` = `http://localhost:8081`
- `accountServiceUrl` = `http://localhost:8082`
- `customerResourceId` = `1`
- `customerBusinessId` = `MM001`
- `accountId` = `1`
- `movementId` = `1`

Puedes editarlas directamente en la coleccion o usar un environment propio.

## Flujo recomendado de validacion manual

Este es el flujo mas util para validar el backend.

### 1. Clientes

Ejecuta:

- `POST /clientes`
- `GET /clientes`
- `GET /clientes/{id}`

Si creas un cliente nuevo, copia el `id` devuelto y actualiza `customerResourceId`.

### 2. Cuentas

Ejecuta:

- `POST /cuentas`
- `GET /cuentas`
- `GET /cuentas/{id}`

Si creas una cuenta nueva, copia el `id` devuelto y actualiza `accountId`.

### 3. Movimientos

Ejecuta:

- `POST /movimientos`
- `GET /movimientos`
- `GET /movimientos/{id}`

Si creas un movimiento nuevo, copia el `id` devuelto y actualiza `movementId`.

### 4. Saldo insuficiente

Ejecuta:

- `POST /movimientos saldo insuficiente`

Respuesta esperada:

```json
{
  "message": "Saldo no disponible",
  "error": "INSUFFICIENT_BALANCE",
  "status": 400
}
```

### 5. Reporte

Ejecuta:

- `GET /reportes`

Con los datos semilla, el reporte de `MM001` entre `2022-02-01` y `2022-02-10` debe reflejar los movimientos de Marianela Montalvo.

## Casos de uso cargados en BaseDatos.sql

### Clientes base

- Jose Lema
- Marianela Montalvo
- Juan Osorio

### Cuentas base

- `478758` Ahorro, Jose Lema, saldo inicial `2000`
- `225487` Corriente, Marianela Montalvo, saldo inicial `100`
- `495878` Ahorros, Juan Osorio, saldo inicial `0`
- `496825` Ahorros, Marianela Montalvo, saldo inicial `540`
- `585545` Corriente, Jose Lema, saldo inicial `1000`

### Movimientos base

- `478758` retiro `-575`, saldo `1425`
- `225487` deposito `600`, saldo `700`
- `495878` deposito `150`, saldo `150`
- `496825` retiro `-540`, saldo `0`

## Pruebas automatizadas

### customer-service

```powershell
cd customer-service
.\mvnw.cmd -B verify
```

### account-service

```powershell
cd account-service
.\mvnw.cmd -B verify
```

### Lo que valida la suite

- CRUD de clientes
- CRUD de cuentas
- CRUD de movimientos
- regla de saldo disponible
- error de saldo insuficiente
- reporte de estado de cuenta
- prueba unitaria de dominio para `Customer`
- prueba de integracion explicita para F6

## CI

El pipeline de GitHub Actions valida:

- estructura del repositorio
- build Java
- pruebas
- cobertura minima por modulo

## Git flow del proyecto

- `main`: rama protegida
- `develop`: rama de integracion
- `feature/*`: ramas de trabajo

Convenciones:

- commits en espanol con conventional commits
- commits atomicos
- push inmediato por commit

## Observaciones finales

- No se cambiaron las rutas actuales de los endpoints.
- El flujo principal recomendado para evaluacion es con Docker Compose.
- Si vas a usar una maquina nueva, sigue primero la seccion `Forma recomendada de ejecucion desde cero`.
