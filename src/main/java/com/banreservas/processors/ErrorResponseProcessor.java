package com.banreservas.processors;

import com.banreservas.model.outbound.orq.ResponseRegistrationOrqDto;

import jakarta.enterprise.context.ApplicationScoped;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processor que maneja las respuestas de error del sistema de registro MICM.
 * Genera respuestas estructuradas cuando ocurren errores durante el procesamiento.
 * 
 * @author Roberto Kepp
 * @version 1.0
 * @since 2025-07-01
 */
@ApplicationScoped
public class ErrorResponseProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(ErrorResponseProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        logger.info("Procesando respuesta de error");

        String errorMessage = getErrorMessage(exchange);
        String errorCode = getErrorCode(exchange);

        logger.error("Error procesado - Código: {}, Mensaje: {}", errorCode, errorMessage);

        // Crear respuesta de error estructurada
        ResponseRegistrationOrqDto errorResponse = new ResponseRegistrationOrqDto(
            "false",
            errorMessage,
            buildErrorObject(errorCode, errorMessage),
            null // No hay datos en caso de error
        );

        exchange.getIn().setBody(errorResponse);
    }

    /**
     * Extrae el mensaje de error del exchange.
     * 
     * @param exchange El intercambio de Camel
     * @return Mensaje de error
     */
    private String getErrorMessage(Exchange exchange) {
        String message = (String) exchange.getProperty("Mensaje");
        if (message != null && !message.isEmpty()) {
            return message;
        }

        // Mensaje por defecto si no hay uno específico
        return "Error interno del servidor durante el procesamiento del registro";
    }

    /**
     * Extrae el código de error del exchange.
     * 
     * @param exchange El intercambio de Camel
     * @return Código de error
     */
    private String getErrorCode(Exchange exchange) {
        Object codigo = exchange.getProperty("Codigo");
        if (codigo != null) {
            return codigo.toString();
        }

        // Código por defecto
        return "500";
    }

    /**
     * Construye el objeto de error con información detallada.
     * 
     * @param errorCode Código del error
     * @param errorMessage Mensaje del error
     * @return Objeto con información del error
     */
    private Object buildErrorObject(String errorCode, String errorMessage) {
        return new ErrorDetail(errorCode, errorMessage, System.currentTimeMillis());
    }

    /**
     * Clase interna para representar detalles del error.
     */
    private record ErrorDetail(String code, String message, long timestamp) {}
}