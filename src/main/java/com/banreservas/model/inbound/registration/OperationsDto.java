package com.banreservas.model.inbound.registration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;

/**
 * DTO que encapsula los datos de las operaciones para el registro de inscripción.
 * Incluye información sobre tipos de aviso, conciliación, fechas y montos.
 * 
 * @author Roberto Kepp
 * @version 1.0
 * @since 2025-07-01
 */

@RegisterForReflection
public record OperationsDto(
        @JsonProperty("noticeRegistrationTypeId")
        Integer noticeRegistrationTypeId,

        @JsonProperty("reconciliationType")
        Integer reconciliationType,

        @JsonProperty("expirationDate")
        String expirationDate,

        @JsonProperty("comments")
        String comments,

        @JsonProperty("currency")
        String currency,

        @JsonProperty("amount")
        Integer amount,

        @JsonProperty("movableGuaranteeType")
        Integer movableGuaranteeType,

        @JsonProperty("seizureTypeId")
        Integer seizureTypeId,

        @JsonProperty("seizureStateNotAvailableAdminJud")
        String seizureStateNotAvailableAdminJud,

        @JsonProperty("executionGuaranteedObligationDescription")
        String executionGuaranteedObligationDescription,

        @JsonProperty("executionDebtorDefaultDescription")
        String executionDebtorDefaultDescription,

        @JsonProperty("executionDefaultEvidenceDescription")
        String executionDefaultEvidenceDescription,

        @JsonProperty("executionBalanceAmountDescription")
        Integer executionBalanceAmountDescription,

        @JsonProperty("executionLegalCosts")
        Integer executionLegalCosts
) implements Serializable {
}