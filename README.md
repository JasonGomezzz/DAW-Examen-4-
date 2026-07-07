# ATS - Sistema de Reclutamiento (Evaluación 04)

Arquitectura de microservicios con Spring Boot 3.5.0 + Spring Cloud 2025.0.0 + Java 21.

## Estado actual

✅ eureka-server (puerto 8761)
✅ config-server (puerto 8888, modo nativo → lee de `config-repo/`)
✅ api-gateway (puerto 8080)
✅ ms-usuarios (puerto 8081) — registro/login, JWT, roles CANDIDATO/ADMIN
✅ ms-vacantes (puerto 8082) — CRUD de vacantes, acceso público a vacantes abiertas
✅ ms-postulaciones (puerto 8083) — Feign hacia ms-usuarios/ms-vacantes + Resilience4j (Circuit Breaker + Retry)
✅ `docker-compose.yml` con los 6 servicios + MySQL
✅ `database.sql` (schema + datos de prueba)
✅ `ATS-Postman-Collection.json` (colección completa, incluye caso de Circuit Breaker)

**Importante:** todo lo anterior está **codificado y compila sin errores**
(`mvn clean package -DskipTests` pasa limpio en los 3 microservicios de negocio), pero
**todavía no se ha verificado en ejecución real** (levantar los 6 servicios juntos, confirmar
registro en Eureka, correr la colección de Postman de punta a punta, disparar el Circuit
Breaker de verdad). Ver "Progreso y próximos pasos" más abajo para el detalle de qué falta.

## Progreso y próximos pasos

Lo desarrollado hasta ahora (código completo, compilado y verificado a nivel de build):
- Los 3 microservicios de negocio completos: entidades, capas, seguridad JWT, Feign,
  Resilience4j, manejo global de excepciones, Actuator, Dockerfile
- `docker-compose.yml` validado sintácticamente (`docker compose config`)
- `database.sql` con datos de prueba reales (passwords BCrypt generados de verdad, no inventados)
- Colección de Postman con JSON válido cubriendo CRUD, JWT, Feign entre servicios y Circuit Breaker

Pendiente (parte manual, siguiente sesión):
1. Levantar los 6 servicios en orden en IntelliJ (o `docker compose up --build`) y confirmar
   que los 6 aparecen `UP` en http://localhost:8761
2. Correr la colección de Postman completa y tomar capturas de evidencia
3. Probar el escenario de Circuit Breaker apagando `ms-vacantes` en caliente
4. Revisar si hace falta agregar tests automatizados
5. Decidir si se endurece la validación de `idUsuario` en `POST /api/postulaciones`
   (actualmente viaja en el body en vez de derivarse del JWT — ver limitaciones abajo)

## Arquitectura de cada microservicio de negocio

Los 3 microservicios de negocio (`ms-usuarios`, `ms-vacantes`, `ms-postulaciones`) siguen la misma
arquitectura en capas:

```
controller/   -> expone los endpoints REST, valida entrada con @Valid
service/      -> interfaz + implementación con la lógica de negocio y logs SLF4J
repository/   -> Spring Data JPA
entity/       -> entidades JPA
dto/          -> request/response, nunca se expone la entidad directamente
security/     -> JwtUtil + JwtAuthenticationFilter + SecurityConfig (valida el JWT emitido por ms-usuarios)
exception/    -> excepciones de negocio + GlobalExceptionHandler (@RestControllerAdvice)
```

`ms-postulaciones` añade además:
```
client/       -> OpenFeign clients hacia ms-usuarios y ms-vacantes
config/       -> FeignClientConfig (reenvía el header Authorization a las llamadas Feign)
```

### Autenticación JWT entre microservicios

`ms-usuarios` es el único que **emite** tokens JWT (login). `ms-vacantes` y `ms-postulaciones` solo
los **validan**, usando el mismo secreto compartido (`jwt.secret` en cada `config-repo/ms-*.yml`).
Cuando `ms-postulaciones` llama a `ms-usuarios`/`ms-vacantes` vía Feign, reenvía el header
`Authorization` de la petición original para que la llamada interna también quede autenticada.

### Resilience4j en ms-postulaciones

Las llamadas Feign a `ms-usuarios` y `ms-vacantes` están envueltas con `@Retry` + `@CircuitBreaker`
(paquete `io.github.resilience4j`, configurado en `config-repo/ms-postulaciones.yml`). Si el servicio
dependiente no responde (caído, timeout), tras agotar los reintentos y abrirse el circuito, se
devuelve `503` con el mensaje `"No fue posible consultar el servicio. Intente nuevamente."`. Un 404
real (el recurso no existe) no cuenta como fallo del circuito — se excluye explícitamente
(`ignoreExceptions: feign.FeignException$NotFound`) para no confundir "no existe" con "no disponible".

## Cómo correr todo (Windows + IntelliJ, sin Docker)

**Orden obligatorio de arranque:** eureka-server → config-server → api-gateway → (MySQL debe estar
corriendo) → ms-usuarios → ms-vacantes → ms-postulaciones. Espera a que cada uno termine de arrancar
(log `Started ...Application`) antes de lanzar el siguiente.

