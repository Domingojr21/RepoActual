package com.banreservas.processors;

import com.banreservas.model.outbound.orq.ResponseRegistrationOrqDto;
import com.banreservas.util.Constants;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Collections;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processor que maneja las respuestas de error del sistema de registro MICM.
 * Genera respuestas estructuradas cuando ocurren errores durante el procesamiento.
 * 
 * @author Domingo Ruiz c-djruiz@banreservas.com
 * @version 1.0
 * @since 2025-07-02
 */
 @ApplicationScoped
public class ErrorResponseProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(ErrorResponseProcessor.class);

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
            Collections.emptyMap(), 
            null
        );

        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, httpCode);
        exchange.getIn().setBody(response);

        logger.info("Respuesta de error de registro generada: HTTP {} - {}", httpCode, mensajeTexto);
    }

    private String getDefaultMessageForCode(Integer httpCode) {
        return switch (httpCode) {
            case 400 -> "Validacion de request ha fallado";
            case 401 -> "No autorizado para registro de inscripción";
            case 403 -> "Acceso denegado para registro de inscripción";
            case 500 -> "Error interno del servidor en registro de inscripción";
            case 502 -> "Error en servicio externo de registro de inscripción";
            case 503 -> "Servicio de registro de inscripción no disponible";
            default -> "Error en el procesamiento de registro de inscripción";
        };
    }
}