# ms-orq-cambia-estado-operacion-micm

Este proyecto es un microservicio Json funcionando como orquestador encargado de consumir el servicio **CAMBIAESTADOOPERACIONMICM** y **LOGINMICM** de legado.

Si quieres aprender más de Quarkus, por favor visita el [sitio web oficial](https://quarkus.io/).

## Ejecución de la aplicación en modo de desarrollo

Puedes ejecutar tu aplicación en modo de desarrollo, lo que permite la codificación en vivo, utilizando el siguiente comando:

```shell script
./mvnw compile quarkus:dev
```

> **_NOTA:_**  Quarkus incluye una interfaz Dev UI que está disponible solo en modo de desarrollo en http://localhost:8080/q/dev/.

## Empaquetado y ejecución de la aplicación

La aplicación se puede empaquetar utilizando:

```shell script
./mvnw package
```
Este comando genera el archivo quarkus-run.jar en el directorio target/quarkus-app/. Ten en cuenta que no es un über-jar, ya que las dependencias se copian en el directorio target/quarkus-app/lib/.

La aplicación se puede ejecutar utilizando:

```shell script
java -jar target/quarkus-app/quarkus-run.jar
```
Si deseas construir un über-jar, ejecuta el siguiente comando:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

La aplicación empaquetada como un über-jar se puede ejecutar utilizando:

```shell script
java -jar target/*-runner.jar
```

## Creación de un ejecutable nativo

Puedes crear un ejecutable nativo utilizando:

```shell script
./mvnw package -Dnative
```

O, si no tienes GraalVM instalado, puedes construir el ejecutable nativo en un contenedor utilizando:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

Luego, puedes ejecutar tu ejecutable nativo con:

```shell script
./target/ms-orq-cambia-estado-operacion-micm-1.0.0-SNAPSHOT-runner
```

Para obtener más información sobre cómo construir ejecutables nativos, consulta https://quarkus.io/guides/maven-tooling.

## Guías Relacionadas

- **Quarkus CXF:** Capacidades principales para implementar clientes SOAP y servicios JAX-WS.
- **RESTEasy Reactive:** Implementación de Jakarta REST utilizando procesamiento en tiempo de compilación y Vert.x.
- **SmallRye OpenTracing:** Trazado de servicios con SmallRye OpenTracing.
- **SmallRye OpenAPI:** Documenta tus APIs REST con OpenAPI, incluye Swagger UI.
- **Jacoco - Cobertura de Código:** Soporte para cobertura de pruebas con Jacoco.
- **SmallRye Health:** Monitorea la salud de tu aplicación usando SmallRye Health.
- **Micrometer Metrics:** Instrumenta el tiempo de ejecución y tu aplicación con métricas dimensionales usando Micrometer.

## Endpoints

**POST:** /change/status/operation/micm/api/v1/ChangeStatusOperation

**Descripción:** Este endpoint permite obtener la información de clientes banreservas.

**GET:** /change/status/operation/micm/api/v1/health

**Descripción:** Monitorea la salud de tu aplicación utilizando SmallRye Health.

**GET:** /change/status/operation/micm/api/v1/swagger-ui

**Descripción:** Para acceder a Swagger UI y ver la documentación de las apis.

**Códigos de Respuesta:**

    200 OK - Exitoso: Éxito en la operación.
    400 Bad Request - BAD_REQUEST: Solicitud incorrecta.
    401 Unauthorized - UNAUTHOTIZE: No autorizado.
    500 Internal Server Error - INTERNAL_SERVER_ERROR: Error interno del servidor.
    503 BAD_GATEWAY: Error de conexión al servicio.
    500 UNEXPECTED_ERROR: Error interno inesperado del servidor.

## Integración con Servicios Externos

El servicio invoca dos servicio REST llamado ms-log-in-micm y ms-cambia-estado-operacion-micmm, el de login para obtener el token para autenticarse en cambia estado operacion micm y poder realizar embargos, ejecuciones y cancelaciones.

##### Variables de Entorno/Secrets

La integración con el servicio REST ms-orq-cambia-estado-operacion-micm requiere la siguiente configuración para consumo:

```properties
micm.login.url=https://ms-login-micm-dev.apps.az-aro-dev.banreservas.com/api/v1/login-micm
micm.login.email=ilozada@banreservas.com
micm.login.password=Inicio.01
micm.login.timeout=30000

micm.change.operation.status.url=https://ms-cambia-estado-operacion-dev.apps.az-aro-dev.banreservas.com/api/v1/cambia-estado-operacion
micm.change.operation.status.timeout=1500
```

### Ejemplo de Solicitud (Request):

```curl Cancelación
curl --location --request PUT 'http://localhost:8080/change/status/operation/micm/api/v1/ChangeStatusOperation' \
--header 'Content-Type: application/json' \
--header 'sessionId: 123' \
--header 'Version: 1' \
--header 'Servicio: CambiaEstadoOperacionMICM' \
--header 'FechaHora: 2022-11-02T09:59:49.518472-04:00' \
--header 'Terminal: 0.0.0.0' \
--header 'Usuario: Prueba' \
--header 'Canal: MICM' \
--header 'Authorization: Bearer eyJ' \
--header 'Cookie: 846a2fbdbcf444064ba9a2e854131af7=8e7111252b675297b7289520ec5c2d3b; 9736b9f74f30f14c9d506a655fea2fac=46fbb85d3974795fa60cedff14efd07e' \
--data '{
    "id": 78904,
    "status": 2,
    "descripcionEstatus": "string"
}'
```

```curl Embargos
curl --location --request PUT 'http://localhost:8080/change/status/operation/micm/api/v1/ChangeStatusOperation' \
--header 'Content-Type: application/json' \
--header 'sessionId: 123' \
--header 'Version: 1' \
--header 'Servicio: CambiaEstadoOperacionMICM' \
--header 'FechaHora: 2022-11-02T09:59:49.518472-04:00' \
--header 'Terminal: 0.0.0.0' \
--header 'Usuario: Prueba' \
--header 'Canal: MICM' \
--header 'Authorization: Bearer EYJ' \
--header 'Cookie: 846a2fbdbcf444064ba9a2e854131af7=8e7111252b675297b7289520ec5c2d3b; 9736b9f74f30f14c9d506a655fea2fac=46fbb85d3974795fa60cedff14efd07e; 846a2fbdbcf444064ba9a2e854131af7=8e7111252b675297b7289520ec5c2d3b; 9736b9f74f30f14c9d506a655fea2fac=46fbb85d3974795fa60cedff14efd07e' \
--data '{
    "id": 403528,
    "status": 3,
    "estadoEmbargoNoDispAdmjud": "string",
    "idTipoEmbargo": 1,
    "ejecucionDescripcionMontofijado": 0,
    "ejecucionDescripcionMontoSaldo": 0,
    "ejecucionCostaProcesales": 0,
    "ejecucionDescripcionObligaciongarantizada": "string",
    "ejecucionDescripcionIncumplimientoDeudor": "string",
    "descripcionEstatus": "string"
}'
```

```curl Ejecución
curl --location --request PUT 'http://localhost:8080/change/status/operation/micm/api/v1/ChangeStatusOperation' \
--header 'Content-Type: application/json' \
--header 'sessionId: 123' \
--header 'Version: 1' \
--header 'Servicio: CambiaEstadoOperacionMICM' \
--header 'FechaHora: 2022-11-02T09:59:49.518472-04:00' \
--header 'Terminal: 0.0.0.0' \
--header 'Usuario: Prueba' \
--header 'Canal: MICM' \
--header 'Authorization: Bearer EYJ' \
--header 'Cookie: 846a2fbdbcf444064ba9a2e854131af7=8e7111252b675297b7289520ec5c2d3b; 9736b9f74f30f14c9d506a655fea2fac=46fbb85d3974795fa60cedff14efd07e' \
--data '{
    "id": 403528,
    "status": 4,
    "tipoConciliacion": 1,
    "ejecucionDescripcionMontofijado": 0,
    "ejecucionDescripcionMontoSaldo": 0,
    "ejecucionCostaProcesales": 0,
    "ejecucionDescripcionObligaciongarantizada": "test1",
    "ejecucionDescripcionIncumplimientoDeudor": "0"
}'
```

### Encabezados de la Solicitud

- **sessionId:** 123
- **Authorization:** Bearer eyj
- **Version:** 1
- **Servicio:** CambiaEstadoOperacionMICM
- **FechaHora:** 2022-11-02T09:59:49.518472-04:00
- **Terminal:** 0.0.0.0
- **Usuario:** Prueba
- **Canal:** MICM

## Cuerpo de la Solicitud (Request Body)

```json Cancelación
   {
    "id": 78904,
    "status": 2,
    "descripcionEstatus": "string"
}
```

```json Embargos
  {
    "id": 403528,
    "status": 3,
    "estadoEmbargoNoDispAdmjud": "string",
    "idTipoEmbargo": 1,
    "ejecucionDescripcionMontofijado": 0,
    "ejecucionDescripcionMontoSaldo": 0,
    "ejecucionCostaProcesales": 0,
    "ejecucionDescripcionObligaciongarantizada": "string",
    "ejecucionDescripcionIncumplimientoDeudor": "string",
    "descripcionEstatus": "string"
}
```

```json Ejecución
 {
    "id": 403528,
    "status": 4,
    "tipoConciliacion": 1,
    "ejecucionDescripcionMontofijado": 0,
    "ejecucionDescripcionMontoSaldo": 0,
    "ejecucionCostaProcesales": 0,
    "ejecucionDescripcionObligaciongarantizada": "test1",
    "ejecucionDescripcionIncumplimientoDeudor": "0"
}
```

## Documentación de Respuestas

##### Códigos de Respuesta

La API de ms-orq-cambia-estado-operacion-micm utiliza varios códigos de respuesta para indicar el estado de la solicitud. A continuación se describen los códigos de respuesta específicos utilizados por la API:

- **Código de Respuesta:** 200
- **Descripción:** Success.

```json
 {
    "succeeded": "false",
    "message": "Inscripcion Ejecutada",
    "errors": {},
    "data": {}
}
```

- **Código de Respuesta:** 400
- **Descripción:** Bad Request, uno o más encabezados parametros están vacíos.

```json
  {
    "succeeded": "false",
    "message": "ID es requerido y debe ser mayor a 0",
    "errors": {},
    "data": {}
}
```

- **Código de Respuesta:** 401
- **Descripción:** Unauthorized, autenticación fallida.

```json
  {
    "succeeded": "false",
    "message": "Credenciales inválidas o Token Expirado",
    "errors": {},
    "data": {}
}
```

- **Código de Respuesta:** 500
- **Descripción:** Error interno del servidor.

```json
  {
    "succeeded": "false",
    "message": "Error interno en servicio de cambio estado operacion",
    "errors": {},
    "data": {}
}
```

- **Código de Respuesta:** 400
- **Descripción:** Uno o más encabezados necesarios están vacíos.
```xml
{
    "succeeded": "false",
    "message": "Header sessionId cannot be null",
    "errors": {},
    "data": {}
}
```

Cobertura de pruebas unitarias:
![alt text](coverage.png)

## Configuración de Quarkus

##### Exclusión de Clases en Jacoco

Para excluir ciertas clases del reporte de cobertura de código con Jacoco, utiliza la siguiente configuración:

```properties
quarkus.jacoco.excludes=**/org/tempuri/*.class,**/banreservas/util/**,**/banreservas/model/**,**/banreservas/exceptions/**,**/banreservas/processors/**
```
##### Configuración de swagger-ui

Para documentar tus API REST utilizando swagger-ui y hacerlas accesibles:

```properties
quarkus.swagger-ui.path=change/status/operation/micm/api/v1/swagger-ui
```

##### Configuración de Health Check

Monitorea la salud de tu aplicación utilizando SmallRye Health:

```properties
quarkus.smallrye-health.root-path=change/status/operation/micm/api/v1/health
```

##### Configuración de LogAppender (Auditoría)

Para configurar de manera correcta la auditoría a nivel de logs requerimos:

```properties
log.appender.applicationName=ms-orq-cambia-estado-operacion-micm
log.appender.urlService=https://ms-audit-receiver-dev.apps.az-aro-dev.banreservas.com/audit/api/v1/auditReceiver
log.appender.ofuscado=NumeroProducto[0:4]
log.appender.queue=audit-queue
```

## Author

* **Domingo Ruiz** - *Desarrollador* - [C-DJRuiz@banreservas.com]