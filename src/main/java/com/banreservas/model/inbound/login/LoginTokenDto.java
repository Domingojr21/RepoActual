package com.banreservas.model.inbound.login;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO que representa el token de autenticación JWT devuelto por el servicio MICM.
 * Contiene toda la información relacionada con el token de acceso y su validez.
 * 
 * Este token es fundamental para realizar operaciones posteriores en el sistema MICM,
 * ya que debe incluirse en las cabeceras de autorización de las siguientes llamadas.
 * 
 * @param number Token JWT como cadena de texto
 * @param expiration Fecha y hora de expiración del token en formato ISO 8601
 * @param contractValidation Indica si se requiere validación de contrato
 * @param succeed Indica si la generación del token fue exitosa
 * @param message Mensaje descriptivo sobre el estado del token
 * 
 * @author Domingo Ruiz c-djruiz@banreservas.com
 * @version 1.0
 * @since 2025-07-01
 */
@RegisterForReflection
public record LoginTokenDto(
        @JsonProperty("number") String number,
        @JsonProperty("expiration") String expiration,
        @JsonProperty("contractValidation") boolean contractValidation,
        @JsonProperty("succeed") boolean succeed,
        @JsonProperty("message") String message
) implements Serializable {}