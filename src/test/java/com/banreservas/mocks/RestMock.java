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

        // Stubs específicos primero (máxima prioridad)
        stubSpecificSuccessfulLogin();
        stubSpecificSuccessfulChangeStatus();
        
        // Stubs generales
        stubSuccessfulLogin();
        stubFailedLogin();
        stubSuccessfulChangeStatus();
        stubFailedChangeStatus();
        
        // Catch-all al final (mínima prioridad)
        stubCatchAllStubs();

        return Map.of(
            "micm.login.url", "http://localhost:8089/api/v1/login-micm",
            "micm.change.operation.status.url", "http://localhost:8089/api/v1/cambia-estado-operacion"
        );
    }

    @Override
    public void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    private void stubSpecificSuccessfulLogin() {
        // Stub específico para sessionId="123" (usado en testSuccessfulChangeStatusOperation)
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/login-micm"))
                .withHeader("sessionId", equalTo("123"))
                .withHeader("Authorization", containing("Bearer"))
                .atPriority(1)
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
                                            "number": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token.123",
                                            "expiration": "2025-07-01T11:14:51.162+00:00",
                                            "contractValidation": true,
                                            "succeed": true,
                                            "message": "Authenticate success"
                                        }
                                    }
                                }
                            }
                            """)));
    }

    private void stubSpecificSuccessfulChangeStatus() {
        // Caso: Inscripcion Ejecutada (Status 4)
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/cambia-estado-operacion"))
                .withRequestBody(containing("\"id\":403528"))
                .withRequestBody(containing("\"status\":4"))
                .atPriority(1)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "Header": {
                                    "ResponseCode": 200,
                                    "ResponseMessage": "Exitoso"
                                },
                                "Body": {
                                    "Operation": {
                                        "id": 403528,
                                        "status": 4,
                                        "statusDescription": "Inscripcion Ejecutada",
                                        "nonAvailableJudicialSeizureState": null,
                                        "seizureTypeId": null,
                                        "fixedAmountExecutionDescription": 0,
                                        "balanceAmountExecutionDescription": 0,
                                        "proceduralCostsExecution": 0,
                                        "guaranteedObligationExecutionDescription": "test1",
                                        "debtorNonComplianceExecutionDescription": "0",
                                        "conciliationType": 1,
                                        "seizureReleaseDate": null,
                                        "seizureReleaseReason": null
                                    },
                                    "Succeeded": false,
                                    "Message": "Inscripcion Ejecutada",
                                    "Errors": null,
                                    "Security": {
                                        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token.123"
                                    }
                                }
                            }
                            """)));

        // Caso: Inscripcion Embargada (Status 3)
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/cambia-estado-operacion"))
                .withRequestBody(containing("\"id\":403528"))
                .withRequestBody(containing("\"status\":3"))
                .atPriority(1)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "Header": {
                                    "ResponseCode": 200,
                                    "ResponseMessage": "Exitoso"
                                },
                                "Body": {
                                    "Operation": {
                                        "id": 403528,
                                        "status": 3,
                                        "statusDescription": "Inscripcion Embargada",
                                        "nonAvailableJudicialSeizureState": "string",
                                        "seizureTypeId": 1,
                                        "fixedAmountExecutionDescription": 0,
                                        "balanceAmountExecutionDescription": 0,
                                        "proceduralCostsExecution": 0,
                                        "guaranteedObligationExecutionDescription": "string",
                                        "debtorNonComplianceExecutionDescription": "string",
                                        "conciliationType": null,
                                        "seizureReleaseDate": null,
                                        "seizureReleaseReason": null
                                    },
                                    "Succeeded": false,
                                    "Message": "Inscripcion Embargada",
                                    "Errors": null,
                                    "Security": {
                                        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token.123"
                                    }
                                }
                            }
                            """)));

        // Caso: Inscripcion Cancelada (Status 2)
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/cambia-estado-operacion"))
                .withRequestBody(containing("\"id\":78904"))
                .withRequestBody(containing("\"status\":2"))
                .atPriority(1)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "Header": {
                                    "ResponseCode": 200,
                                    "ResponseMessage": "Exitoso"
                                },
                                "Body": {
                                    "Operation": {
                                        "id": 78904,
                                        "status": 2,
                                        "statusDescription": "Inscripcion Cancelada",
                                        "nonAvailableJudicialSeizureState": null,
                                        "seizureTypeId": null,
                                        "fixedAmountExecutionDescription": null,
                                        "balanceAmountExecutionDescription": null,
                                        "proceduralCostsExecution": null,
                                        "guaranteedObligationExecutionDescription": null,
                                        "debtorNonComplianceExecutionDescription": null,
                                        "conciliationType": null,
                                        "seizureReleaseDate": null,
                                        "seizureReleaseReason": null
                                    },
                                    "Succeeded": false,
                                    "Message": "Inscripcion Cancelada",
                                    "Errors": null,
                                    "Security": {
                                        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token.123"
                                    }
                                }
                            }
                            """)));
    }

    private void stubSuccessfulLogin() {
        // Login exitoso para sessionId="123456"
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/login-micm"))
                .withHeader("sessionId", equalTo("123456"))
                .withHeader("Authorization", containing("Bearer"))
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
    }

    private void stubFailedLogin() {
        // Login fallido para sessionId="invalid" y "no-auth"
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/login-micm"))
                .withHeader("sessionId", equalTo("invalid"))
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

        wireMockServer.stubFor(post(urlEqualTo("/api/v1/login-micm"))
                .withHeader("sessionId", equalTo("no-auth"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "header": {
                                    "responseCode": 401,
                                    "responseMessage": "Credenciales inválidas o Token Expirado"
                                },
                                "body": {
                                    "security": {
                                        "token": {
                                            "number": "",
                                            "expiration": "",
                                            "contractValidation": false,
                                            "succeed": false,
                                            "message": "Authorization header required"
                                        }
                                    }
                                }
                            }
                            """)));
    }

    private void stubSuccessfulChangeStatus() {
        // Cambio de estado exitoso para ID=37544, Status=2
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/cambia-estado-operacion"))
                .withRequestBody(containing("\"id\":37544"))
                .withRequestBody(containing("\"status\":2"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "Header": {
                                    "ResponseCode": 200,
                                    "ResponseMessage": "Exitoso"
                                },
                                "Body": {
                                    "Operation": {
                                        "id": 37544,
                                        "status": 2,
                                        "statusDescription": "Status Description",
                                        "nonAvailableJudicialSeizureState": null,
                                        "seizureTypeId": null,
                                        "fixedAmountExecutionDescription": null,
                                        "balanceAmountExecutionDescription": null,
                                        "proceduralCostsExecution": null,
                                        "guaranteedObligationExecutionDescription": null,
                                        "debtorNonComplianceExecutionDescription": null,
                                        "conciliationType": null,
                                        "seizureReleaseDate": null,
                                        "seizureReleaseReason": null
                                    },
                                    "Succeeded": true,
                                    "Message": "Operación completada exitosamente",
                                    "Errors": null,
                                    "Security": {
                                        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token"
                                    }
                                }
                            }
                            """)));
    }

    private void stubFailedChangeStatus() {
        // Cambio de estado fallido para ID=-1
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/cambia-estado-operacion"))
                .withRequestBody(containing("\"id\":-1"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                              "Header": {
                                "ResponseCode": 400,
                                "ResponseMessage": "ID de operación inválido"
                              },
                              "Body": {
                                "Operation": null,
                                "Succeeded": false,
                                "Message": "ID debe ser mayor a 0",
                                "Errors": ["Validation error"],
                                "Security": null
                              }
                            }
                            """)));
    }

    private void stubCatchAllStubs() {
        // Catch-all para login (prioridad baja)
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/login-micm"))
                .atPriority(10)
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                              "header": {
                                "responseCode": 400,
                                "responseMessage": "sessionId es requerido"
                              },
                              "body": {
                                "security": {
                                  "token": {
                                    "number": "",
                                    "expiration": "",
                                    "contractValidation": false,
                                    "succeed": false,
                                    "message": "sessionId header is required"
                                  }
                                }
                              }
                            }
                            """)));

        // Catch-all para cambio estado (prioridad baja)
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/cambia-estado-operacion"))
                .atPriority(10)
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                              "Header": {
                                "ResponseCode": 400,
                                "ResponseMessage": "Request inválido"
                              },
                              "Body": {
                                "Operation": null,
                                "Succeeded": false,
                                "Message": "Request inválido",
                                "Errors": ["Bad request"],
                                "Security": null
                              }
                            }
                            """)));
    }
}