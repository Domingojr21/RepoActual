package com.banreservas.model.inbound.registration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO que representa la información de un activo o bien en el sistema MICM.
 * Incluye detalles sobre propiedades, inmuebles y características del activo.
 * 
 * @author Roberto Kepp
 * @version 1.0
 * @since 2025-07-01
 */
@RegisterForReflection
public record AssetDto(
        @JsonProperty("propertyTypeId")
        Integer propertyTypeId,

        @JsonProperty("assetTypeId")
        Integer assetTypeId,

        @JsonProperty("serialNumber")
        String serialNumber,

        @JsonProperty("assetDescription")
        String assetDescription,

        @JsonProperty("realEstateIncorporation")
        Boolean realEstateIncorporation,

        @JsonProperty("realEstateIncorporationDescription")
        String realEstateIncorporationDescription,

        @JsonProperty("realEstateIncorporationRegistration")
        String realEstateIncorporationRegistration,

        @JsonProperty("realEstateCadastralDistrict")
        String realEstateCadastralDistrict,

        @JsonProperty("realEstateParcelNumber")
        String realEstateParcelNumber,

        @JsonProperty("exclusionDate")
        Date exclusionDate,

        @JsonProperty("registrationRecord")
        String registrationRecord,

        @JsonProperty("propertyLocation")
        String propertyLocation
) implements Serializable {
}
