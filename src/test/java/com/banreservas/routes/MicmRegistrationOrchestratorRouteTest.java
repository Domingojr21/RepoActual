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
class MicmRegistrationOrchestratorRouteTest extends CamelQuarkusTestSupport {

    private static final String AUTH_TOKEN = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUI";
    private static final String ENDPOINT = "/registration/micm/api/v1/registrationInscription";

    @Test
    void shouldReturn400WhenRequestBodyIsNull() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .header("Canal", "MICM")
                .header("Usuario", "TestUser")
                .header("Terminal", "127.0.0.1")
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("succeeded", is("false"))
                .body("message", is("Request body es requerido"))
                .body("errors", notNullValue())
                .body("data", nullValue());
    }

    @Test
    void shouldReturn400WhenRequestBodyIsEmpty() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .header("Canal", "MICM")
                .header("Usuario", "TestUser")
                .header("Terminal", "127.0.0.1")
                .body("")
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("succeeded", is("false"))
                .body("message", is("Request body es requerido"));
    }

    @Test
    void shouldReturn400WhenOperacionesIsNull() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .header("Canal", "MICM")
                .header("Usuario", "TestUser")
                .header("Terminal", "127.0.0.1")
                .body(getRequestWithoutOperaciones())
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("succeeded", is("false"))
                .body("message", containsString("operaciones"))
                .body("errors", notNullValue())
                .body("data", nullValue());
    }

    @Test
    void shouldReturn400WhenBienesIsNull() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .header("Canal", "MICM")
                .header("Usuario", "TestUser")
                .header("Terminal", "127.0.0.1")
                .body(getRequestWithoutBienes())
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("succeeded", is("false"))
                .body("message", containsString("bienes"))
                .body("errors", notNullValue())
                .body("data", nullValue());
    }

    @Test
    void shouldReturn400WhenBienesIsEmpty() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .header("Canal", "MICM")
                .header("Usuario", "TestUser")
                .header("Terminal", "127.0.0.1")
                .body(getRequestWithEmptyBienes())
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("succeeded", is("false"))
                .body("message", containsString("bienes"))
                .body("errors", notNullValue())
                .body("data", nullValue());
    }

    @Test
    void shouldReturn400WhenDeudoresIsNull() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .header("Canal", "MICM")
                .header("Usuario", "TestUser")
                .header("Terminal", "127.0.0.1")
                .body(getRequestWithoutDeudores())
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("succeeded", is("false"))
                .body("message", containsString("deudores"))
                .body("errors", notNullValue())
                .body("data", nullValue());
    }

    @Test
    void shouldReturn400WhenDeudoresIsEmpty() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .header("Canal", "MICM")
                .header("Usuario", "TestUser")
                .header("Terminal", "127.0.0.1")
                .body(getRequestWithEmptyDeudores())
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("succeeded", is("false"))
                .body("message", containsString("deudores"))
                .body("errors", notNullValue())
                .body("data", nullValue());
    }

    @Test
    void shouldReturn400WhenAcreedoresIsNull() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .header("Canal", "MICM")
                .header("Usuario", "TestUser")
                .header("Terminal", "127.0.0.1")
                .body(getRequestWithoutAcreedores())
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("succeeded", is("false"))
                .body("message", containsString("acreedores"))
                .body("errors", notNullValue())
                .body("data", nullValue());
    }

    @Test
    void shouldReturn400WhenAcreedoresIsEmpty() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .header("Canal", "MICM")
                .header("Usuario", "TestUser")
                .header("Terminal", "127.0.0.1")
                .body(getRequestWithEmptyAcreedores())
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("succeeded", is("false"))
                .body("message", containsString("acreedores"))
                .body("errors", notNullValue())
                .body("data", nullValue());
    }

    @Test
    void shouldReturn401WhenLoginFails() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "invalid")
                .header("Canal", "MICM")
                .header("Usuario", "TestUser")
                .header("Terminal", "127.0.0.1")
                .body(getValidRegistrationRequest())
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(401)
                .body("succeeded", is("false"))
                .body("message", containsString("Credenciales"))
                .body("errors", notNullValue())
                .body("data", nullValue());
    }

    private String getValidRegistrationRequest() {
        return """
            {
                "operaciones": {
                    "idTipoAvisoInscripcion": 1,
                    "tipoConciliacion": 1,
                    "fechaVencimiento": "2025-12-31T23:59:59.999Z",
                    "comentarios": "Test registration",
                    "moneda": "DOP",
                    "monto": 150000,
                    "tipoDeGarantiaMobiliario": 1
                },
                "acreedores": [
                    {
                        "rncCedula": "12345678901",
                        "nombreAcreedor": "Banco de Reservas",
                        "idMunicipio": "0101",
                        "domicilio": "Calle Principal #123",
                        "correoElectronico": "test@banreservas.com",
                        "telefono": "809-555-0123",
                        "nacional": true
                    }
                ],
                "deudores": [
                    {
                        "rncCedula": "98765432109",
                        "IdTipoDeudor": "1",
                        "nombreDeudor": "Juan Pérez",
                        "idMunicipio": "0101",
                        "domicilio": "Avenida Central #456",
                        "correoElectronico": "juan.perez@email.com",
                        "telefono": "809-555-0456",
                        "nacional": true
                    }
                ],
                "bienes": [
                    {
                        "idTipoPropiedad": 1,
                        "idTipoBien": 1,
                        "numeroSerial": "VEH123456789",
                        "descripcionBien": "Toyota Corolla 2023",
                        "incorporacionInmueble": false,
                        "incorporacionInmuebleDescripcion": "No aplica",
                        "registroDondeSeEnCuentraInscrito": "DGII",
                        "ubicacionDelInmueble": "Santo Domingo"
                    }
                ]
            }
            """;
    }

    private String getRequestWithoutOperaciones() {
        return """
            {
                "acreedores": [
                    {
                        "rncCedula": "12345678901",
                        "nombreAcreedor": "Test Creditor",
                        "nacional": true
                    }
                ],
                "deudores": [
                    {
                        "rncCedula": "98765432109",
                        "IdTipoDeudor": "1",
                        "nombreDeudor": "Test Debtor",
                        "nacional": true
                    }
                ],
                "bienes": [
                    {
                        "idTipoPropiedad": 1,
                        "idTipoBien": 1,
                        "numeroSerial": "TEST123",
                        "descripcionBien": "Test Asset"
                    }
                ]
            }
            """;
    }

    private String getRequestWithoutBienes() {
        return """
            {
                "operaciones": {
                    "idTipoAvisoInscripcion": 1,
                    "tipoConciliacion": 1
                },
                "acreedores": [
                    {
                        "rncCedula": "12345678901",
                        "nombreAcreedor": "Test Creditor",
                        "nacional": true
                    }
                ],
                "deudores": [
                    {
                        "rncCedula": "98765432109",
                        "IdTipoDeudor": "1",
                        "nombreDeudor": "Test Debtor",
                        "nacional": true
                    }
                ]
            }
            """;
    }

    private String getRequestWithEmptyBienes() {
        return """
            {
                "operaciones": {
                    "idTipoAvisoInscripcion": 1,
                    "tipoConciliacion": 1
                },
                "acreedores": [
                    {
                        "rncCedula": "12345678901",
                        "nombreAcreedor": "Test Creditor",
                        "nacional": true
                    }
                ],
                "deudores": [
                    {
                        "rncCedula": "98765432109",
                        "IdTipoDeudor": "1",
                        "nombreDeudor": "Test Debtor",
                        "nacional": true
                    }
                ],
                "bienes": []
            }
            """;
    }

    private String getRequestWithoutDeudores() {
        return """
            {
                "operaciones": {
                    "idTipoAvisoInscripcion": 1,
                    "tipoConciliacion": 1
                },
                "acreedores": [
                    {
                        "rncCedula": "12345678901",
                        "nombreAcreedor": "Test Creditor",
                        "nacional": true
                    }
                ],
                "bienes": [
                    {
                        "idTipoPropiedad": 1,
                        "idTipoBien": 1,
                        "numeroSerial": "TEST123",
                        "descripcionBien": "Test Asset"
                    }
                ]
            }
            """;
    }

    private String getRequestWithEmptyDeudores() {
        return """
            {
                "operaciones": {
                    "idTipoAvisoInscripcion": 1,
                    "tipoConciliacion": 1
                },
                "acreedores": [
                    {
                        "rncCedula": "12345678901",
                        "nombreAcreedor": "Test Creditor",
                        "nacional": true
                    }
                ],
                "deudores": [],
                "bienes": [
                    {
                        "idTipoPropiedad": 1,
                        "idTipoBien": 1,
                        "numeroSerial": "TEST123",
                        "descripcionBien": "Test Asset"
                    }
                ]
            }
            """;
    }

    private String getRequestWithoutAcreedores() {
        return """
            {
                "operaciones": {
                    "idTipoAvisoInscripcion": 1,
                    "tipoConciliacion": 1
                },
                "deudores": [
                    {
                        "rncCedula": "98765432109",
                        "IdTipoDeudor": "1",
                        "nombreDeudor": "Test Debtor",
                        "nacional": true
                    }
                ],
                "bienes": [
                    {
                        "idTipoPropiedad": 1,
                        "idTipoBien": 1,
                        "numeroSerial": "TEST123",
                        "descripcionBien": "Test Asset"
                    }
                ]
            }
            """;
    }

    private String getRequestWithEmptyAcreedores() {
        return """
            {
                "operaciones": {
                    "idTipoAvisoInscripcion": 1,
                    "tipoConciliacion": 1
                },
                "acreedores": [],
                "deudores": [
                    {
                        "rncCedula": "98765432109",
                        "IdTipoDeudor": "1",
                        "nombreDeudor": "Test Debtor",
                        "nacional": true
                    }
                ],
                "bienes": [
                    {
                        "idTipoPropiedad": 1,
                        "idTipoBien": 1,
                        "numeroSerial": "TEST123",
                        "descripcionBien": "Test Asset"
                    }
                ]
            }
            """;
    }

    private String getInvalidRegistrationRequest() {
        return """
            {
                "operaciones": {
                    "idTipoAvisoInscripcion": 1,
                    "tipoConciliacion": 1
                },
                "acreedores": [
                    {
                        "rncCedula": "12345678901",
                        "nombreAcreedor": "Test Creditor",
                        "nacional": true
                    }
                ],
                "deudores": [
                    {
                        "rncCedula": "98765432109",
                        "IdTipoDeudor": "1",
                        "nombreDeudor": "Test Debtor",
                        "nacional": true
                    }
                ],
                "bienes": [
                    {
                        "idTipoPropiedad": 1,
                        "idTipoBien": 1,
                        "numeroSerial": "TEST123",
                        "descripcionBien": "Test Asset",
                        "invalidField": "this should cause validation error"
                    }
                ]
            }
            """;
    }

    private String getComplexValidRequest() {
        return """
            {
                "operaciones": {
                    "idTipoAvisoInscripcion": 2,
                    "tipoConciliacion": 2,
                    "fechaVencimiento": "2026-06-30T12:00:00.000Z",
                    "comentarios": "Complex test case with multiple items",
                    "moneda": "USD",
                    "monto": 500000,
                    "tipoDeGarantiaMobiliario": 2,
                    "idTipoEmbargo": 1,
                    "estadoEmbargoNoDispAdmjud": "Test State",
                    "ejecucionDescripcionObligaciongarantizada": "Test Obligation",
                    "ejecucionDescripcionIncumplimientoDeudor": "Test Default",
                    "ejecucionDescripcionMontoSaldo": 100000,
                    "ejecucionCostaProcesales": 50000
                },
                "acreedores": [
                    {
                        "rncCedula": "11111111111",
                        "nombreAcreedor": "First Creditor",
                        "idMunicipio": "0101",
                        "domicilio": "Address 1",
                        "correoElectronico": "creditor1@test.com",
                        "telefono": "809-111-1111",
                        "nacional": true
                    },
                    {
                        "rncCedula": "22222222222",
                        "nombreAcreedor": "Second Creditor",
                        "idMunicipio": "0102",
                        "domicilio": "Address 2",
                        "correoElectronico": "creditor2@test.com",
                        "telefono": "809-222-2222",
                        "nacional": false
                    }
                ],
                "deudores": [
                    {
                        "rncCedula": "33333333333",
                        "IdTipoDeudor": "1",
                        "nombreDeudor": "First Debtor",
                        "idMunicipio": "0103",
                        "domicilio": "Debtor Address 1",
                        "correoElectronico": "debtor1@test.com",
                        "telefono": "809-333-3333",
                        "nacional": true
                    },
                    {
                        "rncCedula": "44444444444",
                        "IdTipoDeudor": "2",
                        "nombreDeudor": "Second Debtor",
                        "idMunicipio": "0104",
                        "domicilio": "Debtor Address 2",
                        "correoElectronico": "debtor2@test.com",
                        "telefono": "809-444-4444",
                        "nacional": true
                    }
                ],
                "bienes": [
                    {
                        "idTipoPropiedad": 1,
                        "idTipoBien": 1,
                        "numeroSerial": "ASSET001",
                        "descripcionBien": "First Asset Description",
                        "incorporacionInmueble": true,
                        "incorporacionInmuebleDescripcion": "Real Estate Description",
                        "registroDondeSeEnCuentraInscrito": "Registry 1",
                        "ubicacionDelInmueble": "Location 1"
                    },
                    {
                        "idTipoPropiedad": 2,
                        "idTipoBien": 2,
                        "numeroSerial": "ASSET002",
                        "descripcionBien": "Second Asset Description",
                        "incorporacionInmueble": false,
                        "incorporacionInmuebleDescripcion": "Not applicable",
                        "registroDondeSeEnCuentraInscrito": "Registry 2",
                        "ubicacionDelInmueble": "Location 2"
                    }
                ]
            }
            """;
    }

    private String getMinimalValidRequest() {
        return """
            {
                "operaciones": {
                    "idTipoAvisoInscripcion": 1,
                    "tipoConciliacion": 1
                },
                "acreedores": [
                    {
                        "rncCedula": "12345678901",
                        "nombreAcreedor": "Minimal Creditor",
                        "nacional": true
                    }
                ],
                "deudores": [
                    {
                        "rncCedula": "98765432109",
                        "IdTipoDeudor": "1",
                        "nombreDeudor": "Minimal Debtor",
                        "nacional": true
                    }
                ],
                "bienes": [
                    {
                        "idTipoPropiedad": 1,
                        "idTipoBien": 1,
                        "numeroSerial": "MIN001",
                        "descripcionBien": "Minimal Asset"
                    }
                ]
            }
            """;
    }

    private String getPartiallyValidRequest() {
        return """
            {
                "operaciones": {
                    "idTipoAvisoInscripcion": 1,
                    "tipoConciliacion": 1,
                    "comentarios": "Partial request test"
                },
                "acreedores": [
                    {
                        "rncCedula": "12345678901",
                        "nombreAcreedor": "Partial Creditor",
                        "idMunicipio": "0101",
                        "nacional": true
                    }
                ],
                "deudores": [
                    {
                        "rncCedula": "98765432109",
                        "IdTipoDeudor": "1",
                        "nombreDeudor": "Partial Debtor",
                        "domicilio": "Some address",
                        "nacional": true
                    }
                ],
                "bienes": [
                    {
                        "idTipoPropiedad": 1,
                        "idTipoBien": 1,
                        "numeroSerial": "PART001",
                        "descripcionBien": "Partial Asset",
                        "incorporacionInmueble": true,
                        "ubicacionDelInmueble": "Partial Location"
                    }
                ]
            }
            """;
    }
}