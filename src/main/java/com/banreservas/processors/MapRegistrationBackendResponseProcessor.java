package com.banreservas.processors;

import com.banreservas.model.outbound.registration.RegistrationResponse;

import jakarta.enterprise.context.ApplicationScoped;

import com.banreservas.model.outbound.orq.ResponseRegistrationOrqDto;
import com.banreservas.model.outbound.orq.RegistrationDataOrqDto;
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
        logger.info("Mapeando respuesta del backend MICM");

        String tipoRespuesta = (String) exchange.getProperty("Tipo");
        
        if ("0".equals(tipoRespuesta)) {
            procesarRespuestaExitosa(exchange);
        } else {
            procesarRespuestaError(exchange);
        }
    }

    /**
     * Procesa una respuesta exitosa del backend.
     * 
     * @param exchange El intercambio de Camel
     */
    private void procesarRespuestaExitosa(Exchange exchange) {
        try {
            RegistrationResponse backendResponse = exchange.getIn().getBody(RegistrationResponse.class);
            
            if (backendResponse != null && backendResponse.body() != null && backendResponse.body().data() != null) {
                
                var registrationData = backendResponse.body().data();
                
                // Crear datos simplificados para el orquestador
                RegistrationDataOrqDto dataOrq = new RegistrationDataOrqDto(
                    registrationData.id(),
                    registrationData.numeroRegistro(),
                    formatDate(registrationData.fechaRegistro()),
                    determineEstado(registrationData)
                );

                ResponseRegistrationOrqDto responseOrq = new ResponseRegistrationOrqDto(
                    "true",
                    "Registro de inscripción procesado exitosamente",
                    null,
                    dataOrq
                );

                exchange.getIn().setBody(responseOrq);
                logger.info("Respuesta exitosa mapeada - ID: {}", registrationData.id());
                
            } else {
                logger.warn("Respuesta del backend está vacía o incompleta");
                procesarRespuestaError(exchange);
            }
            
        } catch (Exception e) {
            logger.error("Error al mapear respuesta exitosa: {}", e.getMessage(), e);
            exchange.setProperty("Mensaje", "Error al procesar respuesta del backend");
            procesarRespuestaError(exchange);
        }
    }

    /**
     * Procesa una respuesta de error.
     * 
     * @param exchange El intercambio de Camel
     */
    private void procesarRespuestaError(Exchange exchange) {
        String mensaje = (String) exchange.getProperty("Mensaje");
        if (mensaje == null || mensaje.isEmpty()) {
            mensaje = "Error durante el procesamiento del registro";
        }

        ResponseRegistrationOrqDto errorResponse = new ResponseRegistrationOrqDto(
            "false",
            mensaje,
            buildErrorInfo(exchange),
            null
        );

        exchange.getIn().setBody(errorResponse);
        logger.warn("Respuesta de error mapeada: {}", mensaje);
    }

    /**
     * Formatea una fecha para la respuesta.
     * 
     * @param date Fecha a formatear
     * @return Fecha formateada como string
     */
    private String formatDate(java.util.Date date) {
        if (date == null) {
            return null;
        }
        return DATE_FORMAT.format(date);
    }

    /**
     * Determina el estado basado en los datos del registro.
     * 
     * @param data Datos del registro
     * @return Estado descriptivo
     */
    private String determineEstado(com.banreservas.model.outbound.registration.RegistrationDataDto data) {
        if (data.descripcionEstatus() != null) {
            return data.descripcionEstatus();
        }
        
        if (data.idEstado() != null) {
            return "Estado ID: " + data.idEstado();
        }
        
        return "Registrado";
    }

    /**
     * Construye información del error para la respuesta.
     * 
     * @param exchange El intercambio de Camel
     * @return Información del error
     */
    private Object buildErrorInfo(Exchange exchange) {
        String codigo = (String) exchange.getProperty("Codigo");
        String mensaje = (String) exchange.getProperty("Mensaje");
        
        return new ErrorInfo(
            codigo != null ? codigo : "500",
            mensaje != null ? mensaje : "Error desconocido",
            System.currentTimeMillis()
        );
    }

    /**
     * Clase interna para información de errores.
     */
    private record ErrorInfo(String code, String message, long timestamp) {}
}