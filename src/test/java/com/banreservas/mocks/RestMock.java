package com.banreservas.mocks;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class RestMock implements QuarkusTestResourceLifecycleManager {

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();

        // ============= LOGIN MICM =============
        // Login exitoso general
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/login-micm"))
                .atPriority(5)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "header": {
                                    "responseCode": 200,
                                    "responseMessage": "Exitoso"
                                },
                                "body": {
                                    "security": {
                                        "token": {
                                            "number": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token",
                                            "expiration": "2025-07-01T11:14:51.162+00:00",
                                            "contractValidation": true,
                                            "succeed": true,
                                            "message": "Authenticate success"
                                        }
                                    }
                                }
                            }
                            """)));

        // Login fallido para casos específicos
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/login-micm"))
                .withHeader("sessionId", equalTo("invalid"))
                .atPriority(1)
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "header": {
                                    "responseCode": 401,
                                    "responseMessage": "Credenciales inválidas"
                                },
                                "body": {
                                    "security": {
                                        "token": {
                                            "number": "",
                                            "expiration": "",
                                            "contractValidation": false,
                                            "succeed": false,
                                            "message": "Credenciales incorrectas"
                                        }
                                    }
                                }
                            }
                            """)));

        // ============= REGISTRO INSCRIPCION MICM =============
        // Registro exitoso general
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/master-registro-inscripcion"))
                .atPriority(5)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "header": {
                                    "responseCode": 200,
                                    "responseMessage": "Exitoso"
                                },
                                "body": {
                                    "data": {
                                        "id": 404969,
                                        "idTipoAvisoInscripcion": null,
                                        "tipoConciliacion": null,
                                        "numeroRegistro": null,
                                        "fechaRegistro": null,
                                        "fechaVencimiento": null,
                                        "fechaLevantamientoEmbargo": null,
                                        "comentarios": null,
                                        "moneda": null,
                                        "monto": null,
                                        "sucursalLey": null,
                                        "otro": null,
                                        "tipoDeGarantiaMobiliario": null,
                                        "idUsuario": null,
                                        "idOrganizacion": null,
                                        "idSucursal": null,
                                        "idEstado": null,
                                        "idTipoEmbargo": null,
                                        "motivoLevantamientoEmbargo": null,
                                        "descripcionEstatus": null,
                                        "estadoEmbargoNoDispAdmjud": null,
                                        "ejecucionDescripcionObligacionGarantizada": null,
                                        "ejecucionDescripcionIncumplimientoDeudor": null,
                                        "ejecucionDescripcionPruebaIncumplimiento": null,
                                        "ejecucionDescripcionMontoSaldo": null,
                                        "ejecucionDescripcionMontofijado": null,
                                        "ejecucionCostaProcesales": null,
                                        "numeroSentenciaPrivilegio": null,
                                        "tipoAvisosInscripcion": null,
                                        "idSucursalNavigation": null,
                                        "idUsuarioNavigation": null,
                                        "idOrganizacionNavigation": null,
                                        "acreedores": null,
                                        "bienes": null,
                                        "deudores": null
                                    }
                                }
                            }
                            """)));

        // Registro con token inválido
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/master-registro-inscripcion"))
                .withHeader("sessionid", equalTo("invalid-token"))
                .atPriority(1)
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "header": {
                                    "responseCode": 401,
                                    "responseMessage": "Token inválido"
                                },
                                "body": {
                                    "data": null
                                }
                            }
                            """)));

        // Registro con error del servidor
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/master-registro-inscripcion"))
                .withHeader("sessionid", equalTo("server-error"))
                .atPriority(1)
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "header": {
                                    "responseCode": 500,
                                    "responseMessage": "Error interno del servidor"
                                },
                                "body": {
                                    "data": null
                                }
                            }
                            """)));

        // Registro con datos inválidos
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/master-registro-inscripcion"))
                .withRequestBody(containing("\"invalidField\""))
                .atPriority(1)
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "header": {
                                    "responseCode": 400,
                                    "responseMessage": "Datos de entrada inválidos"
                                },
                                "body": {
                                    "data": null
                                }
                            }
                            """)));

        return Map.of(
            "micm.login.url", "http://localhost:8089/api/v1/login-micm",
            "micm.registration.url", "http://localhost:8089/api/v1/master-registro-inscripcion"
        );
    }

    @Override
    public void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }
}