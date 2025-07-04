package com.banreservas.model.inbound.login;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO que encapsula el cuerpo de la respuesta del login MICM.
 * Contiene la información de seguridad obtenida tras una autenticación exitosa.
 * 
 * Este DTO actúa como contenedor principal para los datos de seguridad,
 * incluyendo el token de autenticación y información relacionada.
 * 
 * @param security Objeto que contiene los datos de seguridad y token
 * 
 * @author Domingo Ruiz c-djruiz@banreservas.com
 * @version 1.0
 * @since 2025-07-01
 */
@RegisterForReflection
public record LoginBodyDto(
        @JsonProperty("security") LoginSecurityDto security
) implements Serializable {}

