package com.banreservas.model.inbound.registration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;

/**
 * DTO que encapsula la información de seguridad para las operaciones de registro.
 * Contiene el token de autenticación necesario para las llamadas al servicio MICM.
 * 
 * @author Roberto Kepp
 * @version 1.0
 * @since 2025-07-01
 */
@RegisterForReflection
public record SecurityRegistrationDto(
        @JsonProperty("token")
        TokenRegistrationDto token
) implements Serializable {
}