package com.banreservas.processors;

import com.banreservas.model.outbound.registration.RegistrationResponse;
import com.banreservas.model.outbound.orq.ResponseRegistrationOrqDto;
import com.banreservas.model.outbound.orq.CompleteRegistrationDataOrqDto;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

/**
 * Processor que mapea la respuesta del backend MICM al formato del orquestador.
 * Transforma la respuesta completa del servicio de registro a la estructura 
 * completa requerida por el cliente.
 * 
 * @author Domingo Ruiz c-djruiz@banreservas.com
 * @version 1.0
 * @since 2025-07-02
 */

@ApplicationScoped
public class MapRegistrationBackendResponseProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(MapRegistrationBackendResponseProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        logger.info("Mapeando respuesta del backend MICM de registro");

        try {
            String jsonResponse = exchange.getIn().getBody(String.class);
            
            if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
                logger.error("Respuesta del backend está vacía");
                handleError(exchange, "Respuesta vacía del servicio de registro MICM", "502");
                return;
            }
            
            String trimmedResponse = jsonResponse.trim();
            if (!trimmedResponse.endsWith("}") && !trimmedResponse.endsWith("]")) {
                logger.error("Respuesta del backend está incompleta");
                handleError(exchange, "Respuesta incompleta del servicio de registro MICM", "502");
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            RegistrationResponse backendResponse = mapper.readValue(jsonResponse, RegistrationResponse.class);

            if (backendResponse != null && backendResponse.header() != null) {
                
                String succeeded = "false";
                Object message = Collections.emptyMap();
                CompleteRegistrationDataOrqDto dataOrq = null;

                if (backendResponse.body() != null && backendResponse.body().data() != null) {
                    var registrationData = backendResponse.body().data();
                    
                    String idAsString = registrationData.id() != null ? 
                                       registrationData.id().toString() : "";
                    
                    dataOrq = CompleteRegistrationDataOrqDto.fromId(idAsString);
                    succeeded = "true";
                    message = Collections.emptyMap();
                }

                if (backendResponse.header().responseCode() != 200) {
                    succeeded = "false";
                    
                    // Para errores, devolver el mensaje del backend directamente
                    message = backendResponse.header().responseMessage() != null ? 
                             backendResponse.header().responseMessage() : 
                             "Error en el registro de inscripción";
                    
                    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, backendResponse.header().responseCode());
                    dataOrq = null;
                }

                ResponseRegistrationOrqDto response = new ResponseRegistrationOrqDto(
                    succeeded,
                    message,
                    Collections.emptyMap(),
                    dataOrq
                );

                exchange.getIn().setBody(response);
                logger.info("Respuesta de registro procesada: succeeded={}, hasData={}", succeeded, dataOrq != null);

            } else {
                handleError(exchange, "Respuesta inválida del servicio de registro MICM", "502");
            }

        } catch (com.fasterxml.jackson.core.JsonParseException e) {
            logger.error("Error parsing JSON del backend: {}", e.getMessage());
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
            errorMessage,
            Collections.emptyMap(),  
            null
        );

        exchange.getIn().setBody(errorResponse);
        logger.error("Error mapeado: {}", errorMessage);
    }
}