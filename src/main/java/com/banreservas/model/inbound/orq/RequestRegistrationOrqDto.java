package com.banreservas.model.inbound.orq;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;
import java.util.List;

/**
 * DTO simplificado para el request del orquestador que recibe desde el cliente.
 * Contiene las operaciones, deudores, activos y acreedores sin información de seguridad.
 * 
 * @author Roberto Kepp
 * @version 1.0
 * @since 2025-07-01
 */
@RegisterForReflection
public record RequestRegistrationOrqDto(
        @JsonProperty("operaciones")
        OperationsOrqDto operations,

        @JsonProperty("acreedores")
        List<CreditorOrqDto> creditors,

        @JsonProperty("deudores")
        List<DebtorOrqDto> debtors,

        @JsonProperty("bienes")
        List<AssetOrqDto> assets
) implements Serializable {
}