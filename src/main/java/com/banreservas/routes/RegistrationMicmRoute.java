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
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

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

        // Manejo de errores de validación
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

        // Manejo de timeout
        onException(SocketTimeoutException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, logger, "Timeout conectando al servicio registro MICM")
                .setProperty(Constants.MESSAGE_PROPERTIE, constant(
                        "Timeout al conectar con servicio de Registro Inscripción MICM"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .process(errorResponseProcessor)
                .marshal().json(JsonLibrary.Jackson)
                .end();

        // Manejo de excepciones generales
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

                // Preparar request
                .process(generateRegistrationRequestProcessor)
                .marshal().json(JsonLibrary.Jackson)
                
                // Configurar headers requeridos por el servicio MICM
                .setHeader("id_consumidor", constant("123"))
                .setHeader("fecha_hora", simple("${date:now:yyyy-MM-dd}"))
                .setHeader("usuario", exchangeProperty("usuarioRq"))
                .setHeader("terminal", exchangeProperty("terminalRq"))
                .setHeader("operacion", constant("MasterRegistroInscripcion"))
                .setHeader("sessionid", exchangeProperty("sessionIdRq"))
                
                // Configurar headers HTTP estándar
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setHeader("Authorization", exchangeProperty("authorizationRq"))

                .process(exchange -> {
                    String body = exchange.getIn().getBody(String.class);
                    logger.info("Request body que se enviará al servicio MICM: {}", body);
                    
                    // Log de headers importantes
                    logger.info("Headers del request:");
                    logger.info("  Authorization: {}", exchange.getIn().getHeader("Authorization"));
                    logger.info("  sessionid: {}", exchange.getIn().getHeader("sessionid"));
                    logger.info("  usuario: {}", exchange.getIn().getHeader("usuario"));
                    logger.info("  operacion: {}", exchange.getIn().getHeader("operacion"));
                    logger.info("  id_consumidor: {}", exchange.getIn().getHeader("id_consumidor"));
                })
                
                // TEMPORAL: Comparar con JSON que funciona
                .process(new com.banreservas.processors.DebugJsonProcessor())

                // Llamar al servicio con configuración robusta de timeout
                .toD(registrationUrl + "?bridgeEndpoint=true&throwExceptionOnFailure=false&connectionTimeout=30000&socketTimeout=30000")

                // Evaluar respuesta según el código HTTP
                .choice()
                
                // 200 - Operación exitosa
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(200))
                    .log(LoggingLevel.INFO, logger, "Respuesta exitosa del servicio registro - HTTP 200")
                    .process(exchange -> {
                        String responseBody = exchange.getIn().getBody(String.class);
                        
                        exchange.setProperty("Tipo", "0");
                        exchange.setProperty("Codigo", "200");
                        exchange.setProperty(Constants.MESSAGE_PROPERTIE, "Registro completado exitosamente");
                        
                        exchange.getIn().setBody(responseBody);
                    })
                
                // 400 - Request inválido
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(400))
                    .log(LoggingLevel.WARN, logger, "Request inválido para servicio registro - HTTP 400")
                    .process(exchange -> {
                        String responseBody = exchange.getIn().getBody(String.class);
                        logger.error("===== RESPUESTA COMPLETA HTTP 400 =====");
                        logger.error("Status Code: {}", exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE));
                        logger.error("Response Body: {}", responseBody);
                        logger.error("Headers de respuesta: {}", exchange.getIn().getHeaders());
                        logger.error("==========================================");
                        
                        String errorMessage = "Request inválido para registro de inscripción";
                        
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
                            logger.warn("Error parseando mensaje de error del servicio: {}", e.getMessage());
                        }
                        
                        exchange.setProperty(Constants.MESSAGE_PROPERTIE, errorMessage);
                        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
                    })
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()
                
                // 401 - No autorizado
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(401))
                    .log(LoggingLevel.WARN, logger, "Token inválido para servicio registro - HTTP 401")
                    .setProperty(Constants.MESSAGE_PROPERTIE, constant("Token inválido o expirado"))
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(401))
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()
                
                // 403 - Forbidden
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(403))
                    .log(LoggingLevel.WARN, logger, "Acceso denegado para servicio registro - HTTP 403")
                    .setProperty(Constants.MESSAGE_PROPERTIE, constant("No tiene permisos para realizar esta operación de registro"))
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(403))
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()
                
                // 500 - Error interno
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(500))
                    .log(LoggingLevel.ERROR, logger, "Error interno en servicio registro - HTTP 500")
                    .setProperty(Constants.MESSAGE_PROPERTIE, constant("Error interno en servicio de registro inscripción"))
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(502))
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()
                
                // Otros códigos de error
                .otherwise()
                    .log(LoggingLevel.ERROR, logger, "Error inesperado en servicio registro - HTTP ${header.CamelHttpResponseCode}")
                    .process(exchange -> {
                        Integer httpCode = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
                        exchange.setProperty(Constants.MESSAGE_PROPERTIE, "Error inesperado en registro inscripción: HTTP " + httpCode);
                        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 502);
                    })
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()
                .end();
    }
}