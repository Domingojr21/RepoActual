package com.banreservas.routes;

import com.banreservas.processors.GenerateRegistrationMicmRequestProcessor;
import com.banreservas.processors.ErrorResponseProcessor;
import com.banreservas.util.Constants;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketTimeoutException;

@ApplicationScoped
public class RegistrationMicmRoute extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationMicmRoute.class);

    @ConfigProperty(name = "micm.registration.url")
    String registrationUrl;

    @Inject
    GenerateRegistrationMicmRequestProcessor generateRegistrationRequestProcessor;

    @Inject
    ErrorResponseProcessor errorResponseProcessor;

    @Override
    public void configure() throws Exception {

        onException(IllegalArgumentException.class)
                .handled(true)
                .log(LoggingLevel.WARN, logger, "Error de validación en registro: ${exception.message}")
                .process(exchange -> {
                    Exception exception = (Exception) exchange.getProperty(Exchange.EXCEPTION_CAUGHT);
                    exchange.setProperty(Constants.MESSAGE_PROPERTIE, exception.getMessage());
                    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
                })
                .process(errorResponseProcessor)
                .marshal().json(JsonLibrary.Jackson)
                .end();

        onException(SocketTimeoutException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, logger, "Timeout conectando al servicio registro MICM")
                .setProperty(Constants.MESSAGE_PROPERTIE, constant(
                        "Timeout al conectar con servicio de Registro Inscripción MICM"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(502))
                .process(errorResponseProcessor)
                .marshal().json(JsonLibrary.Jackson)
                .end();

        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, logger, "Error inesperado en registro: ${exception.message}")
                .setProperty(Constants.MESSAGE_PROPERTIE, simple("${exception.message}"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .process(errorResponseProcessor)
                .marshal().json(JsonLibrary.Jackson)
                .end();

        from("direct:registration-micm")
                .routeId("registration-micm-service")
                .log(LoggingLevel.INFO, logger, "Iniciando registro inscripción MICM")

                .process(generateRegistrationRequestProcessor)
                .marshal().json(JsonLibrary.Jackson)
                
                .setHeader("id_consumidor", constant("123"))
                .setHeader("fecha_hora", simple("${date:now:yyyy-MM-dd}"))
                .setHeader("usuario", exchangeProperty("usuarioRq"))
                .setHeader("terminal", exchangeProperty("terminalRq"))
                .setHeader("operacion", constant("MasterRegistroInscripcion"))
                .setHeader("sessionid", exchangeProperty("sessionIdRq"))
                
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setHeader("Authorization", exchangeProperty("authorizationRq"))

                .toD(registrationUrl + "?bridgeEndpoint=true&throwExceptionOnFailure=false&connectionTimeout=30000&socketTimeout=30000")

                .choice()
                
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(200))
                    .log(LoggingLevel.INFO, logger, "Respuesta exitosa del servicio registro - HTTP 200")
                    .process(exchange -> {
                        String responseBody = exchange.getIn().getBody(String.class);
                        
                        exchange.setProperty("Tipo", "0");
                        exchange.setProperty("Codigo", "200");
                        exchange.setProperty(Constants.MESSAGE_PROPERTIE, "Registro completado exitosamente");
                        
                        exchange.getIn().setBody(responseBody);
                    })
                
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(400))
                    .log(LoggingLevel.WARN, logger, "Request inválido para servicio registro - HTTP 400")
                    .process(exchange -> {
                        String responseBody = exchange.getIn().getBody(String.class);
                        String errorMessage = "Datos de entrada inválidos";
                        
                        try {
                            if (responseBody != null && !responseBody.trim().isEmpty()) {
                                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                                com.fasterxml.jackson.databind.JsonNode jsonNode = mapper.readTree(responseBody);
                                
                                String headerMessage = jsonNode.path("header").path("responseMessage").asText();
                                String bodyMessage = jsonNode.path("body").path("message").asText();
                                String directMessage = jsonNode.path("message").asText();
                                String responseMessage = jsonNode.path("responseMessage").asText();
                                
                                if (!headerMessage.isEmpty()) {
                                    errorMessage = headerMessage;
                                } else if (!bodyMessage.isEmpty()) {
                                    errorMessage = bodyMessage;
                                } else if (!directMessage.isEmpty()) {
                                    errorMessage = directMessage;
                                } else if (!responseMessage.isEmpty()) {
                                    errorMessage = responseMessage;
                                }
                            }
                        } catch (Exception e) {
                            logger.error("Error parseando mensaje de error del servicio: {}", e.getMessage(), e);
                        }
                        
                        exchange.setProperty(Constants.MESSAGE_PROPERTIE, errorMessage);
                        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
                    })
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()
                
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(401))
                    .log(LoggingLevel.WARN, logger, "Token inválido para servicio registro - HTTP 401")
                    .process(exchange -> {
                        String responseBody = exchange.getIn().getBody(String.class);
                        String errorMessage = "Token inválido o expirado";
                        
                        try {
                            if (responseBody != null && !responseBody.trim().isEmpty()) {
                                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                                com.fasterxml.jackson.databind.JsonNode jsonNode = mapper.readTree(responseBody);
                                
                                String headerMessage = jsonNode.path("header").path("responseMessage").asText();
                                if (!headerMessage.isEmpty()) {
                                    errorMessage = headerMessage;
                                }
                            }
                        } catch (Exception e) {
                            logger.error("Error parseando mensaje de error 401: {}", e.getMessage());
                        }
                        
                        exchange.setProperty(Constants.MESSAGE_PROPERTIE, errorMessage);
                        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 401);
                    })
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()
                
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(403))
                    .log(LoggingLevel.WARN, logger, "Acceso denegado para servicio registro - HTTP 403")
                    .setProperty(Constants.MESSAGE_PROPERTIE, constant("No tiene permisos para realizar esta operación de registro"))
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(403))
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()
                
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(500))
                    .log(LoggingLevel.ERROR, logger, "Error interno en servicio registro - HTTP 500")
                    .process(exchange -> {
                        String responseBody = exchange.getIn().getBody(String.class);
                        String errorMessage = "Error interno en servicio de registro inscripción";
                        
                        try {
                            if (responseBody != null && !responseBody.trim().isEmpty()) {
                                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                                com.fasterxml.jackson.databind.JsonNode jsonNode = mapper.readTree(responseBody);
                                
                                String headerMessage = jsonNode.path("header").path("responseMessage").asText();
                                if (!headerMessage.isEmpty()) {
                                    errorMessage = headerMessage;
                                }
                            }
                        } catch (Exception e) {
                            logger.error("Error parseando mensaje de error 500: {}", e.getMessage());
                        }
                        
                        exchange.setProperty(Constants.MESSAGE_PROPERTIE, errorMessage);
                        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 502);
                    })
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()
                
                .otherwise()
                    .log(LoggingLevel.ERROR, logger, "Error inesperado en servicio registro - HTTP ${header.CamelHttpResponseCode}")
                    .process(exchange -> {
                        Integer httpCode = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
                        String errorMessage = "Error inesperado en registro inscripción: HTTP " + httpCode;
                        exchange.setProperty(Constants.MESSAGE_PROPERTIE, errorMessage);
                        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 502);
                    })
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()
                .end();
    }
}