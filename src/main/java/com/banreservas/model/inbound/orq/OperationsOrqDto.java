package com.banreservas.model.inbound.orq;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;

/**
 * DTO simplificado de operaciones para el orquestador.
 * Versión reducida de OperationsDto para uso interno del orquestador.
 * 
 * @author Roberto Kepp
 * @version 1.0
 * @since 2025-07-01
 */
@RegisterForReflection
public record OperationsOrqDto(
        @JsonProperty("idTipoAvisoInscripcion")
        Integer noticeRegistrationTypeId,

        @JsonProperty("tipoConciliacion")
        Integer reconciliationType,

        @JsonProperty("fechaVencimiento")
        String expirationDate,

        @JsonProperty("comentarios")
        String comments,

        @JsonProperty("moneda")
        String currency,

        @JsonProperty("monto")
        Integer amount,

        @JsonProperty("tipoDeGarantiaMobiliario")
        Integer movableGuaranteeType
) implements Serializable {
}