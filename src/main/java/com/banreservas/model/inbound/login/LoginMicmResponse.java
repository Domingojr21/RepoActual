package com.banreservas.model.inbound.login;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO principal para la respuesta del servicio de autenticación MICM.
 * Contiene el header con información de la respuesta y el body con los datos de seguridad.
 * 
 * Esta respuesta incluye el token JWT necesario para realizar operaciones posteriores
 * en el sistema MICM, así como información sobre el estado de la autenticación.
 * 
 * @param header Información del header de respuesta (código y mensaje)
 * @param body Cuerpo de la respuesta con datos de seguridad y token
 * 
 * @author Roberto Kepp
 * @version 1.0
 * @since 2025-07-01
 */
@RegisterForReflection
public record LoginMicmResponse(
        @JsonProperty("header") LoginHeaderDto header,
        @JsonProperty("body") LoginBodyDto body
) implements Serializable {}