### 1. Eureka Server
1. Abre IntelliJ → `File > Open` → selecciona la carpeta `eureka-server`
2. Espera a que Maven descargue las dependencias (barra de progreso abajo)
3. Click derecho en `EurekaServerApplication.java` → `Run`
4. Verifica en el navegador: http://localhost:8761 → deberías ver el dashboard de Eureka

### 2. Config Server
1. Nueva ventana IntelliJ → carpeta `config-server`
2. Run sobre `ConfigServerApplication.java`
3. Verifica: http://localhost:8888/ms-usuarios/default debe devolver un JSON con `propertySources`
   que incluya `ms-usuarios.yml` y `application.yml`.

### 3. API Gateway
1. Nueva ventana IntelliJ → carpeta `api-gateway`
2. Run sobre `ApiGatewayApplication.java`
3. Verifica que aparezca registrado en Eureka y que http://localhost:8080/actuator/health responda `UP`.

### 4. MySQL
Verifica que el servicio MySQL esté corriendo en `localhost:3306` (usuario `root` / password `root`).
No necesitas crear las bases de datos a mano: cada microservicio las crea con
`createDatabaseIfNotExist=true`. Si prefieres poblarlas manualmente o restaurar datos de prueba,
ejecuta `database.sql` en MySQL Workbench.

### 5. ms-usuarios, 6. ms-vacantes, 7. ms-postulaciones
Mismo patrón: nueva ventana IntelliJ → carpeta del proyecto → Run sobre la clase `*Application.java`.
Respeta el orden (postulaciones depende de que usuarios y vacantes ya estén arriba y registrados en
Eureka, porque las valida vía Feign). Verifica cada uno en http://localhost:8761 y en su
`/actuator/health` (8081, 8082, 8083).

## Cómo correr todo con Docker

Desde la raíz del repo:

```bash
docker compose up --build
```

Esto levanta MySQL + los 6 servicios en el orden correcto usando `depends_on` con healthchecks
(nadie arranca hasta que su dependencia responde saludable). Puertos expuestos: 8761, 8888, 8080,
8081, 8082, 8083 y 3306.

Para probar el Circuit Breaker con Docker: `docker stop ms-vacantes` y repite la petición de
"Crear postulación" en Postman — verás el fallback `503`.

## Colección de Postman

Importa `ATS-Postman-Collection.json`. Todas las peticiones de negocio pasan por el API Gateway
(`http://localhost:8080`). Los tests de cada request guardan automáticamente en variables de
colección el `token` (candidato/admin) y los IDs creados, para encadenar las siguientes peticiones.
Incluye:
- Registro/login/consulta de usuarios (JWT)
- CRUD de vacantes con verificación de rol ADMIN
- Creación de postulaciones (comunicación Feign real entre microservicios)
- Un caso 403 (acceso sin rol ADMIN) y un caso 404 (vacante inexistente)
- Un caso de Circuit Breaker: instrucciones para apagar `ms-vacantes` y ver el fallback

## Checklist de la evaluación

Código implementado (verificado que compila) / Verificado en ejecución real:

- [x] código / [ ] ejecución — Arquitectura de microservicios (6 proyectos independientes)
- [x] código / [ ] ejecución — Eureka: los 6 se registran correctamente
- [x] código / [ ] ejecución — API Gateway enruta a los 3 microservicios de negocio (`/api/usuarios/**`, `/api/vacantes/**`, `/api/postulaciones/**`)
- [x] código / [ ] ejecución — Config Server centralizado (puerto, nombre app, BD, mensajes personalizados en `config-repo/`)
- [x] código / [ ] ejecución — Resilience4j: Circuit Breaker + Retry en ms-postulaciones (Feign hacia ms-usuarios y ms-vacantes)
- [x] código / [ ] ejecución — Actuator (`/actuator/health`, `/actuator/info`, `/actuator/metrics`) expuesto en los 6 servicios
- [x] código — Cada microservicio de negocio tiene su propia BD (`db_usuarios`, `db_vacantes`, `db_postulaciones`, sin tablas compartidas)
- [x] código — Dockerfile en cada proyecto (multi-stage `maven:3.9-eclipse-temurin-21` → `eclipse-temurin:21-jre-alpine`)
- [x] código (`docker compose config` validado) / [ ] ejecución — `docker-compose.yml` levanta los 6 servicios + MySQL con orden de arranque garantizado
- [x] código (JSON válido) / [ ] ejecución — Postman cubre CRUD, comunicación entre microservicios, acceso vía Gateway y Circuit Breaker

## Limitaciones conocidas

- `POST /api/postulaciones` recibe `idUsuario` en el body en vez de derivarlo del JWT: un
  candidato autenticado podría, en teoría, enviar el id de otro usuario. Pendiente de decidir
  si se endurece.
- No hay tests automatizados (unitarios/integración) en ninguno de los 3 microservicios.
- `jwt.secret` está en texto plano en `config-repo/ms-*.yml` (duplicado en los 3 archivos, ya
  que `config-repo/application.yml` está fuera de scope). Aceptable para una evaluación local,
  no para producción.

## Repositorio

```
ats-microservices/
├── eureka-server/
├── config-server/
├── config-repo/
├── api-gateway/
├── ms-usuarios/
├── ms-vacantes/
├── ms-postulaciones/
├── docker-compose.yml
├── database.sql
├── ATS-Postman-Collection.json
└── README.md
```
