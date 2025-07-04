package com.banreservas.processors;

import com.banreservas.model.inbound.login.LoginMicmRequest;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processor que genera la solicitud de autenticación para el servicio LogInMICM_WS.
 * Construye el request con las credenciales configuradas y prepara los headers necesarios.
 * 
 * Este processor toma las credenciales del usuario desde la configuración de la aplicación
 * y genera un objeto LoginMicmRequest que será enviado al servicio de autenticación MICM.
 * También configura los headers HTTP requeridos para la llamada.
 * 
 * @author Domingo Ruiz c-djruiz@banreservas.com
 * @version 1.0
 * @since 2025-06-10
 */
@ApplicationScoped
public class GenerateLoginMicmRequestProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(GenerateLoginMicmRequestProcessor.class);

    @ConfigProperty(name = "micm.login.email")
    String micmEmail;

    @ConfigProperty(name = "micm.login.password")
    String micmPassword;

    @Override
    public void process(Exchange exchange) throws Exception {
        logger.info("Generando request de autenticación MICM");

        // Configurar headers para el servicio de login
        exchange.getIn().setHeader("sessionId", exchange.getIn().getHeader("sessionId"));
        exchange.getIn().setHeader("Accept", "application/json");
        exchange.getIn().setHeader("Content-Type", "application/json");
        exchange.getIn().setHeader("Authorization", exchange.getIn().getHeader("Authorization"));

        LoginMicmRequest loginRequest = new LoginMicmRequest(micmEmail, micmPassword);

        exchange.getIn().setBody(loginRequest);
        logger.info("Request de autenticación generado exitosamente");
    }
}