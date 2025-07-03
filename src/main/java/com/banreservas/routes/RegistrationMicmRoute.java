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

    @ConfigProperty(name = "micm.registration.timeout", defaultValue = "30000")
    String registrationTimeout;

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
                        "Timeout al conectar con servicio de Registro MICM"))
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
                .log(LoggingLevel.INFO, logger, "Iniciando registro inscripcion MICM")

                // Preparar request
                .process(generateRegistrationRequestProcessor)
                .marshal().json(JsonLibrary.Jackson)
                
                // Configurar headers HTTP del request original
                .setHeader("Canal", exchangeProperty("canalRq"))
                .setHeader("Usuario", exchangeProperty("usuarioRq"))
                .setHeader("Terminal", exchangeProperty("terminalRq"))
                .setHeader("FechaHora", exchangeProperty("fechaHoraRq"))
                .setHeader("Version", exchangeProperty("versionRq"))
                .setHeader("Servicio", constant("RegistroInscripcionMICM"))
                
                // Configurar headers HTTP estándar
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))

                // Llamar al servicio
                .toD(registrationUrl + "?bridgeEndpoint=true&throwExceptionOnFailure=false")

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
                
                // 500 - Error interno
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(500))
                    .log(LoggingLevel.ERROR, logger, "Error interno en servicio registro - HTTP 500")
                    .setProperty(Constants.MESSAGE_PROPERTIE, constant("Error interno en servicio de registro"))
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(502))
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()
                
                // Otros códigos de error
                .otherwise()
                    .log(LoggingLevel.ERROR, logger, "Error inesperado en servicio registro - HTTP ${header.CamelHttpResponseCode}")
                    .process(exchange -> {
                        Integer httpCode = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
                        exchange.setProperty(Constants.MESSAGE_PROPERTIE, "Error inesperado en registro: HTTP " + httpCode);
                        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 502);
                    })
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()
                .end();
    }
}