package com.banreservas.model.outbound.orq;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;

/**
 * DTO para la respuesta del orquestador al cliente.
 * Versión simplificada que retorna solo los datos esenciales del registro.
 * 
 * @author Roberto Kepp
 * @version 1.0
 * @since 2025-07-01
 */
@RegisterForReflection
public record ResponseRegistrationOrqDto(
        @JsonProperty("succeeded")
        String succeeded,

        @JsonProperty("message")
        Object message,

        @JsonProperty("errors")
        Object errors,

        @JsonProperty("data")
        CompleteRegistrationDataOrqDto data
) implements Serializable {
}