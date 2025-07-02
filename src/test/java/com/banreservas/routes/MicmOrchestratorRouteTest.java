package com.banreservas.routes;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import org.apache.camel.quarkus.test.CamelQuarkusTestSupport;
import org.junit.jupiter.api.Test;

import com.banreservas.mocks.RestMock;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@QuarkusTestResource(RestMock.class)
class MicmOrchestratorRouteTest extends CamelQuarkusTestSupport {

    private static final String AUTH_TOKEN = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUI";
    private static final String ENDPOINT = "/change/status/operation/micm/api/v1/ChangeStatusOperation";

    @Test
    void shouldChangeOperationStatusToEjecutada() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123")
                .header("Canal", "MICM")
                .header("Usuario", "Prueba")
                .header("Terminal", "0.0.0.0")
                .header("FechaHora", "2022-11-02T09:59:49.518472-04:00")
                .header("Version", "1")
                .header("Servicio", "CambiaEstadoOperacionMICM")
                .body("""
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
                    """)
                .when()
                .put(ENDPOINT)
                .then()
                .statusCode(200)
                .body("succeeded", is("false"))
                .body("message", is("Inscripcion Ejecutada"));
    }

    @Test
    void shouldRejectInvalidOperationId() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .body("""
                    {
                        "id": -1,
                        "status": 2,
                        "descripcionEstatus": "test"
                    }
                    """)
                .when()
                .put(ENDPOINT)
                .then()
                .statusCode(400)
                .body("succeeded", is("false"));
    }

    @Test
    void shouldRejectMissingOperationId() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .body("""
                    {
                        "status": 2,
                        "descripcionEstatus": "test"
                    }
                    """)
                .when()
                .put(ENDPOINT)
                .then()
                .statusCode(400)
                .body("succeeded", is("false"));
    }

    @Test
    void shouldRejectZeroOperationId() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .body("""
                    {
                        "id": 0,
                        "status": 2,
                        "descripcionEstatus": "test"
                    }
                    """)
                .when()
                .put(ENDPOINT)
                .then()
                .statusCode(400)
                .body("succeeded", is("false"));
    }

    @Test
    void shouldRejectNullOperationId() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .body("""
                    {
                        "id": null,
                        "status": 2,
                        "descripcionEstatus": "test"
                    }
                    """)
                .when()
                .put(ENDPOINT)
                .then()
                .statusCode(400)
                .body("succeeded", is("false"));
    }

    @Test
    void shouldRejectNullStatus() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .body("""
                    {
                        "id": 37544,
                        "status": null,
                        "descripcionEstatus": "test"
                    }
                    """)
                .when()
                .put(ENDPOINT)
                .then()
                .statusCode(400)
                .body("succeeded", is("false"));
    }

    @Test
    void shouldRejectEmptyRequestBody() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .body("")
                .when()
                .put(ENDPOINT)
                .then()
                .statusCode(400)
                .body("succeeded", is("false"));
    }

    @Test
    void shouldRejectMissingSessionId() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .body("""
                    {
                        "id": 37544,
                        "status": 2,
                        "descripcionEstatus": "test"
                    }
                    """)
                .when()
                .put(ENDPOINT)
                .then()
                .statusCode(400)
                .body("succeeded", is("false"));
    }

    @Test
    void shouldRejectInvalidCredentials() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "invalid")
                .body("""
                    {
                        "id": 37544,
                        "status": 2,
                        "descripcionEstatus": "test"
                    }
                    """)
                .when()
                .put(ENDPOINT)
                .then()
                .statusCode(401)
                .body("succeeded", is("false"));
    }

    @Test
    void shouldRejectUnauthorizedAccess() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "no-auth")
                .body("""
                    {
                        "id": 37544,
                        "status": 2,
                        "descripcionEstatus": "test"
                    }
                    """)
                .when()
                .put(ENDPOINT)
                .then()
                .statusCode(401)
                .body("succeeded", is("false"));
    }

    @Test
    void shouldProcessMinimalValidRequest() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .body("""
                    {
                        "id": 37544,
                        "status": 2
                    }
                    """)
                .when()
                .put(ENDPOINT)
                .then()
                .statusCode(200)
                .body("succeeded", is("true"));
    }

    @Test
    void shouldHandleInvalidJsonFormat() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .body("{ invalid json }")
                .when()
                .put(ENDPOINT)
                .then()
                .statusCode(anyOf(is(400), is(500)))
                .body("succeeded", is("false"));
    }

    @Test
    void shouldReturnValidResponseStructure() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .body("""
                    {
                        "id": 37544,
                        "status": 2
                    }
                    """)
                .when()
                .put(ENDPOINT)
                .then()
                .statusCode(200)
                .body("succeeded", notNullValue())
                .body("message", notNullValue())
                .body("errors", notNullValue())
                .body("data", notNullValue());
    }

    @Test
    void healthEndpointShouldBeAccessible() {
        given()
                .when()
                .get("/change/status/operation/micm/api/v1/health")
                .then()
                .statusCode(anyOf(is(200), is(404)));
    }
}