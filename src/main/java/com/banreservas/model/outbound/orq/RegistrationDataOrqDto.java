package com.banreservas.model.outbound.orq;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;

/**
 * DTO simplificado que contiene los datos esenciales del registro para la respuesta del orquestador.
 * Incluye solo los campos más relevantes del registro creado.
 * 
 * @author Roberto Kepp
 * @version 1.0
 * @since 2025-07-01
 */
@RegisterForReflection
public record RegistrationDataOrqDto(
        @JsonProperty("id")
        Integer id,

        @JsonProperty("numeroRegistro")
        String numeroRegistro,

        @JsonProperty("fechaRegistro")
        String fechaRegistro,

        @JsonProperty("estado")
        String estado
) implements Serializable {
}