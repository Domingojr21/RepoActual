package com.banreservas.model.outbound.registration;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;

/**
 * DTO principal para la respuesta del servicio de registro de inscripción MICM.
 * Contiene el header con códigos de respuesta y el body con los datos del registro.
 * 
 * @author Roberto Kepp
 * @version 1.0
 * @since 2025-07-01
 */
@RegisterForReflection
public record RegistrationResponse(
        RegistrationHeaderDto header,
        RegistrationBodyDto body
) implements Serializable {
}
