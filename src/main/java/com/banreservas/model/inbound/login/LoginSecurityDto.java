package com.banreservas.model.inbound.login;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO que contiene la información de seguridad del usuario autenticado.
 * Encapsula el token de acceso obtenido tras la autenticación exitosa.
 * 
 * Este DTO sirve como wrapper para el token JWT y cualquier información
 * adicional de seguridad que el servicio MICM pueda proporcionar.
 * 
 * @param token Objeto que contiene el token JWT y sus propiedades
 * 
 * @author Roberto Kepp
 * @version 1.0
 * @since 2025-07-01
 */
@RegisterForReflection
public record LoginSecurityDto(
        @JsonProperty("token") LoginTokenDto token
) implements Serializable {}