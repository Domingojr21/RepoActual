package com.banreservas.model.inbound.orq;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;

/**
 * DTO simplificado de acreedor para el orquestador.
 * Versión reducida de CreditorDto para uso interno del orquestador.
 * 
 * @author Domingo Ruiz c-djruiz@banreservas.com
 * @version 1.0
 * @since 2025-07-01
 */
@RegisterForReflection
public record CreditorOrqDto(
        @JsonProperty("rncCedula")
        String rncCedula,

        @JsonProperty("nombreAcreedor")
        String creditorName,

        @JsonProperty("idMunicipio")
        String municipalityId,

        @JsonProperty("domicilio")
        String address,

        @JsonProperty("correoElectronico")
        String email,

        @JsonProperty("telefono")
        String phone,

        @JsonProperty("nacional")
        Boolean national
) implements Serializable {
}