package com.banreservas.model.inbound.orq;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;

/**
 * DTO simplificado de deudor para el orquestador.
 * Versión reducida de DebtorDto para uso interno del orquestador.
 * 
 * @author Domingo Ruiz c-djruiz@banreservas.com
 * @version 1.0
 * @since 2025-07-01
 */
@RegisterForReflection
public record DebtorOrqDto(
        @JsonProperty("rncCedula")
        String rncCedula,

        @JsonProperty("IdTipoDeudor")
        String debtorTypeId,

        @JsonProperty("nombreDeudor")
        String debtorName,

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