package com.banreservas.model.outbound.registration;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;

/**
 * DTO que representa el header de respuesta del servicio de registro MICM.
 * Incluye código y mensaje de respuesta para indicar el resultado de la operación.
 * 
 * @author Roberto Kepp
 * @version 1.0
 * @since 2025-07-01
 */
@RegisterForReflection
public record RegistrationHeaderDto(
        int responseCode,
        String responseMessage
) implements Serializable {
}