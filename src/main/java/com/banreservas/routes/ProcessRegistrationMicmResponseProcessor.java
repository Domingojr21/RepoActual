package com.banreservas.routes;

import com.banreservas.exceptions.ValidationException;
import com.banreservas.model.outbound.registration.RegistrationResponse;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processor que procesa la respuesta del servicio MasterRegistroInscripcionMICM_WS.
 * Valida que el registro sea exitoso y extrae la información del registro creado.
 *
 * Estructura de respuesta esperada:
 * - header.responseCode: 200 para éxito
 * - body.data: Datos del registro creado
 *
 * @author Roberto Kepp
 * @version 1.0
 * @since 2025-07-01
 */
public class ProcessRegistrationMicmResponseProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(ProcessRegistrationMicmResponseProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        logger.info("Procesando respuesta del registro MICM");

        RegistrationResponse registrationResponse = exchange.getIn().getBody(RegistrationResponse.class);

        if (registrationResponse == null) {
            logger.error("Respuesta de registro MICM es nula");
            throw new ValidationException("Error en la respuesta del servicio de registro MICM");
        }

        // Validar header de respuesta
        if (registrationResponse.header() == null || registrationResponse.header().responseCode() != 200) {
            String errorMsg = registrationResponse.header() != null ?
                    registrationResponse.header().responseMessage() : "Error desconocido en registro MICM";
            logger.error("Error en header de registro MICM: {}", errorMsg);
            throw new ValidationException("Error en registro MICM: " + errorMsg);
        }

        // Validar body y datos
        if (registrationResponse.body() == null || registrationResponse.body().data() == null) {
            logger.error("Estructura de respuesta de registro MICM inválida");
            throw new ValidationException("Error en la estructura de respuesta del registro MICM");
        }

        var registrationData = registrationResponse.body().data();

        if (registrationData.id() == null) {
            logger.error("ID de registro no obtenido");
            throw new ValidationException("Error: no se obtuvo ID del registro creado");
        }

        // Guardar datos del registro para uso posterior
        exchange.setProperty("registrationId", registrationData.id());
        exchange.setProperty("numeroRegistro", registrationData.numeroRegistro());
        
        logger.info("Registro MICM procesado exitosamente. ID: {}", registrationData.id());
        logger.debug("Número de registro: {}", registrationData.numeroRegistro());
    }
}