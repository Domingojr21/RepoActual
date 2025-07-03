package com.banreservas.model.inbound.registration;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO que representa la información de un acreedor en el sistema MICM.
 * Contiene datos de identificación, contacto y fechas relevantes.
 * 
 * @author Roberto Kepp
 * @version 1.0
 * @since 2025-07-01
 */

@RegisterForReflection
public record CreditorDto(
        @JsonProperty("rncCedula")
        String rncCedula,

        @JsonProperty("creditorName")
        String creditorName,

        @JsonProperty("municipalityId")
        String municipalityId,

        @JsonProperty("address")
        String address,

        @JsonProperty("email")
        String email,

        @JsonProperty("phone")
        String phone,

        @JsonProperty("exclusionDate")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        Date exclusionDate,

        @JsonProperty("national")
        Boolean national
) implements Serializable {
}