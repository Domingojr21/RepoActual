package com.banreservas.model.inbound.orq;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;

/**
 * DTO simplificado de activo para el orquestador.
 * Versión reducida de AssetDto para uso interno del orquestador.
 * 
 * @author Roberto Kepp
 * @version 1.0
 * @since 2025-07-01
 */
@RegisterForReflection
public record AssetOrqDto(
        @JsonProperty("idTipoPropiedad")
        Integer propertyTypeId,

        @JsonProperty("idTipoBien")
        Integer assetTypeId,

        @JsonProperty("numeroSerial")
        String serialNumber,

        @JsonProperty("descripcionBien")
        String assetDescription,

        @JsonProperty("incorporacionInmueble")
        Boolean realEstateIncorporation,

        @JsonProperty("incorporacionInmuebleDescripcion")
        String realEstateIncorporationDescription,

        @JsonProperty("registroDondeSeEnCuentraInscrito")
        String registrationRecord,

        @JsonProperty("ubicacionDelInmueble")
        String propertyLocation
) implements Serializable {
}