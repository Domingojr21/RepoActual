package com.banreservas.model.inbound.registration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;
import java.util.List;

/**
 * DTO principal para la solicitud de registro de inscripción en MICM.
 * Contiene las operaciones, deudores, activos, acreedores y datos de seguridad.
 * 
 * @author Domingo Ruiz c-djruiz@banreservas.com
 * @version 1.0
 * @since 2025-07-01
 */

@RegisterForReflection
public record RequestRegistrationDto(
        @JsonProperty("operations")
        OperationsDto operations,

        @JsonProperty("debtors")
        List<DebtorDto> debtors,

        @JsonProperty("assets")
        List<AssetDto> assets,

        @JsonProperty("creditors")
        List<CreditorDto> creditors,

        @JsonProperty("security")
        SecurityRegistrationDto security
) implements Serializable {
}