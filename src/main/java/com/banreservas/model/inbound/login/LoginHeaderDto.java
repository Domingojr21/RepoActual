package com.banreservas.model.inbound.login;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO que representa el header de respuesta del servicio de login MICM.
 * Contiene información sobre el resultado de la operación de autenticación.
 * 
 * El responseCode indica si la operación fue exitosa (200) o si hubo algún error,
 * mientras que responseMessage proporciona una descripción textual del resultado.
 * 
 * @param responseCode Código numérico de respuesta (200 = exitoso)
 * @param responseMessage Mensaje descriptivo del resultado de la operación
 * 
 * @author Roberto Kepp
 * @version 1.0
 * @since 2025-07-01
 */
@RegisterForReflection
public record LoginHeaderDto(
        @JsonProperty("responseCode") int responseCode,
        @JsonProperty("responseMessage") String responseMessage
) implements Serializable {}
