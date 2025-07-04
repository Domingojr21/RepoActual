package com.banreservas.model.inbound.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;

/**
 * DTO principal para la solicitud de autenticación en el sistema MICM.
 * Contiene las credenciales de usuario necesarias para obtener un token de acceso.
 * 
 * Este DTO se utiliza para enviar las credenciales al servicio LogInMICM_WS
 * y obtener un token JWT válido para posteriores operaciones.
 * 
 * @param email Email del usuario para autenticación
 * @param password Contraseña del usuario
 * 
 * @author Domingo Ruiz c-djruiz@banreservas.com
 * @version 1.0
 * @since 2025-07-01
 */
@RegisterForReflection
public record LoginMicmRequest(
        @JsonProperty("email")
        String email,

        @JsonProperty("password")
        String password
) implements Serializable {
}
