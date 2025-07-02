package com.banreservas.processors;

import com.banreservas.exceptions.ValidationException;
import com.banreservas.model.inbound.login.LoginMicmResponse;

import jakarta.enterprise.context.ApplicationScoped;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processor que procesa la respuesta del servicio LogInMICM_WS.
 * Valida que el login sea exitoso y extrae el token JWT necesario
 * para la búsqueda externa en MICM.
 *
 * Estructura de respuesta esperada:
 * - header.responseCode: 200 para éxito
 * - body.security.token.succeed: true para autenticación exitosa
 * - body.security.token.number: Token JWT a extraer
 *
 * Autor: Roberto Kepp rkepp@banreservas.com
 * Fecha: 10/06/2025
 * Versión: 1.0
 */
@ApplicationScoped
public class ProcessLoginMicmResponseProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(ProcessLoginMicmResponseProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        logger.info("Procesando respuesta del login MICM");

        LoginMicmResponse loginResponse = exchange.getIn().getBody(LoginMicmResponse.class);

        if (loginResponse == null) {
            logger.error("Respuesta de login MICM es nula");
            throw new ValidationException("Error en la respuesta del servicio de login MICM");
        }

        // Validar header de respuesta
        if (loginResponse.header() == null || loginResponse.header().responseCode() != 200) {
            String errorMsg = loginResponse.header() != null ?
                    loginResponse.header().responseMessage() : "Error desconocido en login MICM";
            logger.error("Error en header de login MICM: {}", errorMsg);
            throw new ValidationException("Error en autenticación MICM: " + errorMsg);
        }

        // Validar body y token
        if (loginResponse.body() == null ||
                loginResponse.body().security() == null ||
                loginResponse.body().security().token() == null) {
            logger.error("Estructura de respuesta de login MICM inválida");
            throw new ValidationException("Error en la estructura de respuesta del login MICM");
        }

        var token = loginResponse.body().security().token();

        if (!token.succeed() || token.number() == null || token.number().isEmpty()) {
            String errorMsg = token.message() != null ? token.message() : "Autenticación fallida";
            logger.error("Token MICM inválido: {}", errorMsg);
            throw new ValidationException("Error en autenticación MICM: " + errorMsg);
        }

        exchange.setProperty("micmToken", token.number());
    }
}
