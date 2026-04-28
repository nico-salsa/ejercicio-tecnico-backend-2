# ejercicio-tecnico-backend-2

Placeholder inicial del repositorio.

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

## Estado

El repositorio queda preparado como línea base para iniciar la implementación.
