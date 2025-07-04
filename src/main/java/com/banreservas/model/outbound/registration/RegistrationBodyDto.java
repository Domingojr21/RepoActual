package com.banreservas.model.outbound.registration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;

/**
 * DTO que encapsula el cuerpo de la respuesta del servicio de registro MICM.
 * Contiene los datos del registro creado o actualizado.
 * 
 * @author Domingo Ruiz c-djruiz@banreservas.com
 * @version 1.0
 * @since 2025-07-01
 */
@RegisterForReflection
public record RegistrationBodyDto(
        @JsonProperty("data")
        RegistrationDataDto data
) implements Serializable {
}