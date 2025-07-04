package com.banreservas.model.inbound.registration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;

/**
 * DTO que representa la información de un deudor en el sistema MICM.
 * Contiene datos de identificación, tipo, contacto y ubicación.
 * 
 * @author Domingo Ruiz c-djruiz@banreservas.com
 * @version 1.0
 * @since 2025-07-01
 */
@RegisterForReflection
public record DebtorDto(
        @JsonProperty("rncCedula")
        String rncCedula,

        @JsonProperty("debtorTypeId")
        Integer debtorTypeId,

        @JsonProperty("debtorName")
        String debtorName,

        @JsonProperty("municipalityId")
        String municipalityId,

        @JsonProperty("address")
        String address,

        @JsonProperty("email")
        String email,

        @JsonProperty("phone")
        String phone,

        @JsonProperty("national")
        Boolean national
) implements Serializable {
}
