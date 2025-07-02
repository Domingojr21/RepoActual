package com.banreservas.routes;

import com.banreservas.model.inbound.login.LoginMicmResponse;
import com.banreservas.processors.GenerateLoginMicmRequestProcessor;
import com.banreservas.processors.ProcessLoginMicmResponseProcessor;
import com.banreservas.processors.ErrorResponseProcessor;
import com.banreservas.util.Constants;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketTimeoutException;

@ApplicationScoped
public class LoginMicmRoute extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(LoginMicmRoute.class);

    @ConfigProperty(name = "micm.login.url")
    String loginUrl;

    @ConfigProperty(name = "micm.login.timeout")
    String loginTimeout;

    @Inject
    GenerateLoginMicmRequestProcessor generateLoginRequestProcessor;

    @Inject
    ErrorResponseProcessor errorResponseProcessor;

    @Override
    public void configure() throws Exception {

        JacksonDataFormat loginResponseFormat = new JacksonDataFormat(LoginMicmResponse.class);

        // Manejo de errores de validación
        onException(IllegalArgumentException.class)
                .handled(true)
                .log(LoggingLevel.WARN, logger, "Error de validación en login: ${exception.message}")
                .setProperty(Constants.MESSAGE_PROPERTIE, simple("${exception.message}"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                .process(errorResponseProcessor)
                .marshal().json(JsonLibrary.Jackson)
                .end();

        // Manejo de timeout
        onException(SocketTimeoutException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, logger, "Timeout conectando al servicio login MICM")
                .setProperty(Constants.MESSAGE_PROPERTIE, constant("Timeout al conectar con servicio de login MICM"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .process(errorResponseProcessor)
                .marshal().json(JsonLibrary.Jackson)
                .end();

        // Manejo de excepciones generales
        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, logger, "Error inesperado en login: ${exception.message}")
                .setProperty(Constants.MESSAGE_PROPERTIE, simple("${exception.message}"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .process(errorResponseProcessor)
                .marshal().json(JsonLibrary.Jackson)
                .end();

        from("direct:login-micm")
                .routeId("login-micm-service")
                .log(LoggingLevel.INFO, logger, "Iniciando autenticación MICM")

                // Preparar request de login
                .process(generateLoginRequestProcessor)
                .marshal().json(JsonLibrary.Jackson)

                // Configurar headers HTTP
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))

                // Llamar al servicio de login
                .toD(loginUrl + "?bridgeEndpoint=true&throwExceptionOnFailure=false")

                // Evaluar respuesta según el código HTTP
                .choice()
                
                // 200 - Login exitoso
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(200))
                    .log(LoggingLevel.INFO, logger, "Autenticación MICM exitosa - HTTP 200")
                    .unmarshal(loginResponseFormat)
                    .process(new ProcessLoginMicmResponseProcessor())
                
                // 400 - Request inválido (sessionId faltante u otros errores de validación)
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(400))
                    .log(LoggingLevel.WARN, logger, "Request inválido para login MICM - HTTP 400")
                    .process(exchange -> {
                        String responseBody = exchange.getIn().getBody(String.class);
                        String errorMessage = "Request inválido para login MICM";
                        
                        try {
                            if (responseBody != null && responseBody.contains("responseMessage")) {
                                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                                com.fasterxml.jackson.databind.JsonNode jsonNode = mapper.readTree(responseBody);
                                String headerMessage = jsonNode.path("header").path("responseMessage").asText();
                                if (!headerMessage.isEmpty()) {
                                    errorMessage = headerMessage;
                                }
                            }
                        } catch (Exception e) {
                            logger.warn("Error parseando mensaje de error del login: {}", e.getMessage());
                        }
                        
                        exchange.setProperty(Constants.MESSAGE_PROPERTIE, errorMessage);
                        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
                    })
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()
                
                // 401 - Credenciales inválidas
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(401))
                    .log(LoggingLevel.WARN, logger, "Credenciales inválidas para login MICM - HTTP 401")
                    .setProperty(Constants.MESSAGE_PROPERTIE, constant("Credenciales inválidas o Token Expirado"))
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(401))
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()
                
                // 403 - Forbidden
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(403))
                    .log(LoggingLevel.WARN, logger, "Acceso denegado para login MICM - HTTP 403")
                    .setProperty(Constants.MESSAGE_PROPERTIE, constant("Acceso denegado para login MICM"))
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(403))
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()
                
                // 500 - Error interno del servidor
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(500))
                    .log(LoggingLevel.ERROR, logger, "Error interno en servicio login MICM - HTTP 500")
                    .setProperty(Constants.MESSAGE_PROPERTIE, constant("Error interno en servicio de login MICM"))
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(502))
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()
                
                // Otros códigos de error
                .otherwise()
                    .log(LoggingLevel.ERROR, logger, "Error inesperado en login MICM - HTTP ${header.CamelHttpResponseCode}")
                    .process(exchange -> {
                        Integer httpCode = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
                        exchange.setProperty(Constants.MESSAGE_PROPERTIE, "Error inesperado en login MICM: HTTP " + httpCode);
                        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 502);
                    })
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()
                .end();
    }
}