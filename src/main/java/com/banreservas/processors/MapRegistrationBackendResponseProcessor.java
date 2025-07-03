package com.banreservas.processors;

import com.banreservas.model.outbound.registration.RegistrationResponse;
import com.banreservas.model.outbound.orq.ResponseRegistrationOrqDto;
import com.banreservas.model.outbound.orq.RegistrationDataOrqDto;
import com.banreservas.util.Constants;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

/**
 * Processor que mapea la respuesta del backend MICM al formato del orquestador.
 * Transforma la respuesta completa del servicio de registro a un formato simplificado
 * para el cliente final.
 * 
 * @author Roberto Kepp
 * @version 1.0
 * @since 2025-07-01
 */
@ApplicationScoped
public class MapRegistrationBackendResponseProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(MapRegistrationBackendResponseProcessor.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @Override
    public void process(Exchange exchange) throws Exception {
        logger.info("Mapping MICM backend response");

        String responseType = (String) exchange.getProperty("Tipo");
        
        if ("0".equals(responseType)) {
            processSuccessfulResponse(exchange);
        } else {
            processErrorResponse(exchange);
        }
    }

    private void processSuccessfulResponse(Exchange exchange) {
        try {
            RegistrationResponse backendResponse = exchange.getIn().getBody(RegistrationResponse.class);
            
            if (backendResponse != null && backendResponse.body() != null && backendResponse.body().data() != null) {
                
                var registrationData = backendResponse.body().data();
                
                // Create simplified data for orchestrator
                RegistrationDataOrqDto dataOrq = new RegistrationDataOrqDto(
                    registrationData.id(),
                    registrationData.numeroRegistro(),
                    formatDate(registrationData.fechaRegistro()),
                    determineStatus(registrationData)
                );

                ResponseRegistrationOrqDto responseOrq = new ResponseRegistrationOrqDto(
                    "true",
                    "Registration inscription processed successfully",
                    null,
                    dataOrq
                );

                exchange.getIn().setBody(responseOrq);
                logger.info("Successful response mapped - ID: {}", registrationData.id());
                
            } else {
                logger.warn("Backend response is empty or incomplete");
                processErrorResponse(exchange);
            }
            
        } catch (Exception e) {
            logger.error("Error mapping successful response: {}", e.getMessage(), e);
            exchange.setProperty(Constants.MESSAGE_PROPERTIE, "Error processing backend response");
            processErrorResponse(exchange);
        }
    }

    private void processErrorResponse(Exchange exchange) {
        String message = (String) exchange.getProperty(Constants.MESSAGE_PROPERTIE);
        if (message == null || message.isEmpty()) {
            message = "Error during registration processing";
        }

        ResponseRegistrationOrqDto errorResponse = new ResponseRegistrationOrqDto(
            "false",
            message,
            buildErrorInfo(exchange),
            null
        );

        exchange.getIn().setBody(errorResponse);
        logger.warn("Error response mapped: {}", message);
    }

    private String formatDate(java.util.Date date) {
        if (date == null) {
            return null;
        }
        return DATE_FORMAT.format(date);
    }

    private String determineStatus(com.banreservas.model.outbound.registration.RegistrationDataDto data) {
        if (data.descripcionEstatus() != null) {
            return data.descripcionEstatus();
        }
        
        if (data.idEstado() != null) {
            return "Status ID: " + data.idEstado();
        }
        
        return "Registered";
    }

    private Object buildErrorInfo(Exchange exchange) {
        String code = (String) exchange.getProperty("Codigo");
        String message = (String) exchange.getProperty(Constants.MESSAGE_PROPERTIE);
        
        return new ErrorInfo(
            code != null ? code : "500",
            message != null ? message : "Unknown error",
            System.currentTimeMillis()
        );
    }

    private record ErrorInfo(String code, String message, long timestamp) {}
}