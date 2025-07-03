package com.banreservas.processors;

import com.banreservas.model.outbound.registration.RegistrationResponse;
import com.banreservas.model.outbound.orq.ResponseRegistrationOrqDto;
import com.banreservas.model.outbound.orq.RegistrationDataOrqDto;
import com.banreservas.util.Constants;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Map;

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
        logger.info("Mapeando respuesta del backend MICM de registro");

        try {
            String jsonResponse = exchange.getIn().getBody(String.class);
            
            // Validar que la respuesta no esté vacía o truncada
            if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
                logger.error("Respuesta del backend está vacía");
                handleError(exchange, "Respuesta vacía del servicio de registro MICM", "502");
                return;
            }
            
            // Log de la respuesta para debugging (primeros 500 caracteres)
            logger.debug("Respuesta del backend (primeros 500 chars): {}", 
                        jsonResponse.length() > 500 ? jsonResponse.substring(0, 500) + "..." : jsonResponse);
            
            // Verificar si la respuesta está completa (debe terminar con '}' o ']')
            String trimmedResponse = jsonResponse.trim();
            if (!trimmedResponse.endsWith("}") && !trimmedResponse.endsWith("]")) {
                logger.error("Respuesta del backend está incompleta o truncada. Termina con: '{}'", 
                           trimmedResponse.substring(Math.max(0, trimmedResponse.length() - 50)));
                handleError(exchange, "Respuesta incompleta del servicio de registro MICM - posible timeout de red", "502");
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            RegistrationResponse backendResponse = mapper.readValue(jsonResponse, RegistrationResponse.class);

            if (backendResponse != null && backendResponse.header() != null) {
                
                // Inicializar variables
                String messageString = ""; // String vacío para éxito
                String succeeded = "false";
                RegistrationDataOrqDto dataOrq = null;

                if (backendResponse.body() != null && backendResponse.body().data() != null) {
                    var registrationData = backendResponse.body().data();
                    
                    // Crear datos simplificados para el orquestador
                    dataOrq = new RegistrationDataOrqDto(
                        registrationData.id(),
                        registrationData.numeroRegistro(),
                        formatDate(registrationData.fechaRegistro()),
                        determineStatus(registrationData)
                    );
                    
                    succeeded = "true";
                    // Message VACÍO para éxito
                    messageString = "";
                }

                // Validar código de respuesta del header
                if (backendResponse.header().responseCode() != 200) {
                    succeeded = "false";
                    
                    // Message con contenido para errores
                    messageString = backendResponse.header().responseMessage() != null ? 
                                   backendResponse.header().responseMessage() : 
                                   "Error en el registro de inscripción";
                    
                    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, backendResponse.header().responseCode());
                    dataOrq = null; // No hay datos en caso de error
                }

                ResponseRegistrationOrqDto response = new ResponseRegistrationOrqDto(
                    succeeded,
                    messageString, // String vacío para éxito, String con contenido para error
                    Collections.emptyMap(), // errors como OBJETO VACÍO en caso exitoso
                    dataOrq
                );

                exchange.getIn().setBody(response);
                logger.info("Respuesta de registro procesada: succeeded={}, hasData={}", succeeded, dataOrq != null);

            } else {
                handleError(exchange, "Respuesta inválida del servicio de registro MICM", "502");
            }

        } catch (com.fasterxml.jackson.core.JsonParseException e) {
            logger.error("Error parsing JSON del backend - respuesta malformada: {}", e.getMessage());
            handleError(exchange, "Respuesta malformada del servicio de registro MICM", "502");
            
        } catch (com.fasterxml.jackson.databind.JsonMappingException e) {
            logger.error("Error mapeando JSON del backend: {}", e.getMessage());
            handleError(exchange, "Error mapeando respuesta del servicio de registro MICM", "502");
            
        } catch (Exception e) {
            logger.error("Error procesando respuesta del backend de registro: {}", e.getMessage(), e);
            handleError(exchange, "Error interno al procesar la respuesta del backend de registro", "500");
        }
    }
    
    private void handleError(Exchange exchange, String errorMessage, String errorCode) {
        ResponseRegistrationOrqDto errorResponse = new ResponseRegistrationOrqDto(
            "false",
            errorMessage, // Message como STRING para errores
            Map.of(  // errors como OBJETO con información del error
                "code", errorCode,
                "message", errorMessage,
                "timestamp", System.currentTimeMillis(),
                "serviceType", "registration"
            ),
            null
        );

        exchange.getIn().setBody(errorResponse);
        logger.error("Error mapeado: {}", errorMessage);
    }

    private String formatDate(java.util.Date date) {
        if (date == null) {
            return null;
        }
        return DATE_FORMAT.format(date);
    }

    private String determineStatus(com.banreservas.model.outbound.registration.RegistrationDataDto data) {
        if (data.descripcionEstatus() != null && !data.descripcionEstatus().isEmpty()) {
            return data.descripcionEstatus();
        }
        
        if (data.idEstado() != null) {
            return "Estado ID: " + data.idEstado();
        }
        
        return "Registrado";
    }
}