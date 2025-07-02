package com.banreservas.model.inbound.registration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;

/**
 * DTO que representa el token de autenticación para operaciones de registro en MICM.
 * Contiene el número del token JWT necesario para autenticación.
 * 
 * @author Roberto Kepp
 * @version 1.0
 * @since 2025-07-01
 */
@RegisterForReflection
public record TokenRegistrationDto(
        @JsonProperty("numero")
        String numero
) implements Serializable {
}
