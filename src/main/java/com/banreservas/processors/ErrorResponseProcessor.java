package com.banreservas.processors;

import com.banreservas.model.outbound.orq.ResponseRegistrationOrqDto;
import com.banreservas.util.Constants;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;

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
    
    // Constantes para evitar duplicación de literales
    private static final String REGISTRATION_TYPE = "registration";

    @Override
    public void process(Exchange exchange) throws Exception {
        logger.info("Procesando respuesta de error para registro MICM");

        Integer httpCode = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
        if (httpCode == null) {
            httpCode = 500;
        }

        String mensajeTexto = exchange.getProperty(Constants.MESSAGE_PROPERTIE, String.class);
        if (mensajeTexto == null || mensajeTexto.isEmpty()) {
            mensajeTexto = getDefaultMessageForCode(httpCode);
        }

        ResponseRegistrationOrqDto response = new ResponseRegistrationOrqDto(
            "false",
            mensajeTexto, 
            buildErrorObject(httpCode, mensajeTexto),
            null // No hay datos en caso de error
        );

        // Establecer el código HTTP en el header
        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, httpCode);
        
        // Establecer el cuerpo de la respuesta
        exchange.getIn().setBody(response);

        logger.info("Respuesta de error de registro generada: HTTP {} - {}", httpCode, mensajeTexto);
    }

    private String getDefaultMessageForCode(Integer httpCode) {
        return switch (httpCode) {
            case 400 -> "Request inválido para registro de inscripción";
            case 401 -> "No autorizado para registro de inscripción";
            case 403 -> "Acceso denegado para registro de inscripción";
            case 500 -> "Error interno del servidor en registro de inscripción";
            case 502 -> "Error en servicio externo de registro de inscripción";
            case 503 -> "Servicio de registro de inscripción no disponible";
            default -> "Error en el procesamiento de registro de inscripción";
        };
    }

    private Object buildErrorObject(Integer httpCode, String message) {
        return Map.of(
            "code", httpCode.toString(),
            "message", message,
            "timestamp", System.currentTimeMillis(),
            "serviceType", REGISTRATION_TYPE
        );
    }
}