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
class RegistrationInscriptionRouteTest extends CamelQuarkusTestSupport {

    private static final String AUTH_TOKEN = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUI";
    private static final String ENDPOINT = "/registration/micm/api/v1/registrationInscription";

    @Test
    void shouldSuccessfullyRegisterInscription() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .header("Canal", "MICM")
                .header("Usuario", "Prueba")
                .header("Terminal", "0.0.0.0")
                .header("FechaHora", "2022-11-02T09:59:49.518472-04:00")
                .header("Version", "1")
                .header("Servicio", "MasterRegistroInscripcionMICM")
                .body(getValidRegistrationRequest())
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(200)
                .body("succeeded", is("true"))
                .body("data.id", notNullValue());
    }

    @Test
    void shouldSuccessfullyRegisterWithMinimalData() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .header("Canal", "MICM")
                .header("Usuario", "Prueba")
                .header("Terminal", "0.0.0.0")
                .body(getMinimalRegistrationRequest())
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(200)
                .body("succeeded", is("true"))
                .body("data.id", notNullValue());
    }

    @Test
    void shouldRejectEmptyRequestBody() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .body("")
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("succeeded", is("false"))
                .body("message", is("Request body es requerido"));
    }

    @Test
    void shouldRejectMissingSessionId() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .body(getValidRegistrationRequest())
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("succeeded", is("false"));
    }

    @Test
    void shouldRejectMissingOperations() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .body("""
                    {
                        "acreedores": [
                            {
                                "rncCedula": "string",
                                "nombreAcreedor": "string",
                                "nacional": true
                            }
                        ],
                        "deudores": [
                            {
                                "rncCedula": "456343523",
                                "IdTipoDeudor": "1",
                                "nombreDeudor": "string",
                                "nacional": true
                            }
                        ],
                        "bienes": [
                            {
                                "idTipoPropiedad": 1,
                                "idTipoBien": 1,
                                "numeroSerial": "456343523",
                                "descripcionBien": "string"
                            }
                        ]
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("succeeded", is("false"))
                .body("message", containsString("operaciones"));
    }

    @Test
    void shouldRejectMissingDebtors() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .body("""
                    {
                        "operaciones": {
                            "idTipoAvisoInscripcion": 1,
                            "tipoConciliacion": 1
                        },
                        "acreedores": [
                            {
                                "rncCedula": "string",
                                "nombreAcreedor": "string",
                                "nacional": true
                            }
                        ],
                        "bienes": [
                            {
                                "idTipoPropiedad": 1,
                                "idTipoBien": 1,
                                "numeroSerial": "456343523",
                                "descripcionBien": "string"
                            }
                        ]
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("succeeded", is("false"))
                .body("message", containsString("deudores"));
    }

    @Test
    void shouldRejectMissingAssets() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .body("""
                    {
                        "operaciones": {
                            "idTipoAvisoInscripcion": 1,
                            "tipoConciliacion": 1
                        },
                        "acreedores": [
                            {
                                "rncCedula": "string",
                                "nombreAcreedor": "string",
                                "nacional": true
                            }
                        ],
                        "deudores": [
                            {
                                "rncCedula": "456343523",
                                "IdTipoDeudor": "1",
                                "nombreDeudor": "string",
                                "nacional": true
                            }
                        ]
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("succeeded", is("false"))
                .body("message", containsString("bienes"));
    }

    @Test
    void shouldRejectMissingCreditors() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .body("""
                    {
                        "operaciones": {
                            "idTipoAvisoInscripcion": 1,
                            "tipoConciliacion": 1
                        },
                        "deudores": [
                            {
                                "rncCedula": "456343523",
                                "IdTipoDeudor": "1",
                                "nombreDeudor": "string",
                                "nacional": true
                            }
                        ],
                        "bienes": [
                            {
                                "idTipoPropiedad": 1,
                                "idTipoBien": 1,
                                "numeroSerial": "456343523",
                                "descripcionBien": "string"
                            }
                        ]
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("succeeded", is("false"))
                .body("message", containsString("acreedores"));
    }

    @Test
    void shouldHandleRegistrationServiceError() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "server-error")
                .header("Canal", "MICM")
                .header("Usuario", "Prueba")
                .header("Terminal", "0.0.0.0")
                .body(getValidRegistrationRequest())
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(502)
                .body("succeeded", is("false"))
                .body("message", containsString("servidor"));
    }

    @Test
    void shouldHandleTokenInvalidInRegistration() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "invalid-token")
                .header("Canal", "MICM")
                .header("Usuario", "Prueba")
                .header("Terminal", "0.0.0.0")
                .body(getValidRegistrationRequest())
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(401)
                .body("succeeded", is("false"))
                .body("message", containsString("inválido"));
    }

    @Test
    void shouldHandleRegistrationValidationError() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .header("Canal", "MICM")
                .header("Usuario", "Prueba")
                .header("Terminal", "0.0.0.0")
                .body("""
                    {
                        "operaciones": {
                            "idTipoAvisoInscripcion": 1,
                            "tipoConciliacion": 1
                        },
                        "acreedores": [
                            {
                                "rncCedula": "string",
                                "nombreAcreedor": "string",
                                "nacional": true
                            }
                        ],
                        "deudores": [
                            {
                                "rncCedula": "456343523",
                                "IdTipoDeudor": "1",
                                "nombreDeudor": "string",
                                "nacional": true
                            }
                        ],
                        "bienes": [
                            {
                                "idTipoPropiedad": 1,
                                "idTipoBien": 1,
                                "numeroSerial": "456343523",
                                "descripcionBien": "string",
                                "invalidField": "this should cause validation error"
                            }
                        ]
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("succeeded", is("false"));
    }

    @Test
    void shouldReturnValidResponseStructure() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .header("Canal", "MICM")
                .header("Usuario", "Prueba")
                .header("Terminal", "0.0.0.0")
                .header("FechaHora", "2022-11-02T09:59:49.518472-04:00")
                .header("Version", "1")
                .header("Servicio", "MasterRegistroInscripcionMICM")
                .body(getValidRegistrationRequest())
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(200)
                .body("succeeded", notNullValue())
                .body("message", notNullValue())
                .body("errors", notNullValue())
                .body("data", notNullValue())
                .body("data.id", notNullValue());
    }

    @Test
    void shouldProcessValidRegistrationWithAllFields() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .header("Canal", "MICM")
                .header("Usuario", "Prueba")
                .header("Terminal", "0.0.0.0")
                .header("FechaHora", "2022-11-02T09:59:49.518472-04:00")
                .header("Version", "1")
                .header("Servicio", "MasterRegistroInscripcionMICM")
                .body(getCompleteRegistrationRequest())
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(200)
                .body("succeeded", is("true"))
                .body("data.id", notNullValue());
    }

    @Test
    void shouldTestCurlExampleRequest() {
        given()
                .contentType(ContentType.JSON)
                .header("Canal", "MICM")
                .header("Usuario", "Prueba")
                .header("Terminal", "0.0.0.0")
                .header("FechaHora", "2022-11-02T09:59:49.518472-04:00")
                .header("Version", "1")
                .header("Servicio", "MasterRegistroInscripcionMICM")
                .header("sessionId", "123456")
                .header("Authorization", AUTH_TOKEN)
                .body(getCurlExampleRequest())
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(200)
                .body("succeeded", is("true"))
                .body("data.id", notNullValue());
    }

    private String getValidRegistrationRequest() {
        return """
            {
                "operaciones": {
                    "idTipoAvisoInscripcion": 1,
                    "tipoConciliacion": 1,
                    "fechaVencimiento": "2023-03-27T05:44:37.843Z",
                    "comentarios": "string",
                    "moneda": "string",
                    "monto": 0,
                    "tipoDeGarantiaMobiliario": 1
                },
                "acreedores": [
                    {
                        "rncCedula": "string",
                        "nombreAcreedor": "string",
                        "idMunicipio": "string",
                        "domicilio": "string",
                        "correoElectronico": "string",
                        "telefono": "string",
                        "nacional": true
                    }
                ],
                "deudores": [
                    {
                        "rncCedula": "456343523",
                        "IdTipoDeudor": "1",
                        "nombreDeudor": "string",
                        "idMunicipio": "string",
                        "domicilio": "string",
                        "correoElectronico": "string",
                        "telefono": "string",
                        "nacional": true
                    }
                ],
                "bienes": [
                    {
                        "idTipoPropiedad": 1,
                        "idTipoBien": 1,
                        "numeroSerial": "456343523",
                        "descripcionBien": "string",
                        "incorporacionInmueble": true,
                        "incorporacionInmuebleDescripcion": "string",
                        "registroDondeSeEnCuentraInscrito": "string",
                        "ubicacionDelInmueble": "string"
                    }
                ]
            }
            """;
    }

    private String getMinimalRegistrationRequest() {
        return """
            {
                "operaciones": {
                    "idTipoAvisoInscripcion": 1,
                    "tipoConciliacion": 1
                },
                "acreedores": [
                    {
                        "rncCedula": "123456789",
                        "nombreAcreedor": "Test Creditor",
                        "nacional": true
                    }
                ],
                "deudores": [
                    {
                        "rncCedula": "987654321",
                        "IdTipoDeudor": "1",
                        "nombreDeudor": "Test Debtor",
                        "nacional": true
                    }
                ],
                "bienes": [
                    {
                        "idTipoPropiedad": 1,
                        "idTipoBien": 1,
                        "numeroSerial": "ASSET001",
                        "descripcionBien": "Test Asset"
                    }
                ]
            }
            """;
    }

    private String getCompleteRegistrationRequest() {
        return """
            {
                "operaciones": {
                    "idTipoAvisoInscripcion": 1,
                    "tipoConciliacion": 1,
                    "fechaVencimiento": "2023-03-27T05:44:37.843Z",
                    "comentarios": "Complete registration test",
                    "moneda": "DOP",
                    "monto": 100000,
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
                        "rncCedula": "10987654321",
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
                        "numeroSerial": "VEHICLE123456",
                        "descripcionBien": "Vehículo Toyota Corolla 2020",
                        "incorporacionInmueble": false,
                        "incorporacionInmuebleDescripcion": "N/A",
                        "registroDondeSeEnCuentraInscrito": "DGII",
                        "ubicacionDelInmueble": "Santo Domingo"
                    }
                ]
            }
            """;
    }

    private String getCurlExampleRequest() {
        return """
            {
                "operaciones": {
                    "idTipoAvisoInscripcion": 1,
                    "tipoConciliacion": 1,
                    "fechaVencimiento": "2023-03-27T05:44:37.843Z",
                    "comentarios": "string",
                    "moneda": "string",
                    "monto": 0,
                    "tipoDeGarantiaMobiliario": 1
                },
                "acreedores": [
                    {
                        "rncCedula": "string",
                        "nombreAcreedor": "string",
                        "idMunicipio": "string",
                        "domicilio": "string",
                        "correoElectronico": "string",
                        "telefono": "string",
                        "nacional": true
                    },
                    {
                        "rncCedula": "456343524",
                        "nombreAcreedor": "string",
                        "idMunicipio": "string",
                        "domicilio": "string",
                        "correoElectronico": "string",
                        "telefono": "string",
                        "nacional": true
                    }
                ],
                "deudores": [
                    {
                        "rncCedula": "456343523",
                        "IdTipoDeudor": "1",
                        "nombreDeudor": "string",
                        "idMunicipio": "string",
                        "domicilio": "string",
                        "correoElectronico": "string",
                        "telefono": "string",
                        "nacional": true
                    },
                    {
                        "rncCedula": "456343524",
                        "IdTipoDeudor": "1",
                        "nombreDeudor": "string",
                        "idMunicipio": "string",
                        "domicilio": "string",
                        "correoElectronico": "string",
                        "telefono": "string",
                        "nacional": true
                    }
                ],
                "bienes": [
                    {
                        "idTipoPropiedad": 1,
                        "idTipoBien": 1,
                        "numeroSerial": "456343523",
                        "descripcionBien": "string",
                        "incorporacionInmueble": true,
                        "incorporacionInmuebleDescripcion": "string",
                        "registroDondeSeEnCuentraInscrito": "string",
                        "ubicacionDelInmueble": "string"
                    },
                    {
                        "idTipoPropiedad": 1,
                        "idTipoBien": 1,
                        "numeroSerial": "456343524",
                        "descripcionBien": "string",
                        "incorporacionInmueble": true,
                        "incorporacionInmuebleDescripcion": "string",
                        "registroDondeSeEnCuentraInscrito": "string",
                        "ubicacionDelInmueble": "string"
                    }
                ]
            }
            """;
    }
}